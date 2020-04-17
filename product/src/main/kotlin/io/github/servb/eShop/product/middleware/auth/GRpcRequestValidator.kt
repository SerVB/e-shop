package io.github.servb.eShop.product.middleware.auth

import io.github.servb.eShop.auth.grpc.client.AccessTokenValidationClient
import io.github.servb.eShop.auth.grpc.client.ValidUserType
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory

class GRpcRequestValidator(private val host: String, private val port: Int) : RequestValidator {

    private val client = AccessTokenValidationClient(host, port)

    override suspend fun validate(accessToken: String) {
        val response = try {
            withTimeout(AUTH_TIMEOUT_MS) {
                client.requestUserType(accessToken)
            }
        } catch (t: Throwable) {
            warnAndThrowProblemsWithConnection(t)
        }

        if (response != ValidUserType.USER) {
            throw InvalidAuthTokenException
        }
    }

    private fun warnAndThrowProblemsWithConnection(t: Throwable) {
        logger.warn("Can't access auth service at $host $port", t)

        throw ProblemsWithConnectionToAuthServiceException(t)
    }

    companion object {

        private const val AUTH_TIMEOUT_MS: Long = 3000

        private val logger = LoggerFactory.getLogger(GRpcRequestValidator::class.java)
    }
}