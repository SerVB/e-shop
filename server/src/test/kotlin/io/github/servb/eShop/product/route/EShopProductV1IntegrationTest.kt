package io.github.servb.eShop.product.route

import io.github.servb.eShop.product.inMemoryEShopProduct
import io.github.servb.eShop.util.parse
import io.github.servb.eShop.util.withTestApplication
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody

class EShopProductV1IntegrationTest : BehaviorSpec({
    given("in-memory e-shop") {
        withTestApplication(Application::inMemoryEShopProduct) {
//            forAll(
//                row(""),
//                row("""{}"""),
//                row("""{"name": "socks"}"""),
//                row("""{"type": 1}""")
//            ) { body ->
//                `when`("I call incomplete POST /v1/product") {
//                    val call = handleRequest(HttpMethod.Post, "/v1/product") {
//                        this.setBody(body)
//                    }
//
//                    then("the response status should be BadRequest") {
//                        call.response.status() shouldBe HttpStatusCode.BadRequest
//                    }
//
//                    and("I decode the response body") {
//                        val responseMap: Map<String, Any?> = call.response.content.parse()
//
//                        then("it should have proper 'data' field") {
//                            responseMap shouldContain ("data" to null)
//                        }
//                    }
//                }
//            }

            `when`("I call POST /v1/product") {
                val call = handleRequest(HttpMethod.Post, "/v1/product") {
                    this.setBody("""{"name": "socks", "id": 10, "type": 1}""")
                }

                then("the response status should be OK") {
                    call.response.status() shouldBe HttpStatusCode.OK
                }

                var firstPostProductId: Int? = null

                and("I decode the response body") {
                    val responseMap: Map<String, Map<String, Any>> = call.response.content.parse()

                    then("it should have proper 'data' field") {
                        responseMap shouldContainKey "data"

                        val data = responseMap.getValue("data")

                        data shouldContain ("name" to "socks")
                        data shouldContain ("type" to 1)
                        data shouldContainKey "id"

                        firstPostProductId = (data.getValue("id") as String).toIntOrNull()

                        firstPostProductId.shouldNotBeNull()
                    }
                }

                and("I call the same POST /v1/product again") {
                    val call2 = handleRequest(HttpMethod.Post, "/v1/product") {
                        this.setBody("""{"name": "socks", "type": 1}""")
                    }

                    then("the response status should be OK") {
                        call2.response.status() shouldBe HttpStatusCode.OK
                    }

                    var secondPostProductId: Int? = null

                    and("I decode the response body") {
                        val responseMap: Map<String, Map<String, Any>> = call2.response.content.parse()

                        then("it should have proper 'data' field") {
                            responseMap shouldContainKey "data"

                            val data = responseMap.getValue("data")

                            data shouldContain ("name" to "socks")
                            data shouldContain ("type" to 1)
                            data shouldContainKey "id"

                            secondPostProductId = (data.getValue("id") as String).toIntOrNull()

                            secondPostProductId.shouldNotBeNull()

                            secondPostProductId shouldNotBe firstPostProductId
                        }
                    }

                    and("I POST another /v1/product") {
                        handleRequest(HttpMethod.Post, "/v1/product") {
                            this.setBody("""{"name": "car", "type": 55}""")
                        }

                        and("I call GET nonexistent /v1/product") {
                            val call3 = handleRequest(
                                HttpMethod.Get,
                                "/v1/product/${firstPostProductId!! + secondPostProductId!! + 1}"
                            )


                        }

                        and("I call GET /v1/product") {

                        }
                    }
                }
            }
        }
    }
})
