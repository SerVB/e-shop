package io.github.servb.eShop.product.v1.singleOperation.incorrect

import io.github.servb.eShop.product.AlwaysNoConnectionRequestValidator
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

class EditProductTest : BehaviorSpec({
    givenTestContainerEShopProduct(AlwaysNoConnectionRequestValidator) { eShopProduct ->
        forAll(
            row("", "1"),
            row("{", "1"),
            row("}", "1"),
            row("""{}""", "1"),
            row("""{"name": "socks"}""", "1")
            // todo: https://github.com/papsign/Ktor-OpenAPI-Generator/issues/28
//            row("""{"name": "socks", "type": 1}""", "abc"),
//            row("""{"name": "socks", "type": 1}""", "a"),
//            row("""{"name": "socks", "type": 1}""", "ad4dsa")
        ) { body, id ->
            `when`("I call PUT nonexistent /v1/product $body $id") {
                val call = eShopProduct.handleRequest(HttpMethod.Put, "/v1/product/$id") {
                    this.setBody(body)
                    this.addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    this.addHeader("X-Access-Token", "no token")
                }

                then("the response status should be BadRequest") {
                    call.response.status() shouldBe HttpStatusCode.BadRequest
                }

                then("the response body should have only proper 'ok' field") {
                    call.response.content shouldMatchJson """{"ok": false}"""
                }
            }
        }
    }
})
