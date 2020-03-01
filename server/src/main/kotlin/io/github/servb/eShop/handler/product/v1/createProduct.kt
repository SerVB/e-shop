package io.github.servb.eShop.handler.product.v1

import io.github.servb.eShop.model.Product
import io.github.servb.eShop.model.productsStorage
import io.github.servb.eShop.model.productsStorageRwLock
import io.github.servb.eShop.util.Do
import io.github.servb.eShop.util.NOT_OK_RESPONSE
import io.github.servb.eShop.util.OK_RESPONSE
import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import io.ktor.response.respond
import kotlin.concurrent.write

data class CreateProductParameters(val name: String, val id: Int, val type: Int) {

    fun toProduct() = Product(
        name = name,
        id = id,
        type = type
    )
}

suspend fun createProduct(call: ApplicationCall) {
    val createProductParameters = call.receive<CreateProductParameters>()
    val productToCreate = createProductParameters.toProduct()

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
        true -> call.respond(message = OK_RESPONSE)
        false -> call.respond(message = NOT_OK_RESPONSE)
    }
}
