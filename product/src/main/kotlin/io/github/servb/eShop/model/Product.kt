package io.github.servb.eShop.model

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder

interface ProductWithoutId {

    val name: String
    val type: Int
}

interface ProductWithId : ProductWithoutId {

    val id: Int
}

object ProductTable : IntIdTable() {
    private val name = varchar("name", 50)
    private val type = integer("type")

    fun ResultRow.toProductWithId(): ProductWithId = ProductWithIdImpl(
        name = this[name],
        id = this[id].value,
        type = this[type]
    )

    fun UpdateBuilder<*>.fromProductWithoutId(product: ProductWithoutId) {
        this[name] = product.name
        this[ProductTable.type] = product.type
    }

    data class ProductWithIdImpl(
        override val name: String,
        override val id: Int,
        override val type: Int
    ) : ProductWithId
}
