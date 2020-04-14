package io.github.servb.eShop.product

import io.github.servb.eShop.module
import io.github.servb.eShop.util.POSTGRES_CONTAINER_NAME
import io.github.servb.eShop.util.ktor.withTestApplication
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.GivenContext
import io.ktor.application.Application
import io.ktor.server.testing.TestApplicationEngine
import org.testcontainers.containers.PostgreSQLContainer

private fun Application.testContainerEShopProduct() {
    val container = PostgreSQLContainer<Nothing>(POSTGRES_CONTAINER_NAME).apply {
        start()
    }

    module(
        dbPort = container.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT),
        dbUser = container.username,
        dbPassword = container.password,
        dbHost = container.containerIpAddress,
        dbDb = container.databaseName,
        authPort = 4242,  // make it unreachable
        authHost = "localhost4242"  // make it unreachable
    )
}

fun BehaviorSpec.givenTestContainerEShopProduct(test: suspend GivenContext.(TestApplicationEngine) -> Unit) {
    given("test container e-shop-product") {
        withTestApplication(Application::testContainerEShopProduct) {
            this@given.test(this)
        }
    }
}
