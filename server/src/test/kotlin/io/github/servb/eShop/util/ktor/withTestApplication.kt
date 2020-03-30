package io.github.servb.eShop.util.ktor

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import io.kotest.assertions.AssertionCounter
import io.kotest.assertions.failure
import io.kotest.assertions.json.shouldMatchJson
import io.kotest.data.Row1
import io.kotest.data.blocking.forAll
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.ktor.application.Application
import io.ktor.jackson.JacksonConverter
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import kotlinx.coroutines.runBlocking
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun withTestApplication(
    moduleFunction: Application.() -> Unit,
    test: suspend TestApplicationEngine.() -> Unit
) = withTestApplication(moduleFunction) {
    runBlocking {
        test()
    }
}
