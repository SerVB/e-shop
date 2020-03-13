package io.github.servb.eShop.handler.product.v1

import io.github.servb.eShop.Db
import io.github.servb.eShop.InMemory
import io.github.servb.eShop.model.ProductTable
import io.github.servb.eShop.storage
import io.github.servb.eShop.util.Do
import io.github.servb.eShop.util.SuccessResult
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.concurrent.write

suspend fun removeProduct(productId: Int, respond: suspend (SuccessResult) -> Unit) {
    val removedProduct: Any? = when (val storage = storage) {
        is InMemory -> storage.productsStorageRwLock.write {
            storage.productsStorage.remove(productId)
        }

        is Db -> transaction {
            val toRemove = ProductTable.select { ProductTable.id.eq(productId) }.firstOrNull()
            ProductTable.deleteWhere { ProductTable.id.eq(productId) }
            toRemove
        }
    }

    Do exhaustive when (removedProduct) {
        null -> respond(SuccessResult.NOT_OK)  // todo: status = HttpStatusCode.NotFound
        else -> respond(SuccessResult.OK)
    }
}
