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
import io.github.servb.eShop.auth.model.UserTable
import io.github.servb.eShop.auth.model.UserWithoutId
import io.github.servb.eShop.util.Do
import io.github.servb.eShop.util.OptionalResult
import io.github.servb.eShop.util.SuccessResult
import io.ktor.http.HttpStatusCode
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

@Request("Create uses request body.")
data class V1UsersPostRequestBody(val users: List<User>) {

    companion object {

        val EXAMPLE = V1UsersPostRequestBody(listOf(User("myName", "pass")))
    }

    data class User(
        override val username: String,
        override val password: String
    ) : UserWithoutId
}

@Response("The users has been created.", statusCode = 200)
object V1UsersPostOkResponse : OptionalResult<SuccessResult> {

    override val data = SuccessResult.SUCCESS
}

data class V1UsersPostConflictResponse(override val data: List<String>) : OptionalResult<List<String>> {

    companion object {

        fun fromException(e: ConflictFoundException) = V1UsersPostConflictResponse(e.conflictAccounts)

        val EXAMPLE = V1UsersPostConflictResponse(listOf("myName"))
    }
}

class ConflictFoundException(val conflictAccounts: List<String>) : Throwable()

fun NormalOpenAPIRoute.createUsers(database: Database) {
    route("users") {
        throws(
            status = HttpStatusCode.BadRequest.description("A request body decoding error."),
            example = OptionalResult.FAIL,
            exClass = JsonProcessingException::class
        ) {
            throws(
                status = HttpStatusCode.Conflict.description("The listed usernames already exist."),
                example = V1UsersPostConflictResponse.EXAMPLE,
                gen = V1UsersPostConflictResponse.Companion::fromException
            ) {
                post(database)
            }
        }
    }
}

private fun NormalOpenAPIRoute.post(database: Database) {
    post<Unit, V1UsersPostOkResponse, V1UsersPostRequestBody>(
        info(
            summary = "Create users.",
            description = "Returns `${OptionalResult::class.simpleName}` saying whether the users has been created."
        ),
        exampleResponse = V1UsersPostOkResponse,
        exampleRequest = V1UsersPostRequestBody.EXAMPLE
    ) { _, body ->
        val conflicts = mutableListOf<String>()

        body.users.forEach { user ->
            newSuspendedTransaction(db = database) {
                Do exhaustive when (UserTable.select { UserTable.username.eq(user.username) }.count()) {
                    0L -> UserTable.insert { it.fromUserWithoutId(user) }

                    else -> conflicts.add(user.username)
                }
            }
        }

        Do exhaustive when (conflicts.isEmpty()) {
            true -> respond(V1UsersPostOkResponse)
            false -> throw ConflictFoundException(conflicts)
        }
    }
}
