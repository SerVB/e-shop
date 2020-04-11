package io.github.servb.eShop.product

import io.github.servb.eShop.module
import io.github.servb.eShop.util.POSTGRES_CONTAINER_NAME
import io.ktor.application.Application
import org.testcontainers.containers.PostgreSQLContainer

fun Application.testContainerEShopProduct() {
    val container = PostgreSQLContainer<Nothing>(POSTGRES_CONTAINER_NAME).apply {
        start()
    }

    module(
        dbPort = container.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT),
        dbUser = container.username,
        dbPassword = container.password,
        dbHost = container.containerIpAddress,
        dbDb = container.databaseName
    )
}
