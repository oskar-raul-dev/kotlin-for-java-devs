# Ejercicios: fechas y horas con java.time

> **Lección:** `datetime` · **Dónde se resuelven:** `datetime/practice/ExNN<Nombre>.kt` y su prueba en
> `src/test/kotlin/.../datetime/practice/ExNN<Nombre>Test.kt` · **Correr:** `./gradlew -p 01-language test --tests '*.datetime.practice.*'`
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil. **🎯 = prioritario** (ruta recomendada); el resto queda para después.
> Cada ejercicio trae firma sugerida y casos que la prueba debe cubrir como mínimo.

**Prioritarios (🎯):** 01, 02, 03, 08, 09.

> Regla para todos: **nunca `now()` sin `Clock`**. Las pruebas fijan el reloj con `Clock.fixed`.

## 🟢 Fáciles

**01 🟢 🎯 Fecha estricta.** Parsear `MM/dd/uuuu` con `ResolverStyle.STRICT`; `02/30/2020` y `13/01/2020` → `null`.
¿Por qué `yyyy` en vez de `uuuu` rompe el modo estricto? Pruébalo.

**02 🟢 🎯 Epoch a fecha.** `timestamp` en milisegundos (como en `OrderEvent`) a `LocalDate` en UTC y en `America/Los_Angeles`.
Encuentra un timestamp que dé días distintos en cada zona.

**03 🟢 🎯 Edad.** Edad cumplida a una fecha con `Period`, incluido el caso del 29 de febrero.

**04 🟢 Días entre fechas.** Con `ChronoUnit.DAYS.between` y con `Period`: ¿por qué `Period.between(...).days` no es el total de días?

**05 🟢 Día de la semana.** El próximo viernes a partir de una fecha con `TemporalAdjusters.next`.

**06 🟢 Formatos.** La misma fecha en ISO, `dd/MM/yyyy`, y en texto largo en español (`"martes, 3 de noviembre de 2026"`).

**07 🟢 Duraciones.** `Duration.ofMinutes(135)` a `"2h 15m"` y `Duration.parse("PT1H30M")`.

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

**12 🟡 Horario de verano.** En `America/New_York`: ¿qué hora local es `2026-03-08T02:30` (no existe)? ¿Y `2026-11-01T01:30` (existe dos veces)?
Muestra cómo `ZonedDateTime.of` resuelve cada caso y cómo elegir el otro offset con `withLaterOffsetAtOverlap`.

**13 🟡 Reuniones entre zonas.** Una reunión a las 9:00 de Bogotá: hora en Madrid, Nueva York y Tokio, en una fecha de invierno y otra de verano.

**14 🟡 Edad mínima.** Validar que alguien tenga al menos 18 años **a la fecha del evento** (no a hoy).
- Firma: `fun isAdultAt(dob: LocalDate, eventDay: LocalDate): Boolean`

**15 🟡 Expiración.** Un token válido por 15 minutos: `isExpired(issuedAt: Instant, clock: Clock)`; prueba el instante exacto del borde.

## 🟠 Difíciles

**16 🟠 Rangos que se cruzan.** Dadas reservas `[inicio, fin)` como `LocalDateTime`, detectar choques y fusionar las contiguas.

**17 🟠 Calendario mensual.** Imprimir un mes como `cal` de Unix (semanas empiezan el lunes), con `WeekFields.of(Locale("es"))`.

**18 🟠 Cron simple.** El próximo instante que cumple `"0 9 * * MON-FRI"` (minuto, hora, día, mes, día de semana) desde un `Clock`.

**19 🟠 Series temporales con huecos.** Conteos diarios con días faltantes: rellenar con cero entre el primer y el último día, y calcular
la media móvil de 7 días.

**20 🟠 Reloj controlable.** Un `MutableClock` para pruebas con `advance(Duration)`, y úsalo para probar un `RateLimiter` de 10 llamadas por minuto.

## 🔴 Muy difíciles

**21 🔴 Zonas en la base.** Guardar `Instant`, `LocalDateTime` y `OffsetDateTime` en PostgreSQL (`timestamptz` y `timestamp`) por JDBC y
mostrar qué cambia al cambiar la zona de la sesión (`SET TIME ZONE`). Usa el compose de la lección JDBC.

**22 🔴 Recurrencias.** "El último viernes de cada mes" y "cada 2 semanas el martes" entre dos fechas, con excepciones.

**23 🔴 Ventanas de negocio.** El número de horas hábiles (lunes a viernes, 8–17, en una zona) entre dos `Instant`, con festivos y DST.

**24 🔴 kotlinx-datetime.** Reescribe 5 ejercicios con `kotlinx-datetime` (agrega la dependencia en un experimento aparte) y compara
la API con `java.time`: ¿qué gana y qué pierde en un proyecto solo JVM?
