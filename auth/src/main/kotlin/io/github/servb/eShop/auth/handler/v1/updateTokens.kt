package io.github.servb.eShop.auth.handler.v1

import com.fasterxml.jackson.core.JsonProcessingException
import com.papsign.ktor.openapigen.annotations.Request
import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.put
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import io.github.servb.eShop.auth.model.SessionTable
import io.github.servb.eShop.auth.util.ParamsProvider
import io.github.servb.eShop.auth.util.TokenCreator
import io.github.servb.eShop.util.OptionalResult
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime

@Request("Update tokens request body.")
data class V1TokensPutRequestBody(
    val refresh: String
) {

    companion object {

        val EXAMPLE = V1TokensPutRequestBody("re")
    }
}

@Response("The tokens have been updated.", statusCode = 200)
data class V1TokensPutOkResponse(
    override val data: Data
) : OptionalResult<V1TokensPutOkResponse.Data> {

    companion object {

        val EXAMPLE = V1TokensPutOkResponse(
            Data(
                access = "ac",
                refresh = "re"
            )
        )
    }

    data class Data(val access: String, val refresh: String)
}

fun NormalOpenAPIRoute.updateTokens(database: Database) {
    route("tokens") {
        throws(
            status = HttpStatusCode.BadRequest.description("A request body decoding error."),
            example = OptionalResult.FAIL,
            exClass = JsonProcessingException::class
        ) {
            throws(
                status = HttpStatusCode.Forbidden.description("Bad refresh token."),
                example = OptionalResult.FAIL,
                exClass = IllegalArgumentException::class
            ) {
                put(database)
            }
        }
    }
}

private fun NormalOpenAPIRoute.put(database: Database) {
    put<Unit, V1TokensPutOkResponse, V1TokensPutRequestBody>(
        info(
            summary = "Update tokens.",
            description = "Returns `${OptionalResult::class.simpleName}` saying whether tokens have been updated."
        ),
        exampleResponse = V1TokensPutOkResponse.EXAMPLE,
        exampleRequest = V1TokensPutRequestBody.EXAMPLE
    ) { _, body ->
        val access = TokenCreator.createToken()
        val refresh = TokenCreator.createToken()

        val now = LocalDateTime.now()

        val accessTokenExpireAt = now.plusNanos(ParamsProvider.ACCESS_TOKEN_EXPIRATION_NANOS)
        val refreshTokenExpireAt = now.plusNanos(ParamsProvider.REFRESH_TOKEN_EXPIRATION_NANOS)

        newSuspendedTransaction(db = database) {
            val row = requireNotNull(
                SessionTable
                    .select { SessionTable.refreshToken.eq(body.refresh) }
                    .singleOrNull()
            )

            val userId = row[SessionTable.userId]
            val previousRefreshExpiration = row[SessionTable.refreshTokenExpireAt]

            SessionTable.deleteWhere { SessionTable.refreshToken.eq(body.refresh) }

            require(now <= previousRefreshExpiration)

            SessionTable.insert {
                it.fromUserWithId(
                    userId = userId,
                    accessToken = access,
                    accessTokenExpireAt = accessTokenExpireAt,
                    refreshToken = refresh,
                    refreshTokenExpireAt = refreshTokenExpireAt
                )
            }
        }

        respond(
            V1TokensPutOkResponse(
                V1TokensPutOkResponse.Data(
                    access = access,
                    refresh = refresh
                )
            )
        )
    }
}
