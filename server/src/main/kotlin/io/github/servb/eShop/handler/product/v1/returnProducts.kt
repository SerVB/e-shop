package io.github.servb.eShop.handler.product.v1

import io.github.servb.eShop.model.productsStorage
import io.github.servb.eShop.model.productsStorageRwLock
import io.github.servb.eShop.route.product.v1.ProductUsable
import io.github.servb.eShop.util.OptionalResult
import kotlin.concurrent.read

suspend fun returnProducts(offset: Int, limit: Int, respond: suspend (OptionalResult<List<ProductUsable>>) -> Unit) {
    val products = productsStorageRwLock.read {
        productsStorage
            .asSequence()
            .map { it.value }
            .drop(offset)
            .take(limit)
            .toList()
    }

    respond(OptionalResult(products.map(ProductUsable.Companion::fromProduct)))
}
