// Tareas de la raíz que recorren todas las secciones incluidas en settings.gradle.kts:
//   ./gradlew build | test | check | clean
// Para filtrar pruebas o correr una lección se entra a la sección:
//   ./gradlew -p 01-language test --tests '*.strings.*'
//   ./gradlew -p 01-language run -Plesson=strings
listOf("build", "test", "check", "clean").forEach { name ->
    tasks.register(name) {
        group = "monorepo"
        description = "Corre ':$name' en todas las secciones Gradle"
        dependsOn(gradle.includedBuilds.map { it.task(":$name") })
    }
}
