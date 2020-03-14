package io.github.servb.eShop.handler.product.v1

import io.github.servb.eShop.Db
import io.github.servb.eShop.InMemory
import io.github.servb.eShop.model.InMemoryProduct
import io.github.servb.eShop.model.ProductTable
import io.github.servb.eShop.model.ProductTable.toProductUsable
import io.github.servb.eShop.route.product.v1.ProductList
import io.github.servb.eShop.storage
import io.github.servb.eShop.util.OptionalResult
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.concurrent.read

suspend fun returnProducts(offset: Int, limit: Int, respond: suspend (OptionalResult<ProductList?>) -> Unit) {
    val (total, products) = when (val storage = storage) {
        is InMemory -> storage.productsStorageRwLock.read {
            val total = storage.productsStorage.size

            val products = storage.productsStorage
                .asSequence()
                .map { it.value }
                .drop(offset)
                .take(limit)
                .map(InMemoryProduct::toProductUsable)  // todo: this line can be done without a lock
                .toList()

            total to products
        }

        is Db -> newSuspendedTransaction {
            val total = ProductTable.selectAll().count().toInt()

            val products = ProductTable
                .selectAll()
                .limit(n = limit, offset = offset.toLong())
                .map { it.toProductUsable() }

            total to products
        }
    }

    respond(OptionalResult(ProductList(total, products)))
}
