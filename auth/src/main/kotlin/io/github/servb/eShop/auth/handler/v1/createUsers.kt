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
import io.github.servb.eShop.util.Do
import io.github.servb.eShop.util.OptionalResult
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

data class V1UsersPostRequestParams(
    @HeaderParam("Auth token.")
    val `X-Access-Token`: String?
)

@Request("Create uses request body.")
data class V1UsersPostRequestBody(val users: List<User>) {

    companion object {

        val EXAMPLE = V1UsersPostRequestBody(
            listOf(
                User("myName", "pass", Role.USER),
                User("myName1", "pass", Role.USER),
                User("myName2", "pass", Role.ADMIN)
            )
        )
    }

    data class User(
        override val username: String,
        override val password: String,
        override val role: Role
    ) : UserWithoutId
}

@Response("Result response.", statusCode = 200)
class V1UsersPostOkResponse(override val data: ResultLists) : OptionalResult<ResultLists>

data class ResultLists(
    val created: List<String>,
    val conflicts: List<String>,
    val noRights: List<String>
) {

    companion object {

        val EXAMPLE = ResultLists(
            listOf("myName"),
            listOf("myName1"),
            listOf("myName2")
        )
    }
}

fun NormalOpenAPIRoute.createUsers(database: Database) {
    route("users") {
        throws(
            status = HttpStatusCode.BadRequest.description("A request body decoding error."),
            example = OptionalResult.FAIL,
            exClass = JsonProcessingException::class
        ) {
            post(database)
        }
    }
}

private fun NormalOpenAPIRoute.post(database: Database) {
    post<V1UsersPostRequestParams, V1UsersPostOkResponse, V1UsersPostRequestBody>(
        info(
            summary = "Create users.",
            description = "Returns `${OptionalResult::class.simpleName}` saying whether the users has been created."
        ),
        exampleResponse = V1UsersPostOkResponse(ResultLists.EXAMPLE),
        exampleRequest = V1UsersPostRequestBody.EXAMPLE
    ) { params, body ->
        val creatorUserType = retrieveUserType(database, params.`X-Access-Token`)
        val hasAdmins = newSuspendedTransaction(db = database) {
            UserTable.select { UserTable.role.eq(Role.ADMIN) }.firstOrNull() != null
        }

        if (hasAdmins && creatorUserType != AccessTokenValidationReply.UserType.Admin) {
            respond(
                V1UsersPostOkResponse(
                    ResultLists(
                        created = emptyList(),
                        conflicts = emptyList(),
                        noRights = body.users.map(V1UsersPostRequestBody.User::username)
                    )
                )
            )
            @Suppress("LABEL_NAME_CLASH")
            return@post
        }

        val created = mutableListOf<String>()
        val conflicts = mutableListOf<String>()

        body.users.forEach { userToCreate ->
            newSuspendedTransaction(db = database) {
                Do exhaustive when (UserTable.select { UserTable.username.eq(userToCreate.username) }.count()) {
                    0L -> {
                        UserTable.insert { it.fromUserWithoutId(userToCreate) }
                        created.add(userToCreate.username)
                    }

                    else -> conflicts.add(userToCreate.username)
                }
            }
        }

        respond(
            V1UsersPostOkResponse(
                ResultLists(
                    created = created,
                    conflicts = conflicts,
                    noRights = emptyList()
                )
            )
        )
    }
}
