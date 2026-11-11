package dev.raul.oskar.learning.kotlin.oop

import java.math.BigDecimal

// Clases, constructores, propiedades, herencia y polimorfismo.
// En Kotlin las clases y métodos son FINAL por defecto: para heredar hace falta `open`.

/** Constructor primario en la cabecera; `val`/`var` lo convierten en propiedad. */
abstract class Account(val id: String, initialBalance: BigDecimal = BigDecimal.ZERO) {

    // Propiedad con setter privado: se lee desde fuera, se cambia solo desde dentro.
    var balance: BigDecimal = initialBalance
        private set

    // Bloque init: validaciones del constructor (como el cuerpo de un constructor Java).
    init {
        require(id.isNotBlank()) { "id vacío" }
        require(initialBalance >= BigDecimal.ZERO) { "saldo inicial negativo" }
    }

    /** Propiedad calculada: sin campo de respaldo, se evalúa en cada lectura. */
    val isEmpty: Boolean get() = balance.signum() == 0

    abstract val kind: String

    fun deposit(amount: BigDecimal) {
        require(amount > BigDecimal.ZERO) { "monto debe ser positivo" }
        balance += amount
    }

    /** open: las subclases pueden sobrescribirlo. */
    open fun withdraw(amount: BigDecimal) {
        require(amount > BigDecimal.ZERO) { "monto debe ser positivo" }
        check(amount <= balance + overdraftLimit()) { "fondos insuficientes en $id" }
        balance -= amount
    }

    /** protected: visible en subclases, como en Java (pero NO en el mismo paquete). */
    protected open fun overdraftLimit(): BigDecimal = BigDecimal.ZERO

    override fun toString(): String = "$kind($id, $balance)"
}

class SavingsAccount(id: String, initial: BigDecimal, private val rate: BigDecimal) : Account(id, initial) {
    override val kind = "Savings"

    fun interest(): BigDecimal = balance * rate
}

class CheckingAccount(id: String, initial: BigDecimal, private val overdraft: BigDecimal) : Account(id, initial) {
    override val kind = "Checking"

    override fun overdraftLimit(): BigDecimal = overdraft
}

/** Constructor secundario: delega en el primario con this(...). */
class LockedAccount(id: String, initial: BigDecimal) : Account(id, initial) {
    constructor(id: String) : this(id, BigDecimal.ZERO)

    override val kind = "Locked"

    override fun withdraw(amount: BigDecimal) {
        throw UnsupportedOperationException("cuenta bloqueada: $id")
    }
}

/** Polimorfismo: la misma llamada, distinta implementación según la clase real. */
fun transfer(from: Account, to: Account, amount: BigDecimal) {
    from.withdraw(amount)
    to.deposit(amount)
}
