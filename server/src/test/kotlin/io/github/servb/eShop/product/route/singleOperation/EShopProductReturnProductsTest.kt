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

class EShopProductReturnProductsTest : BehaviorSpec({
    given("in-memory e-shop") {
        withTestApplication(Application::inMemoryEShopProduct) {
            `when`("I call GET /v1/products") {
                val call = handleRequest(HttpMethod.Get, "/v1/products")

                then("the response status should be OK") {
                    call.response.status() shouldBe HttpStatusCode.OK
                }

                and("I decode the response body") {
                    val responseMap: Map<String, Any?> = call.response.content.parse()

                    then("it should have only proper 'data' field") {
                        responseMap shouldContainExactly mapOf(
                            "data" to mapOf(
                                "totalCount" to 0.0,
                                "foundRequestedData" to emptyList<Nothing>()
                            )
                        )
                    }
                }
            }
        }
    }
})