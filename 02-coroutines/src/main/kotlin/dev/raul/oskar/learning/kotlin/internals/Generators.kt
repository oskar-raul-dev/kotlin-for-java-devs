package dev.raul.oskar.learning.kotlin.internals

import dev.raul.oskar.learning.kotlin.cases.Money
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * `sequence { yield(x) }` es una corrutina **restringida** de la stdlib: solo puede suspender con `yield`, no necesita
 * hilos ni dispatcher, y corre en el hilo de quien pide el siguiente elemento. Es el generador que Java no tiene. Esta
 * genera precios de un ticker con una caminata determinista y anota cuántos generó en [generated].
 */
fun priceWalk(start: Money, stepPercent: BigDecimal, generated: MutableList<Money>): Sequence<Money> = sequence {
    var price = start
    var up = true
    while (true) {
        generated += price
        yield(price)
        val factor = if (up) BigDecimal.ONE + stepPercent else BigDecimal.ONE - stepPercent
        price = Money.of((price.amount * factor).setScale(2, RoundingMode.HALF_EVEN))
        up = !up
    }
}

/** Una máquina de estados escrita como código secuencial: los estados de una orden, uno por cada `yield`. */
fun orderLifecycle(filledInSteps: List<Long>): Sequence<String> = sequence {
    yield("NEW")
    var filled = 0L
    for (qty in filledInSteps) {
        filled += qty
        yield("PARTIAL $filled")
    }
    yield("FILLED $filled")
}
