package io.github.servb.eShop.product.route.singleOperation

import io.github.servb.eShop.product.inMemoryEShopProduct
import io.github.servb.eShop.util.parse
import io.github.servb.eShop.util.withTestApplication
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody

class EShopProductCreateProductTest : BehaviorSpec({
    given("in-memory e-shop") {
        withTestApplication(Application::inMemoryEShopProduct) {
            `when`("I call POST /v1/product") {
                val call = handleRequest(HttpMethod.Post, "/v1/product") {
                    this.setBody("""{"name": "abc", "type": 1234}""")
                    this.addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }

                then("the response status should be OK") {
                    call.response.status() shouldBe HttpStatusCode.OK
                }

                and("I decode the response body") {
                    val responseMap: Map<String, Map<String, Any?>> = call.response.content.parse()

                    then("it should have only proper 'data' field") {
                        val data = responseMap["data"]

                        data.shouldNotBeNull()

                        data.size shouldBe 1

                        data["assignedId"].shouldBeTypeOf<Double>()
                    }
                }
            }
        }
    }
})
