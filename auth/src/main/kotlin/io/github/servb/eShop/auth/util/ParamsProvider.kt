package io.github.servb.eShop.auth.util

import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.TemporalAmount

object ParamsProvider {

    private val DEFAULT_ACCESS_TOKEN_EXPIRATION_NANOS = Duration.ofMinutes(15).toNanos()
    private val DEFAULT_REFRESH_TOKEN_EXPIRATION_NANOS = Duration.ofHours(4).toNanos()

    val ACCESS_TOKEN_EXPIRATION_NANOS = System.getenv("E_SHOP_ACCESS_TOKEN_EXPIRATION_NANOS")?.toLongOrNull()
        ?: DEFAULT_ACCESS_TOKEN_EXPIRATION_NANOS

    val REFRESH_TOKEN_EXPIRATION_NANOS = System.getenv("E_SHOP_REFRESH_TOKEN_EXPIRATION_NANOS")?.toLongOrNull()
        ?: DEFAULT_REFRESH_TOKEN_EXPIRATION_NANOS
}
