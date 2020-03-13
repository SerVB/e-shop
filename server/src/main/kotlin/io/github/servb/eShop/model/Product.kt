package io.github.servb.eShop.model

import io.github.servb.eShop.route.product.v1.ProductUsable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.UpdateBuilder

data class InMemoryProduct(val name: String, val id: Int, val type: Int) {

    fun toProductUsable() = ProductUsable(
        name = name,
        id = id,
        type = type
    )

    companion object {

        fun fromProductUsable(product: ProductUsable) = InMemoryProduct(
            name = product.name,
            id = product.id,
            type = product.type
        )
    }
}

object ProductTable : Table() {
    val name = varchar("name", 50)
    val id = integer("id")
    val type = integer("type")

    fun ResultRow.toProductUsable() = ProductUsable(
        name = this[name],
        id = this[id],
        type = this[type]
    )

    fun UpdateBuilder<*>.fromProductUsable(product: ProductUsable) {
        this[name] = product.name
        this[id] = product.id
        this[ProductTable.type] = product.type
    }
}
