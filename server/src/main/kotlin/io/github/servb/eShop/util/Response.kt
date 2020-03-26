package io.github.servb.eShop.util

interface SuccessResult {

    val ok: Boolean
}

interface OptionalResult<out ResultType : Any> {

    val data: ResultType?
}
