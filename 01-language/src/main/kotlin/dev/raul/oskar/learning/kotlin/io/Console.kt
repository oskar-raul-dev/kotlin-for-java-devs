package dev.raul.oskar.learning.kotlin.io

import java.io.BufferedReader

// Consola: println/print escriben en System.out; readln()/readlnOrNull() leen de System.in.
// Para que sea testeable, la lógica recibe un BufferedReader y un Appendable
// (en Main se les pasa System.in y System.out).

/** Lee números hasta "fin" o EOF y devuelve la suma. Ignora líneas no numéricas avisando. */
fun sumNumbers(input: BufferedReader, out: Appendable): Int {
    var total = 0
    while (true) {
        out.append("número (o 'fin')> ")
        val line = input.readLine()?.trim() ?: break         // null = EOF (Ctrl+D)
        if (line.equals("fin", ignoreCase = true)) break
        val n = line.toIntOrNull()
        if (n == null) out.append("  '$line' no es un número\n") else total += n
    }
    out.append("\ntotal = $total\n")
    return total
}

/** Una tabla alineada con format (String.format de Java). La descripción se recorta para no romper columnas. */
fun formatTable(lines: List<StatementLine>): String = buildString {
    appendLine("%-10s %-32s %10s %-9s".format("fecha", "descripción", "monto", "ref"))
    lines.forEach {
        appendLine("%-10s %-32s %10s %-9s".format(it.date, it.description.take(32), it.amount.toPlainString(), it.reference))
    }
}
