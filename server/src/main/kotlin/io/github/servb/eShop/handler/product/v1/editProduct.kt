package io.github.servb.eShop.handler.product.v1

import io.github.servb.eShop.model.productsStorage
import io.github.servb.eShop.model.productsStorageRwLock
import io.github.servb.eShop.route.product.v1.ProductUsable
import io.github.servb.eShop.util.Do
import io.github.servb.eShop.util.SuccessResult
import kotlin.concurrent.write

suspend fun editProduct(body: ProductUsable, respond: suspend (SuccessResult) -> Unit) {
    val productToEdit = body.toProduct()

    val result = productsStorageRwLock.write {
        when (productToEdit.id in productsStorage) {
            true -> {
                productsStorage[productToEdit.id] = productToEdit

                true
            }

            false -> false
        }
    }

    Do exhaustive when (result) {
        true -> respond(SuccessResult.OK)
        false -> respond(SuccessResult.NOT_OK)
    }
}
