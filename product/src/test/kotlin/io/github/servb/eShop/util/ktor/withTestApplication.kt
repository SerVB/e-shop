package io.github.servb.eShop.util.ktor

import io.ktor.application.Application
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.withTestApplication
import kotlinx.coroutines.runBlocking

fun withTestApplication(
    moduleFunction: Application.() -> Unit,
    test: suspend TestApplicationEngine.() -> Unit
) = withTestApplication(moduleFunction) {
    runBlocking {
        test()
    }
}
