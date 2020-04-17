package io.github.servb.eShop.auth

import io.github.servb.eShop.util.POSTGRES_CONTAINER_NAME
import io.github.servb.eShop.util.ktor.withTestApplication
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.GivenContext
import io.ktor.application.Application
import io.ktor.server.testing.TestApplicationEngine
import org.testcontainers.containers.PostgreSQLContainer

private fun Application.testContainerEShopAuth() {
    val container = PostgreSQLContainer<Nothing>(POSTGRES_CONTAINER_NAME).apply {
        start()
    }

    module(
        createDatabase(
            dbPort = container.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT),
            dbUser = container.username,
            dbPassword = container.password,
            dbHost = container.containerIpAddress,
            dbDb = container.databaseName
        )
    )
}

fun BehaviorSpec.givenTestContainerEShopAuth(test: suspend GivenContext.(TestApplicationEngine) -> Unit) {
    given("test container e-shop-auth") {
        withTestApplication(Application::testContainerEShopAuth) {
            this@given.test(this)
        }
    }
}
