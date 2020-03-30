package io.github.servb.eShop.util.kotest

import io.kotest.assertions.failure
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

// https://github.com/kotest/kotest/pull/1315
@OptIn(ExperimentalContracts::class)
fun String?.shouldBeInteger(radix: Int = 10): Int {
    contract {
        returns() implies (this@shouldBeInteger != null)
    }

    return when (this) {
        null -> throw failure("String is null, but it should be integer.")

        else -> when (val integer = this.toIntOrNull(radix)) {
            null -> throw failure("String '$this' is not integer, but it should be.")

            else -> integer
        }
    }
}
