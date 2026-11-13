# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A Kotlin learning project for a Java developer: runnable lessons, tests, and graded exercises (no solutions),
aimed at backend web and mobile development. **Code in English, comments and docs in Spanish** (same convention as
`java-champion-review`): identifiers, packages, directories and test names in English; README, `Exercises.md`, code
comments and KDoc in Spanish. `README.md` is the source of truth for lesson tables, exercise counts, and the "Ruta
prioritaria". The repo is meant to be public: never mention specific companies or hiring processes.

## Versions are pinned on purpose

Kotlin 2.4.21 (plugins `jvm`, `serialization`, `jpa`, `allopen`), kotlinx-serialization-json 1.13.0, Kermit 2.2.0,
JDK 21 toolchain (`.sdkmanrc`), Gradle 9.9.1 wrapper; `04-data` adds Hibernate 7.4.15, H2 2.5.252 and the JDBC
drivers, JUnit BOM 5.14.4. `docs/Versions.md` is the source of truth for versions and how to bump them. Do not
upgrade ad hoc; versions are repeated in each section's `build.gradle.kts`, keep them in sync. The legacy tracks
(`20`–`22`) pin their own Kotlin on purpose (1.4.32, 1.6.21, 1.9.22).

## Commands

```bash
./gradlew test                                                # all sections (root composite)
./gradlew -p 01-language test                                 # one section
./gradlew -p 01-language test --tests '*.strings.*'           # one lesson (includes its practice/)
./gradlew -p 01-language test --tests '*.strings.practice.*'  # only the user's exercise solutions
./gradlew -p 01-language test --tests '*.StringsTest'         # a single test class
./gradlew -p 01-language run -Plesson=basics                  # run a lesson's Main.kt (default: basics)
./gradlew -p 01-language run -Plesson=io --console=plain      # io reads stdin; plain console avoids progress-bar noise
./gradlew -p 04-data run -Plesson=jdbc.postgres               # default in 04-data: jdbc.sqlite
./gradlew -p 04-data run -Plesson=jpa --args=postgres         # JPA against Docker Postgres (drop-and-create)
cd 22-legacy-kotlin19 && ./gradlew test                       # Kotlin 1.9 legacy track (own wrapper, not in composite)
```

`-Plesson=<subpackage>` sets `mainClass` to `dev.raul.oskar.learning.kotlin.<lesson>.MainKt` (see each section's `build.gradle.kts`).
`run` forwards stdin and uses the project root as working dir. `jdbc` itself has no `Main` (only `Demo.kt`), so use
`jdbc.sqlite`/`jdbc.postgres`/`jdbc.mysql`.

Databases (optional):

```bash
docker compose -f docker/postgres/compose.yaml up -d --wait   # PostgreSQL 17.2 on 127.0.0.1:15432
docker compose -f docker/mysql/compose.yaml up -d --wait      # MySQL 8.4.3 on 127.0.0.1:13306
docker compose -f docker/<db>/compose.yaml down -v            # also wipes the volume
```

DB/user/password are all `learning`; override with `PG_URL`/`PG_USER`/`PG_PASSWORD` and
`MYSQL_URL`/`MYSQL_USER`/`MYSQL_PASSWORD`. If a container is down its tests are **SKIPPED**, not failed, so
`./gradlew test` always runs. SQLite (in-memory) and JPA (H2 in-memory) need no Docker.

## Architecture

- **Monorepo of independent projects**, as in `java-champion-review` and `groovy-for-java-devs`. Root
  `settings.gradle.kts` is a composite build that only `includeBuild`s the sections that have a build (today
  `01-language` and `04-data`); root `build.gradle.kts` registers aggregate `build`/`test`/`check`/`clean`.
  Legacy tracks (`22-legacy-kotlin19` today) stay **outside** the composite with their own wrapper, Kotlin version and
  `.sdkmanrc`; run them from their folder and never bump them. `Legacy19SmokeTest` fails on purpose if that track stops
  running Kotlin 1.9.22 / K1. Each
  section has its own `settings.gradle.kts` and `build.gradle.kts`, no shared parent; versions are repeated on
  purpose, keep them in sync. `--tests` and `-Plesson` only work inside a section (`-p <section>`). Empty
  sections hold a `.gitkeep`; the full layout and roadmap are in `Plan-Migracion-Monorepo.md`.
- `01-language`: `basics`, `control`, `strings`, `oop`, `collections`, `functional`, `exceptions`, `io`, `datetime`,
  `json`. `04-data`: `jdbc.*` and `jpa`. `run` uses the section directory as working dir.
- Root package `dev.raul.oskar.learning.kotlin`; one subpackage per lesson (`basics`, `control`, `strings`, `oop`,
  `io`, `collections`, `functional`, `json`, `datetime`, `jdbc.*`, `jpa`, `exceptions`). Each has a `Main.kt`
  (demo only), logic in separate `.kt` files so it is testable, an `Exercises.md`, and a mirrored
  `src/test/.../<lesson>/<Lesson>Test.kt` using `kotlin.test` on JUnit 5.
- `jdbc/` root holds shared code: `ProductRepository`, the `Dialect` interface, `mapRows`/`inTransaction`
  extensions, and `runDemo`. `jdbc/{sqlite,postgres,mysql}` each supply only a `*Dialect` and `Main`. Tests
  subclass the abstract `ProductRepositoryContract`, providing `dialect` and `openConnection()`; returning `null`
  triggers a JUnit `assumeTrue` skip. Engine differences belong in the dialect, not the repository. The three
  engines share one `jdbc/Exercises.md` with exercises tagged `[todos]`/`[SQLite]`/`[PG]`/`[MySQL]`.
- `jpa/` is Hibernate 7 without Spring: `EntityManagerFactory` built by hand from `META-INF/persistence.xml`
  (unit `learning`, H2 by default). `h2Factory(name)` gives each test its own in-memory DB; `postgresFactory()`
  targets Docker; `inTransaction`, `statementCount()` and `resetStatistics()` (for N+1 demos) are extensions in
`jpa/Jpa.kt`.
- Sample data for io/json lessons is on the classpath: `01-language/src/main/resources/data/{sales.csv,orders.json}`.

## Exercise conventions

- Solutions go in `<section>/src/main/kotlin/.../<lesson>/practice/ExNN<Name>.kt`, tests in the mirrored
  `<section>/src/test/kotlin/.../<lesson>/practice/ExNN<Name>Test.kt`. Follow the signature and minimum cases from the
  exercise statement.
- If two exercises in the same lesson define the same function name, wrap each in `object ExNN { … }`.
- "Explica en un comentario" answers go in the test, next to the case that demonstrates it.
- Unimplemented stubs use `TODO("ejercicio NN")`. Don't write solutions unless asked; this is the user's practice.
  Tests against a stub fail with `NotImplementedError` on purpose; that is how pending exercises show up.
- `practice/` packages don't exist until the user solves the first exercise in that lesson.
- When adding or removing tests or exercises, update the counts in `README.md` ("Estado actual: N pruebas", the
  number of exercises per lesson, and the total).
- Legend: 🟢 easy · 🟡 medium · 🟠 hard · 🔴 very hard · 🎯 priority, the recommended route (★ = do first).

## Adding a lesson

In the right section, create `<topic>/Main.kt` with `fun main()`, put logic in other files, add `<Topic>Test.kt`, write `Exercises.md`
with the same header as the others (20–30 graded exercises with signature and cases), and add the row to the
README lessons table.

## Known gotchas (already handled — don't "fix" them away)

- Keep `platform("org.junit:junit-bom:…")` and `testRuntimeOnly("org.junit.platform:junit-platform-launcher")`: with
  Kotlin 1.9.22 (the `22-legacy-kotlin19` track) tests fail without them ("Failed to load JUnit Platform").
- Kotlin `split(String)` is literal, not regex (`"03/14/1985".split("\\/")` returns the whole string) — a common bug
  when porting Java code, demonstrated in `strings/`.
- `explicitNulls` needed `@file:OptIn(ExperimentalSerializationApi::class)` in kotlinx-serialization 1.6.2; it is
  stable in 1.13. `kotlin.io.path.deleteRecursively` still needs `@OptIn(ExperimentalPathApi::class)` in 2.4.
- `CollectionsTest` compares arrays with `==` on purpose (with `@Suppress`) to demonstrate the trap; K2 warns about it.
- Hibernate logs via java.util.logging unless `org.jboss.logging.provider=slf4j` is set before Hibernate loads
  (`jpa/Jpa.kt`); `simplelogger.properties` keeps SLF4J at WARN.
- JPA entities must not be `data class`; the `jpa` (no-arg) and `allopen` plugins are configured for
  `@Entity`/`@MappedSuperclass`/`@Embeddable`.
- MySQL `ON DUPLICATE KEY UPDATE` consumes `AUTO_INCREMENT` values even when it updates.
- `*.code-workspace` is git-ignored on purpose (personal VS Code settings), as in `java-champion-review`.
- Don't commit unless the user asks. Commit messages in English, with no `Co-Authored-By` trailer.
