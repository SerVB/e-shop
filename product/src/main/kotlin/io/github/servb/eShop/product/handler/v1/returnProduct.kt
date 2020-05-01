package io.github.servb.eShop.product.handler.v1

import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.annotations.parameters.HeaderParam
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import io.github.servb.eShop.product.middleware.auth.RequestValidator
import io.github.servb.eShop.product.middleware.auth.throwsAuthExceptions
import io.github.servb.eShop.product.model.ProductTable
import io.github.servb.eShop.product.model.ProductTable.toProductWithId
import io.github.servb.eShop.product.model.ProductWithoutId
import io.github.servb.eShop.util.OptionalResult
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

@Path("{id}")
data class V1ProductGetRequestParams(
    @HeaderParam("Auth token.")
    val `X-Access-Token`: String,
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

fun NormalOpenAPIRoute.returnProduct(database: Database, requestValidator: RequestValidator) {
    route("product") {
        throws(
            status = HttpStatusCode.NotFound.description("The product does not exist."),
            example = OptionalResult.FAIL,
            exClass = IllegalArgumentException::class
        ) {
            throwsAuthExceptions(OptionalResult.FAIL) {
                get(database, requestValidator)
            }
        }
    }
}

private fun NormalOpenAPIRoute.get(database: Database, requestValidator: RequestValidator) {
    get<V1ProductGetRequestParams, V1ProductGetOkResponse>(
        info(
            summary = "Return a product.",
            description = "The product is returned only if a product with the same ID exists. Returns `${OptionalResult::class.simpleName}` containing the product data."
        ),
        example = V1ProductGetOkResponse.EXAMPLE
    ) { params ->
        requestValidator.validate(params.`X-Access-Token`, needAdmin = false)

        val product = newSuspendedTransaction(db = database) {
            ProductTable.select { ProductTable.id.eq(params.id) }.firstOrNull()?.toProductWithId()
        }

        requireNotNull(product)

        respond(V1ProductGetOkResponse(V1ProductGetOkResponse.Data(product.name, product.type)))
    }
}
