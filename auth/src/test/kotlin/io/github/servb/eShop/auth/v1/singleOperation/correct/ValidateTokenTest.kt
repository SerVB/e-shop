package io.github.servb.eShop.auth.v1.singleOperation.correct

import io.github.servb.eShop.auth.givenTestContainerEShopAuth
import io.github.servb.eShop.util.kotest.shouldContainOnlyJsonKey
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest

class ValidateTokenTest : BehaviorSpec({
    givenTestContainerEShopAuth { eShopAuth ->
        `when`("I call GET /v1/token") {
            val call = eShopAuth.handleRequest(HttpMethod.Get, "/v1/token") {
                this.addHeader("X-Access", "ac")
                this.addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }

            then("the response status should be Forbidden") {
                call.response.status() shouldBe HttpStatusCode.Forbidden
            }

            then("the response body should have only proper 'ok' field") {
                call.response.content.shouldContainOnlyJsonKey("ok") shouldBe "false"
            }
        }
    }
})
