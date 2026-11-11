package dev.raul.oskar.learning.kotlin.jpa

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.math.BigDecimal
import java.time.Instant

// JPA en Kotlin, las reglas que importan:
//  1. NO usar data class para entidades: su equals/hashCode usa todos los campos (incluidas
//     colecciones lazy) y cambia cuando cambia el id -> se rompen Sets y proxies.
//  2. Constructor sin argumentos: lo genera el plugin kotlin("plugin.jpa") (no-arg).
//  3. Clases y propiedades no final: lo hace kotlin("plugin.allopen") para @Entity (proxies lazy).
//  4. id nullable (`Long? = null`): null = todavía no persistida.
//  5. equals/hashCode por id, con hashCode constante por clase (patrón recomendado para Hibernate).

@Entity
@Table(name = "customer")
class Customer(
    @Column(nullable = false, length = 120)
    var name: String,

    @Column(nullable = false, unique = true, length = 200)
    var email: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    // mappedBy: el dueño de la relación es PurchaseOrder.customer (la FK está en purchase_order).
    @OneToMany(mappedBy = "customer", cascade = [CascadeType.ALL], orphanRemoval = true)
    val orders: MutableList<PurchaseOrder> = mutableListOf()

    /** Mantener los dos lados de la relación sincronizados. */
    fun placeOrder(order: PurchaseOrder): PurchaseOrder {
        orders += order
        order.customer = this
        return order
    }

    override fun equals(other: Any?) = other is Customer && id != null && id == other.id
    override fun hashCode() = Customer::class.hashCode()
    override fun toString() = "Customer(id=$id, name=$name)"   // nunca imprimir colecciones lazy
}

enum class OrderStatus { NEW, PAID, SHIPPED, CANCELLED }

@Entity
@Table(name = "purchase_order")
class PurchaseOrder(
    @Column(nullable = false)
    var createdAt: Instant = Instant.now(),

    // EnumType.STRING: guarda "PAID". ORDINAL guardaría 1 y se rompe al reordenar el enum.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: OrderStatus = OrderStatus.NEW,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    // ManyToOne es EAGER por defecto en JPA: casi siempre conviene LAZY.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    var customer: Customer? = null

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    val lines: MutableList<OrderLine> = mutableListOf()

    /** Bloqueo optimista: Hibernate incrementa version en cada UPDATE y falla si alguien se adelantó. */
    @Version
    var version: Long? = null

    fun addLine(product: String, quantity: Int, unitPrice: BigDecimal): PurchaseOrder {
        lines += OrderLine(product, quantity, unitPrice).also { it.order = this }
        return this
    }

    fun total(): BigDecimal = lines.fold(BigDecimal.ZERO) { acc, l -> acc + l.unitPrice * l.quantity.toBigDecimal() }

    override fun equals(other: Any?) = other is PurchaseOrder && id != null && id == other.id
    override fun hashCode() = PurchaseOrder::class.hashCode()
    override fun toString() = "PurchaseOrder(id=$id, status=$status, version=$version)"
}

@Entity
@Table(name = "order_line")
class OrderLine(
    @Column(nullable = false, length = 80)
    var product: String,

    @Column(nullable = false)
    var quantity: Int,

    @Column(nullable = false, precision = 12, scale = 2)
    var unitPrice: BigDecimal,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    var order: PurchaseOrder? = null

    override fun equals(other: Any?) = other is OrderLine && id != null && id == other.id
    override fun hashCode() = OrderLine::class.hashCode()
    override fun toString() = "OrderLine(id=$id, $product x$quantity)"
}
