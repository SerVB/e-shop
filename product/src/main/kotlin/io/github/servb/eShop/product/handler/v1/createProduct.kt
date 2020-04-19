package io.github.servb.eShop.product.handler.v1

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonProcessingException
import com.papsign.ktor.openapigen.annotations.Request
import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.annotations.parameters.HeaderParam
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import io.github.servb.eShop.product.middleware.auth.RequestValidator
import io.github.servb.eShop.product.middleware.auth.throwsAuthExceptions
import io.github.servb.eShop.product.model.ProductTable
import io.github.servb.eShop.product.model.ProductWithoutId
import io.github.servb.eShop.util.OptionalResult
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

data class V1ProductPostRequestParams(
    @HeaderParam("Auth token.")
    val `X-Access-Token`: String
)

@Request("Create product request body.")
data class V1ProductPostRequestBody(
    override val name: String,
    @JsonProperty(required = true)
    override val type: Int
) : ProductWithoutId {

    companion object {

        val EXAMPLE = V1ProductPostRequestBody("Socks", 5)
    }
}

@Response("The product has been created.", statusCode = 200)
data class V1ProductPostOkResponse(
    override val data: Data
) : OptionalResult<V1ProductPostOkResponse.Data> {

    companion object {

        val EXAMPLE = V1ProductPostOkResponse(Data(42))
    }

    data class Data(val id: Int)
}

fun NormalOpenAPIRoute.createProduct(database: Database, requestValidator: RequestValidator) {
    route("product") {
        throws(
            status = HttpStatusCode.BadRequest.description("A request body decoding error."),
            example = OptionalResult.FAIL,
            exClass = JsonProcessingException::class
        ) {
            throwsAuthExceptions(OptionalResult.FAIL) {
                post(database, requestValidator)
            }
        }
    }
}

private fun NormalOpenAPIRoute.post(database: Database, requestValidator: RequestValidator) {
    post<V1ProductPostRequestParams, V1ProductPostOkResponse, V1ProductPostRequestBody>(
        info(
            summary = "Create a product.",
            description = "Returns `${OptionalResult::class.simpleName}` saying whether the product has been created."
        ),
        exampleResponse = V1ProductPostOkResponse.EXAMPLE,
        exampleRequest = V1ProductPostRequestBody.EXAMPLE
    ) { params, body ->
        requestValidator.validate(params.`X-Access-Token`)

        val id = newSuspendedTransaction(db = database) {
            ProductTable.insertAndGetId { it.fromProductWithoutId(body) }.value
        }

        respond(V1ProductPostOkResponse(V1ProductPostOkResponse.Data(id)))
    }
}
