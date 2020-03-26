package io.github.servb.eShop.handler.product.v1

import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.annotations.Request
import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.put
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import io.github.servb.eShop.Db
import io.github.servb.eShop.InMemory
import io.github.servb.eShop.model.InMemoryProduct
import io.github.servb.eShop.model.ProductTable
import io.github.servb.eShop.model.ProductWithoutId
import io.github.servb.eShop.storage
import io.github.servb.eShop.util.Do
import io.github.servb.eShop.util.SuccessResult
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import kotlin.concurrent.write

@Path("{id}")
data class V1ProductPutRequestParams(
    @PathParam("ID of the product to edit")
    val id: Int
)

@Request("Edit product request body")
data class V1ProductPutRequestBody(
    override val name: String,
    override val type: Int
) : ProductWithoutId {

    companion object {

        val EXAMPLE = V1ProductPutRequestBody("Socks", 5)
    }
}

@Response("Product edited", statusCode = 200)
object V1ProductPutOkResponse : SuccessResult {
    override val ok = true
}

@Response("ID of the product to edit does not exist", statusCode = 404)
object V1ProductPutNotFoundResponse : SuccessResult {
    override val ok = false
}

@Response("Bad request body", statusCode = 400)
object V1ProductPutBadRequestResponse : SuccessResult {
    override val ok = false
}

fun NormalOpenAPIRoute.editProduct() {
    route("product") {
        throws(
            status = HttpStatusCode.BadRequest,
            example = V1ProductPutBadRequestResponse,
            exClass = Throwable::class
        ) {
            put<V1ProductPutRequestParams, SuccessResult, V1ProductPutRequestBody>(
                info(
                    summary = "Edit a product.",
                    description = "The product is edited only if a product with the same ID exists. Returns `${SuccessResult::class.simpleName}` saying whether the product has been edited."
                ),
                exampleResponse = V1ProductPutOkResponse,
                exampleRequest = V1ProductPutRequestBody.EXAMPLE
            ) { params, body ->
                val result = when (val storage = storage) {
                    is InMemory -> {
                        val productToEdit = InMemoryProduct.fromProductWithoutId(params.id, body)

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

                    is Db -> newSuspendedTransaction {
                        when (ProductTable.select { ProductTable.id.eq(params.id) }.firstOrNull()) {
                            null -> false

                            else -> {
                                ProductTable.update({ ProductTable.id.eq(params.id) }) {
                                    it.fromProductWithoutId(body)
                                }

                                true
                            }
                        }
                    }
                }

                Do exhaustive when (result) {
                    true -> respond(V1ProductPutOkResponse)
                    false -> respond(V1ProductPutNotFoundResponse)
                }
            }
        }
    }
}
