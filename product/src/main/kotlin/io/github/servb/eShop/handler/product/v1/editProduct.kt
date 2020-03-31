package io.github.servb.eShop.handler.product.v1

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonProcessingException
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
    @PathParam("ID of the product to edit.")
    val id: Int
)

@Request("Edit product request body.")
data class V1ProductPutRequestBody(
    override val name: String,
    @JsonProperty(required = true)
    override val type: Int
) : ProductWithoutId {

    companion object {

        val EXAMPLE = V1ProductPutRequestBody("Socks", 5)
    }
}

@Response("The product has been edited.", statusCode = 200)
object V1ProductPutOkResponse : SuccessResult {
    override val ok = true
}

fun NormalOpenAPIRoute.editProduct() {
    route("product") {
        throws(
            status = HttpStatusCode.BadRequest.description("A request body decoding error."),
            example = SuccessResult.FAIL,
            exClass = JsonProcessingException::class
        ) {
            throws(
                status = HttpStatusCode.NotFound.description("The product does not exist."),
                example = SuccessResult.FAIL,
                exClass = IllegalArgumentException::class
            ) {
                put<V1ProductPutRequestParams, V1ProductPutOkResponse, V1ProductPutRequestBody>(
                    info(
                        summary = "Edit a product.",
                        description = "The product is edited only if a product with the same ID exists. Returns `${SuccessResult::class.simpleName}` saying whether the product has been edited."
                    ),
                    exampleResponse = V1ProductPutOkResponse,
                    exampleRequest = V1ProductPutRequestBody.EXAMPLE
                ) { params, body ->
                    Do exhaustive when (val storage = storage) {
                        is InMemory -> storage.productsStorageRwLock.write {
                            require(params.id in storage.productsStorage)

                            storage.productsStorage[params.id] = InMemoryProduct.fromProductWithoutId(params.id, body)
                        }

                        is Db -> newSuspendedTransaction {
                            require(ProductTable.select { ProductTable.id.eq(params.id) }.count() != 0L)

                            ProductTable.update({ ProductTable.id.eq(params.id) }) {
                                it.fromProductWithoutId(body)
                            }
                        }
                    }

                    respond(V1ProductPutOkResponse)
                }
            }
        }
    }
}
