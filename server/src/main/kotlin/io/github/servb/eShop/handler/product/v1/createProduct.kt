package io.github.servb.eShop.handler.product.v1

import io.github.servb.eShop.model.productsStorage
import io.github.servb.eShop.model.productsStorageRwLock
import io.github.servb.eShop.route.product.v1.ProductUsable
import io.github.servb.eShop.util.Do
import io.github.servb.eShop.util.SuccessResult
import kotlin.concurrent.write

suspend fun createProduct(body: ProductUsable, respond: suspend (SuccessResult) -> Unit) {
    val productToCreate = body.toProduct()

    val result = productsStorageRwLock.write {
        when (productToCreate.id in productsStorage) {
            true -> false

            false -> {
                productsStorage[productToCreate.id] = productToCreate

                true
            }
        }
    }

    Do exhaustive when (result) {
        true -> respond(SuccessResult.OK)
        false -> respond(SuccessResult.NOT_OK)
    }
}
