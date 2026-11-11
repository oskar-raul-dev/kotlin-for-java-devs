package dev.raul.oskar.learning.kotlin.datetime

import java.time.Clock
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

fun main() {
    val ny = ZoneId.of("America/New_York")
    val bogota = ZoneId.of("America/Bogota")

    println("parseUsDate(03/14/1985) = ${parseUsDate("03/14/1985")}")
    println("parseUsDate(02/30/2020) = ${parseUsDate("02/30/2020")}  (estricto)")
    println("formatUsDate            = ${formatUsDate(LocalDate.of(2026, 11, 3))}")
    println("edad hoy                = ${age(LocalDate.of(1985, 3, 14))}")
    println("edad con reloj fijo     = ${age(LocalDate.of(1988, 2, 29), Clock.fixed(LocalDate.of(2026, 2, 28).atStartOfDay(bogota).toInstant(), bogota))}")
    println("dateOf(1734739200000)   = ${dateOf(1734739200000)} UTC / ${dateOf(1734739200000, ny)} NY")
    println("isoInstant              = ${isoInstant(1734739200000)}")
    println("ventana 90d antes 21-12 = ${windowBefore(LocalDate.of(2024, 12, 21))}")
    println("días 2026-01-01..12-25  = ${daysBetween(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 25))}")
    println("hábiles 03..09-11       = ${businessDays(LocalDate.of(2026, 11, 3), LocalDate.of(2026, 11, 9))}")
    println("31-01 + 1 mes           = ${plusOneMonth(LocalDate.of(2026, 1, 31))}")
    println("último día feb 2028     = ${lastDayOfMonth(LocalDate.of(2028, 2, 10))}")
    println("horas 2026-03-08 NY     = ${hoursInDay(LocalDate.of(2026, 3, 8), ny)}  (inicio horario de verano)")
    println("horas 2026-03-08 Bogotá = ${hoursInDay(LocalDate.of(2026, 3, 8), bogota)}")
    println("09:00 Bogotá en NY      = ${convert(LocalDateTime.of(2026, 11, 3, 9, 0), bogota, ny)}")
    println("duración                = ${formatDuration(Duration.ofSeconds(3725))}")
    println("abierto 08:59 / 09:00   = ${isOpen(LocalTime.of(8, 59))} / ${isOpen(LocalTime.of(9, 0))}")
    println("ahora (zona del sistema)= ${zonedNow(Clock.systemDefaultZone())}")
}
