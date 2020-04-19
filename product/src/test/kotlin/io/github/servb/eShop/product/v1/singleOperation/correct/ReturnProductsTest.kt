package io.github.servb.eShop.product.v1.singleOperation.correct

import io.github.servb.eShop.product.AlwaysNoConnectionRequestValidator
import io.github.servb.eShop.product.givenTestContainerEShopProduct
import io.github.servb.eShop.util.kotest.shouldMatchJson
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest

class ReturnProductsTest : BehaviorSpec({
    givenTestContainerEShopProduct(AlwaysNoConnectionRequestValidator) { eShopProduct ->
        `when`("I call GET /v1/products") {
            val call = eShopProduct.handleRequest(HttpMethod.Get, "/v1/products")

            then("the response status should be OK") {
                call.response.status() shouldBe HttpStatusCode.OK
            }

            then("the response body should have only proper 'data' field") {
                call.response.content shouldMatchJson """{"data": {"totalCount": 0, "foundRequestedData": []}}"""
            }
        }
    }
})