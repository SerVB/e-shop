package io.github.servb.eShop.auth.grpc.client

import io.github.servb.eShop.auth.grpc.protocol.AccessTokenValidationGrpcKt
import io.github.servb.eShop.auth.grpc.protocol.AccessTokenValidationReply
import io.github.servb.eShop.auth.grpc.protocol.AccessTokenValidationRequest
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.coroutineScope
import java.io.Closeable
import java.util.concurrent.TimeUnit

class AccessTokenValidationClient(host: String, port: Int) : Closeable {

    private val channel = ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext()
        .executor(Dispatchers.Default.asExecutor())
        .build()

    private val stub = AccessTokenValidationGrpcKt.AccessTokenValidationCoroutineStub(channel)

    suspend fun requestUserType(accessToken: String): ValidUserType? = coroutineScope {
        val request = AccessTokenValidationRequest
            .newBuilder()
            .setAccessToken(accessToken)
            .build()

        val response = stub.validateAccessToken(request)

        when (response.userType) {
            AccessTokenValidationReply.UserType.User -> ValidUserType.USER
            AccessTokenValidationReply.UserType.Admin -> ValidUserType.ADMIN
            else -> null
        }
    }

    override fun close() {
        channel.shutdown().awaitTermination(0, TimeUnit.MILLISECONDS)
    }
}
