package io.github.servb.eShop.auth.handler.v1

import com.fasterxml.jackson.core.JsonProcessingException
import com.papsign.ktor.openapigen.annotations.Request
import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import io.github.servb.eShop.auth.model.SessionTable
import io.github.servb.eShop.auth.model.UserTable
import io.github.servb.eShop.auth.model.UserWithoutId
import io.github.servb.eShop.auth.util.ParamsProvider
import io.github.servb.eShop.auth.util.PasswordHasher
import io.github.servb.eShop.auth.util.TokenCreator
import io.github.servb.eShop.util.OptionalResult
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime

@Request("Create tokens request body.")
data class V1TokensPostRequestBody(
    override val username: String,
    override val password: String
) : UserWithoutId {

    companion object {

        val EXAMPLE = V1TokensPostRequestBody("myName", "pass")
    }
}

@Response("The tokens have been created.", statusCode = 200)
data class V1TokensPostOkResponse(
    override val data: Data
) : OptionalResult<V1TokensPostOkResponse.Data> {

    companion object {

        val EXAMPLE = V1TokensPostOkResponse(
            Data(
                access = "ac",
                refresh = "re"
            )
        )
    }

    data class Data(val access: String, val refresh: String)
}

fun NormalOpenAPIRoute.createTokens(database: Database) {
    route("tokens") {
        throws(
            status = HttpStatusCode.BadRequest.description("A request body decoding error."),
            example = OptionalResult.FAIL,
            exClass = JsonProcessingException::class
        ) {
            throws(
                status = HttpStatusCode.Forbidden.description("Bad username and/or password."),
                example = OptionalResult.FAIL,
                exClass = IllegalArgumentException::class
            ) {
                post(database)
            }
        }
    }
}

private fun NormalOpenAPIRoute.post(database: Database) {
    post<Unit, V1TokensPostOkResponse, V1TokensPostRequestBody>(
        info(
            summary = "Create tokens.",
            description = "Returns `${OptionalResult::class.simpleName}` saying whether tokens have been created."
        ),
        exampleResponse = V1TokensPostOkResponse.EXAMPLE,
        exampleRequest = V1TokensPostRequestBody.EXAMPLE
    ) { _, body ->
        val access = TokenCreator.createToken()
        val refresh = TokenCreator.createToken()

        val now = LocalDateTime.now()

        val accessTokenExpireAt = now.plusNanos(ParamsProvider.ACCESS_TOKEN_EXPIRATION_NANOS)
        val refreshTokenExpireAt = now.plusNanos(ParamsProvider.REFRESH_TOKEN_EXPIRATION_NANOS)

        newSuspendedTransaction(db = database) {
            val row = requireNotNull(
                UserTable
                    .select {
                        UserTable.username.eq(body.username) and UserTable.hash.eq(PasswordHasher.createHash(body.password))
                    }
                    .singleOrNull()
            )

            val userId = row[UserTable.id].value

            SessionTable.deleteWhere { SessionTable.userId.eq(userId) }

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
            V1TokensPostOkResponse(
                V1TokensPostOkResponse.Data(
                    access = access,
                    refresh = refresh
                )
            )
        )
    }
}
