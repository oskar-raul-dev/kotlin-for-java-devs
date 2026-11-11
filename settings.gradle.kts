// Agregador de los proyectos Gradle del monorepo (composite build, sin herencia).
// Cada sección es autocontenida y se construye sola con ./gradlew -p <sección> <tarea>.
// Las secciones vacías se agregan aquí cuando tengan build. Fuera de este agregador
// quedarán 12-android (AGP con su propio wrapper) y los tracks 20-22 (Kotlin legacy).
rootProject.name = "kotlin-for-java-devs"

listOf(
    "01-language",
    "04-data",
).forEach { includeBuild(it) }
