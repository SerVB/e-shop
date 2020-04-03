package io.github.servb.eShop.auth.util

import java.security.MessageDigest
import kotlin.random.Random


object TokenCreator {

    fun createToken(): String {
        val base = Random.Default.nextLong().toString(16)

        val digest = MessageDigest.getInstance("SHA-256")

        return digest.digest(base.toByteArray()).joinToString(separator = "") { (it + 128).toString(16) }
    }
}
