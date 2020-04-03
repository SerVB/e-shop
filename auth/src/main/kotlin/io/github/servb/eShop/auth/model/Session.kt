package io.github.servb.eShop.auth.model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.datetime
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.time.LocalDateTime

object SessionTable : Table() {
    val userId = integer("userId")
    val accessToken = text("accessToken")
    val accessTokenExpireAt = datetime("accessTokenExpireAt")
    val refreshToken = text("refreshToken")
    val refreshTokenExpireAt = datetime("refreshTokenExpireAt")

    override val primaryKey = PrimaryKey(userId)

    fun UpdateBuilder<*>.fromUserWithId(
        userId: Int,
        accessToken: String,
        accessTokenExpireAt: LocalDateTime,
        refreshToken: String,
        refreshTokenExpireAt: LocalDateTime
    ) {
        this[SessionTable.userId] = userId
        this[SessionTable.accessToken] = accessToken
        this[SessionTable.accessTokenExpireAt] = accessTokenExpireAt
        this[SessionTable.refreshToken] = refreshToken
        this[SessionTable.refreshTokenExpireAt] = refreshTokenExpireAt
    }
}
