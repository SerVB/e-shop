package io.github.servb.eShop.handler.product.v1

import io.github.servb.eShop.model.productsStorage
import io.github.servb.eShop.model.productsStorageRwLock
import io.github.servb.eShop.route.product.v1.ProductUsable
import io.github.servb.eShop.util.Do
import io.github.servb.eShop.util.OptionalResult
import kotlin.concurrent.read

suspend fun returnProduct(productId: Int, respond: suspend (OptionalResult<ProductUsable?>) -> Unit) {
    val product = productsStorageRwLock.read {
        productsStorage[productId]
    }

    Do exhaustive when (product) {
        null -> respond(OptionalResult.NOT_OK)  // todo: status = HttpStatusCode.NotFound
        else -> respond(OptionalResult(ProductUsable.fromProduct(product)))
    }
}
