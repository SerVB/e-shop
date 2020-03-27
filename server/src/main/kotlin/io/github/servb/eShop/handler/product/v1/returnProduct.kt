package io.github.servb.eShop.handler.product.v1

import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import io.github.servb.eShop.Db
import io.github.servb.eShop.InMemory
import io.github.servb.eShop.model.ProductTable
import io.github.servb.eShop.model.ProductTable.toProductWithId
import io.github.servb.eShop.model.ProductWithoutId
import io.github.servb.eShop.storage
import io.github.servb.eShop.util.OptionalResult
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.concurrent.read

@Path("{id}")
data class V1ProductGetRequestParams(
    @PathParam("ID of the product to return.")
    val id: Int
)

@Response("The product has been found.", statusCode = 200)
data class V1ProductGetOkResponse(
    override val data: Data
) : OptionalResult<V1ProductGetOkResponse.Data> {

    companion object {

        val EXAMPLE = V1ProductGetOkResponse(
            Data(
                name = "socks",
                type = 5
            )
        )
    }

    data class Data(
        override val name: String,
        override val type: Int
    ) : ProductWithoutId
}

fun NormalOpenAPIRoute.returnProduct() {
    route("product") {
        throws(
            status = HttpStatusCode.NotFound.description("The product does not exist."),
            example = OptionalResult.FAIL,
            exClass = IllegalArgumentException::class
        ) {
            get<V1ProductGetRequestParams, V1ProductGetOkResponse>(
                info(
                    summary = "Return a product.",
                    description = "The product is returned only if a product with the same ID exists. Returns `${OptionalResult::class.simpleName}` containing the product data."
                ),
                example = V1ProductGetOkResponse.EXAMPLE
            ) { param ->
                val product = when (val storage = storage) {
                    is InMemory -> storage.productsStorageRwLock.read {
                        storage.productsStorage[param.id]
                    }

                    is Db -> newSuspendedTransaction {
                        ProductTable.select { ProductTable.id.eq(param.id) }.firstOrNull()?.toProductWithId()
                    }
                }

                requireNotNull(product)

                respond(V1ProductGetOkResponse(V1ProductGetOkResponse.Data(product.name, product.type)))
            }
        }
    }
}
