package io.github.servb.eShop.handler.product.v1

import io.github.servb.eShop.model.productsStorage
import io.github.servb.eShop.model.productsStorageRwLock
import io.github.servb.eShop.util.Do
import io.github.servb.eShop.util.SuccessResult
import kotlin.concurrent.write

suspend fun removeProduct(productId: Int, respond: suspend (SuccessResult) -> Unit) {
    val removedProduct = productsStorageRwLock.write {
        productsStorage.remove(productId)
    }

    Do exhaustive when (removedProduct) {
        null -> respond(SuccessResult.NOT_OK)  // todo: status = HttpStatusCode.NotFound
        else -> respond(SuccessResult.OK)
    }
}
