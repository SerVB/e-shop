package io.github.servb.eShop.auth.util

import java.security.MessageDigest


object PasswordHasher {

    fun createHash(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")

        return digest.digest(password.toByteArray()).joinToString(separator = "") { (it + 128).toString(16) }
    }
}
