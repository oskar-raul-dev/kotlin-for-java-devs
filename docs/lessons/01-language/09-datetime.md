# 09 · `datetime` — `java.time` correcto

**Sección:** `01-language` · **Paquete:** `dev.raul.oskar.learning.kotlin.datetime` · **Caso:** Ambos
**Enlaces:** [temario](../../Temario.md#01--datetime--javatime-correcto) · [ejercicios](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/datetime/Exercises.md) · [código](../../../01-language/src/main/kotlin/dev/raul/oskar/learning/kotlin/datetime/) · [pruebas](../../../01-language/src/test/kotlin/dev/raul/oskar/learning/kotlin/datetime/DateTimeTest.kt)

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

## Estado

- **Hecho:** código en los casos (B0′ tanda 4) y 8 pruebas; 24 ejercicios (5 🎯); 0 resueltos en `practice/`.
- **Falta:** B2 (⚡ velas por sesión).
