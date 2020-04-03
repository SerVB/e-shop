package io.github.servb.eShop.auth

import io.github.servb.eShop.auth.model.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantReadWriteLock

class DatabaseConnection(
    dbPort: Int,
    dbUser: String,
    dbPassword: String,
    dbHost: String,
    dbDb: String
) {

    val database = Database.connect(
        url = "jdbc:postgresql://$dbHost:$dbPort/$dbDb",
        user = dbUser,
        password = dbPassword
    ).also {
        while (true) {
            try {
                transaction {
                    SchemaUtils.createMissingTablesAndColumns(UserTable, SessionTable)
                }

                break
            } catch (e: Throwable) {
                println("Waiting for db...")

                Thread.sleep(1000)
            }
        }
    }
}
