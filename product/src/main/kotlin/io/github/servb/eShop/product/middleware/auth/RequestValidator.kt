package io.github.servb.eShop.product.middleware.auth

interface RequestValidator {

    suspend fun validate(accessToken: String, needAdmin: Boolean)
}
