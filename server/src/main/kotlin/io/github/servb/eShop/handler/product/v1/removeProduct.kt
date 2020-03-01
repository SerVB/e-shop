package io.github.servb.eShop.handler.product.v1

import io.github.servb.eShop.model.productsStorage
import io.github.servb.eShop.model.productsStorageRwLock
import io.github.servb.eShop.util.Do
import io.github.servb.eShop.util.NOT_OK_RESPONSE
import io.github.servb.eShop.util.OK_RESPONSE
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import kotlin.concurrent.write

suspend fun removeProduct(productId: Int, call: ApplicationCall) {
    val removedProduct = productsStorageRwLock.write {
        productsStorage.remove(productId)
    }

    Do exhaustive when (removedProduct) {
        null -> call.respond(status = HttpStatusCode.NotFound, message = NOT_OK_RESPONSE)
        else -> call.respond(message = OK_RESPONSE)
    }
}
