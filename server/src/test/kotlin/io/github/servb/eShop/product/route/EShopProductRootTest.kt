package io.github.servb.eShop.product.route

import io.github.servb.eShop.product.inMemoryEShopProduct
import io.github.servb.eShop.util.withTestApplication
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.parseMap

@OptIn(ImplicitReflectionSerializer::class, UnstableDefault::class)
class EShopProductRootTest : BehaviorSpec({
    given("in-memory e-shop") {
        withTestApplication(Application::inMemoryEShopProduct) {
            `when`("I call GET /") {
                val call = handleRequest(HttpMethod.Get, "/")

                then("the response status should be OK") {
                    call.response.status() shouldBe HttpStatusCode.OK
                }

                and("I decode the response body") {
                    val responseMap = Json.parseMap<String, String>(call.response.content!!)

                    then("it should have proper 'name' field") {
                        responseMap shouldContain ("name" to "e-shop")
                    }

                    then("it should have proper 'uptime' field") {
                        responseMap shouldContainKey "uptime"

                        val uptime = responseMap.getValue("uptime")

                        uptime shouldEndWith "s"
                        uptime.dropLast(1).toIntOrNull().shouldNotBeNull()
                    }
                }
            }
        }
    }
})
