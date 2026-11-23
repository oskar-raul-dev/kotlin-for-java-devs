# Ejercicios: POO: clases, herencia, polimorfismo y enums

> **Lección:** `oop` · **Dónde se resuelven:** `oop/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../oop/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 01-language test --tests '*.oop.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección · `[1.9]` = versión de Kotlin en que es estable.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo. Los números 25 a 40 se agregaron
> después y van dentro de su nivel; el ⚡ es el 41.

**Prioritarios (🎯):** 01, 02, 08, 09. **Entrevista (🎤):** 01, 11, 13, 20, 25, 28.

## 🟢 Fáciles

**01 🟢 🎯 🎤 `data class` de ticks.** Escribir `Tick` como en `json/Model.kt` (sin serialización) y probar `equals`,
`copy` y destructuring. ¿Qué pasa con `equals` si agregas una `var` en el cuerpo?
- Casos: dos ticks iguales, `copy(price = …)`, `val (seq, ticker) = tick`. Explica en un comentario qué cambia frente
  a un `record` de Java y frente a `@Data` de Lombok.

**02 🟢 🎯 Enum con `of`.** `enum class OrderType(val code: String)` con `LIMIT`, `MARKET`, `STOP`, `STOP_LIMIT` y un
`companion object { fun of(code: String?): OrderType? }` que tolere mayúsculas y espacios.
- Casos: `"limit"`, `" Market "`, `"STOP_LIMIT"`, `"iceberg"` → `null`, `null` → `null`. Compara con `valueOf` (que lanza).

**03 🟢 Propiedad con validación.** `class Temperature(celsius: Double)` con `var celsius` cuyo setter rechaza menos de −273.15
y una propiedad calculada `fahrenheit` de lectura y escritura.

**04 🟢 Setter privado.** `class Counter` con `var count` de solo lectura hacia fuera y `increment()`/`reset()`.
Prueba que `counter.count = 5` no compila (déjalo comentado en la prueba).

**05 🟢 `object` como registro.** `object Registry` que guarda plugins por nombre, rechaza duplicados y permite `clear()` para las pruebas.

**06 🟢 Constructor secundario.** `class Range2(val from: Int, val to: Int)` con `constructor(size: Int)` que delega en `this(0, size - 1)`
e `init` que valida `from <= to`.

**07 🟢 `toString` útil.** Una clase normal (no `data`) con `toString`, `equals` y `hashCode` escritos a mano; prueba el contrato
(`equals` ⇒ mismo `hashCode`) usando la clase en un `HashSet`.

**25 🟢 🎤 `final` por defecto.** `open class FeeCalculator` con `open fun fee(notional: Money): Money` (0.1 %) y
`class VipFeeCalculator : FeeCalculator()` que cobra la mitad; una tercera clase que herede de `VipFeeCalculator` y no
pueda cambiar `fee` (`final override`).
- Casos: las tres comisiones sobre `1000.00`; deja comentado en la prueba lo que no compila (heredar de una clase sin
  `open`, sobrescribir un `final override`). Explica en un comentario qué rompe `final` en Spring (proxies CGLIB) y en
  Hibernate (lazy loading), y qué hacen los plugins `allopen`, `kotlin-spring` y `jpa`.

**26 🟢 `[1.9]` `entries` en vez de `values()`.** Los códigos de `AccountType` de la lección, en orden, con `entries`.
- Firma: `fun accountTypeCodes(): List<String>`
- Casos: la lista completa; `AccountType.values() !== AccountType.values()` (cada llamada copia el arreglo) y
  `AccountType.entries === AccountType.entries`; buscar por código con `entries.firstOrNull { … }`.

**27 🟢 `[1.9]` `data object`.** `sealed interface BookCommand` con `data class Place(val orderId: String, val qty: Long)`,
`data object Snapshot` y un `object Clear` (sin `data`) para comparar.
- Casos: `Snapshot.toString() == "Snapshot"`, `Clear.toString()` empieza con el nombre de la clase y trae `@`; `equals`
  de los dos; un `when` exhaustivo sobre los tres.

**28 🟢 🎤 ¿`sealed` o `enum`?** El estado de una transferencia, escrito dos veces: como `enum class TransferStatus
{ PENDING, SETTLED, FAILED }` y como `sealed interface TransferState` donde `Failed` lleva el motivo y `Settled` la fecha.
- Firmas: `fun describe(status: TransferStatus): String`, `fun describe(state: TransferState): String`
- Casos: los tres estados de cada uno. Explica en un comentario cuándo eliges cada uno (¿hay datos por caso? ¿hace
  falta `entries` o `valueOf`? ¿se serializa como texto?).

**29 🟢 Visibilidad.** `open class Vault` con un miembro `protected`, otro `internal` y una función `private` de nivel
superior en el mismo archivo; una subclase en otro archivo del mismo paquete.
- Casos: desde la subclase se ve lo `protected`; desde una prueba del mismo módulo se ve lo `internal`. Deja comentado
  lo que no compila y explica en un comentario por qué en Kotlin no hay package-private y qué significa "módulo".

## 🟡 Medios

**08 🟡 🎯 `sealed` para resultados.** `sealed interface OrderResult` con `Accepted`, `Rejected(reason: String)`,
`PartiallyFilled(filled: Long, remaining: Long)`; una función que lo traduce a texto con `when` **sin `else`**. Agrega un caso nuevo y comprueba
que el compilador te obliga a cubrirlo.

**09 🟡 🎯 Reglas polimórficas.** `interface CommissionRule { val name: String; fun applies(o: Order): Boolean }` con tres
implementaciones (orden grande, cuenta con mucho volumen en el mes, orden a mercado) y un
`RuleEngine(rules: List<CommissionRule>)` que devuelve qué reglas aplicaron a cada orden.
- Casos: ningún disparo, uno, todos; agregar una regla nueva sin tocar el motor (abierto/cerrado).

**10 🟡 Clase abstracta plantilla.** `abstract class Report` con `fun render() = header() + body() + footer()`, donde
solo `body()` es abstracto y `header()` es `open`. Dos subclases.

**11 🟡 🎤 `[1.5]` `value class` para identificadores.** `AccountId` y `OrderId` como `@JvmInline value class` (como el `TransferId` de la lección) para que el compilador
impida pasar uno por otro. Valida el formato en `init`. Explica en un comentario por qué `Money` no puede ser una
`value class`.

**12 🟡 Operadores de `Money`.** `data class Money(val cents: Long, val currency: String)` con `plus`, `minus`, `times(Int)`,
`compareTo` y `unaryMinus`; sumar monedas distintas lanza.

**13 🟡 🎤 Delegación con `by`.** `class AuditedMap<K, V>(inner: MutableMap<K, V>) : MutableMap<K, V> by inner` que registra
cada `put` y `remove`. ¿Qué pasa con `putAll`? Pruébalo y explica.

**14 🟡 Propiedades delegadas.** Usar `Delegates.observable` para registrar cambios y `Delegates.vetoable` para rechazar
valores negativos en una clase `Settings`.

**15 🟡 Enum con comportamiento.** `enum class Operation { PLUS, MINUS, TIMES, DIV; abstract fun apply(a: Int, b: Int): Int }`
con cuerpo por constante, y `fun parse(symbol: Char): Operation`.

**30 🟡 `[2.0]` `copy` y el constructor privado.** `data class AccountNumber private constructor(val value: String)` con
`companion object { fun of(raw: String): AccountNumber }` que valida el formato `ACC-…` de `strings`.
- Casos: `of` acepta uno válido y rechaza uno inválido; demuestra que `copy(value = "basura")` se salta la validación
  (y el aviso del compilador); agrega `@ConsistentCopyVisibility` y deja comentada la llamada a `copy` que ya no compila.

**31 🟡 `[1.5]` `@JvmRecord`.** `@JvmRecord data class TickRecord(val seq: Long, val ticker: String, val price: BigDecimal)`
y una comprobación desde Java reflection.
- Casos: `TickRecord::class.java.isRecord`, los nombres de `recordComponents` en orden, el accessor `seq()` existe por
  reflexión. Explica en un comentario qué exige (`jvmTarget` 16 o más, sin `var`, sin herencia) y cuándo vale la pena.

**32 🟡 Dos interfaces, el mismo método.** `interface Fee { fun label(): String = "fee" }` e
`interface Tax { fun label(): String = "tax" }`; una clase que implementa las dos tiene que sobrescribir `label`.
- Firma: `class WithholdingFee : Fee, Tax` con `label()` que devuelve `"fee+tax"` usando `super<Fee>` y `super<Tax>`
- Casos: el resultado; una propiedad con getter por defecto en la interfaz (`val rate: BigDecimal get() = …`);
  explica en un comentario por qué una interfaz no puede tener estado (campos) y qué genera el compilador para el
  método por defecto (mira `-Xjvm-default`).

**33 🟡 Posición con costo promedio.** Aplicar ejecuciones a la `Position` de la lección: las compras recalculan el
costo promedio ponderado y las ventas devuelven la ganancia realizada.
- Firma: `fun Position.apply(side: Side, quantity: Long, price: Money): Pair<Position, Money>` (posición nueva y
  ganancia realizada)
- Casos: comprar 10 a `100.00` y 10 a `110.00` → 20 a `105.00`; vender 5 a `120.00` → ganancia `75.00` y el costo
  promedio no cambia; vender todo → cantidad 0; vender más de lo que hay → `IllegalArgumentException`.

**34 🟡 Fábrica con caché.** `class CurrencyPair private constructor(val base: Currency, val quote: Currency)` con
`companion object { fun of(text: String): CurrencyPair }` que interna las instancias en un mapa.
- Casos: `CurrencyPair.of("eur/usd") === CurrencyPair.of("EUR/USD")`, `toString() == "EUR/USD"`, `inverse()` →
  `USD/EUR` (también interna), la misma moneda en los dos lados o un código inexistente → `IllegalArgumentException`.

**35 🟡 `lateinit` y `lazy`.** `class StatementReport` con `lateinit var source: List<String>` (se asigna después de
construir) y `val totals: Map<String, Long> by lazy { … }`.
- Casos: leer `source` antes de asignarla → `UninitializedPropertyAccessException`; `::source.isInitialized`;
  `totals` se calcula una sola vez (un contador lo prueba) aunque se lea varias veces. Explica en un comentario por qué
  `lateinit` no sirve con `Int` ni con tipos nullables.

## 🟠 Difíciles

**16 🟠 Máquina de estados.** `enum class OrderState` (`NEW → PARTIALLY_FILLED → FILLED`, y `CANCELLED` o `REJECTED` desde `NEW`) con un mapa de
transiciones válidas y `fun next(action: OrderAction): OrderState` que lanza `IllegalStateException` en transiciones inválidas.
Prueba **todas** las combinaciones.

**17 🟠 Genéricos con varianza.** `interface Source<out T> { fun next(): T }` y `interface Sink<in T> { fun put(t: T) }`;
demuestra con código que compila (y comentarios con el que no) por qué `Source<String>` sirve como `Source<Any>`.

**18 🟠 Visitor contra `sealed`.** Un árbol de expresiones (`Num`, `Add`, `Mul`, `Neg`) evaluado e impreso de dos formas:
patrón visitor clásico y `when` sobre `sealed`. ¿Cuál es más fácil de extender con un nodo nuevo? ¿Y con una operación nueva?

**19 🟠 Builder idiomático.** Un `HttpRequest` inmutable construido con un DSL: `request { url = "…"; header("A", "1"); body { … } }`
usando lambdas con receptor y `@DslMarker`.

**20 🟠 🎤 Equals con herencia.** `open class Point(x, y)` y `class ColorPoint(x, y, color)`: muestra con pruebas cómo un `equals`
ingenuo rompe la simetría y arréglalo (con `javaClass` o haciendo la jerarquía `sealed`).

**36 🟠 Cuentas selladas.** Rehacer las cuentas de la lección como `sealed class` con reglas de retiro por tipo:
corriente con sobregiro, ahorro con un máximo de 6 retiros por mes, custodia bloqueada salvo con una orden de
liberación; el retiro devuelve un resultado sellado en vez de lanzar.
- Firma: `fun withdraw(amount: Money): WithdrawResult` con `sealed interface WithdrawResult` (`Ok(newBalance)`,
  `InsufficientFunds(missing)`, `LimitReached`, `Blocked`)
- Casos: una tabla con cada tipo de cuenta por cada resultado posible; el séptimo retiro de ahorro; un monto en otra
  moneda → `IllegalArgumentException` (eso sí es un error de programación, no un resultado).

**37 🟠 Entidad frente a valor.** Una `data class` con una `var` dentro de un `HashSet` se pierde si cambias la `var`.
- Firmas: `data class AccountRow(val id: String, var balance: Long)` y `class AccountEntity(val id: String, var balance: Long)`
  con `equals`/`hashCode` solo por `id`
- Casos: meter un `AccountRow` en un `HashSet`, cambiar `balance` y comprobar que `contains` da `false` (y que sigue
  ahí al recorrer); lo mismo con `AccountEntity` da `true`. Explica en un comentario por qué las entidades de JPA no
  son `data class`.

**38 🟠 Precios con operadores.** `data class Price(val ticks: Long)` con tick de `0.01`: `plus`, `minus`,
`compareTo`, `inc`/`dec` (un tick), `rangeTo` que devuelve un `PriceRange` iterable y `contains` para usar `in`.
- Casos: `Price.of("100.00")..Price.of("100.05")` tiene 6 precios, `Price.of("100.02") in rango`, `p++` sube un tick,
  `toString() == "100.02"`, un precio con más de dos decimales → `IllegalArgumentException`, un rango al revés está vacío.

**39 🟠 Eventos del ledger anidados.** `sealed interface LedgerEvent` con dos subjerarquías selladas,
`AccountEvent` (`Opened`, `Closed`) y `MoneyEvent` (`Deposited`, `Withdrawn`, `Transferred`), y un `replay` que
reconstruye los saldos.
- Firma: `fun replay(events: List<LedgerEvent>): Map<String, Money>`
- Casos: una secuencia válida; un `when` que trata `MoneyEvent` como grupo y otro que baja a cada caso; depositar en
  una cuenta cerrada o que no existe → `IllegalStateException` con el índice del evento; cerrar una cuenta con saldo →
  `IllegalStateException`.

**40 🟠 Decoradores con `by`.** `interface AccountRepository { fun find(id: String): Money?; fun save(id: String, balance: Money); fun total(): Money }`
y dos decoradores con `by`: uno que cuenta llamadas y otro que guarda en caché `find`.
- Casos: apilar los dos en los dos órdenes y comprobar cuántas llamadas llegan al repositorio de abajo; demostrar que
  si la implementación de `total()` llama a su propio `find`, esa llamada no pasa por el decorador (el problema de la
  auto-llamada) y explicarlo en un comentario; `toString` no se delega.

## 🔴 Muy difíciles

**21 🔴 Inyección sin framework.** Un mini contenedor: `container.register<Repo> { InMemoryRepo() }`, `container.get<Service>()`
resolviendo dependencias por constructor con `reified` y reflexión de Kotlin (`kotlin-reflect` no está: usa la reflexión de Java).
Detecta ciclos.

**22 🔴 Sistema de eventos tipado.** `class EventBus` con `subscribe<T : Event>(handler: (T) -> Unit)` y `publish(event)` que
entrega solo a los suscriptores del tipo (y supertipos) correctos, con orden de entrega y desuscripción.

**23 🔴 Banco con invariantes.** `Account`, `Ledger` y `TransferService` donde **nunca** se puede crear dinero: cada transferencia
genera dos asientos, el ledger siempre suma cero y una propiedad prueba (1000 transferencias aleatorias con semilla) lo verifica.

**24 🔴 Rediseño de un script.** Escribe primero un `main` monolítico que lea `/data/ticks.json`, valide, agrupe y genere un reporte, y luego propón (en `oop/practice`) un diseño con `TickReport`, `TickSource`,
`TickValidator` y `ReportSink` como interfaces, con implementaciones en memoria y pruebas. Objetivo: que cada pieza se pruebe sola.

## ⚡ Saiyajin

**41 ⚡ Evaluador de expresiones con `sealed`.** Fórmulas de comisión como `max(1.00, 0.001 * notional)` sobre un AST
sellado, evaluadas con `BigDecimal`.
- Firma: `sealed interface Expr` con `Num(val value: BigDecimal)`, `Var(val name: String)`, `Neg(val e: Expr)`,
  `Bin(val op: BinOp, val left: Expr, val right: Expr)` y `Call(val fn: String, val args: List<Expr>)`;
  `fun parse(text: String): Expr`, `fun Expr.eval(env: Map<String, BigDecimal>): BigDecimal`,
  `fun Expr.simplify(): Expr` y `fun Expr.render(): String`. Funciones: `max`, `min`, `round(x, n)` (`HALF_EVEN`).
- Errores: variable sin valor, función desconocida o con aridad equivocada, división por cero y texto mal formado,
  cada uno con su excepción y la posición en el texto cuando aplica.
- **Se verifica con:** pruebas de evaluación (precedencia, menos unario, las tres funciones, fórmulas del caso con
  `notional` y `quantity`); de simplificación (`x * 1 → x`, `0 + x → x`, `x * 0 → 0`, constantes plegadas,
  `-(-x) → x`); de ida y vuelta texto → AST → texto (con los paréntesis mínimos) sobre al menos 20 fórmulas; y una
  prueba que documenta, con el código comentado, que agregar un nodo sin su rama en un `when` no compila.
