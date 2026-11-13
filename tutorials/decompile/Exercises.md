# Ejercicios: decompilar Kotlin

> **Tutorial:** [`decompile`](README.md) · **Dónde se resuelven:** `tutorials/decompile/practice/` (fuentes `.kt`,
> un `NOTAS.md` con los comandos, fragmentos de `javap`/CFR y las respuestas; el ⚡ con su propio `check.sh`) ·
> **Verificar:** cada ejercicio dice qué tiene que aparecer (o no) en el bytecode.
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil · ⚡ Saiyajin (miniproyecto que cierra el tutorial) ·
> 🎤 pregunta típica de entrevista · **🎯 = prioritario** (ruta mínima).
>
> Compila siempre con `-jvm-target 21` salvo que el ejercicio pida otro: es lo que genera un proyecto Gradle del curso.

**Prioritarios (🎯):** 01, 03, 05, 08, 11.

## 🟢 Fáciles

**01 🟢 🎯 Show Kotlin Bytecode.** Abre `examples/Classes.kt` en IntelliJ, usa *Show Kotlin Bytecode* y luego
*Decompile*. Compara el Java que muestra con el de CFR para `Position`.
- Casos en `NOTAS.md`: dos diferencias entre los dos decompiladores.

**02 🟢 `val` frente a `var` frente a `const`.** Escribe un `object Limits` con `val maxOrder = 1000`, `var dailyCap =
5000` y `const val minOrder = 1`, y lista con `javap -p` qué genera cada uno.
- Casos: qué campo es `public`, cuál tiene getter y setter, y cómo se ve cada uno desde Java.

**03 🟢 🎯 🎤 ¿Por qué `HelloKt`?** Explica con `javap` dónde van las funciones de nivel superior, qué cambia
`@file:JvmName` y qué pasa si dos archivos del mismo paquete usan el mismo `@JvmName` (y cómo lo arregla
`@file:JvmMultifileClass`; mira el `k` de su `@Metadata`).

**04 🟢 Chequeos de null.** Escribe `fun transfer(from: String, to: String?, amount: java.math.BigDecimal)` y cuenta
los `checkNotNullParameter` en el bytecode.
- Casos: dos (no `to`). Luego márcala `private` y vuelve a contar: explica la diferencia.

## 🟡 Medios

**05 🟡 🎯 Lambdas: inline o no.** Escribe tres funciones que filtren ticks por precio: con `filter { }`, guardando la
lambda en un `val` antes de usarla, y con una `fun interface PricePredicate` y un objeto anónimo.
- Casos: cuál tiene `invokedynamic`, cuál ninguno y cuál una clase propia (`…$1.class`). Anota en `NOTAS.md`.

**06 🟡 La máscara de `$default`.** Una función con cuatro parámetros por defecto. Llámala desde Kotlin omitiendo el
2.º y el 4.º, y busca en el bytecode del llamador la máscara que pasa.
- Casos: el número exacto (en binario y decimal) y cómo lo usa `$default` según CFR.

**07 🟡 `data class` desde Java.** Desde un `Main.java`, usa `Account`: créala, cópiala cambiando el saldo y
desestructúrala.
- Casos: qué método usas para cada cosa; por qué `copy` con un solo argumento no existe desde Java (y cómo lo resolverías).

**08 🟡 🎯 `when` y el jvmTarget.** Un `when` sobre `Order` con tres ramas (agrega `StopOrder`). Compílalo con
`-jvm-target` 1.8, 17 y 21.
- Casos: `instanceof` en 1.8 y 17, `typeSwitch` en 21 (comprueba si 17 ya lo usa). Anota desde qué target aparece
  `PermittedSubclasses`.

**09 🟡 `@JvmStatic` y compañía.** Para un `companion object` con una función, compara el bytecode sin anotación, con
`@JvmStatic` y con `@JvmField` en una propiedad.
- Casos: la llamada desde Java en cada caso; qué desaparece con `@JvmField`.

**10 🟡 La trampa del `lateinit`.** Desde Java, lee directamente el campo `lastTrade` de una `Position` nueva y luego
llama a `getLastTrade()`.
- Casos: el primero devuelve `null` sin error, el segundo lanza `UninitializedPropertyAccessException`. Explica por qué
  el campo es público.

## 🟠 Difíciles

**11 🟠 🎯 `suspend` por dentro.** Escribe una función `suspend` con dos puntos de suspensión (dos llamadas a otra
`suspend`) y una variable local que sobrevive entre ellos. Decompílala con CFR.
- Casos en `NOTAS.md`: la clase de continuación generada, el campo `label`, dónde se guarda la variable local y el
  `COROUTINE_SUSPENDED`. Es la base de `02-coroutines/internals`.

**12 🟠 `value class` en la frontera.** Usa `Cents` en: una firma, un `List<Cents>`, un parámetro nullable `Cents?` y
una interfaz genérica `Box<T>`.
- Casos: en cuáles se encajona (`box-impl`) y en cuáles no; el nombre mutilado y cómo se llama desde Java con
  `@JvmName`.

**13 🟠 El costo de `data class`.** Mide con `javap -c` cuántas instrucciones tiene `equals` de una `data class` de
cinco propiedades, y escribe a mano un `equals` equivalente en Java.
- Casos: la comparación de `BigDecimal` con `Intrinsics.areEqual` (y por qué `equals` y no `compareTo`: la trampa de
  escala de [`docs/Casos.md`](../../docs/Casos.md#money)).

**14 🟠 Metadatos.** Extiende `examples/metadata.main.kts` para mostrar los parámetros por defecto, la visibilidad y si
una función es `inline` o `operator`.
- Casos: sobre `Functions` y `Account`, con su salida en `NOTAS.md`.

## 🔴 Muy difíciles

**15 🔴 Leer un jar sin fuentes.** Toma `kotlinx-serialization-json-jvm-1.13.0.jar` de `~/.m2` o de la caché de Gradle
y, solo con `javap`, CFR y `metadata.main.kts`, explica cómo está hecha `Json.Default`: qué clase es, qué tipo de
`object` y qué funciones de nivel superior expone.

## ⚡ Saiyajin: catálogo "qué genera kotlinc"

**16 ⚡ El catálogo.** En `practice/catalog/`, un catálogo de **20 construcciones** de Kotlin, cada una con: el
fuente mínimo, el fragmento de `javap -c -p` que la explica, el Java que da CFR y una línea de "lo que cuesta". Todo
**verificado con pruebas**: un `practice/catalog/check.sh` al estilo del de este tutorial, que compile los fuentes y
compruebe con `javap` lo que el catálogo afirma.
- Construcciones (las del tutorial cuentan si las amplías): `data class`, `object`, `companion` + `@JvmStatic`,
  `const`, `lateinit`, `by lazy`, delegación de clase (`by`), propiedad delegada propia, `sealed` + `when`, `enum` con
  `entries`, `value class`, extensión, parámetros por defecto + `@JvmOverloads`, lambda inline frente a la que escapa,
  `inline` + `reified`, `crossinline`/`noinline`, `suspend`, `vararg` y el operador *spread* (`*array`), plantillas de
  cadena, rangos en `for` (`for (i in 0..<n)` frente a `for (x in list)`), `@JvmRecord`.
- Casos: el `check.sh` pasa con `-jvm-target 21` y marca qué cambia con 1.8; cada entrada indica en qué versión de
  Kotlin apareció la construcción (`[1.0]`, `[1.5]`, `[1.9]`…).
- Opcional: correr el mismo catálogo con el `kotlinc` 1.9.22 del track legacy y anotar qué entradas cambian (lambdas,
  `when`, nombres mutilados).
