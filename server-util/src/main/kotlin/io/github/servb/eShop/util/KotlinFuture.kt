package io.github.servb.eShop.util

object Do { // https://youtrack.jetbrains.com/issue/KT-12380

    inline infix fun <reified T> exhaustive(any: T) = any
}
