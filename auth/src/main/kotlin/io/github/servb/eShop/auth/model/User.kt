package io.github.servb.eShop.auth.model

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.statements.UpdateBuilder

interface UserWithoutId {

    val username: String
    val password: String  // todo: don't use plain pass
}

object UserTable : IntIdTable() {
    val username = text("username")
    val password = text("password")

    fun UpdateBuilder<*>.fromUserWithoutId(user: UserWithoutId) {
        this[username] = user.username
        this[password] = user.password
    }
}
