package io.github.servb.eShop.product.middleware.auth

import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.throws
import io.ktor.http.HttpStatusCode

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
