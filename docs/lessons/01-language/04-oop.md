# 04 · `oop` — Clases, herencia, `sealed`, enums, objetos

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.oop` · **Caso:** Ambos
**Enlaces:** [temario](../../Temario.md#01--oop--clases-herencia-sealed-enums-objetos) · [ejercicios](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/oop/Exercises.md) · [código](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/oop/) · [pruebas](../../../01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/oop/OopTest.kt)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Modelar dominio con clases, `data class`, jerarquías selladas, enums y objetos, y entender qué cambia frente a Java en
herencia, propiedades, inicialización, igualdad y visibilidad, y por qué Kotlin lo decidió así.

## Teoría

### 1. Clases cerradas por defecto: herencia como decisión

En Java toda clase se puede heredar salvo que se marque `final`; en Kotlin es al revés: clases y métodos son `final`
salvo `open` (o `abstract`). Es la recomendación de *Effective Java* hecha regla del lenguaje ("diseña para la herencia
o prohíbela", ítem 19): heredar de una clase que no se pensó para eso rompe con cualquier cambio interno (el problema
de la clase base frágil). En el curso, `Account` es `abstract`, `withdraw` es `open` y solo `overdraftLimit` se deja
sobrescribir (`protected open`). El costo es conocido: los frameworks que crean subclases en runtime (proxies de
Spring, entidades *lazy* de Hibernate) necesitan los plugins `allopen`/`kotlin-spring`/`kotlin-jpa` (`06 ·
compilerplugins`). → [Inheritance][inheritance]

### 2. Propiedades en vez de campos y getters

`class Account(val id: String, …)` declara a la vez el constructor, el campo privado y el getter: lo que en Java son
diez líneas (o un `record`, o Lombok). Una propiedad tiene campo de respaldo solo si lo necesita; puede tener setter
privado (`var balance … private set`), ser calculada (`val isEmpty get() = …`, sin campo) o validarse en un setter con
`field`. Desde Java se ve igual que siempre (`getBalance()`), así que la API sigue siendo compatible. →
[Properties][properties], [Classes][classes]

### 3. Constructores e inicialización (y su trampa)

Hay un constructor primario en la cabecera; los secundarios delegan en él con `this(…)` (`EscrowAccount`). La lógica
del constructor va en bloques `init`, que corren **en el orden en que están escritos**, intercalados con los
inicializadores de las propiedades. Al heredar, se inicializa primero la clase base: si el constructor de la base llama
a un método `open` que la subclase sobrescribe y usa una propiedad propia, esa propiedad todavía no tiene valor: es
`null` aunque su tipo diga `String`. Medido con 2.4.21: el compilador **no** avisa (lo marca una inspección de
IntelliJ) y en runtime salta un `NullPointerException`. La regla es la misma que en Java: no llamar miembros `open`
desde constructores ni bloques `init`. → [Derived class initialization order][init-order]

### 4. `data class` frente a `record`

Una `data class` genera `equals`, `hashCode`, `toString`, `copy` y `componentN` a partir de las propiedades **del
constructor primario**; las del cuerpo quedan fuera (por eso `Position.lastPrice` no cambia la igualdad). El `record`
de Java 16 es parecido, pero es `final`, sus campos son siempre inmutables y no tiene `copy`. En Kotlin, `val` y `var`
deciden la mutabilidad (una `data class` con `var` usada como clave de un `HashMap` "desaparece" al cambiar), y
`@JvmRecord` genera un `record` real cuando hace falta interoperar. No sirve para entidades JPA (`04 · jpa`). →
[Data classes][data-classes], [JEP 395: Records][jep395]

### 5. Jerarquías cerradas: `sealed` y `enum`

Un `sealed interface Order` dice "estas son todas las subclases": el compilador las conoce y un `when` sobre `Order`
es exhaustivo sin `else` (`priceHint`); agregar `StopOrder` hace fallar la compilación en cada `when` que no lo cubra.
Java 17 tiene lo mismo con `sealed … permits` (JEP 409); Kotlin no pide `permits` porque las subclases tienen que estar
en el mismo módulo y paquete. ¿`enum` o `sealed`? Un `enum` es un conjunto fijo de **instancias** (`Side.BUY`); un
`sealed` es un conjunto fijo de **tipos**, cada uno con sus propios datos (`LimitOrder` lleva límite, `MarketOrder` no).
Los enums se recorren con `entries` (desde 1.9; `values()` copia un arreglo en cada llamada) y la fábrica va en el
`companion` (`AccountType.of`). → [Sealed classes][sealed], [Enum classes][enum], [JEP 409][jep409]

### 6. `object`, `companion` y la ausencia de `static`

Kotlin no tiene `static`. Un `object` es un singleton creado al primer uso, seguro entre hilos (`IdGenerator`); un
`companion object` es el objeto único asociado a una clase, donde van las fábricas y constantes (`Ticker.parse`). La
diferencia con `static` es que el `companion` **es un objeto**: puede implementar interfaces y pasarse como
argumento. Desde Java se llama como `Ticker.Companion.parse(…)` salvo que se marque `@JvmStatic` (`01 · interop`). Un
`data object` (1.9) agrega un `toString` legible, útil en jerarquías selladas. → [Object declarations][objects]

### 7. `value class`, delegación y visibilidad

- `@JvmInline value class TransferId(val value: String)` es un tipo distinto en compilación y un `String` en runtime
  (sin objeto extra, salvo como nullable o genérico): el compilador impide pasar una clave de idempotencia donde va un
  `TransferId`. → [Inline value classes][value-classes]
- `class CountingList<T>(…) : MutableList<T> by inner` implementa la interfaz delegando en `inner`: el patrón
  Decorator sin escribir veinte métodos; solo se sobrescribe lo que cambia (`delegation`). → [Delegation][delegation]
- La visibilidad por defecto es `public`; `internal` es "visible en el mismo módulo de Gradle" y reemplaza en la
  práctica a la visibilidad de paquete de Java, que no existe; `protected` **no** incluye el paquete.
  → [Visibility modifiers][visibility]

## Cómo correrla

```bash
./gradlew -p 01-language run -Plesson=oop
./gradlew -p 01-language test --tests '*.oop.*'
```

## Recorrido sugerido

1. `Accounts.kt`: cuentas del Banco con `Money` (de `basics`): constructor primario, `init` con `require`,
   `private set` (`balance`, `status`), propiedad calculada, `abstract`/`open`, `protected` (`overdraftLimit`),
   constructor secundario (`EscrowAccount`) y polimorfismo (`transfer`).
2. `Enums.kt`: `AccountType` con propiedades, cuerpo por constante (`closesAtMonthEnd`) y fábrica en `companion`
   (`of`, `ofOrThrow`); `AccountStatus` con propiedades calculadas; `Side` con `when` exhaustivo.
3. `Orders.kt`: interfaz con método por defecto (`Described`), `sealed interface Order` (`LimitOrder`,
   `MarketOrder`) y `when` exhaustivo (`priceHint`).
4. `Objects.kt`: `object` (`IdGenerator`), `data class` con propiedad en el cuerpo (`Position`), constructor privado
   con fábrica (`Ticker`), `value class` con `init` (`TransferId`), operadores (`Vector2`), delegación `by`,
   `lazy` y genéricos con cota.
5. `OopTest.kt` y `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `Accounts.kt` | Herencia y visibilidad | `Account`, `SavingsAccount`, `CheckingAccount`, `EscrowAccount`, `transfer` | `OopTest` (`init block validates…`, `overridden overdraft…`, `polymorphic transfer and account status`) |
| `Enums.kt` | Enums con `companion` y cuerpo por constante | `AccountType`, `AccountStatus`, `Side`, `label` | `OopTest` (`enum factory in companion and bodies per constant`) |
| `Orders.kt` | `sealed` + `when` exhaustivo | `Described`, `Order`, `LimitOrder`, `MarketOrder`, `priceHint`, `committed` | `OopTest` (`sealed hierarchy and default methods`) |
| `Objects.kt` | `object`, `data`, `value`, operadores, `by` | `IdGenerator`, `Position`, `Ticker`, `TransferId`, `Vector2`, `CountingList`, `Config`, `maxOf3` | `OopTest` (`data class equality…`, `object singleton…`, `operators and value classes`, `delegation with by and lazy`, `generic with upper bound`) |

## Para profundizar

**Documentación oficial**

- [Classes][classes], [Inheritance][inheritance] y [Properties][properties]: constructores, `init`, `open`, campos de
  respaldo.
- [Derived class initialization order][init-order]: la trampa de llamar miembros `open` desde el constructor.
- [Data classes][data-classes], [Sealed classes and interfaces][sealed] y [Enum classes][enum].
- [Object declarations and expressions][objects]: `object`, `companion`, `data object`.
- [Inline value classes][value-classes], [Delegation][delegation] y [Visibility modifiers][visibility].
- [JEP 395: Records][jep395] y [JEP 409: Sealed Classes][jep409]: lo equivalente en Java, para comparar.

**Lecturas**

- *Effective Java*, 3.ª edición (J. Bloch), ítems 17 ("Minimize mutability") y 19 ("Design and document for
  inheritance or else prohibit it"): el razonamiento detrás de `val` y de las clases `final` por defecto.
- [Complete technical guide: sealed classes, sealed interfaces & enums in Kotlin][sealed-guide] (ProAndroidDev):
  cuándo usar cada uno.
- [Usa clases y objetos en Kotlin][android-classes] (Android Developers) *(es)*: clases, constructores, herencia e
  interfaces en un codelab.
- [Capacitación de Kotlin para programadores 4: programación orientada a objetos][bootcamp-oop] (Google) *(es)*.
- [6 tipos de clases en Kotlin][keepcoding] (KeepCoding) *(es)*: `data`, `sealed`, `enum`, `object` y anidadas, en
  resumen.

**Videos**

- [Sealed Classes Opened My Mind][sealed-talk] (KotlinConf 2018): modelar estado con jerarquías selladas en una app
  real.
- [Data objects in Kotlin: pretty-print your objects!][data-object-video]: `data object` y su `toString`.
- [Kotlin Value Classes (vs Data Class and Type Alias)][value-video] (no oficial): cuándo usar cada uno.
- [Exploring the Kotlin Type Hierarchy from Top to Bottom][pryce] (Nat Pryce, KotlinConf 2018): subtipos, `Any` y
  `Nothing` (complementa `basics`).

## Estado

- **Hecho:** código en los casos (B0′ tanda 3) y 10 pruebas; 24 ejercicios (4 🎯); 0 resueltos en `practice/`.
- **Falta:** B2 (⚡ evaluador de expresiones con
  `sealed`).

[classes]: https://kotlinlang.org/docs/classes.html
[inheritance]: https://kotlinlang.org/docs/inheritance.html
[init-order]: https://kotlinlang.org/docs/inheritance.html#derived-class-initialization-order
[properties]: https://kotlinlang.org/docs/properties.html
[data-classes]: https://kotlinlang.org/docs/data-classes.html
[sealed]: https://kotlinlang.org/docs/sealed-classes.html
[enum]: https://kotlinlang.org/docs/enum-classes.html
[objects]: https://kotlinlang.org/docs/object-declarations.html
[value-classes]: https://kotlinlang.org/docs/inline-classes.html
[delegation]: https://kotlinlang.org/docs/delegation.html
[visibility]: https://kotlinlang.org/docs/visibility-modifiers.html
[jep395]: https://openjdk.org/jeps/395
[jep409]: https://openjdk.org/jeps/409
[sealed-guide]: https://proandroiddev.com/complete-technical-guide-sealed-classes-sealed-interfaces-enums-in-kotlin-28ffc39116df
[android-classes]: https://developer.android.com/codelabs/basic-android-kotlin-compose-classes-and-objects?hl=es
[bootcamp-oop]: https://developers.google.com/codelabs/kotlin-bootcamp-classes?hl=es-419
[keepcoding]: https://keepcoding.io/blog/6-tipos-de-clases-en-kotlin/
[sealed-talk]: https://www.youtube.com/watch?v=uGMm3StjqLI
[data-object-video]: https://www.youtube.com/watch?v=ovAqcwFhEGc
[value-video]: https://www.youtube.com/watch?v=KbNM7WK-Rl8
[pryce]: https://www.youtube.com/watch?v=juFkdMv4B9s
