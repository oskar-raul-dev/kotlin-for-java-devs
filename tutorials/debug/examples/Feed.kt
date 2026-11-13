// Corrutinas del caso Mercado para ver con -Dkotlinx.coroutines.debug y con DebugProbes (como -javaagent).
// Necesita kotlinx-coroutines-core y kotlinx-coroutines-debug en el classpath: deps.main.kts los baja a ~/.m2.
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.debug.DebugProbes

@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking {
    val feed = launch(CoroutineName("feed-ACME")) {
        println("feed corre en: ${Thread.currentThread().name}")
        delay(10_000)   // queda suspendida aquí cuando se hace el volcado
    }
    val book = launch(CoroutineName("libro-ACME")) {
        delay(10_000)
    }
    delay(200)
    if (System.getProperty("dump") != null) DebugProbes.dumpCoroutines()   // requiere -javaagent:…debug.jar
    feed.cancel()
    book.cancel()
}
