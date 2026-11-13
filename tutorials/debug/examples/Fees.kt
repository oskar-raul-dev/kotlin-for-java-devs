// Programa para depurar con jdb (o IntelliJ): comisiones del caso Banco, con mínimo de USD 1.00.
// La línea 12 (return) es la del punto de parada que usa el tutorial: no la muevas sin actualizar README y check.sh.
import java.math.BigDecimal
import java.math.RoundingMode

// Comisión del 0.5 % con mínimo de USD 1.00, redondeo bancario.
fun fee(amount: BigDecimal): BigDecimal {
    val raw = amount * BigDecimal("0.005")
    val min = BigDecimal("1.00")
    // Punto de parada del tutorial: aquí raw y min ya existen.
    val result = raw.max(min)
    return result.setScale(2, RoundingMode.HALF_EVEN)
}

fun main() {
    val amounts = listOf("100", "1000", "50.10").map { BigDecimal(it) }   // map es inline: mira el SMAP
    val fees = amounts.map { fee(it) }
    println(fees)
}
