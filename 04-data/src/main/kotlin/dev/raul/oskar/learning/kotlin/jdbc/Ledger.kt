package dev.raul.oskar.learning.kotlin.jdbc

import java.math.BigDecimal
import java.math.RoundingMode

// Filas del ledger del Banco (docs/Casos.md, caso A) tal como salen de la base.
// 04-data es un proyecto aparte de 01-language: no comparte su Money, así que los montos son BigDecimal con escala 2.

fun money(text: String): BigDecimal = BigDecimal(text).setScale(2, RoundingMode.HALF_EVEN)

/** Una cuenta con su saldo materializado (la suma de sus postings, guardada para no recalcularla). */
data class AccountRow(val id: String, val currency: String, val balance: BigDecimal, val status: String = "OPEN")

/**
 * Una línea de un asiento visto desde una cuenta. Convención de la cuenta de efectivo del cliente (un activo para él):
 * DEBIT aumenta el saldo y CREDIT lo disminuye, como en el puente de Casos.md.
 */
data class MovementRow(val entryId: Long, val description: String, val direction: Char, val amount: BigDecimal)
