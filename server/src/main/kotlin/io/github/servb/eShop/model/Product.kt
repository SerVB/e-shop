package io.github.servb.eShop.model

import java.util.concurrent.locks.ReentrantReadWriteLock

val productsStorage = mutableMapOf<Int, Product>()
val productsStorageRwLock = ReentrantReadWriteLock()

data class Product(val name: String, val id: Int, val type: Int)
