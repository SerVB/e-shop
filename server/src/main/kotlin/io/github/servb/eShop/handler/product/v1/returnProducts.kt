package io.github.servb.eShop.handler.product.v1

import io.github.servb.eShop.model.Product
import io.github.servb.eShop.model.productsStorage
import io.github.servb.eShop.model.productsStorageRwLock
import io.github.servb.eShop.util.createOkResponse
import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import kotlin.concurrent.read

data class ReturnProductsAnswerData(val name: String, val id: Int, val type: Int) {

    companion object {

        fun fromProduct(product: Product) = ReturnProductsAnswerData(
            name = product.name,
            id = product.id,
            type = product.type
        )
    }
}

suspend fun returnProducts(offset: Int, limit: Int, call: ApplicationCall) {
    val products = productsStorageRwLock.read {
        productsStorage
            .asSequence()
            .map { it.value }
            .drop(offset)
            .take(limit)
            .toList()
    }

    call.respond(message = createOkResponse(products.map { ReturnProductsAnswerData.fromProduct(it) }))
}
