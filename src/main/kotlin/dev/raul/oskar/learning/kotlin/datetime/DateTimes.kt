package dev.raul.oskar.learning.kotlin.datetime

import java.time.Clock
import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

// En Kotlin/JVM se usa java.time directamente (kotlinx-datetime existe para multiplataforma).
// Mapa mental:
//   Instant        -> un punto en la línea de tiempo (UTC), lo que se guarda.
//   LocalDate/Time -> fecha/hora "de calendario", sin zona (cumpleaños, fecha de un reporte).
//   ZonedDateTime  -> Local + zona: lo que se muestra a una persona.
//   Duration       -> tiempo exacto (segundos); Period -> años/meses/días de calendario.

/** Fechas de EE. UU.: MM/dd/yyyy, estricto (rechaza 02/30). */
private val US_DATE: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/uuuu").withResolverStyle(ResolverStyle.STRICT)

fun parseUsDate(text: String): LocalDate? =
    try { LocalDate.parse(text, US_DATE) } catch (e: DateTimeParseException) { null }

fun formatUsDate(date: LocalDate): String = date.format(US_DATE)

/** Edad cumplida a una fecha: Period, no división de días entre 365. */
fun ageAt(birth: LocalDate, on: LocalDate): Int = Period.between(birth, on).years

/** El reloj se inyecta para que las pruebas sean deterministas (nunca LocalDate.now() suelto). */
fun age(birth: LocalDate, clock: Clock = Clock.systemDefaultZone()): Int = ageAt(birth, LocalDate.now(clock))

/** epoch millis (timestamp de los eventos) -> fecha en una zona. */
fun dateOf(epochMillis: Long, zone: ZoneId = ZoneOffset.UTC): LocalDate =
    Instant.ofEpochMilli(epochMillis).atZone(zone).toLocalDate()

/** La ventana de los N días anteriores a un día (sin incluirlo): [day-N, day). */
fun windowBefore(day: LocalDate, days: Long = 90): ClosedRange<LocalDate> = day.minusDays(days)..day.minusDays(1)

fun daysBetween(a: LocalDate, b: LocalDate): Long = ChronoUnit.DAYS.between(a, b)

/** Iterar días: generateSequence sobre LocalDate. */
fun businessDays(from: LocalDate, toInclusive: LocalDate): List<LocalDate> =
    generateSequence(from) { it.plusDays(1) }
        .takeWhile { !it.isAfter(toInclusive) }
        .filter { it.dayOfWeek != DayOfWeek.SATURDAY && it.dayOfWeek != DayOfWeek.SUNDAY }
        .toList()

fun lastDayOfMonth(date: LocalDate): LocalDate = date.with(TemporalAdjusters.lastDayOfMonth())

/** Sumar un mes al 31 de enero da el 28/29 de febrero (no se desborda). */
fun plusOneMonth(date: LocalDate): LocalDate = date.plusMonths(1)

/** Cambio de horario: un "día" no siempre dura 24 h. */
fun hoursInDay(date: LocalDate, zone: ZoneId): Long =
    Duration.between(date.atStartOfDay(zone), date.plusDays(1).atStartOfDay(zone)).toHours()

/** Misma instante, distinta hora local. */
fun convert(at: LocalDateTime, from: ZoneId, to: ZoneId): LocalDateTime =
    at.atZone(from).withZoneSameInstant(to).toLocalDateTime()

fun isoInstant(epochMillis: Long): String = DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(epochMillis))

fun formatDuration(d: Duration): String = "%dh %02dm %02ds".format(d.toHours(), d.toMinutesPart(), d.toSecondsPart())

fun isOpen(at: LocalTime, opens: LocalTime = LocalTime.of(9, 0), closes: LocalTime = LocalTime.of(18, 0)): Boolean =
    !at.isBefore(opens) && at.isBefore(closes)

fun zonedNow(clock: Clock): ZonedDateTime = ZonedDateTime.now(clock)
