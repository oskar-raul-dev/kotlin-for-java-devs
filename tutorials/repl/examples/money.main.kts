// Funciones compartidas entre scripts: otro script las trae con @file:Import("money.main.kts").
fun usd(cents: Long): String = "USD %d.%02d".format(cents / 100, cents % 100)
