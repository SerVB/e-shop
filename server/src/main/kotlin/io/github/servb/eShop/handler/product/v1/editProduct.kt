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

data class EditProductParameters(val name: String, val id: Int, val type: Int) {

    fun toProduct() = Product(
        name = name,
        id = id,
        type = type
    )
}

suspend fun editProduct(call: ApplicationCall) {
    val editedProductParameters = call.receive<EditProductParameters>()
    val productToEdit = editedProductParameters.toProduct()

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
        true -> call.respond(message = OK_RESPONSE)
        false -> call.respond(message = NOT_OK_RESPONSE)
    }
}
