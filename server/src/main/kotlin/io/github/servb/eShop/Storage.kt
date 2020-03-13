package io.github.servb.eShop

import io.github.servb.eShop.model.InMemoryProduct
import io.github.servb.eShop.model.ProductTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.locks.ReentrantReadWriteLock

lateinit var storage: Storage

sealed class Storage

class InMemory : Storage() {

    val productsStorage = mutableMapOf<Int, InMemoryProduct>()
    val productsStorageRwLock = ReentrantReadWriteLock()
}

class Db(
    dbPort: Int,
    dbUser: String,
    dbPassword: String,
    dbHost: String,
    dbDb: String
) : Storage() {

    init {
        Database.connect(
            url = "jdbc:postgresql://$dbHost:$dbPort/$dbDb",
            user = dbUser,
            password = dbPassword
        )

        transaction {
            SchemaUtils.createMissingTablesAndColumns(ProductTable)
        }
    }
}
