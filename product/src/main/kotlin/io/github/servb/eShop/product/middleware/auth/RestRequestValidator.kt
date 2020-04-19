package io.github.servb.eShop.product.middleware.auth

import io.github.servb.eShop.util.SuccessResult
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpStatusCode
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory

class RestRequestValidator(private val authBaseUrl: String) : RequestValidator {

    @OptIn(KtorExperimentalAPI::class)
    private val httpClient = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
    }

    override suspend fun validate(accessToken: String) {
        try {
            withTimeout(AUTH_TIMEOUT_MS) {
                httpClient.get<AuthResult>("$authBaseUrl/v1/token") {
                    header("X-Access", accessToken)
                }
            }
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.Forbidden) {
                throw InvalidAuthTokenException
            } else {
                warnAndThrowProblemsWithConnection(e)
            }
        } catch (t: Throwable) {
            warnAndThrowProblemsWithConnection(t)
        }
    }

    private fun warnAndThrowProblemsWithConnection(t: Throwable) {
        logger.warn("Can't access auth service at $authBaseUrl", t)

        throw ProblemsWithConnectionToAuthServiceException(t)
    }

    private data class AuthResult(override val ok: Boolean) : SuccessResult

    companion object {

        private const val AUTH_TIMEOUT_MS: Long = 3000

        private val logger = LoggerFactory.getLogger(RestRequestValidator::class.simpleName)
    }
}