package io.github.servb.eShop.product.route.singleOperation

import io.github.servb.eShop.product.inMemoryEShopProduct
import io.github.servb.eShop.util.parse
import io.github.servb.eShop.util.withTestApplication
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody

class EShopProductEditProductTest : BehaviorSpec({
    given("in-memory e-shop") {
        withTestApplication(Application::inMemoryEShopProduct) {
            `when`("I call PUT nonexistent /v1/product") {
                val call = handleRequest(HttpMethod.Put, "/v1/product/20") {
                    this.setBody("""{"name": "abc", "type": 1234}""")
                    this.addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }

                then("the response status should be NotFound") {
                    call.response.status() shouldBe HttpStatusCode.NotFound
                }

                and("I decode the response body") {
                    val responseMap: Map<String, Any?> = call.response.content.parse()

                    then("it should have only proper 'ok' field") {
                        responseMap shouldContainExactly mapOf("ok" to false)
                    }
                }
            }
        }
    }
})
