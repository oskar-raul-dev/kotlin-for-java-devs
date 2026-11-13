# Ejercicios: Kotlin desde la línea de comandos

> **Tutorial:** [`cli`](README.md) · **Dónde se resuelven:** `tutorials/cli/practice/` (archivos `.kt`, `.java` y un
> `NOTAS.md` con los comandos y las respuestas) · **Verificar:** cada ejercicio trae el comando y lo que debe salir.
> No hay pruebas de Gradle: el tutorial es de nivel 0 (sin build).
>
> **Semáforo:** 🟢 fácil · 🟡 medio · 🟠 difícil · 🔴 muy difícil · 🎤 pregunta típica de entrevista ·
> **🎯 = prioritario** (ruta mínima).

**Prioritarios (🎯):** 01, 03, 05, 08, 11.

## 🟢 Fáciles

**01 🟢 🎯 Tu propio Hello.** Escribe `Greet.kt` con un `main` que reciba un nombre y un saldo (`"Ana" "1500.5"`) e
imprima `Ana tiene USD 1500.50`. Compílalo a clases y córrelo con `kotlin`.
- Comando: `kotlinc Greet.kt -d out && kotlin -cp out GreetKt Ana 1500.5`
- Casos: con los dos argumentos; sin el saldo, imprime `Ana tiene USD 0.00`; sin nada, un mensaje de uso y código de
  salida 2 (`kotlin.system.exitProcess(2)`).

**02 🟢 El nombre de la clase.** Sin compilar, predice el nombre de la clase que genera cada archivo y compruébalo con
`ls out`: `Greet.kt`, `bank-report.kt`, `Main.kt` dentro de `package app`, y un archivo con `@file:JvmName("Tools")`.
- Anota en `NOTAS.md` qué pasa con el guion de `bank-report.kt`.

**03 🟢 🎯 El error de la stdlib.** Corre tu `GreetKt` con `java` en lugar de `kotlin` y explica el error en
`NOTAS.md`: qué clase falta, de qué jar viene y por qué `kotlin` no falla.
- Comando: `java -cp out GreetKt Ana`, y luego la versión que funciona agregando `kotlin-stdlib.jar`.

**04 🟢 Jar chico y jar gordo.** Empaqueta `Greet.kt` con y sin `-include-runtime`, mide los dos con `ls -l` y lista
qué paquetes trae el gordo (`unzip -l … | awk …`).
- Casos: el chico no corre con `java -jar`; el gordo sí. Anota los tamaños y qué es `kotlin/reflect` ahí dentro.

**05 🟢 🎯 javap.** Con `javap` (y `javap -p` para lo privado), muestra cómo ve Java esta clase y anota cada miembro
generado:
```kotlin
data class Account(val id: String, var balance: java.math.BigDecimal)
```
- Casos mínimos en `NOTAS.md`: getters, el setter de `balance`, `component1`/`component2`, `copy`, `copy$default`,
  `equals`/`hashCode`/`toString`.

## 🟡 Medios

**06 🟡 Librería en dos pasos.** Divide el ejemplo `bank/` en tres jars: `money.jar`, `ledger.jar` (una función
`balance(postings: List<Money>): Money` que usa `money`) y `app.jar`. Compila cada uno solo con el classpath mínimo.
- Comando final: `kotlin -cp money.jar:ledger.jar:app.jar …MainKt`
- Casos: demuestra que `app.jar` **no** compila sin `money.jar` aunque solo importe `ledger` (o explica por qué sí, si
  tu diseño lo evita).

**07 🟡 Java llama a Kotlin.** Desde un `Report.java`, usa tu `ledger.jar`: llama a `balance(…)`, lee una propiedad
y usa el operador `plus` como método.
- Casos: compila con `javac -cp`, corre con `java -cp` y anota qué jar hace falta solo en tiempo de ejecución.

**08 🟡 🎯 `-jvm-target`.** Compila `Greet.kt` con `-jvm-target` 1.8, 17 y 21, y saca la versión del bytecode con
`javap -v | grep major`.
- Casos: 52, 61 y 65. Luego corre el de 21 con un JDK 17 (`sdk use java 17…`) y copia el error.

**09 🟡 Un aviso y `-Werror`.** Escribe un archivo que compile con al menos tres avisos distintos de K2 (uno es `!!`
innecesario). Compílalo normal y con `-Werror`.
- Casos: los tres avisos con su texto exacto; el código de salida en cada caso (`echo $?`).

**10 🟡 `-language-version`.** Prueba `-language-version` 1.9, 2.0, 2.2 y 2.4 sobre `Greet.kt` y anota qué responde
cada una.
- Explica en `NOTAS.md` por qué el curso no usa esta opción para el track `22-legacy-kotlin19`.

**11 🟡 🎯 🎤 ¿Qué hace `kotlin` que no hace `java`?** Responde en `NOTAS.md` con una demostración: dos comandos que
corran la misma clase, uno con `kotlin` y otro con `java`, con el mismo resultado.
- Pista: `kotlin -cp out GreetKt` frente a `java -cp out:…/kotlin-stdlib.jar GreetKt`.

## 🟠 Difíciles

**12 🟠 Dos `main`.** Pon dos archivos con `main` en la misma carpeta y empaquétalos con `-include-runtime`.
- Casos: el manifiesto sin `Main-Class` y el error de `java -jar`; luego corre cada uno con
  `java -cp app.jar AKt` y `… BKt`. Arregla el jar agregando `Main-Class` a mano (`jar ufm`).

**13 🟠 Sin `kotlin-reflect`.** Arma a mano un jar ejecutable **sin** `kotlin-reflect`: compila sin
`-include-runtime`, descomprime `kotlin-stdlib.jar` junto a tus clases y vuelve a empaquetar con `jar`.
- Casos: corre con `java -jar`; compara el tamaño con el de `-include-runtime`. Luego usa `Account::class.members` en
  el código y muestra qué error aparece sin `kotlin-reflect`.

**14 🟠 `-Xjdk-release`.** Escribe un `main` que use `Math.clamp(150L, 0, 100)` (nuevo en Java 21). Compílalo con
`-jvm-target 17` solo, y luego con `-jvm-target 17 -Xjdk-release=17`.
- Casos: el primero compila, corre en JDK 21 y en un JDK 17 falla con `NoSuchMethodError`; el segundo no compila
  (`unresolved reference 'clamp'`). Explica en `NOTAS.md` qué controla cada opción.

## 🔴 Muy difíciles

**15 🔴 Un build sin Gradle.** Escribe `practice/build.sh` que, sin Gradle, compile el ejemplo `bank/` en jars
separados, corra un `main` de prueba que falle con código distinto de 0 si el total no es `USD 14.75`, y deje un jar
ejecutable en `practice/dist/`.
- Casos: el script falla si cambias un monto; mide cuánto tarda y compáralo con `./gradlew` en una sección (con el
  daemon caliente). Anota por qué Gradle es más rápido en la segunda corrida.
