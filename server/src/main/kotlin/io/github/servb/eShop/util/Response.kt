package io.github.servb.eShop.util

import com.papsign.ktor.openapigen.annotations.Response

@Response("A Success Result Response.")
data class SuccessResult(val ok: Boolean) {

    companion object {

        val NOT_OK = SuccessResult(ok = false)
        val OK = SuccessResult(ok = true)
    }
}

@Response("An Optional Result Response. Contains `null` in case of a failure and an object otherwise.")
data class OptionalResult<out T>(val data: T) {

    companion object {

        val NOT_OK = OptionalResult(data = null)
    }
}
