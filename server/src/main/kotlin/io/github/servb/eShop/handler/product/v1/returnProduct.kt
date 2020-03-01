package io.github.servb.eShop.handler.product.v1

import io.github.servb.eShop.model.Product
import io.github.servb.eShop.model.productsStorage
import io.github.servb.eShop.model.productsStorageRwLock
import io.github.servb.eShop.util.Do
import io.github.servb.eShop.util.NOT_OK_RESPONSE
import io.github.servb.eShop.util.createOkResponse
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import kotlin.concurrent.read

data class ReturnProductAnswerData(val name: String, val id: Int, val type: Int) {

    companion object {

        fun fromProduct(product: Product) = ReturnProductAnswerData(
            name = product.name,
            id = product.id,
            type = product.type
        )
    }
}

suspend fun returnProduct(productId: Int, call: ApplicationCall) {
    val product = productsStorageRwLock.read {
        productsStorage[productId]
    }

    Do exhaustive when (product) {
        null -> call.respond(status = HttpStatusCode.NotFound, message = NOT_OK_RESPONSE)
        else -> call.respond(message = createOkResponse(ReturnProductAnswerData.fromProduct(product)))
    }
}
