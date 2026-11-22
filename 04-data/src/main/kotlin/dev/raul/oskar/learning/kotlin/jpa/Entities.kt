package dev.raul.oskar.learning.kotlin.jpa

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.math.BigDecimal
import java.time.LocalDate

// JPA en Kotlin, las reglas que importan:
//  1. NO usar data class para entidades: su equals/hashCode usa todos los campos (incluidas
//     colecciones lazy) y cambia cuando cambia el id -> se rompen Sets y proxies.
//  2. Constructor sin argumentos: lo genera el plugin kotlin("plugin.jpa") (no-arg).
//  3. Clases y propiedades no final: lo hace kotlin("plugin.allopen") para @Entity (proxies lazy).
//  4. id nullable (`Long? = null`): null = todavía no persistida.
//  5. equals/hashCode por id, con hashCode constante por clase (patrón recomendado para Hibernate).
//
// El modelo es el ledger del Banco (docs/Casos.md): cuentas, asientos de doble partida y sus líneas (postings).

enum class AccountStatus { OPEN, FROZEN }

/** DEBIT aumenta el saldo de la cuenta de efectivo del cliente y CREDIT lo disminuye (convención de Casos.md). */
enum class Direction { DEBIT, CREDIT }

@Entity
@Table(name = "account")
class Account(
    @Column(nullable = false, unique = true, length = 20)
    var number: String,

    @Column(nullable = false, length = 3)
    var currency: String,

    @Column(nullable = false, precision = 14, scale = 2)
    var balance: BigDecimal,

    // EnumType.STRING: guarda "FROZEN". ORDINAL guardaría 1 y se rompe al reordenar el enum.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    var status: AccountStatus = AccountStatus.OPEN,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    /** Bloqueo optimista: Hibernate incrementa version en cada UPDATE y falla si alguien se adelantó. */
    @Version
    var version: Long? = null

    // mappedBy: el dueño de la relación es Posting.account (la FK está en posting). Sin cascade: las líneas son
    // del asiento, no de la cuenta.
    @OneToMany(mappedBy = "account")
    val postings: MutableList<Posting> = mutableListOf()

    override fun equals(other: Any?) = other is Account && id != null && id == other.id
    override fun hashCode() = Account::class.hashCode()
    override fun toString() = "Account($number, $balance $currency, $status, v$version)"   // nunca colecciones lazy
}

@Entity
@Table(name = "journal_entry")
class JournalEntry(
    @Column(nullable = false, length = 200)
    var description: String,

    @Column(nullable = false)
    var bookedOn: LocalDate,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    // Las líneas viven y mueren con su asiento: cascade ALL + orphanRemoval.
    @OneToMany(mappedBy = "entry", cascade = [CascadeType.ALL], orphanRemoval = true)
    val postings: MutableList<Posting> = mutableListOf()

    /** Mantener los dos lados de las relaciones sincronizados (el asiento y la cuenta ven la línea). */
    fun post(account: Account, direction: Direction, amount: BigDecimal): JournalEntry {
        val line = Posting(direction, amount).also { it.entry = this; it.account = account }
        postings += line
        account.postings += line
        return this
    }

    fun isBalanced(): Boolean {
        fun sum(d: Direction) = postings.filter { it.direction == d }.fold(BigDecimal.ZERO) { acc, p -> acc + p.amount }
        return sum(Direction.DEBIT).compareTo(sum(Direction.CREDIT)) == 0
    }

    override fun equals(other: Any?) = other is JournalEntry && id != null && id == other.id
    override fun hashCode() = JournalEntry::class.hashCode()
    override fun toString() = "JournalEntry(id=$id, $description)"
}

@Entity
@Table(name = "posting")
class Posting(
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 6)
    var direction: Direction,

    @Column(nullable = false, precision = 14, scale = 2)
    var amount: BigDecimal,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    // ManyToOne es EAGER por defecto en JPA: casi siempre conviene LAZY.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entry_id", nullable = false)
    var entry: JournalEntry? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    var account: Account? = null

    override fun equals(other: Any?) = other is Posting && id != null && id == other.id
    override fun hashCode() = Posting::class.hashCode()
    override fun toString() = "Posting(id=$id, $direction $amount)"
}

/**
 * Una transferencia como asiento: CREDIT en el origen, DEBIT en el destino, y los saldos materializados al día.
 * Las cuentas tienen que estar gestionadas (cargadas en la misma transacción): el commit escribe todo.
 */
fun transferEntry(from: Account, to: Account, amount: BigDecimal, description: String, day: LocalDate): JournalEntry {
    require(amount.signum() > 0) { "monto debe ser positivo: $amount" }
    check(from.status == AccountStatus.OPEN) { "cuenta ${from.number} ${from.status}" }
    check(from.balance >= amount) { "fondos insuficientes en ${from.number}" }
    from.balance -= amount
    to.balance += amount
    return JournalEntry(description, day).post(from, Direction.CREDIT, amount).post(to, Direction.DEBIT, amount)
}
