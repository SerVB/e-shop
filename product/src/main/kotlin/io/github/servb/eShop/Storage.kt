package io.github.servb.eShop

import io.github.servb.eShop.model.InMemoryProduct
import io.github.servb.eShop.model.ProductTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantReadWriteLock

lateinit var storage: Storage

sealed class Storage

class InMemory : Storage() {

    val productsStorage = mutableMapOf<Int, InMemoryProduct>()
    val productsStorageRwLock = ReentrantReadWriteLock()

    val nextId = AtomicInteger()
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
