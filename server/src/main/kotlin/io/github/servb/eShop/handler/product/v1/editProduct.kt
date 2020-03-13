package io.github.servb.eShop.handler.product.v1

import io.github.servb.eShop.Db
import io.github.servb.eShop.InMemory
import io.github.servb.eShop.model.InMemoryProduct
import io.github.servb.eShop.model.ProductTable
import io.github.servb.eShop.route.product.v1.ProductUsable
import io.github.servb.eShop.storage
import io.github.servb.eShop.util.Do
import io.github.servb.eShop.util.SuccessResult
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.concurrent.write

suspend fun editProduct(body: ProductUsable, respond: suspend (SuccessResult) -> Unit) {
    val result = when (val storage = storage) {
        is InMemory -> {
            val productToEdit = InMemoryProduct.fromProductUsable(body)

            storage.productsStorageRwLock.write {
                when (productToEdit.id in storage.productsStorage) {
                    true -> {
                        storage.productsStorage[productToEdit.id] = productToEdit

                        true
                    }

                    false -> false
                }
            }
        }

        is Db -> transaction {
            when (ProductTable.select { ProductTable.id.eq(body.id) }.firstOrNull()) {
                null -> false

                else -> {
                    ProductTable.deleteWhere { ProductTable.id.eq(body.id) }

                    ProductTable.insert {
                        it.fromProductUsable(body)
                    }

                    true
                }
            }
        }
    }

    Do exhaustive when (result) {
        true -> respond(SuccessResult.OK)
        false -> respond(SuccessResult.NOT_OK)
    }
}
