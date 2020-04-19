package io.github.servb.eShop.product.v1.singleOperation.correct

import io.github.servb.eShop.product.AlwaysFailRequestValidator
import io.github.servb.eShop.product.AlwaysNoConnectionRequestValidator
import io.github.servb.eShop.product.AlwaysSuccessRequestValidator
import io.github.servb.eShop.product.givenTestContainerEShopProduct
import io.github.servb.eShop.util.kotest.shouldMatchJson
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest

class ReturnProductTest : BehaviorSpec({
    forAll(
        row(AlwaysNoConnectionRequestValidator),
        row(AlwaysFailRequestValidator),
        row(AlwaysSuccessRequestValidator)
    ) { requestValidator ->
        givenTestContainerEShopProduct(requestValidator) { eShopProduct ->
            `when`("I call GET nonexistent /v1/product") {
                val call = eShopProduct.handleRequest(HttpMethod.Get, "/v1/product/2")

                then("the response status should be NotFound") {
                    call.response.status() shouldBe HttpStatusCode.NotFound
                }

                then("the response body should have only proper 'data' field") {
                    call.response.content shouldMatchJson """{"data": null}"""
                }
            }
        }
    }
})