# Contenedores de los tracks legacy de Kotlin

Un servicio por track, cada uno con el JDK de su época. Solo se conteneriza el JDK: Gradle llega con el wrapper de
cada track y el compilador de Kotlin con su plugin de Gradle. El porqué y qué cambia entre versiones está en
[`docs/LegacyKotlin.md`](../../docs/LegacyKotlin.md).

## Imágenes

Fijadas por digest (`@sha256:…` del índice multi-arquitectura) el **13 de noviembre de 2026**. Todas tienen variante
`linux/arm64`, así que en Apple Silicon corren **nativas**, sin emulación.

| Servicio | Track | Imagen | JDK | Estado |
|---|---|---|---|---|
| `k14` | `20-legacy-kotlin14` | `eclipse-temurin:11-jdk@sha256:8b64035b…` | Temurin 11.0.33+10 | Imagen lista; el track se crea en B5 |
| `k16` | `21-legacy-kotlin16` | `eclipse-temurin:17-jdk@sha256:54658156…` | Temurin 17.0.21+9 | Imagen lista; el track se crea en B5 |
| `k19` | `22-legacy-kotlin19` | `eclipse-temurin:21-jdk@sha256:0a3f3298…` | Temurin 21.0.13+9 | ✅ Probado en Apple Silicon |

Base: Ubuntu 26.04 LTS. Los digests completos están en [`compose.yaml`](compose.yaml).

## Cómo se usa

Siempre a través de [`scripts/legacy.sh`](../../scripts/legacy.sh), desde la raíz del repo:

```bash
./scripts/legacy.sh 19                       # ./gradlew test en 22-legacy-kotlin19 con JDK 21
./scripts/legacy.sh 19 clean test --console=plain
./scripts/legacy.sh 19 run --console=plain   # el Main del track
./scripts/legacy.sh 19 shell                 # bash dentro del contenedor (java -version, ./gradlew --version…)
```

Lo que hace por debajo:

```bash
docker compose -f docker/legacy-kotlin/compose.yaml run --rm k19 ./gradlew test
```

- El repo se monta completo en `/work` y cada servicio entra a la carpeta de su track.
- El volumen con nombre `gradle-home` guarda el wrapper, las dependencias y la caché de Gradle entre corridas. La
  primera corrida del track 19 tarda ~1 minuto (baja Gradle 9.2.1 y el compilador); las siguientes, ~4 segundos.
- El daemon de Gradle está apagado (`GRADLE_OPTS=-Dorg.gradle.daemon=false`): muere con el contenedor de todos modos.
- `./gradlew --version` dentro del contenedor dice `Kotlin: 2.2.20`: es el Kotlin con el que Gradle corre sus propios
  scripts (`build.gradle.kts`), **no** el del proyecto. El del proyecto lo fija el plugin (1.9.22 en el track 19).

## Contenedor y nativo comparten `build/`

El track se puede alternar entre contenedor y nativo (`cd 22-legacy-kotlin19 && ./gradlew test`) sin limpiar:
probado en las dos direcciones. En Docker Desktop para macOS los archivos que escribe el contenedor quedan a nombre
del usuario de la máquina. En Linux quedarían de `root`; ahí conviene agregar `--user "$(id -u):$(id -g)"` ⚠️ (sin
probar, junto con los permisos del volumen `gradle-home`).

## Bases de datos

Los tracks no las usan todavía. Cuando el track 16 (Spring Boot 2.7) necesite PostgreSQL o MySQL, desde el
contenedor se llega a los de `docker/postgres` y `docker/mysql` por `host.docker.internal:15432` y `:13306` ⚠️ (por
probar en B5), o uniendo los servicios a la misma red de Docker.

## Actualizar un digest

```bash
docker buildx imagetools inspect eclipse-temurin:21-jdk | grep -E '^Digest|Platform'   # digest del índice
docker run --rm eclipse-temurin:21-jdk@sha256:<digest> java -version                 # qué JDK trae
```

Cambiar el digest en `compose.yaml`, la tabla de arriba y [`docs/Versions.md`](../../docs/Versions.md), y correr
`./scripts/legacy.sh 19` antes de commitear. Los tracks legacy no se actualizan de versión mayor: el JDK de cada
uno es parte de lo que se estudia.

## Limpiar

```bash
docker compose -f docker/legacy-kotlin/compose.yaml down -v   # borra el volumen gradle-home
docker image ls --digests eclipse-temurin                      # bajadas por digest: aparecen sin tag
docker image rm <IMAGE ID>…
```
