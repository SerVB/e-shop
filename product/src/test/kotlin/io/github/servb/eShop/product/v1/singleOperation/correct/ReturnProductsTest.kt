package io.github.servb.eShop.product.v1.singleOperation.correct

import io.github.servb.eShop.product.*
import io.github.servb.eShop.util.kotest.shouldContainOnlyJsonKey
import io.github.servb.eShop.util.kotest.shouldMatchJson
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest

class ReturnProductsTest : BehaviorSpec({
    forAll(
        row(AlwaysNoConnectionRequestValidator, HttpStatusCode.NotImplemented, null),
        row(AlwaysFailRequestValidator, HttpStatusCode.Forbidden, null),
        row(OnlyUserRequestValidator, HttpStatusCode.OK, """{"totalCount": 0, "foundRequestedData": []}"""),
        row(AlwaysSuccessRequestValidator, HttpStatusCode.OK, """{"totalCount": 0, "foundRequestedData": []}""")
    ) { requestValidator, status, data ->
        givenTestContainerEShopProduct(requestValidator) { eShopProduct ->
            `when`("I call GET /v1/products") {
                val call = eShopProduct.handleRequest(HttpMethod.Get, "/v1/products") {
                    this.addHeader("X-Access-Token", "no token")
                }

                then("the response status should be $status") {
                    call.response.status() shouldBe status
                }

                then("the response body should have only proper 'data' field") {
                    call.response.content.shouldContainOnlyJsonKey("data") shouldMatchJson data
                }
            }
        }
    }
})