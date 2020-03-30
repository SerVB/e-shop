package io.github.servb.eShop.product.v1.singleOperation.correct

import io.github.servb.eShop.product.inMemoryEShopProduct
import io.github.servb.eShop.util.kotest.shouldMatchJson
import io.github.servb.eShop.util.ktor.withTestApplication
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest

class ReturnProductTest : BehaviorSpec({
    given("in-memory e-shop") {
        withTestApplication(Application::inMemoryEShopProduct) {
            `when`("I call GET nonexistent /v1/product") {
                val call = handleRequest(HttpMethod.Get, "/v1/product/2")

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