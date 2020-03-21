package io.github.servb.eShop.product.route.singleOperation

import io.github.servb.eShop.product.inMemoryEShopProduct
import io.github.servb.eShop.util.parse
import io.github.servb.eShop.util.withTestApplication
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody

class EShopProductCreateProductTest : BehaviorSpec({
    given("in-memory e-shop") {
        withTestApplication(Application::inMemoryEShopProduct) {
            `when`("I call POST /v1/product") {
                val call = handleRequest(HttpMethod.Post, "/v1/product") {
                    this.setBody("""{"name": "abc", "id": 123, "type": 1234}""")
                }

                then("the response status should be OK") {
                    call.response.status() shouldBe HttpStatusCode.OK
                }

                and("I decode the response body") {
                    val responseMap: Map<String, Any?> = call.response.content.parse()

                    then("it should have only proper 'ok' field") {
                        responseMap shouldContainExactly mapOf("ok" to true)
                    }
                }
            }
        }
    }
})
