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
import io.ktor.server.testing.setBody

class CreateUserTest : BehaviorSpec({
    givenTestContainerEShopAuth { eShopAuth ->
        `when`("I call POST /v1/user") {
            val call = eShopAuth.handleRequest(HttpMethod.Post, "/v1/user") {
                this.setBody("""{"username": "abc", "password": "1234", "role": "USER"}""")
                this.addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }

            then("the response status should be OK") {
                call.response.status() shouldBe HttpStatusCode.OK
            }

            then("the response body should have only proper 'ok' field") {
                call.response.content.shouldContainOnlyJsonKey("ok") shouldBe "true"
            }
        }
    }
})
