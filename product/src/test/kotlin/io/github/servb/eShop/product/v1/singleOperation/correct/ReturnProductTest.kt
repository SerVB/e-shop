package io.github.servb.eShop.product.v1.singleOperation.correct

import io.github.servb.eShop.product.*
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
        row(AlwaysNoConnectionRequestValidator, HttpStatusCode.NotImplemented),
        row(AlwaysFailRequestValidator, HttpStatusCode.Forbidden),
        row(OnlyUserRequestValidator, HttpStatusCode.NotFound),
        row(AlwaysSuccessRequestValidator, HttpStatusCode.NotFound)
    ) { requestValidator, status ->
        givenTestContainerEShopProduct(requestValidator) { eShopProduct ->
            `when`("I call GET nonexistent /v1/product") {
                val call = eShopProduct.handleRequest(HttpMethod.Get, "/v1/product/2") {
                    this.addHeader("X-Access-Token", "no token")
                }

                then("the response status should be $status") {
                    call.response.status() shouldBe status
                }

                then("the response body should have only proper 'data' field") {
                    call.response.content shouldMatchJson """{"data": null}"""
                }
            }
        }
    }
})