package io.github.servb.eShop.auth

import io.github.servb.eShop.auth.util.TokenCreator
import io.github.servb.eShop.util.kotest.*
import io.github.servb.eShop.util.ktor.withTestApplication
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeUnique
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldNotBeEmpty
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest

class TokenCreatorTest : FreeSpec({
    "test token creator" - {
        "generated token should be non-empty" {
            TokenCreator.createToken().shouldNotBeEmpty()
        }

        "generated tokens should be different" {
            val tokens = (1..10000).map { TokenCreator.createToken() }

            tokens.shouldBeUnique()
        }
    }
})
