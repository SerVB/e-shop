package io.github.servb.eShop.handler.product.v1

import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.annotations.parameters.QueryParam
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import io.github.servb.eShop.Db
import io.github.servb.eShop.InMemory
import io.github.servb.eShop.model.ProductTable
import io.github.servb.eShop.model.ProductTable.toProductWithId
import io.github.servb.eShop.model.ProductWithId
import io.github.servb.eShop.storage
import io.github.servb.eShop.util.OptionalResult
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.concurrent.read

data class V1ProductsGetRequestParams(
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

fun NormalOpenAPIRoute.returnProducts() {
    route("products") {
        get<V1ProductsGetRequestParams, V1ProductsGetOkResponse>(
            info(
                summary = "Return a list of products.",
                description = "Returns `${OptionalResult::class.simpleName}` containing the list of products data."
            ),
            example = V1ProductsGetOkResponse.EXAMPLE
        ) { param ->
            val offset = param.offset ?: V1ProductsGetRequestParams.DEFAULT_OFFSET
            val limit = param.limit ?: V1ProductsGetRequestParams.MAX_LIMIT

            val offsetInRange = maxOf(0, offset)
            val limitInRange = maxOf(0, minOf(limit, V1ProductsGetRequestParams.MAX_LIMIT))

            val (total, products) = when (val storage = storage) {
                is InMemory -> storage.productsStorageRwLock.read {
                    val total = storage.productsStorage.size

                    val products = storage.productsStorage
                        .asSequence()
                        .map { it.value }
                        .drop(offsetInRange)
                        .take(limitInRange)
                        .toList()

                    total to products
                }

                is Db -> newSuspendedTransaction {
                    val total = ProductTable.selectAll().count().toInt()

                    val products = ProductTable
                        .selectAll()
                        .limit(n = limitInRange, offset = offsetInRange.toLong())
                        .map { it.toProductWithId() }

                    total to products
                }
            }

            respond(V1ProductsGetOkResponse(V1ProductsGetOkResponse.Data(total, products)))
        }
    }
}
