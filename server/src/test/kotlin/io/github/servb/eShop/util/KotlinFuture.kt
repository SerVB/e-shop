package io.github.servb.eShop.util

import io.kotest.data.Row1
import io.kotest.data.blocking.forAll
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

fun <A> forAll(vararg rows: Row1<A>, testfn: suspend (A) -> Unit) = forAll(*rows) {
    runBlocking {
        testfn(it)
    }
}
