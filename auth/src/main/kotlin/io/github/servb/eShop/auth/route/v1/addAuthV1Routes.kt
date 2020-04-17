package io.github.servb.eShop.auth.route.v1

import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import io.github.servb.eShop.auth.handler.v1.createTokens
import io.github.servb.eShop.auth.handler.v1.createUser
import io.github.servb.eShop.auth.handler.v1.updateTokens
import org.jetbrains.exposed.sql.Database

fun NormalOpenAPIRoute.addAuthV1Routes(database: Database) {
    createUser(database)
    createTokens(database)
    updateTokens(database)
}
