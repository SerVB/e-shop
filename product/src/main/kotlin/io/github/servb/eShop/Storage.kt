package io.github.servb.eShop

import io.github.servb.eShop.model.ProductTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

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
                    SchemaUtils.createMissingTablesAndColumns(ProductTable)
                }

                break
            } catch (e: Throwable) {
                println("Waiting for db...")

                Thread.sleep(1000)
            }
        }
    }
}
