# Tutorial: decompilar Kotlin

Qué genera `kotlinc` de verdad, visto con las herramientas que ya conoces de Java (`javap`) y con un decompilador
(CFR). Sirve para entender el costo de cada construcción, para depurar la interoperabilidad con Java y para leer
`@Metadata`, la anotación donde Kotlin guarda todo lo que el bytecode no sabe expresar.

> **Versión:** Kotlin **2.4.21**, CFR 0.152, `kotlin-metadata-jvm` 2.4.21. Cada afirmación de este tutorial está
> en [`check.sh`](check.sh) (47 comprobaciones). **Ejercicios:** [`Exercises.md`](Exercises.md), con su ⚡: un
> catálogo propio de 20 construcciones verificado con pruebas.

**Salto rápido:** [Herramientas](#herramientas) · [Clases](#1-clases) · [Funciones](#2-funciones) ·
[Tipos](#3-tipos) · [El jvmTarget cambia el bytecode](#4-el-jvmtarget-cambia-el-bytecode) · [CFR](#5-cfr) ·
[@Metadata](#6-metadata) · [Resumen](#resumen-qué-genera-kotlinc) · [Trampas](#trampas)

## Herramientas

| Herramienta | Qué muestra | Cómo |
|---|---|---|
| **Show Kotlin Bytecode** (IntelliJ) | El bytecode del archivo abierto, en vivo; con **Decompile**, su equivalente en Java | *Tools → Kotlin → Show Kotlin Bytecode* |
| `javap` (JDK) | Firmas (`-p` incluye lo privado), bytecode (`-c`), todo (`-v`: constantes, atributos, `@Metadata`) | `javap -c -p Clase.class` |
| **CFR** | Java legible a partir del `.class`, incluidas las anotaciones | `java -jar cfr-0.152.jar Clase.class` |
| `kotlin-metadata-jvm` | `@Metadata` decodificado: `data`, `value`, `suspend`, nombres Kotlin | [`examples/metadata.main.kts`](examples/metadata.main.kts) |

Los ejemplos están en [`examples/`](examples/) y usan los casos Banco y Mercado:

```bash
sdk env                                              # Kotlin 2.4.21 (en la raíz del repo)
cd tutorials/decompile/examples
kotlinc *.kt -jvm-target 21 -d out                   # target 21: lo que genera un proyecto Gradle con jvmToolchain(21)
cd out/dev/raul/oskar/learning/kotlin/tutorials/decompile
```

## 1. Clases

[`Classes.kt`](examples/Classes.kt): `data class Account`, una `class Position` con propiedades de varios tipos, un
`companion object` y un `object Market`.

```text
$ javap -p Position.class
public final class …Position {
  public static final …Position$Companion Companion;
  private final java.lang.String ticker;
  private int quantity;
  public java.lang.String lastTrade;                          ← lateinit: ¡campo public!
  public static final int MAX_QUANTITY;                       ← const val del companion
  private static final java.lang.String DEFAULT_TICKER;       ← val del companion, en la clase de afuera
  public …Position(java.lang.String, int);
  public final java.lang.String getTicker();
  public final int getQuantity();                             ← private set: no hay setQuantity
  public final boolean isEmpty();                             ← propiedad calculada: getter sin campo
  public final java.lang.String getLastTrade();
  public final void setLastTrade(java.lang.String);
  public final void buy(int);
  public static final …Position empty(java.lang.String);      ← @JvmStatic
  public static final java.lang.String access$getDEFAULT_TICKER$cp();
  static {};
}
```

| Kotlin | Bytecode | Para Java |
|---|---|---|
| `class` | `public final class` | Todas las clases son `final` salvo `open` |
| `val x` / `var x` | Campo privado + `getX()` (y `setX()`) | Se usa como getter y setter |
| `val isEmpty get() = …` | Solo `isEmpty()`: los `is…` booleanos no llevan `get` | |
| `var q; private set` | `getQuantity()` público, setter privado (sin `setQuantity` visible) | |
| `lateinit var` | **Campo `public`** y un getter que lanza `UninitializedPropertyAccessException` | Java puede leer el campo directo y saltarse el chequeo |
| `companion object` | Clase `Position$Companion` + campo estático `Companion` | `Position.Companion.getDEFAULT_TICKER()` |
| `const val` en el companion | `public static final` en la clase de afuera | `Position.MAX_QUANTITY`, sin getter |
| `val` en el companion | Campo **privado** estático en la clase de afuera, getter en `Companion` y un accesor sintético `access$…$cp` | |
| `@JvmStatic fun` | Además del método en `Companion`, un estático en `Position` | `Position.empty("ACME")` |
| `object` | Campo `INSTANCE` y constructor privado | `Market.INSTANCE.open()` |
| `data class` | `componentN`, `copy`, `copy$default`, `equals`/`hashCode`/`toString`, y un constructor sintético con `DefaultConstructorMarker` para los valores por defecto | |

## 2. Funciones

[`Functions.kt`](examples/Functions.kt), con `@file:JvmName("Functions")`: la fachada se llama `Functions` y no
`FunctionsKt`.

```text
$ javap -p Functions.class
  public static final java.math.BigDecimal fee(java.math.BigDecimal, java.math.BigDecimal);
  public static java.math.BigDecimal fee$default(java.math.BigDecimal, java.math.BigDecimal, int, java.lang.Object);
  public static final java.math.BigDecimal feeForJava(java.math.BigDecimal, java.math.BigDecimal);
  public static final java.math.BigDecimal feeForJava(java.math.BigDecimal);     ← @JvmOverloads
  public static final boolean isTicker(java.lang.String);                         ← extensión de String
  …
```

- **Parámetros por defecto:** `fee$default` recibe una máscara de bits que dice cuáles se omitieron (lo muestra CFR
  en la sección 5). Desde Java solo existe `fee` con los dos parámetros, salvo que agregues `@JvmOverloads`.
- **Extensión:** `fun String.isTicker()` es un estático cuyo primer parámetro es el receptor. No modifica `String`.
- **Parámetros no nullable:** al entrar, `Intrinsics.checkNotNullParameter`. Si Java pasa `null`, la excepción sale
  en la primera línea, con el nombre del parámetro.

**Lambdas:** depende de a quién se las pasas.

```text
$ javap -c -p Functions.class    (fragmentos)
pricesAbove:   (filter es inline: la lambda se copió en el método; no hay invokedynamic ni objeto lambda)
above:         1: invokedynamic #1:invoke:(D)Lkotlin/jvm/functions/Function1;    ← la lambda escapa
total:         invokestatic java/lang/System.nanoTime  …  (measure es inline: no hay llamada a measure)
countStrings:  instanceof #… // class java/lang/String                         ← reified
```

| Construcción | Qué queda en el bytecode |
|---|---|
| Lambda pasada a una función `inline` (`filter`, `map`, `let`…) | Nada: el cuerpo se copia en el llamador |
| Lambda que se guarda o se devuelve | `invokedynamic` (`LambdaMetafactory`), como en Java. En K1 era una clase propia por lambda ([`k2-notes`](../../22-legacy-kotlin19/docs/k2-notes.md)) |
| `inline fun` | No se llama: su cuerpo aparece en cada llamador |
| `reified T` | `instanceof` con la clase real en cada llamador: por eso solo existe en funciones `inline` |

## 3. Tipos

[`Types.kt`](examples/Types.kt): una `sealed interface Order`, una `value class Cents` y una función `suspend`.

```text
$ javap -p TypesKt.class
  public static final java.lang.String kind(…Order);
  public static final long addCents-XyPO2wg(long, long);
  public static final java.lang.Object fetchPrice(java.lang.String, kotlin.coroutines.Continuation<? super java.lang.Double>);
```

- **`value class Cents(val value: Long)`:** en la firma desaparece y queda el `long`. El nombre se **mutila**
  (`addCents-XyPO2wg`) para que no choque con un `addCents(long, long)` normal, y por eso Java no puede llamarla con un
  nombre razonable. La clase `Cents` existe para cuando hay que encajonar (`box-impl`, `unbox-impl`).
- **`suspend`:** un parámetro extra `Continuation` y retorno `Object`, porque puede devolver el resultado o la marca
  `COROUTINE_SUSPENDED`. Es el *continuation-passing style*; la máquina de estados se ve en `02-coroutines`.
- **`when` sobre una `sealed`** (target 21):

```text
       8: invokedynamic #0:typeSwitch:(Ljava/lang/Object;I)I
      13: tableswitch   { 0: …  1: …  default: … }
      56: new           kotlin/NoWhenBranchMatchedException
```

Es el mismo mecanismo que el `switch` con patrones de Java 21 (`SwitchBootstraps.typeSwitch`). El `default` lanza
`NoWhenBranchMatchedException`: el `when` es exhaustivo para el compilador, pero el bytecode se protege de una
subclase que aparezca después (por ejemplo, compilada aparte).

## 4. El jvmTarget cambia el bytecode

Los mismos fuentes compilados con el target por defecto (1.8) y con 21:

| Construcción | `-jvm-target 1.8` (defecto de `kotlinc`) | `-jvm-target 21` (`jvmToolchain(21)` en Gradle) |
|---|---|---|
| Plantilla `"Cuenta ${a.id}: …"` | `new StringBuilder().append(…)` | `invokedynamic makeConcatWithConstants` |
| `when (o) { is LimitOrder -> … }` sobre `sealed` | `instanceof` uno tras otro | `invokedynamic typeSwitch` + `tableswitch` |
| `sealed interface Order` | Interfaz común | Atributo `PermittedSubclasses` (las `sealed` de Java 17) |
| Lambda que escapa | `invokedynamic` | `invokedynamic` |
| Versión de clase | major 52 | major 65 |

Por eso conviene que el target coincida con el JDK donde corre el código: con un target viejo se pierde lo que la JVM
optimiza mejor.

## 5. CFR

```bash
curl -sfLO https://repo1.maven.org/maven2/org/benf/cfr/0.152/cfr-0.152.jar
java -jar cfr-0.152.jar Market.class
```

```java
@Metadata(mv={2, 4, 0}, k=1, xi=48, d1={"\u0000\u0010\n…"}, d2={"L…/Market;", "", "<init>", "()V", "open", ""})
public final class Market {
    @NotNull
    public static final Market INSTANCE = new Market();
    private Market() {
    }
    public final boolean open() {
        return true;
    }
}
```

Fragmentos de las otras clases:

```java
public final String getLastTrade() {                      // lateinit
    String string = this.lastTrade;
    if (string != null) {
        return string;
    }
    Intrinsics.throwUninitializedPropertyAccessException((String)"lastTrade");
    return null;
}

public static /* synthetic */ BigDecimal fee$default(BigDecimal bigDecimal, BigDecimal bigDecimal2, int n, Object object) {
    if ((n & 2) != 0) {                                   // bit 2 = el segundo parámetro se omitió
        bigDecimal2 = new BigDecimal("0.005");
    }
    return Functions.fee(bigDecimal, bigDecimal2);
}

public static final String kind(@NotNull Order order) {  // when sobre sealed, target 21
    Intrinsics.checkNotNullParameter((Object)order, (String)"order");
    return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{LimitOrder.class, MarketOrder.class}, (Object)order, 0)) {
        case 0 -> "límite a " + ((LimitOrder)order).getPrice();
        case 1 -> "a mercado";
        default -> throw new NoWhenBranchMatchedException();
    };
}
```

- `@NotNull` (de `org.jetbrains.annotations`): `kotlinc` anota la nulabilidad para que Java y los IDE la vean.
- El "Decompile" de *Show Kotlin Bytecode* en IntelliJ usa otro decompilador (Fernflower) y da un Java parecido.
- Un decompilador **reconstruye** Java a partir del bytecode: lo que muestra no es lo que escribió nadie, y a veces
  no compila (como `addCents-XyPO2wg`, que no es un nombre válido en Java).

## 6. @Metadata

Toda clase compilada por `kotlinc` lleva `@kotlin.Metadata`: ahí está lo que la JVM no sabe expresar (nulabilidad,
`data`, `value`, `suspend`, propiedades, parámetros por defecto, nombres Kotlin). La usan `kotlin-reflect`, el
compilador cuando consumes una librería y los plugins como kotlinx-serialization.

| Campo | Qué es |
|---|---|
| `mv` | Versión del formato de metadatos: `{2, 4, 0}` con `kotlinc` 2.4.21 |
| `k` | Tipo: `1` clase, `2` fachada de archivo (`TypesKt`), `3` clase sintética (las lambdas en K1), `4` fachada de una clase multiarchivo (`@file:JvmMultifileClass`), `5` una de sus partes |
| `d1` | Los datos, en protobuf codificado como texto |
| `d2` | La tabla de cadenas que usa `d1`: nombres y firmas |
| `xi` | Banderas extra |

[`examples/metadata.main.kts`](examples/metadata.main.kts) los decodifica con `kotlin-metadata-jvm`:

```text
$ kotlin metadata.main.kts out dev.raul.oskar.learning.kotlin.tutorials.decompile.Account …Cents …TypesKt
== …Account  (k=1, mv=2.4.0)
clase: …/Account  kind=CLASS  data=true  value=false
  val id: Class(name=kotlin/String)
  var balance: Class(name=java/math/BigDecimal)
  fun component1  ->  JVM component1()Ljava/lang/String;
  …
== …Cents  (k=1, mv=2.4.0)
clase: …/Cents  kind=CLASS  data=false  value=true
== …TypesKt  (k=2, mv=2.4.0)
  fun kind  ->  JVM kind(L…/Order;)Ljava/lang/String;
  fun addCents  ->  JVM addCents-XyPO2wg(JJ)J
  suspend fun fetchPrice  ->  JVM fetchPrice(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
```

`javap` no puede decirte que `Account` es una `data class` ni que `addCents-XyPO2wg` se llama `addCents`; el
metadato sí.

## Resumen: qué genera kotlinc

| Construcción | Lo que paga en el bytecode |
|---|---|
| `data class` | ~10 métodos generados y un constructor sintético por los valores por defecto |
| Parámetro por defecto | Un método `$default` con máscara de bits |
| Parámetro no nullable | Un `checkNotNullParameter` por parámetro, en cada método público |
| `lateinit` | Un `if (campo == null) throw` en cada lectura; campo público |
| `companion` | Una clase más y un salto por `Companion` (salvo `const` y `@JvmStatic`) |
| Lambda a función `inline` | Nada |
| Lambda que escapa | Un `invokedynamic` (y un objeto por cada vez que se crea, si captura variables) |
| `value class` | Nada en la firma, salvo cuando hay que encajonar; nombres mutilados |
| `suspend` | Un parámetro `Continuation`, retorno `Object` y una máquina de estados si suspende |

## Trampas

- **`kotlinc` a mano compila para Java 8**: lo que veas sin `-jvm-target 21` no es lo que genera tu proyecto Gradle.
- **El campo de un `lateinit` es público**: Java puede leerlo `null` sin pasar por el chequeo.
- **`filter { }` no crea ninguna lambda** (es `inline`), pero guardar la lambda en una variable sí.
- **Las funciones con `value class` en la firma no se pueden llamar cómodamente desde Java** (nombre mutilado).
- **Lo que muestra un decompilador es una reconstrucción**, no el código fuente: para saber qué hay, `javap -c`.
