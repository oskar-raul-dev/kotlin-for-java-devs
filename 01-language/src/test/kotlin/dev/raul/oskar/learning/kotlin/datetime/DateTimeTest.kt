package dev.raul.oskar.learning.kotlin.datetime

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DateTimeTest {

    @Test
    fun `strict parsing of US dates`() {
        assertEquals(LocalDate.of(2026, 10, 26), parseUsDate("10/26/2026"))
        assertNull(parseUsDate("02/30/2026"))
        assertNull(parseUsDate("2026-10-26"))
        assertEquals("02/29/2028", formatUsDate(LocalDate.of(2028, 2, 29)))
    }

    @Test
    fun `the market day is the New York date, not the UTC date`() {
        assertEquals(LocalDate.of(2026, 10, 28), marketDateOf(1793217600000))   // 20:00 UTC = 16:00 NY
        assertEquals(LocalDate.of(2026, 10, 28), marketDateOf(1793237400000))   // 01:30 UTC del 29 = 21:30 NY del 28
        assertEquals("2026-10-29T01:30:00Z", isoInstant(1793237400000))
    }

    @Test
    fun `an injected clock makes now testable`() {
        fun clockAt(utc: String) = Clock.fixed(Instant.parse(utc), ZoneOffset.UTC)
        assertTrue(isOpenNow(clockAt("2026-11-02T14:45:00Z")))           // 09:45 EST
        assertFalse(isOpenNow(clockAt("2026-11-02T14:15:00Z")))          // 09:15 EST: todavía no abre
        assertFalse(isOpenNow(clockAt("2026-11-11T15:00:00Z")))          // feriado (ficticio)
        assertFalse(isOpenNow(clockAt("2026-10-31T15:00:00Z")))          // sábado
    }

    @Test
    fun `the session is half open and moves in UTC with daylight saving`() {
        val (openOct, closeOct) = sessionBounds(LocalDate.of(2026, 10, 30))
        assertEquals(Instant.parse("2026-10-30T13:30:00Z"), openOct)      // EDT, UTC-4
        assertEquals(Instant.parse("2026-11-02T14:30:00Z"), sessionBounds(LocalDate.of(2026, 11, 2)).first)   // EST, UTC-5
        assertTrue(isMarketOpen(openOct))
        assertFalse(isMarketOpen(closeOct))                              // a las 16:00 en punto ya cerró
        assertFalse(isMarketOpen(openOct.minusSeconds(1)))
        assertEquals("6h 30m 00s", formatDuration(Duration.between(MARKET_OPEN, MARKET_CLOSE)))
    }

    @Test
    fun `window of previous days excludes the day itself`() {
        val w = windowBefore(LocalDate.of(2026, 11, 6))
        assertEquals(LocalDate.of(2026, 10, 30), w.start)
        assertEquals(LocalDate.of(2026, 11, 5), w.endInclusive)
        assertTrue(LocalDate.of(2026, 11, 1) in w)
        assertFalse(LocalDate.of(2026, 11, 6) in w)
        assertEquals(7, daysBetween(w.start, w.endInclusive) + 1)
    }

    @Test
    fun `trading days skip weekends and holidays and the month closes on the last one`() {
        assertEquals(4, tradingDays(LocalDate.of(2026, 11, 9), LocalDate.of(2026, 11, 13)).size)   // el 11 es feriado
        assertEquals(LocalDate.of(2026, 10, 30), monthClose(YearMonth.of(2026, 10)))   // el 31 es sábado
        assertEquals(LocalDate.of(2026, 12, 31), monthClose(YearMonth.of(2026, 12)))
    }

    @Test
    fun `calendar arithmetic`() {
        assertEquals(LocalDate.of(2026, 2, 28), plusOneMonth(LocalDate.of(2026, 1, 31)))
        assertEquals(LocalDate.of(2028, 2, 29), lastDayOfMonth(LocalDate.of(2028, 2, 1)))
        assertEquals(Period.of(1, 2, 22), holdingPeriod(LocalDate.of(2025, 8, 15), LocalDate.of(2026, 11, 6)))
    }

    @Test
    fun `daylight saving changes the length of a day`() {
        assertEquals(23, hoursInDay(LocalDate.of(2026, 3, 8), MARKET_ZONE))
        assertEquals(25, hoursInDay(LocalDate.of(2026, 11, 1), MARKET_ZONE))
        assertEquals(24, hoursInDay(LocalDate.of(2026, 11, 1), ZoneId.of("America/Bogota")))
        assertEquals(
            LocalDateTime.of(2026, 11, 3, 9, 0),
            convert(LocalDateTime.of(2026, 11, 3, 9, 0), ZoneId.of("America/Bogota"), MARKET_ZONE),
        )
    }
}
