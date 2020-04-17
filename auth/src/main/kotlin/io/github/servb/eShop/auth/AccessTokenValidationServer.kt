package io.github.servb.eShop.auth

import io.github.servb.eShop.auth.grpc.protocol.AccessTokenValidationGrpcKt
import io.github.servb.eShop.auth.grpc.protocol.AccessTokenValidationReply
import io.github.servb.eShop.auth.grpc.protocol.AccessTokenValidationRequest
import io.github.servb.eShop.auth.model.SessionTable
import io.grpc.Server
import io.grpc.ServerBuilder
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import kotlin.concurrent.thread

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
            val now = LocalDateTime.now()

            val foundMatches = newSuspendedTransaction(db = database) {
                SessionTable
                    .select {
                        SessionTable.accessToken.eq(request.accessToken) and SessionTable.accessTokenExpireAt.greater(
                            now
                        )
                    }
                    .count()
            }

            val userType = when {
                foundMatches == 1L -> AccessTokenValidationReply.UserType.User

                else -> AccessTokenValidationReply.UserType.NotUser
            }

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
