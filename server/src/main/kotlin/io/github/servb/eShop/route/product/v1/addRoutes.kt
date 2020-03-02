package io.github.servb.eShop.route.product.v1

import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.annotations.Request
import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.annotations.parameters.QueryParam
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.*
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import io.github.servb.eShop.handler.product.v1.*
import io.github.servb.eShop.model.Product
import io.github.servb.eShop.route.product.v1.ProductsParam.Companion.DEFAULT_OFFSET
import io.github.servb.eShop.route.product.v1.ProductsParam.Companion.MAX_LIMIT
import io.github.servb.eShop.util.OptionalResult
import io.github.servb.eShop.util.SuccessResult

private val exampleProductUsable = ProductUsable(
    name = "Socks",
    id = 42,
    type = 5
)

fun NormalOpenAPIRoute.addRoutes() {
    route("product") {
        post<Unit, SuccessResult, ProductUsable>(
            info(
                summary = "Create a product.",
                description = "The product is created only if a product with the same ID does not exist. Returns `${SuccessResult::class.simpleName}` saying whether the product has been created."
            ),
            exampleResponse = SuccessResult.OK,
            exampleRequest = exampleProductUsable,
            body = { _, body -> createProduct(body, this::respond) }
        )

        delete<ProductIdParam, SuccessResult>(
            info(
                summary = "Remove a product.",
                description = "The product is removed only if a product with the same ID exists. Returns `${SuccessResult::class.simpleName}` saying whether the product has been removed."
            ),
            example = SuccessResult.OK,
            body = { param -> removeProduct(param.id, this::respond) }
        )

        get<ProductIdParam, OptionalResult<ProductUsable?>>(
            info(
                summary = "Return a product.",
                description = "The product is returned only if a product with the same ID exists. Returns `${OptionalResult::class.simpleName}` containing the product data."
            ),
            example = OptionalResult(exampleProductUsable),
            body = { param -> returnProduct(param.id, this::respond) }
        )

        patch<Unit, SuccessResult, ProductUsable>(
            info(
                summary = "Edit a product.",
                description = "The product is edited only if a product with the same ID exists. Returns `${SuccessResult::class.simpleName}` saying whether the product has been edited."
            ),
            exampleResponse = SuccessResult.OK,
            exampleRequest = exampleProductUsable,
            body = { _, body -> editProduct(body, this::respond) }
        )
    }

    route("products") {
        get<ProductsParam, OptionalResult<List<ProductUsable>>>(
            info(
                summary = "Return a list of products.",
                description = "Returns `${OptionalResult::class.simpleName}` containing the list of products data."
            ),
            example = OptionalResult(listOf(exampleProductUsable))
        ) { param ->
            val offset = param.offset ?: DEFAULT_OFFSET
            val limit = minOf(param.limit ?: MAX_LIMIT, MAX_LIMIT)

            returnProducts(
                limit = limit,
                offset = offset,
                respond = this::respond
            )
        }
    }
}

@Response("A Product Response.")
@Request("A Product Request.")
data class ProductUsable(val name: String, val id: Int, val type: Int) {

    fun toProduct() = Product(
        name = name,
        id = id,
        type = type
    )

    companion object {

        fun fromProduct(product: Product) = ProductUsable(
            name = product.name,
            id = product.id,
            type = product.type
        )
    }
}

@Path("{id}")
data class ProductIdParam(
    @PathParam("The Product ID.") val id: Int
)

data class ProductsParam(
    @QueryParam("How many entries to drop. Default is $DEFAULT_OFFSET.") val offset: Int?,
    @QueryParam("How many entries to return. Maximum and default is $MAX_LIMIT.") val limit: Int?
) {

    companion object {

        const val DEFAULT_OFFSET = 0
        const val MAX_LIMIT = 100
    }
}
