package io.github.servb.eShop.route.product.v1

import io.github.servb.eShop.handler.product.v1.*
import io.ktor.application.call
import io.ktor.routing.*

const val ID = "id"
const val OFFSET = "offset"
const val LIMIT = "limit"

private const val DEFAULT_OFFSET = 0
private const val MAX_LIMIT = 100

fun Route.addRoutes() {
    route("product") {
        post { createProduct(call) }
        delete("{$ID}") { removeProduct(call.parameters[ID]!!.toInt(), call) }
        get("{$ID}") { returnProduct(call.parameters[ID]!!.toInt(), call) }
        patch { editProduct(call) }
    }
    route("products") {
        get {
            val offset = call.request.queryParameters[OFFSET]?.toIntOrNull() ?: DEFAULT_OFFSET
            val limit = minOf(call.request.queryParameters[LIMIT]?.toIntOrNull() ?: MAX_LIMIT, MAX_LIMIT)

            returnProducts(
                limit = limit,
                offset = offset,
                call = call
            )
        }
    }
}
