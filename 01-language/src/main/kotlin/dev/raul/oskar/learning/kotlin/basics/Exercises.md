# Ejercicios: tipos básicos

> **Lección:** `basics` · **Dónde se resuelven:** `basics/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../basics/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 01-language test --tests '*.basics.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo.

**Prioritarios (🎯):** 02, 05, 09, 14, 16.

## 🟢 Fáciles

**01 🟢 Grados.** Convertir Celsius a Fahrenheit y al revés, redondeando a un decimal.
- Firma: `fun cToF(c: Double): Double`, `fun fToC(f: Double): Double`
- Casos: `cToF(100.0) == 212.0`, `fToC(-40.0) == -40.0`, `cToF(36.6) == 97.9`.

**02 🟢 🎯 Parseo seguro.** De `String?` a `Int?` sin excepciones: `null`, vacío, espacios y basura dan `null`.
- Firma: `fun safeInt(raw: String?): Int?`
- Casos: `" 42 "` → 42, `""` → null, `null` → null, `"4x"` → null, `"-7"` → −7.
- Pista: `?.trim()?.toIntOrNull()`.

**03 🟢 Suma que no desborda.** Sumar una `List<Int>` que puede pasar de `Int.MAX_VALUE`.
- Firma: `fun total(xs: List<Int>): Long`
- Casos: `listOf(Int.MAX_VALUE, 1)` → 2147483648. Escribe también la versión ingenua y una prueba que demuestre el desborde.

**04 🟢 Par e impar con negativos.** `isOdd(-3)` debe ser `true`. En Kotlin, como en Java, `-3 % 2 == -1`.
- Firma: `fun isOdd(n: Int): Boolean`
- Casos: 3, −3, 0, `Int.MIN_VALUE`. Compara `%` con `mod` (`(-3).mod(2) == 1`).

**05 🟢 🎯 Dividir la cuenta.** Repartir un monto `BigDecimal` entre `n` personas sin perder centavos: los
centavos que sobran van a los primeros.
- Firma: `fun split(total: BigDecimal, n: Int): List<BigDecimal>`
- Casos: `split(100.00, 3) == [33.34, 33.33, 33.33]`, la suma siempre es igual al total, `n = 0` → `IllegalArgumentException`.

**06 🟢 César.** Desplazar solo las letras `a..z` y `A..Z` `k` posiciones, de forma circular.
- Firma: `fun caesar(text: String, k: Int): String`
- Casos: `caesar("Zz!", 1) == "Aa!"`, `k` negativo, `k = 27`.

**07 🟢 Qué es esto.** Ampliar el `describe` de la lección para `List`, `Map`, `IntArray` y `Array<*>`, con el tamaño.
- Firma: `fun describe(value: Any?): String`
- Casos: `listOf(1, 2)` → `"List(2)"`, `intArrayOf()` → `"IntArray(0)"`.

**08 🟢 Booleano estricto.** `"true"` o `"false"` (sin importar mayúsculas) dan el booleano; cualquier otra cosa, `null`.
- Firma: `fun strictBool(raw: String): Boolean?`
- Casos: `"TRUE"`, `"false"`, `"yes"`, `""`. Busca la función de la stdlib y luego escríbela tú.

## 🟡 Medios

**09 🟡 🎯 Cadena de nulos.** Con `data class Address(val city: String?, val state: String?, val zip: String?)`,
armar `"Miami, FL 33101"` con lo que haya, **sin `!!`**.
- Firma: `fun format(address: Address?): String`
- Casos: todo presente; sin zip → `"Miami, FL"`; solo estado → `"FL"`; `null` → `"(sin dirección)"`.

**10 🟡 Doubles casi iguales.** Comparar con tolerancia relativa y absoluta.
- Firma: `fun approxEquals(a: Double, b: Double, rel: Double = 1e-9, abs: Double = 1e-12): Boolean`
- Casos: `0.1 + 0.2` contra `0.3`, `1e20` contra `1e20 + 1`, `0.0` contra `-0.0`, `NaN` contra `NaN` (false).

**11 🟡 Caché de enteros.** Demostrar con pruebas que `val a: Int? = 127; val b: Int? = 127; a === b` es `true`
y con 128 es `false`. Explica en un comentario por qué (boxing a `Integer` y su caché −128..127) y por qué `==` no tiene el problema.

**12 🟡 Bits.** Contar bits encendidos, saber si es potencia de dos y obtener la siguiente potencia de dos.
- Firmas: `fun bitCount(n: Int): Int`, `fun isPowerOfTwo(n: Int): Boolean`, `fun nextPowerOfTwo(n: Int): Int`
- Casos: 0, 1, 64, 65, `Int.MAX_VALUE`. Usa `and`, `shl`, `ushr`; compara con `Integer.bitCount`.

**13 🟡 Color hexadecimal.** `"#FF8800"` → `Triple(255, 136, 0)`, y de vuelta.
- Firmas: `fun parseColor(hex: String): Triple<Int, Int, Int>?`, `fun toHex(r: Int, g: Int, b: Int): String`
- Casos: con y sin `#`, minúsculas, longitud inválida → `null`, componente fuera de 0..255 → excepción.

**14 🟡 🎯 Modos de redondeo.** Tabla de `HALF_UP`, `HALF_EVEN`, `HALF_DOWN`, `DOWN`, `CEILING` para
`2.345`, `2.355`, `-2.345` a dos decimales.
- Firma: `fun roundAll(x: BigDecimal): Map<RoundingMode, BigDecimal>`
- Casos: la prueba fija la tabla completa. Responde en un comentario: ¿cuál usa la banca y por qué?

**15 🟡 Milisegundos.** Convertir segundos (`Long`) a milisegundos detectando el desborde.
- Firma: `fun toMillis(seconds: Long): Long`
- Casos: 0, 1, `Long.MAX_VALUE / 1000`, `Long.MAX_VALUE / 1000 + 1` → `ArithmeticException` (`Math.multiplyExact`).

**16 🟡 🎯 `Nothing` útil.** Escribir `fun fail(msg: String): Nothing` y usarlo en un Elvis y en una rama de `when`
para que el compilador sepa que el resto del código ya no ve `null`.
- Casos: una función `fun port(env: Map<String, String>): Int` que use ambos. Prueba los caminos de error.

**17 🟡 El valor absoluto que miente.** `Math.abs(Int.MIN_VALUE)` es negativo.
- Firma: `fun safeAbs(n: Int): Long` y `fun strictAbs(n: Int): Int` (lanza en `MIN_VALUE`).
- Casos: −5, 0, `Int.MIN_VALUE` en ambas.

## 🟠 Difíciles

**18 🟠 Fracciones.** `class Fraction` siempre normalizada (MCD, signo en el numerador), con `+`, `-`, `*`, `/`,
`compareTo`, `equals`/`hashCode` coherentes y `toString` `"3/4"`.
- Casos: `1/2 + 1/3 == 5/6`, `2/4 == 1/2`, `1/-2` → `-1/2`, denominador 0 → excepción, usar `Fraction` en un `Set`.

**19 🟠 Números romanos.** Ida y vuelta, 1..3999, validando entradas inválidas (`"IIII"`, `"VX"`).
- Firmas: `fun toRoman(n: Int): String`, `fun fromRoman(s: String): Int?`
- Casos: 1994 ↔ `"MCMXCIV"`; para toda `n` en 1..3999, `fromRoman(toRoman(n)) == n`.

**20 🟠 Suma saturada.** Sumar dos `Int` sin pasar de los límites (se queda en `MAX`/`MIN`) **sin usar `Long`**.
- Firma: `fun addSat(a: Int, b: Int): Int`
- Casos: `MAX + 1`, `MIN + -1`, `MAX + MIN`, signos mezclados.

**21 🟠 Números con formato local.** `"1.234,56"` (es-CO) y `"1,234.56"` (en-US) a `BigDecimal`, con `DecimalFormat`
y `setParseBigDecimal(true)`.
- Firma: `fun parseAmount(text: String, locale: Locale): BigDecimal?`
- Casos: los dos formatos, texto con basura al final → `null` (usa `ParsePosition`).

**22 🟠 Bases.** Convertir a cualquier base 2..36 y de vuelta **sin** `toString(radix)`; luego compara con la stdlib.
- Firmas: `fun toBase(n: Long, radix: Int): String`, `fun fromBase(s: String, radix: Int): Long`
- Casos: 0, negativos, 255 en base 16 y 2, `Long.MAX_VALUE` en base 36.

## 🔴 Muy difíciles

**23 🔴 Grandes números.** Factorial de 100 con `BigInteger`, la suma de sus dígitos, y una exponenciación
modular rápida propia (cuadrado y multiplicación).
- Firmas: `fun factorial(n: Int): BigInteger`, `fun modPow(base: Long, exp: Long, mod: Long): Long`
- Casos: suma de dígitos de 100! = 648; `modPow` contra `BigInteger.modPow` para 1000 valores aleatorios con semilla fija.

**24 🔴 Dinero con reparto.** `Money(amount: Long /* centavos */, currency: Currency)` con suma que exige misma
moneda y `allocate(ratios: List<Int>)` al estilo Fowler: repartir 100 en `[3, 7]` → `[30, 70]`, 5 centavos en `[1, 1]` → `[3, 2]`.
- Casos: la suma del reparto siempre es el original; monedas distintas → excepción; ratios vacíos o en cero.

**25 🔴 Suma de Kahan.** Implementar la suma compensada y mostrar con una prueba la diferencia contra la suma
ingenua al sumar `0.1` diez millones de veces.
- Firma: `fun kahanSum(xs: DoubleArray): Double`
- Casos: la ingenua se aleja de `1_000_000.0` más que Kahan; compara también con `BigDecimal` como referencia.
