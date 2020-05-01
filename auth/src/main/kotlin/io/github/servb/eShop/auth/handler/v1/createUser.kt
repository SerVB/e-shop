package io.github.servb.eShop.auth.handler.v1

import com.fasterxml.jackson.core.JsonProcessingException
import com.papsign.ktor.openapigen.annotations.Request
import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.annotations.parameters.HeaderParam
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import io.github.servb.eShop.auth.grpc.protocol.AccessTokenValidationReply
import io.github.servb.eShop.auth.model.Role
import io.github.servb.eShop.auth.model.UserTable
import io.github.servb.eShop.auth.model.UserWithoutId
import io.github.servb.eShop.auth.retrieveUserType
import io.github.servb.eShop.auth.util.ForbiddenException
import io.github.servb.eShop.util.SuccessResult
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

data class V1UserPostRequestParams(
    @HeaderParam("Auth token.")
    val `X-Access-Token`: String?
)

@Request("Create user request body.")
data class V1UserPostRequestBody(
    override val username: String,
    override val password: String,
    override val role: Role
) : UserWithoutId {

    companion object {

        val EXAMPLE = V1UserPostRequestBody("myName", "pass", Role.ADMIN)
    }
}

@Response("The user has been created.", statusCode = 200)
object V1UserPostOkResponse : SuccessResult {

    override val ok = true
}

fun NormalOpenAPIRoute.createUser(database: Database) {
    route("user") {
        throws(
            status = HttpStatusCode.BadRequest.description("A request body decoding error."),
            example = SuccessResult.FAIL,
            exClass = JsonProcessingException::class
        ) {
            throws(
                status = HttpStatusCode.Conflict.description("A user with the same username already exist."),
                example = SuccessResult.FAIL,
                exClass = IllegalArgumentException::class
            ) {
                throws(
                    status = HttpStatusCode.Forbidden.description("Can't create an admin without admin rights."),
                    example = SuccessResult.FAIL,
                    exClass = ForbiddenException::class
                ) {
                    post(database)
                }
            }
        }
    }
}

private fun NormalOpenAPIRoute.post(database: Database) {
    post<V1UserPostRequestParams, V1UserPostOkResponse, V1UserPostRequestBody>(
        info(
            summary = "Create a user.",
            description = "Returns `${SuccessResult::class.simpleName}` saying whether the user has been created."
        ),
        exampleResponse = V1UserPostOkResponse,
        exampleRequest = V1UserPostRequestBody.EXAMPLE
    ) { params, body ->
        newSuspendedTransaction(db = database) {
            require(UserTable.select { UserTable.username.eq(body.username) }.count() == 0L)

            if (body.role == Role.ADMIN) {
                val userType = retrieveUserType(database, params.`X-Access-Token`)

                if (userType != AccessTokenValidationReply.UserType.Admin) {
                    throw ForbiddenException
                }
            }

            UserTable.insert { it.fromUserWithoutId(body) }
        }

        respond(V1UserPostOkResponse)
    }
}
