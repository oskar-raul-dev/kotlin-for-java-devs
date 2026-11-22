package dev.raul.oskar.learning.kotlin.oop

import dev.raul.oskar.learning.kotlin.basics.Money
import java.math.BigDecimal

// Clases, constructores, propiedades, herencia y polimorfismo, sobre las cuentas del Banco.
// En Kotlin las clases y métodos son FINAL por defecto: para heredar hace falta `open`.
// Money viene de la lección basics.

/** Constructor primario en la cabecera; `val`/`var` lo convierten en propiedad. */
abstract class Account(val id: String, initialBalance: Money = Money.zero()) {

    // Propiedades con setter privado: se leen desde fuera, se cambian solo desde dentro.
    var balance: Money = initialBalance
        private set

    var status: AccountStatus = AccountStatus.OPEN
        private set

    // Bloque init: validaciones del constructor (como el cuerpo de un constructor Java).
    init {
        require(id.isNotBlank()) { "id vacío" }
        require(!initialBalance.isNegative) { "saldo inicial negativo" }
    }

    /** Propiedad calculada: sin campo de respaldo, se evalúa en cada lectura. */
    val isEmpty: Boolean get() = balance.isZero

    abstract val kind: String

    fun deposit(amount: Money) {
        require(!amount.isNegative && !amount.isZero) { "monto debe ser positivo" }
        check(status.acceptsDeposits) { "cuenta $id $status" }
        balance += amount
    }

    /** open: las subclases pueden sobrescribirlo. */
    open fun withdraw(amount: Money) {
        require(!amount.isNegative && !amount.isZero) { "monto debe ser positivo" }
        check(status.acceptsWithdrawals) { "cuenta $id $status" }
        check(amount <= balance + overdraftLimit()) { "fondos insuficientes en $id" }
        balance -= amount
    }

    fun freeze() {
        status = AccountStatus.FROZEN
    }

    /** protected: visible en subclases, como en Java (pero NO en el mismo paquete). */
    protected open fun overdraftLimit(): Money = Money.zero(balance.currency)

    override fun toString(): String = "$kind($id, $balance, $status)"
}

class SavingsAccount(id: String, initial: Money, private val monthlyRate: BigDecimal) : Account(id, initial) {
    override val kind = "Savings"

    fun interest(): Money = balance.times(monthlyRate)
}

class CheckingAccount(id: String, initial: Money, private val overdraft: Money) : Account(id, initial) {
    override val kind = "Checking"

    override fun overdraftLimit(): Money = overdraft
}

/** Cuenta de garantía: recibe depósitos pero no permite retiros. Constructor secundario con this(...). */
class EscrowAccount(id: String, initial: Money) : Account(id, initial) {
    constructor(id: String) : this(id, Money.zero())

    override val kind = "Escrow"

    override fun withdraw(amount: Money) {
        throw UnsupportedOperationException("cuenta de garantía: $id")
    }
}

/** Polimorfismo: la misma llamada, distinta implementación según la clase real. */
fun transfer(from: Account, to: Account, amount: Money) {
    from.withdraw(amount)
    to.deposit(amount)
}
