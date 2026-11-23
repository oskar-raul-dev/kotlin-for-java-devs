package dev.raul.oskar.learning.kotlin.channels

import dev.raul.oskar.learning.kotlin.cases.Tick
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Un `Channel` es el `BlockingQueue` de las corrutinas: `send` y `receive` **suspenden** en vez de bloquear. Con la
 * capacidad por defecto (*rendezvous*, como `SynchronousQueue`), `send` espera a que alguien reciba. Devuelve el log de
 * lo que pasó, en orden.
 */
suspend fun rendezvousLog(): List<String> = coroutineScope {
    val log = mutableListOf<String>()
    val channel = Channel<Int>()                       // Channel.RENDEZVOUS
    launch {
        for (i in 1..2) {
            log += "send $i…"
            channel.send(i)                            // suspende hasta que el consumidor recibe
            log += "send $i listo"
        }
        channel.close()
    }
    delay(100)                                         // el consumidor llega tarde: el productor espera
    for (value in channel) log += "recibí $value"      // el for termina cuando el canal se cierra y se vacía
    log
}

/**
 * Con capacidad [capacity], el productor puede adelantarse hasta llenar el buffer (como `ArrayBlockingQueue(n)`):
 * devuelve cuántos `send` terminaron antes de que el consumidor empezara a recibir.
 */
suspend fun sendsBeforeConsumer(capacity: Int, total: Int = 10): Int = coroutineScope {
    val channel = Channel<Int>(capacity)
    var completed = 0
    launch {
        repeat(total) { channel.send(it); completed++ }
        channel.close()
    }
    delay(100)                                         // el productor corre solo durante 100 ms
    val before = completed
    for (ignored in channel) { /* vacía el canal */ }
    before
}

/**
 * `trySend` no suspende: sobre un canal lleno devuelve un resultado fallido, que hay que mirar (no lanza). Devuelve
 * cuáles de los [total] envíos entraron.
 */
fun trySendIntoFull(capacity: Int, total: Int): List<Boolean> {
    val channel = Channel<Int>(capacity)
    return List(total) { channel.trySend(it).isSuccess }
}

/**
 * `produce` lanza una corrutina productora y devuelve su canal; el canal se cierra solo cuando el bloque termina (y si
 * falla, el consumidor recibe la excepción). Pide `@ExperimentalCoroutinesApi` en 1.11.0 (medido).
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun CoroutineScope.produceTicks(ticks: List<Tick>): ReceiveChannel<Tick> = produce {
    for (tick in ticks) send(tick)
}

/**
 * Varios consumidores de un mismo canal se **reparten** los elementos (*fan-out*): cada tick lo recibe uno solo. Para
 * que todos reciban todo hace falta un `SharedFlow` (`hotflows`). Devuelve qué recibió cada trabajador.
 */
suspend fun fanOut(ticks: List<Tick>, workers: Int): Map<Int, List<Tick>> = coroutineScope {
    val channel = produceTicks(ticks)
    val received = (0 until workers).associateWith { mutableListOf<Tick>() }
    (0 until workers).map { worker ->
        launch {
            for (tick in channel) {
                received.getValue(worker) += tick
                delay(1)                               // trabajo simulado: deja que los demás tomen el siguiente
            }
        }
    }.forEach { it.join() }
    received
}

/** *Fan-in*: varios productores escriben en el mismo canal; el consumidor recibe todo, intercalado. */
suspend fun fanIn(feeds: Map<String, List<Int>>): List<String> = coroutineScope {
    val channel = Channel<String>()
    val producers = feeds.map { (name, values) ->
        launch {
            for (v in values) {
                delay(v.toLong())
                channel.send("$name:$v")
            }
        }
    }
    launch { producers.forEach { it.join() }; channel.close() }
    buildList { for (message in channel) add(message) }
}
