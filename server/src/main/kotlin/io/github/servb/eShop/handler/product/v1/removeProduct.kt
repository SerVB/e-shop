package io.github.servb.eShop.handler.product.v1

import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.delete
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import io.github.servb.eShop.Db
import io.github.servb.eShop.InMemory
import io.github.servb.eShop.model.ProductTable
import io.github.servb.eShop.storage
import io.github.servb.eShop.util.Do
import io.github.servb.eShop.util.SuccessResult
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.concurrent.write

@Path("{id}")
data class V1ProductDeleteRequestParams(
    @PathParam("ID of the product to remove")
    val id: Int
)

@Response("Product removed", statusCode = 200)
object V1ProductDeleteOkResponse : SuccessResult {
    override val ok = true
}

@Response("ID of the product to remove does not exist", statusCode = 404)
object V1ProductDeleteNotFoundResponse : SuccessResult {
    override val ok = false
}

fun NormalOpenAPIRoute.removeProduct() {
    route("product") {
        delete<V1ProductDeleteRequestParams, SuccessResult>(
            info(
                summary = "Remove a product.",
                description = "The product is removed only if a product with the same ID exists. Returns `${SuccessResult::class.simpleName}` saying whether the product has been removed."
            ),
            example = V1ProductDeleteOkResponse
        ) { param ->
            val removedProduct: Any? = when (val storage = storage) {
                is InMemory -> storage.productsStorageRwLock.write {
                    storage.productsStorage.remove(param.id)
                }

                is Db -> newSuspendedTransaction {
                    val toRemove = ProductTable.select { ProductTable.id.eq(param.id) }.firstOrNull()
                    ProductTable.deleteWhere { ProductTable.id.eq(param.id) }
                    toRemove
                }
            }

            Do exhaustive when (removedProduct) {
                null -> respond(V1ProductDeleteNotFoundResponse)
                else -> respond(V1ProductDeleteOkResponse)
            }
        }
    }
}
