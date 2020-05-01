package io.github.servb.eShop.product.handler.v1

import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.annotations.parameters.HeaderParam
import com.papsign.ktor.openapigen.annotations.parameters.QueryParam
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import io.github.servb.eShop.product.middleware.auth.RequestValidator
import io.github.servb.eShop.product.middleware.auth.throwsAuthExceptions
import io.github.servb.eShop.product.model.ProductTable
import io.github.servb.eShop.product.model.ProductTable.toProductWithId
import io.github.servb.eShop.product.model.ProductWithId
import io.github.servb.eShop.util.OptionalResult
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

data class V1ProductsGetRequestParams(
    @HeaderParam("Auth token.")
    val `X-Access-Token`: String,
    @QueryParam("How many entries to drop. Default is $DEFAULT_OFFSET.")
    val offset: Int?,
    @QueryParam("How many entries to return. Maximum and default is $MAX_LIMIT.")
    val limit: Int?
) {

    companion object {

        const val DEFAULT_OFFSET = 0
        const val MAX_LIMIT = 100
    }
}

@Response("Requested data is always returned.", statusCode = 200)
data class V1ProductsGetOkResponse(
    override val data: Data
) : OptionalResult<V1ProductsGetOkResponse.Data> {

    companion object {

        val EXAMPLE = V1ProductsGetOkResponse(
            Data(
                totalCount = 100500,
                foundRequestedData = listOf(
                    Data.Entry(
                        name = "Socks",
                        id = 42,
                        type = 5
                    ),
                    Data.Entry(
                        name = "T-shirt",
                        id = 53,
                        type = 50
                    )
                )
            )
        )
    }

    data class Data(val totalCount: Int, val foundRequestedData: List<ProductWithId>) {

        data class Entry(
            override val name: String,
            override val id: Int,
            override val type: Int
        ) : ProductWithId
    }
}

fun NormalOpenAPIRoute.returnProducts(database: Database, requestValidator: RequestValidator) {
    route("products") {
        throwsAuthExceptions(OptionalResult.FAIL) {
            get(database, requestValidator)
        }
    }
}

private fun NormalOpenAPIRoute.get(database: Database, requestValidator: RequestValidator) {
    get<V1ProductsGetRequestParams, V1ProductsGetOkResponse>(
        info(
            summary = "Return a list of products.",
            description = "Returns `${OptionalResult::class.simpleName}` containing the list of products data."
        ),
        example = V1ProductsGetOkResponse.EXAMPLE
    ) { params ->
        requestValidator.validate(params.`X-Access-Token`, needAdmin = false)

        val offset = params.offset ?: V1ProductsGetRequestParams.DEFAULT_OFFSET
        val limit = params.limit ?: V1ProductsGetRequestParams.MAX_LIMIT

        val offsetInRange = maxOf(0, offset)
        val limitInRange = maxOf(0, minOf(limit, V1ProductsGetRequestParams.MAX_LIMIT))

        val (total, products) = newSuspendedTransaction(db = database) {
            val total = ProductTable.selectAll().count().toInt()

            val products = ProductTable
                .selectAll()
                .limit(n = limitInRange, offset = offsetInRange.toLong())
                .map { it.toProductWithId() }

            total to products
        }

        respond(V1ProductsGetOkResponse(V1ProductsGetOkResponse.Data(total, products)))
    }
}
