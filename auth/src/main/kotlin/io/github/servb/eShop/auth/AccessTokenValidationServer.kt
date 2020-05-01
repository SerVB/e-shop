package io.github.servb.eShop.auth

import io.github.servb.eShop.auth.grpc.protocol.AccessTokenValidationGrpcKt
import io.github.servb.eShop.auth.grpc.protocol.AccessTokenValidationReply
import io.github.servb.eShop.auth.grpc.protocol.AccessTokenValidationRequest
import io.github.servb.eShop.auth.model.Role
import io.github.servb.eShop.auth.model.SessionTable
import io.github.servb.eShop.auth.model.UserTable
import io.grpc.Server
import io.grpc.ServerBuilder
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import kotlin.concurrent.thread

suspend fun retrieveUserType(database: Database, accessToken: String?): AccessTokenValidationReply.UserType {
    accessToken ?: return AccessTokenValidationReply.UserType.NotUser

    val now = LocalDateTime.now()

    val foundMatch = newSuspendedTransaction(db = database) {
        SessionTable
            .select {
                SessionTable.accessToken.eq(accessToken) and SessionTable.accessTokenExpireAt.greater(now)
            }
            .singleOrNull()
    }

    return when (foundMatch) {
        null -> AccessTokenValidationReply.UserType.NotUser

        else -> {
            val userId = foundMatch[SessionTable.userId]

            val user = newSuspendedTransaction(db = database) {
                UserTable
                    .select { UserTable.id.eq(userId) }
                    .single()
            }

            when (user[UserTable.role]) {
                Role.USER -> AccessTokenValidationReply.UserType.User
                Role.ADMIN -> AccessTokenValidationReply.UserType.Admin
            }
        }
    }
}

class AccessTokenValidationServer(database: Database, private val port: Int) {

    private val server: Server = ServerBuilder
        .forPort(port)
        .addService(AccessTokenValidationService(database))
        .build()

    fun start() {
        server.start()
        logger.info("Server started, listening on $port")

        Runtime.getRuntime().addShutdownHook(
            thread(start = false) {
                logger.info("*** shutting down gRPC server since JVM is shutting down")
                this@AccessTokenValidationServer.stop()
                logger.info("*** server shut down")
            }
        )
    }

    private fun stop() {
        server.shutdown()
    }

    private class AccessTokenValidationService(private val database: Database) :
        AccessTokenValidationGrpcKt.AccessTokenValidationCoroutineImplBase() {

        override suspend fun validateAccessToken(request: AccessTokenValidationRequest): AccessTokenValidationReply {
            val userType = retrieveUserType(database, request.accessToken)

            return AccessTokenValidationReply
                .newBuilder()
                .setUserType(userType)
                .build()
        }
    }

    companion object {

        private val logger = LoggerFactory.getLogger(AccessTokenValidationServer::class.java)
    }
}
