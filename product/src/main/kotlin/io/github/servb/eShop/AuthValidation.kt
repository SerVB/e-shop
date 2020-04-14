package io.github.servb.eShop

import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.throws
import io.github.servb.eShop.util.SuccessResult
import io.ktor.client.HttpClient
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory

private const val AUTH_TIMEOUT_MS: Long = 3000

class ProblemsWithConnectionToAuthServiceException(cause: Throwable) : Throwable("Can't validate request", cause)

object InvalidAuthTokenException : Throwable()

inline fun <reified ResponseType> NormalOpenAPIRoute.throwsAuthExceptions(
    response: ResponseType,
    crossinline block: NormalOpenAPIRoute.() -> Unit
) {
    throws(
        status = HttpStatusCode.NotImplemented.description("No connection to auth service."),
        example = response,
        exClass = ProblemsWithConnectionToAuthServiceException::class
    ) {
        throws(
            status = HttpStatusCode.Forbidden.description("Bad auth credentials."),
            example = response,
            exClass = InvalidAuthTokenException::class,
            fn = block
        )
    }
}

private val logger = LoggerFactory.getLogger("AuthValidation")

private data class AuthResult(override val ok: Boolean) : SuccessResult

suspend fun validateRequest(accessToken: String, httpClient: HttpClient, authBaseUrl: String) {
    fun warnAndThrow(t: Throwable) {
        logger.warn("Can't access auth service", t)

        throw ProblemsWithConnectionToAuthServiceException(t)
    }

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
            warnAndThrow(e)
        }
    } catch (t: Throwable) {
        warnAndThrow(t)
    }
}