package dev.raul.oskar.learning.kotlin.loom

import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.Duration
import kotlin.time.measureTime

/**
 * *Pinning* en el JDK 21: un hilo virtual que bloquea **dentro de `synchronized`** queda fijado a su hilo portador y no
 * lo suelta mientras espera, así que el paralelismo cae al número de portadores (tantos como núcleos). Con
 * `ReentrantLock`, el hilo virtual se desmonta y el portador sigue con otros. El JDK 24 lo corrigió (JEP 491); el curso
 * usa el 21. Cada tarea toma **su propio** lock: no hay competencia, solo el efecto del pinning.
 */
fun blockingInsideLocks(tasks: Int, sleepMillis: Long, useSynchronized: Boolean): Duration {
    val monitors = List(tasks) { Any() }
    val locks = List(tasks) { ReentrantLock() }
    return measureTime {
        Executors.newVirtualThreadPerTaskExecutor().use { executor ->
            repeat(tasks) { i ->
                executor.execute {
                    if (useSynchronized) {
                        synchronized(monitors[i]) { Thread.sleep(sleepMillis) }
                    } else {
                        locks[i].withLock { Thread.sleep(sleepMillis) }
                    }
                }
            }
        }
    }
}

/** La versión del JDK donde corre la prueba (el pinning de `synchronized` se arregló en la 24). */
val jdkFeatureVersion: Int get() = Runtime.version().feature()
