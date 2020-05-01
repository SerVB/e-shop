package io.github.servb.eShop.auth.model

import io.github.servb.eShop.auth.util.PasswordHasher
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.statements.UpdateBuilder

enum class Role {
    USER,
    ADMIN,
}

interface UserWithoutRole {

    val username: String
    val password: String
}

interface UserWithoutId : UserWithoutRole {

    val role: Role
}

object UserTable : IntIdTable() {
    val username = text("username")
    val hash = text("hash")
    val role = enumeration("role", Role::class)

    fun UpdateBuilder<*>.fromUserWithoutId(user: UserWithoutId) {
        this[username] = user.username
        this[hash] = PasswordHasher.createHash(user.password)
        this[role] = user.role
    }
}
