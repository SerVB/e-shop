@file:Suppress("NAME_SHADOWING")

package io.github.servb.eShop.product.v1.multipleOperations

import io.github.servb.eShop.product.AlwaysSuccessRequestValidator
import io.github.servb.eShop.product.givenTestContainerEShopProduct
import io.github.servb.eShop.util.kotest.shouldContainOnlyJsonKey
import io.github.servb.eShop.util.kotest.shouldContainOnlyJsonKeyAndValueOfSpecificType
import io.github.servb.eShop.util.kotest.shouldMatchJson
import io.github.servb.eShop.util.kotest.thenWithContract
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.GivenContext
import io.kotest.core.spec.style.WhenAndContext
import io.kotest.matchers.shouldBe
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody

class EShopProductV1MultipleOperationsTest : BehaviorSpec({
    givenTestContainerEShopProduct(AlwaysSuccessRequestValidator) { eShopProduct ->
        makeThreePosts(eShopProduct)
    }
}) {

    companion object {

        private suspend fun GivenContext.makeThreePosts(eShopProduct: TestApplicationEngine) {
            `when`("I call POST /v1/product") {
                val call = eShopProduct.handleRequest(HttpMethod.Post, "/v1/product") {
                    this.setBody("""{"name": "socks", "type": 1}""")
                    this.addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    this.addHeader("X-Access-Token", "no token")
                }

                then("the response status should be OK") {
                    call.response.status() shouldBe HttpStatusCode.OK
                }

                val firstId: Int

                thenWithContract("the response body should have proper 'data' field") {
                    firstId = call.response.content
                        .shouldContainOnlyJsonKey("data")
                        .shouldContainOnlyJsonKeyAndValueOfSpecificType("id")
                }

                and("I call the same POST /v1/product again") {
                    val call = eShopProduct.handleRequest(HttpMethod.Post, "/v1/product") {
                        this.setBody("""{"name": "socks", "type": 1}""")
                        this.addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                        this.addHeader("X-Access-Token", "no token")
                    }

                    then("the response status should be OK") {
                        call.response.status() shouldBe HttpStatusCode.OK
                    }

                    var secondId: Int  // todo: it should be val, but val doesn't work here

                    thenWithContract("the response body should have proper 'data' field") {
                        secondId = call.response.content
                            .shouldContainOnlyJsonKey("data")
                            .shouldContainOnlyJsonKeyAndValueOfSpecificType("id")
                    }

                    and("I POST another /v1/product") {
                        val call = eShopProduct.handleRequest(HttpMethod.Post, "/v1/product") {
                            this.setBody("""{"name": "car", "type": 55}""")
                            this.addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                            this.addHeader("X-Access-Token", "no token")
                        }

                        then("the response status should be OK") {
                            call.response.status() shouldBe HttpStatusCode.OK
                        }

                        val thirdId: Int

                        thenWithContract("the response body should have proper 'data' field") {
                            thirdId = call.response.content
                                .shouldContainOnlyJsonKey("data")
                                .shouldContainOnlyJsonKeyAndValueOfSpecificType("id")
                        }

                        editSecondProduct(eShopProduct, firstId, secondId, thirdId)
                    }
                }
            }
        }

        private suspend fun WhenAndContext.editSecondProduct(
            eShopProduct: TestApplicationEngine,
            firstId: Int,
            secondId: Int,
            thirdId: Int
        ) {
            and("I PUT /v1/product") {
                val call = eShopProduct.handleRequest(HttpMethod.Put, "/v1/product/$secondId") {
                    this.setBody("""{"name": "second", "type": 2}""")
                    this.addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    this.addHeader("X-Access-Token", "no token")
                }

                then("the response status should be OK") {
                    call.response.status() shouldBe HttpStatusCode.OK
                }

                thenWithContract("the response body should have proper 'data' field") {
                    call.response.content
                        .shouldContainOnlyJsonKeyAndValueOfSpecificType<Boolean>("ok") shouldBe true
                }

                makeSomeGets(eShopProduct, firstId, secondId, thirdId)
            }
        }

        private suspend fun WhenAndContext.makeSomeGets(
            eShopProduct: TestApplicationEngine,
            firstId: Int,
            secondId: Int,
            thirdId: Int
        ) {
            and("I call GET nonexistent /v1/product") {
                val call = eShopProduct.handleRequest(HttpMethod.Get, "/v1/product/${thirdId + 1}") {
                    this.addHeader("X-Access-Token", "no token")
                }

                then("the response status should be NotFound") {
                    call.response.status() shouldBe HttpStatusCode.NotFound
                }

                then("the response body should have only proper 'data' field") {
                    call.response.content shouldMatchJson """{"data": null}"""
                }

                and("I call GET existent /v1/product") {
                    val call = eShopProduct.handleRequest(HttpMethod.Get, "/v1/product/$secondId") {
                        this.addHeader("X-Access-Token", "no token")
                    }

                    then("the response status should be OK") {
                        call.response.status() shouldBe HttpStatusCode.OK
                    }

                    then("the response body should have only proper 'data' field") {
                        call.response.content shouldMatchJson """{"data": {"name": "second", "type": 2}}"""
                    }

                    and("I call GET existent /v1/product again") {
                        val call = eShopProduct.handleRequest(HttpMethod.Get, "/v1/product/$secondId") {
                            this.addHeader("X-Access-Token", "no token")
                        }

                        then("the response status should be OK") {
                            call.response.status() shouldBe HttpStatusCode.OK
                        }

                        then("the response body should have only proper 'data' field") {
                            call.response.content shouldMatchJson """{"data": {"name": "second", "type": 2}}"""
                        }

                        // todo: make other operations
                    }
                }
            }
        }
    }
}
