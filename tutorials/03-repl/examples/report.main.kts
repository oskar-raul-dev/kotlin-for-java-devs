// Usa las funciones de money.main.kts. La ruta del @file:Import es relativa a este script.
@file:Import("money.main.kts")

val balancesInCents = mapOf("ACC-00001234-82" to 123456L, "ACC-00005678-39" to 999L)
balancesInCents.forEach { (account, cents) -> println("$account  ${usd(cents)}") }
