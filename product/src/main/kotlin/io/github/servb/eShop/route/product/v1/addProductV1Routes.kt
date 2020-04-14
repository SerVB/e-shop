package io.github.servb.eShop.route.product.v1

import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import io.github.servb.eShop.handler.product.v1.*
import io.ktor.client.HttpClient
import org.jetbrains.exposed.sql.Database

fun NormalOpenAPIRoute.addProductV1Routes(database: Database, httpClient: HttpClient, authBaseUrl: String) {
    createProduct(database, httpClient, authBaseUrl)
    editProduct(database, httpClient, authBaseUrl)
    removeProduct(database, httpClient, authBaseUrl)
    returnProduct(database)
    returnProducts(database)
}
