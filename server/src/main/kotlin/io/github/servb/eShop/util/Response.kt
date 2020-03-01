package io.github.servb.eShop.util

import io.github.servb.eShop.constant.DATA
import io.github.servb.eShop.constant.OK

val NOT_OK_RESPONSE = mapOf(OK to false)

val OK_RESPONSE = mapOf(OK to true)

fun <T : Any> createOkResponse(data: T) = mapOf(
    OK to true,
    DATA to data
)
