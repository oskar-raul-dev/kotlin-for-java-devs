# Decisiones del curso

Las decisiones de diseño del curso, con su estado y fecha. Vienen del plan de migración al monorepo (fases A y B0),
que se cerró el 22 de noviembre de 2026; el plan completo se puede leer en git (`git show migration-closed^:Plan-Migracion-Monorepo.md`).
El avance y el orden de lo que falta están en [`CHECKLIST.md`](../CHECKLIST.md#fase-b).

## Decisiones

| # | Decisión | Estado |
|---|---|---|
| 1 | Un solo repo git con varios proyectos (monorepo), como `java-champion-review` y `groovy-for-java-devs` | ✅ acordado |
| 2 | **Repo público**: sin referencias a empresas ni procesos de selección; 🎯 pasa a significar "prioritario" | ✅ hecho en el curso v1 |
| 3 | Ejercicios con la escala de `java-champion-review`: 🟢 🟡 🟠 🔴 ⚡ 🐉 🎤 🎯, de 25 a 40 por lección más su ⚡ | ✅ acordado |
| 4 | Proyecto base en la **última Kotlin estable** y tracks legacy aparte, cada uno con su compilador real | ✅ acordado |
| 5 | Los tracks legacy corren en **contenedores** (idea de `groovy-for-java-devs/docs/LegacyJdks.md`) y también en nativo | ✅ probado con el track 19 (A8), en contenedor y en nativo |
| 6 | El repo se sigue llamando `kotlin-for-java-devs`, igual que `groovy-for-java-devs` | ✅ acordado |
| 7 | Primero se **mueve sin cambiar comportamiento** (Kotlin 1.9.22) y se prueba, corrigiendo solo lo necesario; la migración a 2.x va en otra sesión | ✅ acordado |
| 8 | **Código en inglés, comentarios y documentos en español**, como en `java-champion-review` | ✅ revisado en el curso v1 |
| 9 | Git con `user.name` y `user.email` locales del repo; mensajes de commit en inglés | ✅ hecho |
| 10 | `kotlin-for-java-devs.code-workspace` copiado de `java-champion-review` e ignorado por git (ajustes personales de VS Code) | ✅ hecho |
| 11 | El caso de punta a punta pasa a **dos casos de finanzas con un puente**: Banco (ledger de doble partida, transferencias, conciliación) y Mercado (feed de precios, velas, libro de órdenes, P&L); cada ejecución se liquida en el ledger. USD como base, sin derivados ni cortos | ✅ acordado el 13 nov 2026; diseño en [`docs/Casos.md`](Casos.md) |
| 12 | Nueva sección **`13-spring-api`**: la API de transferencias del Banco como proyecto guiado en **dos sabores**, `mvc` (Spring MVC, bloqueante) y `reactive` (WebFlux + corrutinas + R2DBC), con el mismo contrato de pruebas. Se puede hacer uno, los dos o ninguno. Cubre Spring Security, springdoc-openapi, Actuator, `@ConfigurationProperties`, beans, reintentos, caché y Testcontainers | ✅ acordado el 22 nov 2026 |
| 13 | Caché: **EhCache 3 por JCache** (con nivel *off-heap*) en `mvc` y **Caffeine asíncrona** en `reactive` (la caché de Spring con `suspend`/`Mono` necesita una caché asíncrona); **Valkey** como caché distribuida en los dos (Spring Data Redis con Lettuce, Docker en el puerto 26379) | ✅ acordado el 22 nov 2026 |
| 14 | `04 · r2dbc` se adelanta de B8 a **B6**, porque `13-spring-api/reactive` lo usa | ✅ acordado el 22 nov 2026 |
| 15 | Las órdenes del libro no son un archivo: `datagen.orderStream(seed, count)` las genera con semilla; los casos chicos van escritos en cada prueba | ✅ 22 nov 2026 |
| 16 | Los tutoriales se numeran en orden de lectura (`tutorials/01-install` … `05-debug`); los paquetes de Kotlin conservan el nombre sin número | ✅ 22 nov 2026 |
| 17 | Las guías de `docs/lessons/` son **guías de estudio**: teoría (más profunda en los temas largos o difíciles), comparación con Java 21, mapa del código y fuentes verificadas; desde B2, cada lección nueva nace con código y guía | ✅ 22 nov 2026 |
| 18 | Fuentes de las guías solo en inglés o español (o con subtítulos fiables en inglés); inglés primero, español marcado *(es)* | ✅ 22 nov 2026 |
| 19 | Puertos de las bases en el rango 20000 (PostgreSQL 25432, MySQL 23306, Valkey 26379) para no chocar con otros proyectos locales | ✅ 22 nov 2026 |
| 20 | El plan de migración (`Plan-Migracion-Monorepo.md`) se cierra y se borra: las decisiones viven aquí, el orden de la fase B en `CHECKLIST.md` y el contenido de cada lección en `docs/Temario.md`. El plan completo queda en el tag `migration-closed^` | ✅ 22 nov 2026 |
| 21 | **Rutas de estudio** en `docs/Rutas.md`: la corta (la ruta 🎯 del curso desde su primera versión, en el README), lenguaje completo, backend sin Spring Boot (Ktor), Spring reactivo, Spring MVC, móvil, concurrencia, legacy, entrevista y completa; las lecciones que faltan se marcan con su fase | ✅ 22 nov 2026 |

El punto 7 importa: si mover y actualizar van juntos, cuando algo falle no se sabe si fue el movimiento o la
versión. Además, la lista de lo que se rompe al pasar de 1.9.22 a 2.x sirve de contenido para el track de 1.9.

## Preguntas de la revisión de B0, ya respondidas

1. ¿Los tres tracks legacy (**1.4.32**, **1.6.21** con Boot 2.7, **1.9.22**) o prefieres otro corte? Los tres (22 de noviembre de 2026).
2. ¿Numeración y nombres de secciones como estaban en el plan? Como están, con los huecos 13–19 (22 de noviembre de 2026); el `13` lo toma después `13-spring-api` (decisión 12).
3. ¿`io`, `datetime` y `json` se quedan en `01-language`, como `02-language` del repo de Java, o van a una sección
   `02-stdlib` aparte? Se quedan en `01-language` (22 de noviembre de 2026).
4. ¿Tienes Android SDK y Xcode instalados? Define el alcance de B7. Sí (medido el 22 de noviembre de 2026: Android
   SDK con emulador en `~/Library/Android/sdk` y Xcode 27.2): el alcance incluye iOS.
5. ¿Qué caso de finanzas reemplaza al dominio provisional? Dos casos, Banco y Mercado, con puente: [`docs/Casos.md`](Casos.md).
6. ¿Arranco con A2–A5? → Hecho el 10 de noviembre de 2026.
7. **Guías de lección**: ¿carpetas por sección o lista plana? ¿Antes de B0′ o dentro de cada tanda? Carpetas por
   sección (`docs/lessons/01-language/01-basics.md`), escritas antes de B0′ (22 de noviembre de 2026).
