# 09 · `datetime` — `java.time` correcto

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.datetime` · **Caso:** Ambos
**Enlaces:** [temario](../../Temario.md#01--datetime--javatime-correcto) · [ejercicios](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/datetime/Exercises.md) · [código](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/datetime/) · [pruebas](../../../01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/datetime/DateTimeTest.kt)

> Guía de estudio: teoría, mapa del código y fuentes. Enlaces revisados el 2026-11-22.

## Qué vas a aprender

Usar `java.time` sin los errores clásicos (zonas, horario de verano, formatos estrictos, "ahora" sin inyectar) con el
calendario del Mercado de Nueva York, y saber qué aporta la stdlib de Kotlin (`kotlin.time`).

## Teoría

### 1. En la JVM se usa `java.time`

Kotlin no reemplaza `java.time` en la JVM: lo usa tal cual (es lo que hace la lección). `kotlinx-datetime` y
`kotlin.time.Instant` (estable en 2.3) existen para el código multiplataforma, donde `java.time` no está (`11 · kmp`).
Lo que sí cambia es la ergonomía: los rangos sobre cualquier `Comparable` (`d in inicio..<fin`, desde 1.9),
`generateSequence(d) { it.plusDays(1) }` para iterar días, y `Duration` de Kotlin (`5.minutes`), que no es la de Java
(cuidado con los *imports*). → [`java.time`][java-time], [Time measurement][kotlin-time]

### 2. Qué tipo para qué

La regla que evita casi todos los errores: **se guarda un instante y se muestra una hora local**.

| Tipo | Qué es | En el curso |
|---|---|---|
| `Instant` | Un punto en la línea de tiempo, en UTC | El `ts` de cada tick |
| `LocalDate`, `LocalTime`, `LocalDateTime` | Fecha u hora "de calendario", sin zona | El día de mercado, la hora de apertura |
| `ZonedDateTime` | Hora local + zona (con sus reglas de horario de verano) | La sesión de 09:30 a 16:00 en Nueva York |
| `Duration` / `Period` | Tiempo exacto (segundos) / años, meses y días de calendario | La duración de la sesión / cuánto se mantuvo una posición |

Un `LocalDateTime` **no** es un instante: guardar un tick como `LocalDateTime` pierde la zona, y la misma hora local
existe dos veces el día que termina el horario de verano. → [Tutorial de Date-Time][oracle-tutorial]

### 3. Zonas y horario de verano: el día de mercado no es el día UTC

`America/New_York` está a UTC−4 en verano y a UTC−5 en invierno. Dos consecuencias que la lección prueba: un tick de las
01:30 UTC del 29 de octubre pertenece al **28** en Nueva York (`marketDateOf`), y la sesión de 09:30 cae a las 13:30 UTC
antes del 1 de noviembre y a las 14:30 después (`sessionBounds`). Un día dura 23 o 25 horas en los cambios
(`hoursInDay`), así que `plusHours(24)` no es `plusDays(1)`. Las reglas vienen de la base de zonas de la IANA, que se
actualiza: por eso se usan zonas con nombre (`America/New_York`), nunca desplazamientos fijos (`-05:00`), para fechas
futuras. → [IANA Time Zone Database][iana]

### 4. "Ahora" se inyecta

`Instant.now()` dentro de la lógica hace que el resultado dependa del reloj de la máquina: imposible de probar. La
lección recibe un `Clock` como parámetro con valor por defecto (`isOpenNow(clock = Clock.systemUTC())`), y las pruebas
pasan un `Clock.fixed(…)` para un lunes a las 09:45, un sábado o un feriado. Es el mismo patrón que en Java; en Kotlin
el parámetro por defecto evita la sobrecarga.

### 5. Formatos estrictos y aritmética de calendario

`DateTimeFormatter.ofPattern("MM/dd/uuuu").withResolverStyle(ResolverStyle.STRICT)` rechaza el 30 de febrero
(`parseUsDate`). La trampa: con `yyyy` (año de la era) en modo estricto, **todo** falla; hace falta `uuuu`. Y la
aritmética de calendario tiene sus propias reglas: el 31 de enero más un mes es el 28 (o 29) de febrero
(`plusOneMonth`), y volver un mes no regresa al 31. El cierre de mes del Banco retrocede desde el último día hasta el
último día hábil (`monthClose`).

## Cómo correrla

```bash
./gradlew -p 01-language run -Plesson=datetime
./gradlew -p 01-language test --tests '*.datetime.*'
```

## Recorrido sugerido

1. `DateTimes.kt`, de arriba abajo: el mapa mental de tipos (comentario inicial); el Mercado (`MARKET_ZONE`,
   `MARKET_OPEN`, `MARKET_CLOSE`, `HOLIDAYS_2026` ficticios); parseo estricto `MM/dd/uuuu` (`parseUsDate`,
   `formatUsDate`); días de mercado (`isTradingDay`, `tradingDays`); el día de mercado de un tick en Nueva York y no en
   UTC (`marketDateOf`); la sesión como instantes que se mueven con el horario de verano (`sessionBounds`,
   `isMarketOpen`); `Clock` inyectado (`isOpenNow`); ventana de 7 días (`windowBefore`); cierre de mes del Banco
   (`monthClose`); calendario (`plusOneMonth`, `lastDayOfMonth`, `holdingPeriod` con `Period`); días de 23 y 25 horas
   (`hoursInDay`) y conversión de zonas (`convert`).
2. `DateTimeTest.kt`: relojes fijos, la sesión en marzo y noviembre, feriados y fines de semana.
3. `Main.kt`.

## Mapa del código

| Archivo | Qué muestra | Símbolos | Lo prueba |
|---|---|---|---|
| `DateTimes.kt` | `java.time` sin trampas, con el calendario del Mercado | `MARKET_ZONE`, `MARKET_OPEN`, `MARKET_CLOSE`, `HOLIDAYS_2026`, `parseUsDate`, `formatUsDate`, `isTradingDay`, `tradingDays`, `marketDateOf`, `sessionBounds`, `isMarketOpen`, `isOpenNow`, `windowBefore`, `daysBetween`, `lastDayOfMonth`, `monthClose`, `plusOneMonth`, `holdingPeriod`, `hoursInDay`, `convert`, `isoInstant`, `formatDuration`, `zonedNow` | `DateTimeTest` (las 8 pruebas, de `strict parsing of US dates` a `daylight saving changes the length of a day`) |

## Para profundizar

**Documentación oficial**

- [Paquete `java.time`][java-time] (Java SE 21): todos los tipos y sus garantías.
- [Tutorial de Date-Time de Java][oracle-tutorial] (Oracle): qué tipo usar para qué, con ejemplos.
- [Time measurement][kotlin-time] (kotlinlang.org): `kotlin.time.Duration`, `measureTime`, fuentes monotónicas.
- [`kotlinx-datetime`][kx-datetime]: la alternativa multiplataforma (se retoma en `11-multiplatform`).
- [IANA Time Zone Database][iana]: de dónde salen las reglas de cada zona.

**Lecturas**

- [Storing UTC is not a silver bullet][skeet-utc] (Jon Skeet): cuándo guardar UTC no alcanza (eventos futuros en una
  zona).

**Videos**

- [Date and Time in Java 8][colebourne] (Stephen Colebourne, autor de `java.time`, JDK IO 2016): el diseño de la API
  contado por quien la escribió.

## Estado

- **Hecho:** código en los casos (B0′ tanda 4) y 8 pruebas; 24 ejercicios (5 🎯); 0 resueltos en `practice/`.
- **Falta:** B2 (⚡ velas por sesión).

[java-time]: https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/package-summary.html
[oracle-tutorial]: https://docs.oracle.com/javase/tutorial/datetime/
[kotlin-time]: https://kotlinlang.org/docs/time-measurement.html
[kx-datetime]: https://github.com/Kotlin/kotlinx-datetime
[iana]: https://www.iana.org/time-zones
[skeet-utc]: https://codeblog.jonskeet.uk/2019/03/27/storing-utc-is-not-a-silver-bullet/
[colebourne]: https://www.youtube.com/watch?v=2HXc3f-C3L0
