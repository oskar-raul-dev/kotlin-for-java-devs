# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A Kotlin course for a senior Java developer, aimed at **backend web** (Spring, WebFlux, Ktor) and **mobile**
(Android, Kotlin Multiplatform). **Kotlin 2.4 is the base**; legacy tracks run the real 1.4 / 1.6 / 1.9 compilers.
Each lesson has runnable code, tests, and graded exercises **without solutions**. It follows the format of
`~/Developer/Learning/Java/java-champion-review` and `~/Developer/Learning/Groovy/groovy-for-java-devs`
(monorepo of independent projects).

The repo is **public on GitHub**: never mention specific companies, clients or hiring processes anywhere (code,
docs, tests, exercises, commit messages).

Phase A (migration to the monorepo, Kotlin 2.x, legacy track 19 and its container) is done. Phase B (new content) is
next; its order is in `Plan-Migracion-Monorepo.md`.

## Documents and what each one owns

| Document | Source of truth for |
|---|---|
| `README.md` | Section index, lesson table, exercise counts, test count ("Estado actual"), "Ruta prioritaria" |
| `CHECKLIST.md` | Progress per section and lesson, and the dated log ("Bitácora") |
| `Plan-Migracion-Monorepo.md` | Roadmap, decisions, phase A/B steps, open questions |
| `docs/Versions.md` | Every pinned version (base, per section, legacy tracks, container images) and how to bump them |
| `docs/LegacyKotlin.md` | What changed from Kotlin 1.0 to 2.x, the three legacy tracks, how to run them |
| `docs/Casos.md` | The two case studies (Bank: ledger/transfers/reconciliation; Market: ticks/candles/order book/P&L) and which lesson uses which |
| `docs/Temario.md` | Per-lesson syllabus (Java → Kotlin mapping, traps); rewritten in phase B0 |
| `docs/Temario-Plan.md` | How B0 is written: lesson template, batches (tandas), measured version tags (from `scripts/version-probes.sh`), status. Each batch runs the master prompt in `docs/prompts/temario-tanda.md`; the old syllabus is in `docs/archive/Temario-v1.md` |
| `22-legacy-kotlin19/docs/k2-notes.md` | What actually changed when the base moved from 1.9.22 (K1) to 2.4.21 (K2) |

**Sync rules** — update in the same change:

- New or removed tests/exercises → README counts ("Estado actual: N pruebas", per-lesson and total exercises).
- Finished step or lesson → `CHECKLIST.md` (status + log entry) and, for plan steps, the plan's status section.
- Version change → every section's `build.gradle.kts` (no shared parent), `docs/Versions.md`, README "Versiones",
  and this file.
- New section with a build → root `settings.gradle.kts` `includeBuild` list (unless it is a legacy track or Android).

## Language rules

- **Code in English, comments and docs in Spanish.** Identifiers, packages, directories, modules and test names in
  English; comments, KDoc, `Exercises.md`, README, checklist and docs in Spanish. Commit messages in English.
- Root package `dev.raul.oskar.learning.kotlin`; one subpackage per lesson (`…kotlin.strings`, `…kotlin.jdbc.mysql`).

## Versions are pinned on purpose

| Where | Kotlin | Gradle | JDK | Notable libraries |
|---|---|---|---|---|
| Base: `01-language`, `04-data` | 2.4.21 | 9.9.1 (root wrapper) | 21 (toolchain, `.sdkmanrc`) | kotlinx-serialization 1.13.0, Kermit 2.2.0, Hibernate 7.4.15, H2 2.5.252, JUnit BOM 5.14.4 |
| `tutorials/` (no build) | 2.4.21 CLI (`.sdkmanrc`: `kotlin=2.4.21`) | — | 21 | Each tutorial has a `check.sh` that re-runs every command in its README; keep them in sync |
| `22-legacy-kotlin19` | 1.9.22 | 9.2.1 (own wrapper) | 21 | JUnit BOM 5.10.3 |
| `20-legacy-kotlin14`, `21-legacy-kotlin16` | 1.4.32, 1.6.21 | 6.8.x, 7.6.x ⚠️ | 11, 17 | Phase B5 |

Never upgrade a version ad hoc; ask first. Legacy tracks are **never** bumped: their old versions are the subject
of study (`Legacy19SmokeTest` fails on purpose if track 19 stops running Kotlin 1.9.22 / K1).

## Commands

```bash
./gradlew test                                                # base sections (root composite)
./gradlew -p 01-language test                                 # one section
./gradlew -p 01-language test --tests '*.strings.*'           # one lesson (includes its practice/)
./gradlew -p 01-language test --tests '*.strings.practice.*'  # only the user's exercise solutions
./gradlew -p 01-language test --tests '*.StringsTest'         # a single test class
./gradlew -p 01-language run -Plesson=basics                  # run a lesson's Main.kt (default: basics)
./gradlew -p 01-language run -Plesson=io --console=plain      # io reads stdin; plain console avoids progress-bar noise
./gradlew -p 04-data run -Plesson=jdbc.postgres               # default in 04-data: jdbc.sqlite
./gradlew -p 04-data run -Plesson=jpa --args=postgres         # JPA against Docker Postgres (drop-and-create)
cd 22-legacy-kotlin19 && ./gradlew test                       # legacy track 19, natively (own wrapper)
./scripts/legacy.sh 19 [gradle args | shell]                  # same, inside its JDK container (default: test)
for t in install cli repl decompile debug; do tutorials/$t/check.sh || break; done   # re-run every tutorial command (after `sdk env`)
scripts/version-probes.sh [name…]                             # in which Kotlin version each construct is stable (all SDKMAN kotlinc)
```

`-Plesson=<subpackage>` sets `mainClass` to `dev.raul.oskar.learning.kotlin.<lesson>.MainKt` (see each section's
`build.gradle.kts`). `run` forwards stdin and uses the **section directory** as working dir. `jdbc` itself has no
`Main` (only `Demo.kt`), so use `jdbc.sqlite`/`jdbc.postgres`/`jdbc.mysql`. `--tests` and `-Plesson` only work
inside a section (`-p <section>`).

Databases (optional):

```bash
docker compose -f docker/postgres/compose.yaml up -d --wait   # PostgreSQL 17.2 on 127.0.0.1:15432
docker compose -f docker/mysql/compose.yaml up -d --wait      # MySQL 8.4.3 on 127.0.0.1:13306
docker compose -f docker/<db>/compose.yaml down -v            # also wipes the volume
```

DB/user/password are all `learning`; override with `PG_URL`/`PG_USER`/`PG_PASSWORD` and
`MYSQL_URL`/`MYSQL_USER`/`MYSQL_PASSWORD`. If a container is down its tests are **SKIPPED**, not failed, so
`./gradlew test` always runs. SQLite (in-memory) and JPA (H2 in-memory) need no Docker. **Before committing, run
all tests with both databases up** (no SKIPPED).

## Architecture

- **Monorepo of independent projects.** Root `settings.gradle.kts` is a composite build that `includeBuild`s the
  base sections with a build (today `01-language` and `04-data`); root `build.gradle.kts` registers aggregate
  `build`/`test`/`check`/`clean`. Each section has its own `settings.gradle.kts` and `build.gradle.kts`, no shared
  parent; versions are repeated on purpose. Empty sections hold a `.gitkeep`.
- **Outside the composite:** legacy tracks `20`–`22` (own wrapper, Kotlin version and `.sdkmanrc`; open them as
  separate IntelliJ projects), `12-android` (AGP needs its own wrapper) and `26-kotlin-next` until 2.5 is stable.
- **Legacy containers:** `docker/legacy-kotlin/compose.yaml` has services `k14`/`k16`/`k19` (Temurin 11/17/21,
  pinned by multi-arch digest, native arm64), the repo mounted at `/work` and a `gradle-home` volume.
  `scripts/legacy.sh <14|16|19>` wraps `docker compose run --rm`. Only the JDK is containerized.
- **Build levels** (how complex each section's build is): 0 no build (`tutorials`) · 1 simple Gradle (`01`, `02`,
  `07`, `25`) · 2 Gradle + libraries/Docker (`03`–`06`) · 3 framework (`09`, `10`) · 4 multi-module/platforms
  (`11`, `12`) · 5 build tooling (`08`) · 6 legacy in a container (`20`–`22`).
- Lessons: `01-language` has `basics`, `control`, `strings`, `oop`, `collections`, `functional`, `exceptions`, `io`,
  `datetime`, `json`; `04-data` has `jdbc.*` and `jpa`. Each lesson has a `Main.kt` (demo only), logic in separate
  `.kt` files so it is testable, an `Exercises.md`, and a mirrored `src/test/.../<lesson>/<Lesson>Test.kt` using
  `kotlin.test` on JUnit 5.
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
  The end-to-end example domain (store orders) is **provisional**; it is being replaced by the Bank and Market
  cases designed in `docs/Casos.md` (step B0′). New examples and exercises must use those cases, all data fictional.

## Exercise conventions

- Solutions go in `<section>/src/main/kotlin/.../<lesson>/practice/ExNN<Name>.kt`, tests in the mirrored
  `<section>/src/test/kotlin/.../<lesson>/practice/ExNN<Name>Test.kt`. Follow the signature and minimum cases from the
  exercise statement.
- If two exercises in the same lesson define the same function name, wrap each in `object ExNN { … }`.
- "Explica en un comentario" answers go in the test, next to the case that demonstrates it.
- Unimplemented stubs use `TODO("ejercicio NN")`. Don't write solutions unless asked; this is the user's practice.
  Tests against a stub fail with `NotImplementedError` on purpose; that is how pending exercises show up.
- `practice/` packages don't exist until the user solves the first exercise in that lesson.
- Legend: 🟢 easy · 🟡 medium · 🟠 hard · 🔴 very hard · ⚡ Saiyajin (mini-project closing a lesson) · 🐉 final boss
  (crosses sections) · 🎤 interview question · 🎯 priority route (★ = do first) · `[2.0]` version where a feature
  became stable · `[exp N]`/`[dep N]`/`[rm N]` experimental/deprecated/removed in N. Today's exercises use only
  🟢🟡🟠🔴🎯; the rest arrive in phase B (target: 25–40 exercises per lesson plus its ⚡).

## Adding a lesson

In the right section, create `<topic>/Main.kt` with `fun main()`, put logic in other files, add `<Topic>Test.kt`,
write `Exercises.md` with the same header as the others (graded exercises with signature and cases), add the row to
the README lessons table and update `CHECKLIST.md`.

## Known gotchas (already handled — don't "fix" them away)

- Keep `platform("org.junit:junit-bom:…")` and `testRuntimeOnly("org.junit.platform:junit-platform-launcher")`: with
  Kotlin 1.9.22 (the `22-legacy-kotlin19` track) tests fail without them ("Failed to load JUnit Platform").
- The Kotlin 1.9.22 Gradle plugin uses Gradle APIs removed in Gradle 10: track 19 stays on Gradle 9.x.
- Kotlin `split(String)` is literal, not regex (`"03/14/1985".split("\\/")` returns the whole string) — a common bug
  when porting Java code, demonstrated in `strings/`.
- `explicitNulls` needed `@file:OptIn(ExperimentalSerializationApi::class)` in kotlinx-serialization 1.6.2; it is
  stable in 1.13. `kotlin.io.path.deleteRecursively` still needs `@OptIn(ExperimentalPathApi::class)` in 2.4.
- `CollectionsTest` compares arrays with `==` on purpose (with `@Suppress`) to demonstrate the trap; K2 warns about it.
- Lambda `toString()` differs: K1 gives `Function1<…>`, K2 (invokedynamic) gives `…$$Lambda/0x…`. Track 19's smoke
  test relies on it.
- `./gradlew --version` reports Gradle's embedded Kotlin (for build scripts), not the project's Kotlin.
- Hibernate logs via java.util.logging unless `org.jboss.logging.provider=slf4j` is set before Hibernate loads
  (`jpa/Jpa.kt`); `simplelogger.properties` keeps SLF4J at WARN.
- JPA entities must not be `data class`; the `jpa` (no-arg) and `allopen` plugins are configured for
  `@Entity`/`@MappedSuperclass`/`@Embeddable`.
- MySQL `ON DUPLICATE KEY UPDATE` consumes `AUTO_INCREMENT` values even when it updates.
- `*.code-workspace` and `.idea/` are git-ignored on purpose (personal IDE settings).

## Git

- Don't commit or push unless the user asks. Commit messages in English, with **no `Co-Authored-By` trailer**.
- Each plan step ends in its own commit, usually with an annotated tag; push with `git push origin main --follow-tags`.
- Before every commit and push, scan for external references (company names, the old example domain) over the staged
  diff and `git log -p --all`; the result must be empty.
