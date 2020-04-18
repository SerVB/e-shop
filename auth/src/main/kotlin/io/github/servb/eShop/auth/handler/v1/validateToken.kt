package io.github.servb.eShop.auth.handler.v1

import com.fasterxml.jackson.core.JsonProcessingException
import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.annotations.parameters.HeaderParam
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import io.github.servb.eShop.auth.model.SessionTable
import io.github.servb.eShop.util.SuccessResult
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime

data class V1TokenGetRequestParam(
    @HeaderParam("Access token to validate.")
    val `X-Access`: String
)

@Response("The token is valid.", statusCode = 200)
object V1TokensGetOkResponse : SuccessResult {

    override val ok = true
}

fun NormalOpenAPIRoute.validateToken(database: Database) {
    route("token") {
        throws(
            status = HttpStatusCode.BadRequest.description("A request param decoding error."),
            example = SuccessResult.FAIL,
            exClass = JsonProcessingException::class
        ) {
            throws(
                status = HttpStatusCode.Forbidden.description("Bad access token."),
                example = SuccessResult.FAIL,
                exClass = IllegalArgumentException::class
            ) {
                get(database)
            }
        }
    }
}

private fun NormalOpenAPIRoute.get(database: Database) {
    get<V1TokenGetRequestParam, V1TokensGetOkResponse>(
        info(
            summary = "Validate token.",
            description = "Returns `${SuccessResult::class.simpleName}` saying whether the token is valid."
        ),
        example = V1TokensGetOkResponse
    ) { param ->
        val now = LocalDateTime.now()

        val foundMatches = newSuspendedTransaction(db = database) {
            SessionTable
                .select {
                    SessionTable.accessToken.eq(param.`X-Access`) and SessionTable.accessTokenExpireAt.greater(now)
                }
                .count()
        }

        require(foundMatches == 1L)

        respond(V1TokensGetOkResponse)
    }
}
