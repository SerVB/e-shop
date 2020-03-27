package io.github.servb.eShop.util

interface SuccessResult {

    val ok: Boolean

    object FAIL : SuccessResult {

        override val ok = false
    }
}

interface OptionalResult<out ResultType : Any> {

    val data: ResultType?

    object FAIL : OptionalResult<Nothing> {

        override val data = null
    }
}
