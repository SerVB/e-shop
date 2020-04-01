package io.github.servb.eShop.util.kotest

import io.kotest.core.spec.style.ThenContext
import io.kotest.core.spec.style.WhenAndContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
suspend fun WhenAndContext.thenWithContract(name: String, test: suspend ThenContext.() -> Unit) {
    contract {
        callsInPlace(test, InvocationKind.EXACTLY_ONCE)
    }

    this.then(name, test)
}
