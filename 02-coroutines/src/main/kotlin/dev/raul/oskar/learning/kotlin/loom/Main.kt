package dev.raul.oskar.learning.kotlin.loom

import java.util.concurrent.atomic.AtomicInteger

fun main() {
    val clients = 10_000
    val latency = 50L
    println("== Difundir un precio a $clients clientes (cada entrega espera $latency ms) ==")
    val models = listOf(PlatformThreadModel(), VirtualThreadModel(), CoroutineModel(), CoroutinesOnVirtualThreadsModel())
    models.forEach { it.broadcast(1_000, 1, AtomicInteger()) }        // calentamiento (JIT)
    for (model in models) {
        val delivered = AtomicInteger()
        val time = model.broadcast(clients, latency, delivered)
        println("%-34s %6d ms  (%d entregas)".format(model.name, time.inWholeMilliseconds, delivered.get()))
    }
    println("Una sola corrida y sin repeticiones: sirve para ver el orden de magnitud, no para comparar fino (⚡).")

    println("\n== Pinning en el JDK $jdkFeatureVersion: 64 tareas que duermen 50 ms dentro de un lock ==")
    val cores = Runtime.getRuntime().availableProcessors()
    println("synchronized:  ${blockingInsideLocks(64, 50, useSynchronized = true).inWholeMilliseconds} ms (portadores: $cores)")
    println("ReentrantLock: ${blockingInsideLocks(64, 50, useSynchronized = false).inWholeMilliseconds} ms")
}
