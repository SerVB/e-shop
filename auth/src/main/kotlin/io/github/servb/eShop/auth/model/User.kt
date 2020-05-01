package io.github.servb.eShop.auth.model

import io.github.servb.eShop.auth.util.PasswordHasher
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.statements.UpdateBuilder

interface UserWithoutId {

    val username: String
    val password: String
}

object UserTable : IntIdTable() {
    val username = text("username")
    val hash = text("hash")

    fun UpdateBuilder<*>.fromUserWithoutId(user: UserWithoutId) {
        this[username] = user.username
        this[hash] = PasswordHasher.createHash(user.password)
    }
}
