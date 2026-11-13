// El "hola mundo" del tutorial: un main con argumentos, sin clase.
// kotlinc lo compila a la clase HelloKt (nombre del archivo + "Kt"), con un main estático.
fun main(args: Array<String>) {
    val name = args.firstOrNull() ?: "mundo"
    println("Hola, $name")
}
