package io.github.servb.eShop.route.product.v1

import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import io.github.servb.eShop.handler.product.v1.*

fun NormalOpenAPIRoute.addProductV1Routes() {
    createProduct()
    editProduct()
    removeProduct()
    returnProduct()
    returnProducts()
}
