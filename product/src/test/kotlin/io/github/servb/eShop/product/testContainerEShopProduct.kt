package io.github.servb.eShop.product

import io.github.servb.eShop.product.middleware.auth.InvalidAuthTokenException
import io.github.servb.eShop.product.middleware.auth.ProblemsWithConnectionToAuthServiceException
import io.github.servb.eShop.product.middleware.auth.RequestValidator
import io.github.servb.eShop.util.POSTGRES_CONTAINER_NAME
import io.github.servb.eShop.util.ktor.withTestApplication
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.GivenContext
import io.ktor.application.Application
import io.ktor.server.testing.TestApplicationEngine
import org.testcontainers.containers.PostgreSQLContainer

private fun Application.testContainerEShopProduct(requestValidator: RequestValidator) {
    val container = PostgreSQLContainer<Nothing>(POSTGRES_CONTAINER_NAME).apply {
        start()
    }

    module(
        dbPort = container.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT),
        dbUser = container.username,
        dbPassword = container.password,
        dbHost = container.containerIpAddress,
        dbDb = container.databaseName,
        requestValidator = requestValidator
    )
}

object AlwaysSuccessRequestValidator : RequestValidator {

    override suspend fun validate(accessToken: String) = Unit
}

object AlwaysNoConnectionRequestValidator : RequestValidator {

    override suspend fun validate(accessToken: String) = throw ProblemsWithConnectionToAuthServiceException(
        Exception("There is always no connection")
    )
}

object AlwaysFailRequestValidator : RequestValidator {

    override suspend fun validate(accessToken: String) = throw InvalidAuthTokenException
}

fun BehaviorSpec.givenTestContainerEShopProduct(
    requestValidator: RequestValidator,
    test: suspend GivenContext.(TestApplicationEngine) -> Unit
) {
    given("test container e-shop-product with $requestValidator") {
        withTestApplication(
            moduleFunction = { testContainerEShopProduct(requestValidator) },
            test = { this@given.test(this) }
        )
    }
}
