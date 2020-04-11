package io.github.servb.eShop.product.v1.singleOperation.incorrect

import io.github.servb.eShop.product.givenTestContainerEShopProduct
import io.github.servb.eShop.util.kotest.shouldMatchJson
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody

class CreateProductTest : BehaviorSpec({
    givenTestContainerEShopProduct { eShopProduct ->
        forAll(
            row(""),
            row("{"),
            row("}"),
            row("""{}"""),
            row("""{"name": "socks"}"""),
            row("""{"type": 1}""")
        ) { body ->
            `when`("I call incorrect POST /v1/product '$body'") {
                val call = eShopProduct.handleRequest(HttpMethod.Post, "/v1/product") {
                    this.setBody(body)
                    this.addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }

                then("the response status should be BadRequest") {
                    call.response.status() shouldBe HttpStatusCode.BadRequest
                }

                then("the response body should have only proper 'data' field") {
                    call.response.content shouldMatchJson """{"data": null}"""
                }
            }
        }
    }
})
