package dev.raul.oskar.learning.kotlin.datetime

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset

fun main() {
    val bogota = ZoneId.of("America/Bogota")
    val tick = 1793217600000          // 2026-10-28T20:00:00Z

    println("parseUsDate(10/26/2026)  = ${parseUsDate("10/26/2026")}")
    println("parseUsDate(02/30/2026)  = ${parseUsDate("02/30/2026")}  (estricto)")
    println("formatUsDate             = ${formatUsDate(LocalDate.of(2026, 11, 3))}")
    println("tick ${isoInstant(tick)} -> día de mercado ${marketDateOf(tick)}")
    println("tick 01:30 UTC del 29    -> día de mercado ${marketDateOf(1793237400000)}  (en Nueva York aún es el 28)")
    println("sesión 2026-10-30 (EDT)  = ${sessionBounds(LocalDate.of(2026, 10, 30))}")
    println("sesión 2026-11-02 (EST)  = ${sessionBounds(LocalDate.of(2026, 11, 2))}")
    val fixed = Clock.fixed(Instant.parse("2026-11-02T14:45:00Z"), ZoneOffset.UTC)
    println("abierto con reloj fijo   = ${isOpenNow(fixed)}  (09:45 en Nueva York)")
    println("abierto feriado 11-11    = ${isMarketOpen(Instant.parse("2026-11-11T15:00:00Z"))}")
    println("días de mercado 11-09..13 = ${tradingDays(LocalDate.of(2026, 11, 9), LocalDate.of(2026, 11, 13))}")
    println("ventana 7d antes 11-06   = ${windowBefore(LocalDate.of(2026, 11, 6))}")
    println("cierre de mes oct / ene  = ${monthClose(YearMonth.of(2026, 10))} / ${monthClose(YearMonth.of(2026, 1))}")
    println("31-01 + 1 mes            = ${plusOneMonth(LocalDate.of(2026, 1, 31))}")
    println("último día feb 2028      = ${lastDayOfMonth(LocalDate.of(2028, 2, 10))}")
    println("posición 08-15..11-06   = ${holdingPeriod(LocalDate.of(2025, 8, 15), LocalDate.of(2026, 11, 6))}")
    println("horas 2026-11-01 NY      = ${hoursInDay(LocalDate.of(2026, 11, 1), MARKET_ZONE)}  (fin del horario de verano)")
    println("horas 2026-11-01 Bogotá  = ${hoursInDay(LocalDate.of(2026, 11, 1), bogota)}")
    println("09:00 Bogotá en NY       = ${convert(LocalDateTime.of(2026, 11, 3, 9, 0), bogota, MARKET_ZONE)}")
    println("duración de la sesión    = ${formatDuration(Duration.between(MARKET_OPEN, MARKET_CLOSE))}")
    println("días 2026-01-01..12-25   = ${daysBetween(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 25))}")
    println("ahora (zona del sistema) = ${zonedNow(Clock.systemDefaultZone())}")
}
