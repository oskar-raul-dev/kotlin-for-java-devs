# Ejercicios: POO: clases, herencia, polimorfismo y enums

> **Lección:** `oop` · **Dónde se resuelven:** `oop/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../oop/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew test --tests '*.oop.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo.

**Prioritarios (🎯):** 01, 02, 08, 09.

## 🟢 Fáciles

**01 🟢 🎯 `data class` de pedidos.** Escribir `OrderEvent` y `Address` como en `json/Model.kt` (sin
serialización) y probar `equals`, `copy` y destructuring. ¿Qué pasa con `equals` si agregas una `var` en el cuerpo?
- Casos: dos eventos iguales, `copy(orderTotal = …)`, `val (ts, email) = event`.

**02 🟢 🎯 Enum con `of`.** `enum class ShippingMethod(val code: String)` con `STANDARD`, `EXPRESS`, `PICKUP`, `FREIGHT` y un
`companion object { fun of(code: String?): ShippingMethod? }` que tolere mayúsculas y espacios.
- Casos: `"Express"`, `" pickup "`, `"drone"` → `null`, `null` → `null`. Compara con `valueOf` (que lanza).

**03 🟢 Propiedad con validación.** `class Temperature(celsius: Double)` con `var celsius` cuyo setter rechaza menos de −273.15
y una propiedad calculada `fahrenheit` de lectura y escritura.

**04 🟢 Setter privado.** `class Counter` con `var count` de solo lectura hacia fuera y `increment()`/`reset()`.
Prueba que `counter.count = 5` no compila (déjalo comentado en la prueba).

**05 🟢 `object` como registro.** `object Registry` que guarda plugins por nombre, rechaza duplicados y permite `clear()` para las pruebas.

**06 🟢 Constructor secundario.** `class Range2(val from: Int, val to: Int)` con `constructor(size: Int)` que delega en `this(0, size - 1)`
e `init` que valida `from <= to`.

**07 🟢 `toString` útil.** Una clase normal (no `data`) con `toString`, `equals` y `hashCode` escritos a mano; prueba el contrato
(`equals` ⇒ mismo `hashCode`) usando la clase en un `HashSet`.

## 🟡 Medios

**08 🟡 🎯 `sealed` para resultados.** `sealed interface Decision` con `Approve`, `Reject(reason: String)`,
`Review(score: Double)`; una función que la traduce a texto con `when` **sin `else`**. Agrega un caso nuevo y comprueba
que el compilador te obliga a cubrirlo.

**09 🟡 🎯 Reglas polimórficas.** `interface PricingRule { val name: String; fun applies(o: Order): Boolean }` con tres
implementaciones (pedido grande, cliente frecuente, envío a otro estado) y un `RuleEngine(rules: List<PricingRule>)`
que devuelve qué reglas aplicaron a cada pedido.
- Casos: ningún disparo, uno, todos; agregar una regla nueva sin tocar el motor (abierto/cerrado).

**10 🟡 Clase abstracta plantilla.** `abstract class Report` con `fun render() = header() + body() + footer()`, donde
solo `body()` es abstracto y `header()` es `open`. Dos subclases.

**11 🟡 `value class` para identificadores.** `CustomerId` y `OrderId` como `@JvmInline value class` para que el compilador
impida pasar uno por otro. Valida el formato en `init`.

**12 🟡 Operadores de `Money`.** `data class Money(val cents: Long, val currency: String)` con `plus`, `minus`, `times(Int)`,
`compareTo` y `unaryMinus`; sumar monedas distintas lanza.

**13 🟡 Delegación con `by`.** `class AuditedMap<K, V>(inner: MutableMap<K, V>) : MutableMap<K, V> by inner` que registra
cada `put` y `remove`. ¿Qué pasa con `putAll`? Pruébalo y explica.

**14 🟡 Propiedades delegadas.** Usar `Delegates.observable` para registrar cambios y `Delegates.vetoable` para rechazar
valores negativos en una clase `Settings`.

**15 🟡 Enum con comportamiento.** `enum class Operation { PLUS, MINUS, TIMES, DIV; abstract fun apply(a: Int, b: Int): Int }`
con cuerpo por constante, y `fun parse(symbol: Char): Operation`.

## 🟠 Difíciles

**16 🟠 Máquina de estados.** `enum class OrderState` (`CART → PLACED → PAID | CANCELLED → SHIPPED`) con un mapa de
transiciones válidas y `fun next(action: OrderAction): OrderState` que lanza `IllegalStateException` en transiciones inválidas.
Prueba **todas** las combinaciones.

**17 🟠 Genéricos con varianza.** `interface Source<out T> { fun next(): T }` y `interface Sink<in T> { fun put(t: T) }`;
demuestra con código que compila (y comentarios con el que no) por qué `Source<String>` sirve como `Source<Any>`.

**18 🟠 Visitor contra `sealed`.** Un árbol de expresiones (`Num`, `Add`, `Mul`, `Neg`) evaluado e impreso de dos formas:
patrón visitor clásico y `when` sobre `sealed`. ¿Cuál es más fácil de extender con un nodo nuevo? ¿Y con una operación nueva?

**19 🟠 Builder idiomático.** Un `HttpRequest` inmutable construido con un DSL: `request { url = "…"; header("A", "1"); body { … } }`
usando lambdas con receptor y `@DslMarker`.

**20 🟠 Equals con herencia.** `open class Point(x, y)` y `class ColorPoint(x, y, color)`: muestra con pruebas cómo un `equals`
ingenuo rompe la simetría y arréglalo (con `javaClass` o haciendo la jerarquía `sealed`).

## 🔴 Muy difíciles

**21 🔴 Inyección sin framework.** Un mini contenedor: `container.register<Repo> { InMemoryRepo() }`, `container.get<Service>()`
resolviendo dependencias por constructor con `reified` y reflexión de Kotlin (`kotlin-reflect` no está: usa la reflexión de Java).
Detecta ciclos.

**22 🔴 Sistema de eventos tipado.** `class EventBus` con `subscribe<T : Event>(handler: (T) -> Unit)` y `publish(event)` que
entrega solo a los suscriptores del tipo (y supertipos) correctos, con orden de entrega y desuscripción.

**23 🔴 Banco con invariantes.** `Account`, `Ledger` y `TransferService` donde **nunca** se puede crear dinero: cada transferencia
genera dos asientos, el ledger siempre suma cero y una propiedad prueba (1000 transferencias aleatorias con semilla) lo verifica.

**24 🔴 Rediseño de un script.** Escribe primero un `main` monolítico que lea `/data/orders.json`, valide, agrupe y genere un reporte, y luego propón (en `oop/practice`) un diseño con `OrderReport`, `OrderSource`,
`OrderValidator` y `ReportSink` como interfaces, con implementaciones en memoria y pruebas. Objetivo: que cada pieza se pruebe sola.
