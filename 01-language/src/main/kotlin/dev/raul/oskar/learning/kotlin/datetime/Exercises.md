# Ejercicios: fechas y horas con java.time

> **Lección:** `datetime` · **Dónde se resuelven:** `datetime/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../datetime/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 01-language test --tests '*.datetime.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> **🎤** = pregunta típica de entrevista · **⚡** = miniproyecto que cierra la lección · `[1.9]` = versión de Kotlin en que es estable.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo. Los números 25 a 40 se agregaron
> después y van dentro de su nivel; el ⚡ es el 41.

**Prioritarios (🎯):** 01, 02, 03, 08, 09. **Entrevista (🎤):** 04, 12, 20, 25, 30, 39.

> Regla para todos: **nunca `now()` sin `Clock`**. Las pruebas fijan el reloj con `Clock.fixed`.

## 🟢 Fáciles

**01 🟢 🎯 Fecha estricta.** Parsear `MM/dd/uuuu` con `ResolverStyle.STRICT`; `02/30/2020` y `13/01/2020` → `null`.
¿Por qué `yyyy` en vez de `uuuu` rompe el modo estricto? Pruébalo.

**02 🟢 🎯 Epoch a fecha.** El `ts` en milisegundos de `ticks.json` a `LocalDate` en UTC y en `America/New_York` (el día de
mercado). Encuentra un `ts` que dé días distintos en cada zona.

**03 🟢 🎯 Fecha de liquidación.** La fecha T+2 de una operación: dos días de mercado después, saltando fines de semana y
feriados.
- Firma: `fun settlementDate(trade: LocalDate, days: Int = 2, holidays: Set<LocalDate> = emptySet()): LocalDate`
- Casos: un jueves (cae el lunes), un viernes, un feriado en el medio, `days = 0`.

**04 🟢 🎤 Días entre fechas.** Con `ChronoUnit.DAYS.between` y con `Period`: ¿por qué `Period.between(...).days` no es el total de días?

**05 🟢 Día de la semana.** El próximo viernes a partir de una fecha con `TemporalAdjusters.next`.

**06 🟢 Formatos.** La misma fecha en ISO, `dd/MM/yyyy`, y en texto largo en español (`"martes, 3 de noviembre de 2026"`).

**07 🟢 Duraciones.** `Duration.ofMinutes(135)` a `"2h 15m"` y `Duration.parse("PT1H30M")`.

**25 🟢 🎤 ¿Cuál se guarda y cuál se muestra?** El `ts` de un tick como `Instant`, `ZonedDateTime` en Nueva York,
`OffsetDateTime` y `LocalDateTime`.
- Firma: `fun views(epochMillis: Long): TickTimes` con
  `data class TickTimes(val instant: Instant, val zoned: ZonedDateTime, val offset: OffsetDateTime, val local: LocalDateTime)`
- Casos: los cuatro representan el mismo instante salvo `local`; volver de `local` a `Instant` con la zona equivocada
  (UTC) da otro instante (4 o 5 horas de diferencia según la fecha). Explica en un comentario qué guardarías en la base
  y qué mostrarías al usuario.

**26 🟢 `[1.6]` `kotlin.time.Duration`.** La duración de la sesión con la API de Kotlin y su paso a `java.time`.
- Firma: `fun sessionLength(): kotlin.time.Duration` (`6.hours + 30.minutes`)
- Casos: `inWholeMinutes == 390`, `toString() == "6h 30m"`, `Duration.parse("6h 30m")` da lo mismo,
  `toJavaDuration()` y `toKotlinDuration()` de ida y vuelta, y que `sessionBounds` de la lección da exactamente esa
  duración en un día normal.

**27 🟢 Fin de mes anclado.** Fechas mensuales de un débito automático que empezó un 31: deben volver al 31 cuando el
mes lo tiene.
- Firma: `fun monthlyDates(start: LocalDate, n: Int): List<LocalDate>` (con `start.plusMonths(i)`, no encadenando)
- Casos: desde el 31 de enero de 2028 → 31 ene, 29 feb (bisiesto), 31 mar, 30 abr; desde el 15, siempre el 15;
  encadenar `plusMonths(1)` da 31 ene, 29 feb, 29 mar (la trampa): pruébalo.

**28 🟢 `YearMonth`.** Los meses entre dos meses, para el cierre mensual del Banco.
- Firma: `fun monthsBetween(from: YearMonth, toInclusive: YearMonth): List<YearMonth>`
- Casos: `2026-11` a `2027-02` → 4 meses, el mismo mes → 1, al revés → vacía; `YearMonth.parse("2026-11")`,
  `lengthOfMonth()` de febrero de 2028, y `monthClose` de la lección para cada mes de la lista.

**29 🟢 Minutos desde la apertura.** Para un tick, cuántos minutos pasaron desde las 09:30 de Nueva York, o `null` si
cayó fuera de la sesión.
- Firma: `fun minutesSinceOpen(epochMillis: Long): Long?` (con `MARKET_ZONE`, `MARKET_OPEN` y `MARKET_CLOSE`)
- Casos: 09:30 → 0, 15:59 → 389, 16:00 → `null`, 09:29 → `null`, un sábado → `null`, un feriado de `HOLIDAYS_2026` →
  `null`, un día después del cambio de horario.

## 🟡 Medios

**08 🟡 🎯 Ventana de 7 días.** Los 7 días anteriores a un día dado como `List<LocalDate>` y como rango; ¿incluye ese día?
Decide, documenta en la prueba y comprueba que son exactamente 7.
- Firma: `fun previousDays(day: LocalDate, n: Long = 7): List<LocalDate>`

**09 🟡 🎯 Agrupar por día local.** Eventos con `epochMillis` agrupados por día **en una zona dada**. Prueba con eventos cerca de
medianoche que cambian de día según la zona.
- Firma: `fun byDay(events: List<Long>, zone: ZoneId): Map<LocalDate, Int>`

**10 🟡 Días hábiles.** Sumar `n` días hábiles a una fecha saltando fines de semana y una lista de festivos.
- Firma: `fun addBusinessDays(start: LocalDate, n: Int, holidays: Set<LocalDate>): LocalDate`

**11 🟡 Último día del mes.** Para cada mes de 2028, el último día y si es fin de semana.

**12 🟡 🎤 Horario de verano.** En `America/New_York`: ¿qué hora local es `2026-03-08T02:30` (no existe)? ¿Y `2026-11-01T01:30` (existe dos veces)?
Muestra cómo `ZonedDateTime.of` resuelve cada caso y cómo elegir el otro offset con `withLaterOffsetAtOverlap`.

**13 🟡 Reuniones entre zonas.** Una reunión a las 9:00 de Bogotá: hora en Madrid, Nueva York y Tokio, en una fecha de invierno y otra de verano.

**14 🟡 Antigüedad mínima.** Validar que una cuenta tenga al menos seis meses de abierta **a la fecha de la transferencia**
(no a hoy), con `Period`, incluido el caso de una cuenta abierta un 31.
- Firma: `fun isSeasoned(openedOn: LocalDate, transferDay: LocalDate): Boolean`

**15 🟡 Expiración.** Un token válido por 15 minutos: `isExpired(issuedAt: Instant, clock: Clock)`; prueba el instante exacto del borde.

**30 🟡 🎤 Probar "ahora".** Las transferencias que llegan después de las 17:00 de Bogotá se procesan el siguiente
día hábil.
- Firma: `class TransferCutoff(private val clock: Clock, private val holidays: Set<LocalDate>)` con
  `fun processingDay(): LocalDate`
- Casos: con `Clock.fixed` a las 16:59 y a las 17:00 de un martes, un viernes a las 18:00 (lunes), la víspera de un
  feriado; `Clock.offset` para mover el reloj en la misma prueba. Explica en un comentario por qué inyectar el `Clock`
  y no simular `LocalDateTime.now()` estático.

**31 🟡 Calendario de un mes.** Los días de mercado de un mes, y el primero y el último.
- Firma: `fun tradingCalendar(month: YearMonth, holidays: Set<LocalDate> = HOLIDAYS_2026): List<LocalDate>`
- Casos: noviembre de 2026 (la prueba fija la cantidad), un mes con un feriado en lunes, un mes que empieza en
  sábado; contra `tradingDays` de la lección.

**32 🟡 La apertura en UTC.** La hora UTC de la apertura cambia dos veces al año.
- Firmas: `fun openUtc(date: LocalDate): LocalTime` y `fun dstTransitions(zone: ZoneId, year: Int): List<ZonedDateTime>`
  (con `zone.rules.nextTransition`)
- Casos: el 15 de enero de 2026 abre a las 14:30 UTC y el 15 de julio a las 13:30; en 2026, Nueva York cambia el 8 de
  marzo y el 1 de noviembre; `Asia/Tokyo` no tiene transiciones; `hoursInDay` de la lección da 23 y 25 esos días.

**33 🟡 Varios formatos de fecha.** Un archivo que mezcla formatos: probar cada uno en orden, sin `try/catch` por
formato si se puede (`parseUnresolved` o `DateTimeFormatterBuilder.appendOptional`).
- Firma: `fun parseFlexible(text: String): LocalDate?`
- Casos: `2026-11-03`, `11/03/2026` (se lee como formato de EE. UU.: documéntalo), `03-Nov-2026` (con `Locale.US`),
  `20261103`, `2026-02-30` → `null`, texto vacío → `null`.

**34 🟡 Hace cuánto.** La antigüedad de una orden en texto para la pantalla.
- Firma: `fun humanAge(created: Instant, clock: Clock): String`
- Casos: `"hace 5 s"`, `"hace 3 min"`, `"hace 2 h"`, `"ayer"`, `"hace 3 días"`, los bordes exactos (59 s, 60 s, 24 h),
  una fecha futura → `IllegalArgumentException`.

**35 🟡 Cubetas de tiempo.** El inicio de la cubeta de `n` minutos de cada instante, para agrupar ticks.
- Firma: `fun bucketStart(at: Instant, size: Duration): Instant`
- Casos: cubetas de 1, 5 y 15 minutos; un instante justo en el borde (es su propio inicio); `size` que no divide la
  hora (7 minutos: alineado al epoch, documéntalo); `size` cero o negativo → `IllegalArgumentException`. Compara con
  `truncatedTo(ChronoUnit.MINUTES)`.

## 🟠 Difíciles

**16 🟠 Rangos que se cruzan.** Dadas reservas `[inicio, fin)` como `LocalDateTime`, detectar choques y fusionar las contiguas.

**17 🟠 Calendario mensual.** Imprimir un mes como `cal` de Unix (semanas empiezan el lunes), con `WeekFields.of(Locale("es"))`.

**18 🟠 Cron simple.** El próximo instante que cumple `"0 9 * * MON-FRI"` (minuto, hora, día, mes, día de semana) desde un `Clock`.

**19 🟠 Series temporales con huecos.** Conteos diarios con días faltantes: rellenar con cero entre el primer y el último día, y calcular
la media móvil de 7 días.

**20 🟠 🎤 Reloj controlable.** Un `MutableClock` para pruebas con `advance(Duration)`, y úsalo para probar un `RateLimiter` de 10 llamadas por minuto.

**36 🟠 Convenciones de días.** La fracción de año entre dos fechas para calcular intereses, según la convención.
- Firma: `fun yearFraction(from: LocalDate, to: LocalDate, convention: DayCount): BigDecimal` con
  `enum class DayCount { ACT_360, ACT_365, THIRTY_360 }` (escala 10, `HALF_EVEN`)
- Casos: del 1 de enero al 1 de julio de 2026 en las tres; un tramo que cruza el 29 de febrero de 2028; la regla del
  fin de mes de 30/360 (el 31 cuenta como 30, y el 28 de febrero no); `from > to` → `IllegalArgumentException`.

**37 🟠 Feriados por reglas.** Un calendario ficticio de feriados calculado por reglas, no por lista: "tercer lunes
de enero", "último lunes de mayo", "4 de julio, o el viernes o el lunes si cae en fin de semana", "cuarto jueves de
noviembre" y "Viernes Santo" (Pascua con el algoritmo anónimo gregoriano).
- Firma: `fun holidays(year: Int): Set<LocalDate>`
- Casos: 2026 (Viernes Santo es el 3 de abril), un año en que el 4 de julio cae en sábado (2026: se pasa al viernes
  3) y otro en domingo, Pascua contra una tabla de 10 años; los feriados resultantes con `isTradingDay`.

**38 🟠 Fecha valor entre plazas.** Una transferencia entre dos plazas se liquida el primer día que es hábil en las
dos y en que se llega antes del corte local de cada una.
- Firma: `fun valueDate(at: Instant, a: Market, b: Market): LocalDate` con
  `data class Market(val zone: ZoneId, val cutoff: LocalTime, val holidays: Set<LocalDate>)`
- Casos: Bogotá y Nueva York un martes por la mañana (el mismo día), después del corte de una de las dos (el
  siguiente), un feriado solo en una plaza, Tokio y Nueva York (las fechas locales difieren: documenta en qué zona
  se expresa la fecha valor).

**39 🟠 🎤 `[1.9]` Reloj monotónico.** Medir duraciones con el reloj de pared falla si el sistema cambia la hora;
`TimeSource.Monotonic` no.
- Firma: `class Stopwatch(private val source: TimeSource = TimeSource.Monotonic)` con `start()`, `lap(): Duration` y
  `elapsed(): Duration`
- Casos: con `TestTimeSource` (avanzar con `+=`): vueltas y total; `lap()` antes de `start()` →
  `IllegalStateException`. Explica en un comentario por qué `Instant.now()` restado no sirve para medir y qué es
  `System.nanoTime()`.

**40 🟠 Zonas que cambiaron de reglas.** Los cambios de offset de una zona entre dos fechas, desde las reglas del JDK.
- Firma: `fun offsetChanges(zone: ZoneId, from: Instant, to: Instant): List<Pair<Instant, ZoneOffset>>`
- Casos: `America/Bogota` tuvo horario de verano en 1992 (cambio el 3 de mayo de 1992); Nueva York en 2026 (dos
  cambios); una zona sin cambios (vacía). Explica en un comentario qué pasa con las fechas futuras guardadas como
  hora local si un país cambia sus reglas, y cómo se actualiza `tzdata` en el JDK.

## 🔴 Muy difíciles

**21 🔴 Zonas en la base.** Guardar `Instant`, `LocalDateTime` y `OffsetDateTime` en PostgreSQL (`timestamptz` y `timestamp`) por JDBC y
mostrar qué cambia al cambiar la zona de la sesión (`SET TIME ZONE`). Usa el compose de la lección JDBC.

**22 🔴 Recurrencias.** "El último viernes de cada mes" y "cada 2 semanas el martes" entre dos fechas, con excepciones.

**23 🔴 Ventanas de negocio.** El número de horas hábiles (lunes a viernes, 09:30–16:00 en `America/New_York`) entre dos `Instant`, con festivos y DST.

**24 🔴 kotlinx-datetime.** Reescribe 5 ejercicios con `kotlinx-datetime` (agrega la dependencia en un experimento aparte) y compara
la API con `java.time`: ¿qué gana y qué pierde en un proyecto solo JVM?

## ⚡ Saiyajin

**41 ⚡ Velas por sesión.** Agrupar los ticks de `ticks.json` (el `Tick` de la lección `json`) en velas de 1 minuto y
en una vela por sesión (09:30–16:00 en Nueva York), respetando la zona y el horario de verano; los ticks fuera de
horario o de días sin mercado se descartan y se reportan.
- Firma: `fun sessionCandles(ticks: Sequence<Tick>, holidays: Set<LocalDate> = HOLIDAYS_2026): SessionReport` con
  `data class SessionReport(val minute: Map<LocalDate, List<Candle>>, val session: Map<LocalDate, Candle>, val rejected: List<Tick>)`
  y una `Candle` con apertura, máximo, mínimo, cierre, volumen e instante de inicio (puedes reusar la de `functional`).
- **Se verifica con:** ticks sintéticos el lunes 9 de marzo y el lunes 2 de noviembre de 2026 (los dos primeros días
  de mercado después de cada cambio de horario): las dos sesiones duran 6.5 h, las velas empiezan a las 09:30 locales
  (13:30 UTC en marzo, 14:30 UTC en noviembre) y agrupar por día UTC daría otro resultado (la prueba lo muestra); un
  tick a las 16:00 exactas queda fuera; y el tick fuera de horario de `ticks.json` aparece en `rejected`.
