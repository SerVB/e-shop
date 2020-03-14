package io.github.servb.eShop.handler.product.v1

import io.github.servb.eShop.Db
import io.github.servb.eShop.InMemory
import io.github.servb.eShop.model.ProductTable
import io.github.servb.eShop.model.ProductTable.toProductUsable
import io.github.servb.eShop.route.product.v1.ProductUsable
import io.github.servb.eShop.storage
import io.github.servb.eShop.util.Do
import io.github.servb.eShop.util.OptionalResult
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.concurrent.read

suspend fun returnProduct(productId: Int, respond: suspend (OptionalResult<ProductUsable?>) -> Unit) {
    val product = when (val storage = storage) {
        is InMemory -> storage.productsStorageRwLock.read {
            storage.productsStorage[productId]?.toProductUsable()
        }

        is Db -> newSuspendedTransaction {
            ProductTable.select { ProductTable.id.eq(productId) }.firstOrNull()?.toProductUsable()
        }
    }

    Do exhaustive when (product) {
        null -> respond(OptionalResult.NOT_OK)  // todo: status = HttpStatusCode.NotFound
        else -> respond(OptionalResult(product))
    }
}
