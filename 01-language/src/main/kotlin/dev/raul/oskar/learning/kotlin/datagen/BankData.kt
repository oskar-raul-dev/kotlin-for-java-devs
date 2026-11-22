package dev.raul.oskar.learning.kotlin.datagen

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Random

// Datos sintéticos del Banco (docs/Casos.md, caso A): los movimientos de octubre de 2026 de la cuenta de efectivo
// de un cliente según el ledger, el extracto externo que hay que conciliar contra ellos, y tasas de cambio.

/** Cuentas de Casos.md: los dígitos de control (mod 97, como el IBAN) ya vienen calculados. */
const val CLIENT_ACCOUNT = "ACC-00001234-82"
const val OTHER_ACCOUNT = "ACC-00005678-39"

val STATEMENT_MONTH: List<LocalDate> =
    generateSequence(LocalDate.of(2026, 10, 1)) { it.plusDays(1) }.takeWhile { it.monthValue == 10 }.toList()

private val BUSINESS_DAYS = STATEMENT_MONTH.filter { it.dayOfWeek != DayOfWeek.SATURDAY && it.dayOfWeek != DayOfWeek.SUNDAY }

/** Las diferencias a propósito entre el extracto y el ledger. */
object StatementFlaws {
    const val DATE_SHIFTED = 3          // la línea del extracto llega un día después
    const val CENT_DIFFERENCE = 2       // el monto difiere en 0.01
    const val ONLY_IN_LEDGER = 1        // el banco todavía no la reporta
    const val ONLY_IN_STATEMENT = 1     // un cargo que el ledger no tiene
    const val MISSING_FX_DAY = "2026-10-14"
}

/** Un movimiento del ledger de la cuenta del cliente (positivo = entra dinero). */
data class LedgerMovement(val id: String, val date: LocalDate, val description: String, val amount: BigDecimal) {
    fun toCsv(): String = listOf(id, date.toString(), csvField(description), amount.toPlainString()).joinToString(",")
}

/** Una línea del extracto externo, con su propia referencia y su propio texto. */
data class StatementRow(val date: LocalDate, val description: String, val amount: BigDecimal, val reference: String) {
    fun toCsv(): String = listOf(date.toString(), csvField(description), amount.toPlainString(), reference).joinToString(",")
}

/** Comillas solo si hace falta, y las comillas internas se duplican (RFC 4180). */
fun csvField(text: String): String =
    if (text.any { it == ',' || it == '"' }) "\"" + text.replace("\"", "\"\"") + "\"" else text

private val MERCHANTS = listOf("Cafetería Luna", "Librería Norte", "Farmacia Sol", "Súper Ahorro", "Gasolinera Ruta 9")

private fun money(rnd: Random, min: Int, max: Int): BigDecimal =
    BigDecimal(min * 100 + rnd.nextInt((max - min) * 100)).movePointLeft(2)

fun ledgerMovements(seed: Long = DATA_SEED): List<LedgerMovement> {
    val rnd = Random(seed + 10)
    val raw = mutableListOf<Triple<LocalDate, String, BigDecimal>>()
    raw += Triple(LocalDate.of(2026, 10, 1), "Depósito de nómina", BigDecimal("3200.00"))
    raw += Triple(LocalDate.of(2026, 10, 16), "Depósito de nómina", BigDecimal("3200.00"))
    repeat(20) {
        val day = BUSINESS_DAYS[rnd.nextInt(BUSINESS_DAYS.size)]
        raw += Triple(day, "Pago con tarjeta ${MERCHANTS[rnd.nextInt(MERCHANTS.size)]}", -money(rnd, 5, 250))
    }
    raw += Triple(LocalDate.of(2026, 10, 7), "Compra 10 ACME", BigDecimal("-1001.00"))
    raw += Triple(LocalDate.of(2026, 10, 21), "Venta 5 ORBX", BigDecimal("211.75"))
    raw += Triple(LocalDate.of(2026, 10, 9), "Transferencia a $OTHER_ACCOUNT", BigDecimal("-500.00"))
    raw += Triple(LocalDate.of(2026, 10, 23), "Pago servicios \"Luz del Valle\", S.A.", BigDecimal("-84.30"))
    raw += Triple(LocalDate.of(2026, 10, 30), "Intereses del mes", BigDecimal("1.23"))
    return raw.sortedBy { it.first }
        .mapIndexed { i, (date, text, amount) -> LedgerMovement("JE-%06d".format(i + 1), date, text, amount) }
}

/** El extracto que manda el banco: casi lo mismo que el ledger, con las diferencias de [StatementFlaws]. */
fun statementRows(seed: Long = DATA_SEED): List<StatementRow> {
    val rnd = Random(seed + 11)
    val ledger = ledgerMovements(seed)
    val indices = ledger.indices.shuffled(rnd)
    val missing = indices.take(StatementFlaws.ONLY_IN_LEDGER).toSet()
    val shifted = indices.drop(StatementFlaws.ONLY_IN_LEDGER).take(StatementFlaws.DATE_SHIFTED).toSet()
    val centOff = indices.drop(StatementFlaws.ONLY_IN_LEDGER + StatementFlaws.DATE_SHIFTED)
        .take(StatementFlaws.CENT_DIFFERENCE).toSet()
    val rows = ledger.mapIndexedNotNull { i, m ->
        if (i in missing) return@mapIndexedNotNull null
        StatementRow(
            date = if (i in shifted) m.date.plusDays(1) else m.date,
            description = bankText(m.description),
            amount = if (i in centOff) m.amount - BigDecimal("0.01") else m.amount,
            reference = "",
        )
    } + StatementRow(LocalDate.of(2026, 10, 31), "CARGO POR MANTENIMIENTO, CUENTA", BigDecimal("-4.99"), "")
    return rows.sortedBy { it.date }.mapIndexed { i, r -> r.copy(reference = "BNK-%05d".format(70001 + i)) }
}

/** El banco escribe en mayúsculas y con su propio formato: "PAGO TARJETA, FARMACIA SOL". */
private fun bankText(ledgerText: String): String = when {
    ledgerText.startsWith("Pago con tarjeta ") -> "PAGO TARJETA, " + ledgerText.removePrefix("Pago con tarjeta ").uppercase()
    else -> ledgerText.uppercase()
}

fun ledgerCsv(seed: Long = DATA_SEED): String =
    (listOf("id,date,description,amount") + ledgerMovements(seed).map { it.toCsv() }).joinToString("\n", postfix = "\n")

fun statementCsv(seed: Long = DATA_SEED): String =
    (listOf("date,description,amount,reference") + statementRows(seed).map { it.toCsv() }).joinToString("\n", postfix = "\n")

/** Tasas USD → EUR y USD → MXN por día hábil, con un día sin tasa (hay que tomar la del día anterior). */
fun fxRatesJson(seed: Long = DATA_SEED): String {
    val rnd = Random(seed + 20)
    var eur = BigDecimal("0.9200")
    var mxn = BigDecimal("18.4500")
    val lines = BUSINESS_DAYS.mapNotNull { day ->
        eur = (eur * BigDecimal.valueOf(1 + rnd.nextGaussian() * 0.003)).setScale(4, RoundingMode.HALF_EVEN)
        mxn = (mxn * BigDecimal.valueOf(1 + rnd.nextGaussian() * 0.003)).setScale(4, RoundingMode.HALF_EVEN)
        if (day.toString() == StatementFlaws.MISSING_FX_DAY) null
        else """  {"date":"$day","base":"USD","rates":{"EUR":"${eur.toPlainString()}","MXN":"${mxn.toPlainString()}"}}"""
    }
    return lines.joinToString(",\n", prefix = "[\n", postfix = "\n]\n")
}
