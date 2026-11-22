package dev.raul.oskar.learning.kotlin.datetime

import java.time.Clock
import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

// En Kotlin/JVM se usa java.time directamente (kotlinx-datetime existe para multiplataforma).
// Mapa mental:
//   Instant        -> un punto en la línea de tiempo (UTC), lo que se guarda (el "ts" de un tick).
//   LocalDate/Time -> fecha/hora "de calendario", sin zona (el día de mercado, la hora de apertura).
//   ZonedDateTime  -> Local + zona: lo que se muestra a una persona.
//   Duration       -> tiempo exacto (segundos); Period -> años/meses/días de calendario.

/** El Mercado de los casos (docs/Casos.md): Nueva York, 09:30 a 16:00, de lunes a viernes. */
val MARKET_ZONE: ZoneId = ZoneId.of("America/New_York")
val MARKET_OPEN: LocalTime = LocalTime.of(9, 30)
val MARKET_CLOSE: LocalTime = LocalTime.of(16, 0)

/** Calendario de feriados propio y ficticio del Mercado para 2026. */
val HOLIDAYS_2026: Set<LocalDate> = setOf(
    LocalDate.of(2026, 1, 1), LocalDate.of(2026, 5, 15), LocalDate.of(2026, 9, 14),
    LocalDate.of(2026, 11, 11), LocalDate.of(2026, 12, 25),
)

/** Fechas de EE. UU.: MM/dd/uuuu, estricto (rechaza 02/30). Con yyyy y STRICT falla todo: yyyy es año de la era. */
private val US_DATE: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/uuuu").withResolverStyle(ResolverStyle.STRICT)

fun parseUsDate(text: String): LocalDate? =
    try { LocalDate.parse(text, US_DATE) } catch (e: DateTimeParseException) { null }

fun formatUsDate(date: LocalDate): String = date.format(US_DATE)

fun isTradingDay(date: LocalDate, holidays: Set<LocalDate> = HOLIDAYS_2026): Boolean =
    date.dayOfWeek != DayOfWeek.SATURDAY && date.dayOfWeek != DayOfWeek.SUNDAY && date !in holidays

/** Iterar días: generateSequence sobre LocalDate. */
fun tradingDays(from: LocalDate, toInclusive: LocalDate, holidays: Set<LocalDate> = HOLIDAYS_2026): List<LocalDate> =
    generateSequence(from) { it.plusDays(1) }
        .takeWhile { !it.isAfter(toInclusive) }
        .filter { isTradingDay(it, holidays) }
        .toList()

/** El día de mercado de un tick: la fecha en Nueva York, NO la fecha UTC (un tick de las 20:30 NY ya es "mañana" en UTC). */
fun marketDateOf(epochMillis: Long): LocalDate = Instant.ofEpochMilli(epochMillis).atZone(MARKET_ZONE).toLocalDate()

/** Apertura y cierre de una sesión como instantes: con el horario de verano caen a otra hora UTC. */
fun sessionBounds(date: LocalDate): Pair<Instant, Instant> =
    date.atTime(MARKET_OPEN).atZone(MARKET_ZONE).toInstant() to date.atTime(MARKET_CLOSE).atZone(MARKET_ZONE).toInstant()

/** [apertura, cierre): a las 16:00 en punto ya está cerrado. */
fun isMarketOpen(at: Instant, holidays: Set<LocalDate> = HOLIDAYS_2026): Boolean {
    val local = at.atZone(MARKET_ZONE)
    val (open, close) = sessionBounds(local.toLocalDate())
    return isTradingDay(local.toLocalDate(), holidays) && !at.isBefore(open) && at.isBefore(close)
}

/** El reloj se inyecta para que las pruebas sean deterministas (nunca Instant.now() suelto). */
fun isOpenNow(clock: Clock = Clock.systemUTC()): Boolean = isMarketOpen(clock.instant())

/** La ventana de los N días anteriores a un día (sin incluirlo): [day-N, day). */
fun windowBefore(day: LocalDate, days: Long = 7): ClosedRange<LocalDate> = day.minusDays(days)..day.minusDays(1)

fun daysBetween(a: LocalDate, b: LocalDate): Long = ChronoUnit.DAYS.between(a, b)

fun lastDayOfMonth(date: LocalDate): LocalDate = date.with(TemporalAdjusters.lastDayOfMonth())

/** El Banco cierra el mes el último día hábil: si el mes termina en fin de semana o feriado, se retrocede. */
fun monthClose(month: YearMonth, holidays: Set<LocalDate> = HOLIDAYS_2026): LocalDate =
    generateSequence(month.atEndOfMonth()) { it.minusDays(1) }.first { isTradingDay(it, holidays) }

/** Sumar un mes al 31 de enero da el 28/29 de febrero (no se desborda). */
fun plusOneMonth(date: LocalDate): LocalDate = date.plusMonths(1)

/** Cuánto se mantuvo una posición, en calendario (Period), no en segundos (Duration). */
fun holdingPeriod(bought: LocalDate, sold: LocalDate): Period = Period.between(bought, sold)

/** Cambio de horario: un "día" no siempre dura 24 h. */
fun hoursInDay(date: LocalDate, zone: ZoneId): Long =
    Duration.between(date.atStartOfDay(zone), date.plusDays(1).atStartOfDay(zone)).toHours()

/** Misma instante, distinta hora local. */
fun convert(at: LocalDateTime, from: ZoneId, to: ZoneId): LocalDateTime =
    at.atZone(from).withZoneSameInstant(to).toLocalDateTime()

fun isoInstant(epochMillis: Long): String = DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(epochMillis))

fun formatDuration(d: Duration): String = "%dh %02dm %02ds".format(d.toHours(), d.toMinutesPart(), d.toSecondsPart())

fun zonedNow(clock: Clock): ZonedDateTime = ZonedDateTime.now(clock)
