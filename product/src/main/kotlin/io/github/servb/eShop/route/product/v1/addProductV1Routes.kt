package io.github.servb.eShop.route.product.v1

import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import io.github.servb.eShop.handler.product.v1.*
import org.jetbrains.exposed.sql.Database

fun NormalOpenAPIRoute.addProductV1Routes(database: Database) {
    createProduct(database)
    editProduct(database)
    removeProduct(database)
    returnProduct(database)
    returnProducts(database)
}
