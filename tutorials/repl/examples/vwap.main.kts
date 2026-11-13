#!/usr/bin/env kotlin
// Script .main.kts: puede bajar dependencias de Maven con @file:DependsOn (un .kts común no).
// VWAP del caso Mercado (docs/Casos.md): suma(precio × cantidad) / suma(cantidad).
//   kotlin vwap.main.kts        o, con el shebang y permiso de ejecución:   ./vwap.main.kts
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-serialization-json:1.13.0")

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

val raw = """[{"ticker": "ACME", "price": 100.0, "qty": 10}, {"ticker": "ACME", "price": 102.0, "qty": 30}]"""
val ticks = Json.parseToJsonElement(raw).jsonArray.map { it.jsonObject }
val notional = ticks.sumOf { it.getValue("price").jsonPrimitive.double * it.getValue("qty").jsonPrimitive.int }
val volume = ticks.sumOf { it.getValue("qty").jsonPrimitive.int }
println("VWAP ACME = ${notional / volume}")
