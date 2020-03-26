package io.github.servb.eShop.handler.product.v1

import com.papsign.ktor.openapigen.annotations.Request
import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import io.github.servb.eShop.Db
import io.github.servb.eShop.InMemory
import io.github.servb.eShop.model.InMemoryProduct
import io.github.servb.eShop.model.ProductTable
import io.github.servb.eShop.model.ProductWithoutId
import io.github.servb.eShop.storage
import io.github.servb.eShop.util.OptionalResult
import io.github.servb.eShop.util.SuccessResult
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.concurrent.write

@Request("Create product request body")
data class V1ProductPostRequestBody(
    override val name: String,
    override val type: Int
) : ProductWithoutId {

    companion object {

        val EXAMPLE = V1ProductPostRequestBody("Socks", 5)
    }
}

@Response("Product created", statusCode = 200)
data class V1ProductPostOkResponse(
    override val data: V1ProductPostOkResponseData
) : OptionalResult<V1ProductPostOkResponseData> {

    companion object {

        val EXAMPLE = V1ProductPostOkResponse(V1ProductPostOkResponseData(42))
    }
}

@Response("Bad request body", statusCode = 400)
object V1ProductPostBadRequestResponse : OptionalResult<Nothing> {
    override val data = null
}

data class V1ProductPostOkResponseData(val id: Int)

fun NormalOpenAPIRoute.createProduct() {
    route("product") {
        throws(
            status = HttpStatusCode.BadRequest,
            example = V1ProductPostBadRequestResponse,
            exClass = Throwable::class
        ) {
            post<Unit, OptionalResult<V1ProductPostOkResponseData>, V1ProductPostRequestBody>(
                info(
                    summary = "Create a product.",
                    description = "Returns `${SuccessResult::class.simpleName}` saying whether the product has been created."
                ),
                exampleResponse = V1ProductPostOkResponse.EXAMPLE,
                exampleRequest = V1ProductPostRequestBody.EXAMPLE
            ) { _, body ->
                val id = when (val storage = storage) {
                    is InMemory -> {
                        val id = storage.nextId.getAndIncrement()
                        val productToCreate = InMemoryProduct.fromProductWithoutId(id, body)

                        storage.productsStorageRwLock.write {
                            storage.productsStorage[productToCreate.id] = productToCreate
                        }

                        id
                    }

                    is Db -> newSuspendedTransaction {
                        ProductTable
                            .insertAndGetId {
                                it.fromProductWithoutId(body)
                            }
                            .value
                    }
                }

                respond(V1ProductPostOkResponse(V1ProductPostOkResponseData(id)))
            }
        }
    }
}
