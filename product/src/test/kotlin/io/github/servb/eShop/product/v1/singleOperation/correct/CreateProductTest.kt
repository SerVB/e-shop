package io.github.servb.eShop.product.v1.singleOperation.correct

import io.github.servb.eShop.product.testContainerEShopProduct
import io.github.servb.eShop.util.kotest.shouldContainOnlyJsonKey
import io.github.servb.eShop.util.kotest.shouldContainOnlyJsonKeyAndValueOfSpecificType
import io.github.servb.eShop.util.ktor.withTestApplication
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody

class CreateProductTest : BehaviorSpec({
    given("in-memory e-shop") {
        withTestApplication(Application::testContainerEShopProduct) {
            `when`("I call POST /v1/product") {
                val call = handleRequest(HttpMethod.Post, "/v1/product") {
                    this.setBody("""{"name": "abc", "type": 1234}""")
                    this.addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }

                then("the response status should be OK") {
                    call.response.status() shouldBe HttpStatusCode.OK
                }

                then("the response body should have only proper 'data' field") {
                    call.response.content
                        .shouldContainOnlyJsonKey("data")
                        .shouldContainOnlyJsonKeyAndValueOfSpecificType<Int>("id")
                }
            }
        }
    }
})
