package io.github.servb.eShop.product

import io.github.servb.eShop.module
import io.ktor.application.Application

fun Application.inMemoryEShopProduct() = module(inMemoryStorage = true)
