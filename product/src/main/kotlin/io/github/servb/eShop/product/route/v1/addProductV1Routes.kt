package io.github.servb.eShop.product.route.v1

import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import io.github.servb.eShop.product.handler.v1.*
import io.github.servb.eShop.product.middleware.auth.RequestValidator
import org.jetbrains.exposed.sql.Database

fun NormalOpenAPIRoute.addProductV1Routes(database: Database, requestValidator: RequestValidator) {
    createProduct(database, requestValidator)
    editProduct(database, requestValidator)
    removeProduct(database, requestValidator)
    returnProduct(database, requestValidator)
    returnProducts(database, requestValidator)
}
