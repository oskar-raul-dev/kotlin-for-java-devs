# Casos de estudio: Banco y Mercado

Diseño de los dos casos de punta a punta que reemplazan al dominio provisional de pedidos de tienda (paso B0′ del
[plan](../Plan-Migracion-Monorepo.md#11-caso-de-punta-a-punta-ideas-de-reemplazo)). Este documento se revisa
**antes** de tocar código; las lecciones y los ejercicios lo toman como referencia. Escrito el **13 de noviembre de
2026**; lo marcado con ⚠️ se decide al implementar.

**Salto rápido:** [Decisiones](#decisiones) · [Convenciones comunes](#convenciones-comunes) · [Banco](#caso-a-banco) ·
[Mercado](#caso-b-mercado) · [Puente](#el-puente-liquidación) · [Datos](#datos) · [Por lección](#qué-usa-cada-lección) ·
[Ruta 🎯](#la-ruta-prioritaria) · [Reemplazo](#plan-de-reemplazo) · [Fuera](#fuera-de-alcance)

## Decisiones

| # | Decisión |
|---|---|
| 1 | **Dos casos con un puente**: Banco (transaccional y de dominio) y Mercado (asíncrono, de flujo y de cálculo). Cada ejecución del mercado se liquida como asientos en el ledger del banco |
| 2 | **Alcance del mercado**: feed de cotizaciones, velas OHLC, libro de órdenes con emparejamiento y P&L del portafolio. Indicadores y backtesting como ⚡. **Sin** derivados, margen ni ventas en corto |
| 3 | **Moneda**: base en **USD**. Multimoneda solo en los ejercicios de tipos de cambio (USD, EUR, MXN) |
| 4 | **Ruta 🎯 de `01-language`**: bloques 1 a 4 con el Mercado (leer, limpiar, agrupar, ventanas, estadística); bloque 5 (errores y diseño) con el Banco |
| 5 | **Todo es ficticio y sintético**: entidades, tickers y datos inventados, generados con semilla fija. Nada de datos de mercado reales (licencias y repo público). Los códigos de moneda ISO sí son reales |

## Convenciones comunes

### `Money`

```kotlin
data class Money(val amount: BigDecimal, val currency: Currency) : Comparable<Money>
```

- **`data class`, no `value class`**: una `value class` solo envuelve una propiedad y aquí son dos (monto y moneda).
  Que una `value class Money` no pueda llevar la moneda es en sí un ejercicio de `oop` 🎤.
- **Escala normalizada en el `init`** a la de la moneda (`Currency.defaultFractionDigits`: 2 para USD y EUR). Así se
  evita la trampa de Java que también vive en Kotlin: `BigDecimal("1.0") == BigDecimal("1.00")` es `false`
  (`equals` compara la escala; `compareTo` no).
- Operadores `plus`, `minus`, `times` (por cantidad) y `unaryMinus`; sumar monedas distintas lanza
  `IllegalArgumentException` con `require`.
- Redondeo **`HALF_EVEN`** (bancario) por defecto; `HALF_UP` aparece solo para comparar.
- Se construye desde `String` o `Long` en centavos, **nunca desde `Double`**: `BigDecimal(0.1)` frente a
  `BigDecimal("0.1")` es una trampa de `basics`.

### Tiempo

- Instantes en UTC (`Instant`); se muestran en la zona del mercado o del cliente solo al final.
- Mercado en **`America/New_York`**, de 09:30 a 16:00, de lunes a viernes, con un calendario de feriados propio y
  ficticio. Da las trampas de horario de verano (marzo y noviembre) y de "el día de mercado no es el día UTC".
- El banco cierra el mes en `America/New_York` también ⚠️ (una sola zona simplifica; los clientes en otras zonas son
  ejercicio de `datetime`).
- `Clock` inyectado en todo lo que dependa de "ahora", para poder probarlo.

### Identificadores

- **Cuentas:** `ACC-` + 8 dígitos + 2 dígitos de control calculados como en el IBAN:
  `control = 98 − (número × 100 mod 97)`. Ejemplos: `ACC-00001234-82`, `ACC-00005678-39`. Validarlos es ejercicio de
  `strings`.
- **Tickers inventados:** `ACME`, `ORBX`, `LUMA`, `KAPA`, `NUBE`. Si alguno coincide con uno real, es casualidad.
- **Órdenes, ejecuciones y transferencias:** `ORD-…`, `EXE-…`, `TRF-…` con número creciente; las transferencias
  llevan además una **clave de idempotencia** (UUID) que manda el cliente.

## Caso A: Banco

Un núcleo bancario mínimo: el libro mayor de doble partida, las transferencias y la conciliación.

### Entidades

| Entidad | Qué es | Invariantes |
|---|---|---|
| `Account` | Cuenta del cliente o interna del banco. Tipo (`ASSET`, `LIABILITY`, `INCOME`, `EXPENSE`), moneda, estado (`OPEN`, `FROZEN`, `CLOSED`) | La moneda no cambia; una cuenta `CLOSED` no recibe asientos |
| `JournalEntry` | Asiento: fecha, descripción y dos o más `Posting` | **Débitos = créditos** en cada moneda; no se borra ni se edita: se corrige con un asiento inverso |
| `Posting` | Línea de un asiento: cuenta, `DEBIT`/`CREDIT`, `Money` | Monto positivo; la moneda es la de la cuenta |
| `Transfer` | Orden de transferencia entre dos cuentas, con clave de idempotencia | La misma clave devuelve el mismo resultado sin duplicar; el saldo disponible **nunca queda negativo** |
| `StatementLine` | Línea del extracto externo (CSV) para conciliar | — |
| `MonthClose` | Cierre de mes: intereses, comisiones, saldos finales | Después del cierre, los asientos de ese mes son de solo lectura |

El **saldo** de una cuenta no se guarda como campo editable: es la suma de sus postings (y, como optimización que se
discute en `04-data`, un saldo materializado con bloqueo optimista o `SELECT … FOR UPDATE`).

### Lo que enseña

- `BigDecimal` y redondeo, `Money` con operadores, `require`/`check` para las invariantes (`basics`, `oop`).
- Jerarquía sellada de errores: `InsufficientFunds`, `AccountFrozen`, `CurrencyMismatch`, `DuplicateTransfer`,
  `UnbalancedEntry`, y su traducción a HTTP (`exceptions`, `09-spring`).
- Transacciones, aislamiento y bloqueos: la actualización perdida en un saldo, `REPEATABLE READ` (MySQL) frente a
  `READ COMMITTED` (PostgreSQL), bloqueo optimista con `@Version` frente a pesimista (`04-data`).
- Idempotencia, reintentos y el patrón outbox (`09-spring`, `07-design`).
- Conciliación: emparejar el extracto con el ledger con tolerancias de fecha y monto, y acumular las diferencias en
  un reporte (`io`, `strings`, `datetime`, `exceptions`).

## Caso B: Mercado

Una bolsa ficticia con cinco tickers: cotizaciones en vivo, un libro de órdenes y el portafolio de un cliente.

### Entidades

| Entidad | Qué es | Invariantes |
|---|---|---|
| `Tick` | Cotización: ticker, instante, precio, volumen; a veces bid/ask | Precio > 0; en el feed crudo puede venir desordenado, duplicado o con nulos (hay que limpiarlo) |
| `Candle` | Vela OHLC de un intervalo (1 min, 1 día): apertura, máximo, mínimo, cierre, volumen, VWAP | `low ≤ open, close ≤ high` |
| `Order` | `sealed`: `LimitOrder` (precio) y `MarketOrder`; lado `BUY`/`SELL`; cantidad entera | Cantidad > 0; precio con 2 decimales (tick de 0.01) |
| `OrderBook` | Órdenes en espera por ticker: compras de mayor a menor precio y ventas de menor a mayor, y FIFO dentro de cada precio | Nunca queda cruzado: la mejor compra está por debajo de la mejor venta |
| `Execution` | Ejecución: compra, venta, precio, cantidad, instante | Precio de la orden que estaba en espera; ejecuciones parciales permitidas |
| `Position` | Posición del cliente por ticker: cantidad y costo | Cantidad ≥ 0 (sin cortos) |
| `Portfolio` | Posiciones más efectivo; P&L realizado y no realizado | Costo por **promedio ponderado** o **FIFO** según la lección (comparar los dos es ejercicio) |

### Lo que enseña

- Leer y limpiar un feed JSON con nulos, claves desconocidas y eventos polimórficos (`json`).
- Agregaciones: velas con `groupBy` y `fold`, VWAP, volatilidad y medias móviles con `windowed`, estadística en una
  pasada (`functional`, `collections`).
- El libro de órdenes con `TreeMap` y `ArrayDeque`, y el emparejamiento por precio-tiempo (`collections`, `oop`).
- Horario de mercado, días hábiles y zonas con horario de verano (`datetime`).
- El feed como `Flow` con ráfagas: `buffer`, `conflate`, `sample` y backpressure; el motor de emparejamiento como
  actor de un solo escritor sobre un `Channel` (`02-coroutines`, `03-reactive`).
- Precios en vivo por WebSocket y alertas por suscripción (`10-ktor`, `11-multiplatform`, `12-android`).
- Pruebas basadas en propiedades sobre el libro de órdenes: "nunca queda cruzado", "la cantidad se conserva"
  (`05-testing`).

## El puente: liquidación

Cada `Execution` del mercado genera un `JournalEntry` en el banco, desde el punto de vista del cliente:

```text
Compra de 10 ACME a 100.00 USD, comisión 1.00 USD

  DEBIT   Inversiones:ACME      1,000.00 USD
  DEBIT   Gastos:Comisiones         1.00 USD
  CREDIT  Efectivo              1,001.00 USD
                                ------------
          débitos = créditos    1,001.00 USD
```

- La venta es el asiento inverso, más la ganancia o pérdida realizada contra `Ingresos:Ganancias` o
  `Gastos:Pérdidas`.
- La cantidad de títulos vive en `Position` (mercado); el ledger solo lleva el **valor** en USD.
- La compra exige saldo: si el efectivo no alcanza, la orden se rechaza **antes** de entrar al libro. Ese chequeo
  cruza los dos casos y es la base del 🐉 full-stack (backend + app).

## Datos

Todo en `src/main/resources/data/` de la sección que lo use, generado por un programa con **semilla fija**
(reproducible). Dónde vive el generador ⚠️: propuesta, un `Main` aparte en `01-language`
(`./gradlew -p 01-language run -Plesson=datagen`), sin agregar herramientas nuevas.

| Archivo | Caso | Contenido | Imperfecciones a propósito |
|---|---|---|---|
| `ticks.json` | Mercado | ~2,000 ticks de los cinco tickers en 10 días hábiles | Nulos, claves desconocidas, duplicados, desorden, un hueco, un tick fuera de horario, el cambio de horario de noviembre |
| `orders.ndjson` ⚠️ | Mercado | Secuencia de órdenes para alimentar el libro | Órdenes que cruzan, parciales, cancelaciones |
| `bank-statement.csv` | Banco | Extracto externo de un mes | Fechas con un día de desfase, montos con centavos de diferencia, una línea sin pareja, comas en la descripción |
| `ledger-seed.json` ⚠️ | Banco | Cuentas y asientos iniciales para `04-data` | — |
| `fx-rates.json` | Banco | Tasas USD/EUR/MXN por día | Un día sin tasa (hay que tomar la anterior) |

Reemplazan a `orders.json` y `sales.csv`, que se borran al final del reemplazo.

## Qué usa cada lección

### Lecciones que ya existen

| Lección | Hoy (pedidos) | Pasa a | Caso |
|---|---|---|---|
| `basics` | `BigDecimal` genérico | `Money`: escala, `HALF_EVEN`, `equals` frente a `compareTo`, nunca desde `Double` | Banco |
| `control` | Tramos de monto y de edad | Tramos de comisión por volumen; búsqueda binaria del precio en un libro ordenado | Ambos |
| `strings` | Fecha MM/dd/yyyy, zip, código de estado | Validar `ACC-…` con mod 97; parsear mensajes estilo FIX (`35=D\|55=ACME\|54=1\|38=10\|44=100.00`) con regex. La trampa de `split("\\/")` se queda | Ambos |
| `oop` | `ShippingMethod`, `data class` de pedidos | `sealed` `Order` (límite y mercado), `enum` de tipos de cuenta con `of`, `Money` como `data class`, operadores | Ambos |
| `io` | `sales.csv` | `bank-statement.csv`: leer con `useLines`, escribir el reporte de conciliación | Banco |
| `collections` | Conteos por categoría y día | El libro de órdenes (`TreeMap` + `ArrayDeque`), ventana de 7 días de cierres, `PriorityQueue` de alertas | Mercado |
| `functional` | `groupBy` de pedidos, `Order` de ejemplo de scope functions | Velas con `groupBy` + `fold`, VWAP, P&L con `fold`, pipeline de limpieza del feed | Mercado |
| `json` | `OrderEvent`, `Address`, `Payment` sellado | `Tick` con `explicitNulls`, `MarketEvent` sellado (`Trade`, `Quote`, `Halt`), `Money` con serializador propio | Mercado |
| `datetime` | Edad, epoch, ventana de días | Horario de mercado en Nueva York, horario de verano, días hábiles con feriados, cierre de mes | Ambos |
| `exceptions` | `OrderException`, `OrderService`, inventario | `BankException` sellada, `TransferService`, validación que acumula errores, traducción a HTTP | Banco |
| `jdbc` (los 3 motores) | `ProductRepository` | `LedgerRepository`: cuentas, asientos y postings; transferencia en una transacción; actualización perdida y `FOR UPDATE`; diferencias de aislamiento entre motores | Banco |
| `jpa` | `Customer`, `PurchaseOrder`, `OrderLine` | `Account` con `@Version`, `JournalEntry` con `Posting` (cascade, `orphanRemoval`), N+1 al listar movimientos | Banco |

`OrderStatus`, `ShippingMethod`, `Customer` y compañía desaparecen. El contrato de `jdbc`
(`ProductRepositoryContract`) se rehace como `LedgerRepositoryContract` con las mismas reglas: una batería, tres
motores, diferencias solo en el `Dialect`.

### Secciones de la fase B

| Sección | Banco | Mercado |
|---|---|---|
| `02-coroutines` | Transferencias concurrentes con `Mutex`; reintentos con `delay` | Feed como `Flow`, ráfagas y backpressure; libro como actor sobre `Channel`; `StateFlow` del último precio |
| `03-reactive` | — | El mismo pipeline del feed en Reactor, RxJava y `Flow` |
| `04-data` (nuevas) | El ledger en Exposed, jOOQ y R2DBC | Velas guardadas y consultadas con funciones de ventana SQL |
| `05-testing` | Testcontainers con el ledger | Propiedades del libro de órdenes con Kotest |
| `07-design` | DDD: agregado `Account`, eventos de dominio, outbox | Hexagonal: el motor de emparejamiento sin dependencias |
| `09-spring` | API REST de transferencias con idempotencia | Reporte de portafolio |
| `10-ktor` | — | WebSocket de precios y alertas |
| `11-multiplatform`, `12-android` | Saldo y movimientos | Portafolio, precios en vivo y alertas |
| 🐉 full-stack | Backend que liquida y registra | Cliente que opera y ve su portafolio |

Las ideas de la sección 11 del plan que no son un caso propio quedan como ⚡: el arbitraje triangular (ciclo
negativo en un grafo de tasas) en `control` o `collections`, e indicadores con backtesting en `functional`.

## La ruta prioritaria

Misma forma de hoy (5 bloques, ~43 ejercicios 🎯, ~15 ★), con otro tema:

| Bloque | Hoy | Pasa a |
|---|---|---|
| 1 · Leer los datos | `orders.json`, `sales.csv`, `data class` de pedidos | `ticks.json` con `@SerialName` y `explicitNulls`; `data class Tick` |
| 2 · Nulos, cadenas y fechas | Nulos de pedidos, fecha MM/dd/yyyy, edad | Ticks incompletos, mensajes FIX, instante del tick en la zona del mercado, día hábil |
| 3 · Categorías y conteos | Conteo por categoría y día | Volumen por ticker y día, velas diarias con `groupBy` + `fold` |
| 4 · Ventanas y estadística | Ventana de 7 días, estadística en una pasada | Media móvil de 7 días de cierres, VWAP y volatilidad en una pasada |
| 5 · Errores y diseño | `!!` en un PR, `require`, jerarquía sellada | Lo mismo sobre una transferencia del Banco: `require` en `Money`, `BankException` sellada a HTTP |

Los números de ejercicio cambian con el reemplazo; la tabla del README se rehace al terminar.

## Plan de reemplazo

Por tandas, cada una con las pruebas en verde (con PostgreSQL y MySQL arriba) y su commit:

1. **Datos y generador:** `ticks.json`, `bank-statement.csv`, `fx-rates.json` y el `Main` generador.
2. **`json` e `io`:** modelo `Tick`/`MarketEvent`, conciliación del extracto.
3. **`basics`, `oop` y `exceptions`:** `Money`, `Order` sellado, `BankException`, `TransferService`.
4. **`strings`, `collections`, `functional`, `datetime`, `control`:** los ejemplos y pruebas que mencionan pedidos.
5. **`04-data`:** `LedgerRepository` con su contrato en los tres motores; entidades JPA del ledger.
6. **Ejercicios y ruta 🎯:** reescribir los enunciados afectados (hoy mencionan pedidos sobre todo `oop`, `json` y
   `jdbc`), rehacer la ruta del README y borrar `orders.json` y `sales.csv`.

El número de pruebas puede cambiar (las de pedidos se reemplazan, no se traducen una a una): el README y el
`CHECKLIST.md` se actualizan en cada tanda. Conviene terminar esto **antes de B2** (ejercicios a la nueva escala)
para no reescribir los enunciados dos veces.

## Fuera de alcance

- Derivados, margen, ventas en corto y cesión temporal de títulos.
- Impuestos, regulación, KYC y antilavado.
- Protocolos reales de mercado (FIX completo, ITCH): solo un subconjunto "estilo FIX" para practicar parseo.
- Datos reales de mercado o de bancos.

## Pendiente de decidir

- [ ] Dónde vive el generador de datos (propuesta: `Main` en `01-language`, lección `datagen`).
- [ ] Formato de las órdenes para el libro: `orders.ndjson` o generadas en las pruebas.
- [ ] Si el cierre de mes usa la zona del mercado o una zona por cliente.
- [ ] Si `ledger-seed.json` hace falta o `04-data` siembra los datos en código, como hoy `jpa/Seed.kt`.
