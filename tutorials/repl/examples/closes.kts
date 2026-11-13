// Script simple (.kts): sin main, se ejecuta de arriba abajo. `args` trae los argumentos.
//   kotlinc -script closes.kts 100 101.5 99.75      o      kotlin closes.kts 100 101.5 99.75
val closes = args.map { it.toDouble() }.ifEmpty { listOf(100.0, 101.5, 99.75) }
println("cierres = $closes")
println("promedio = ${closes.average()}")
