package io.github.servb.eShop

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.request.httpMethod
import io.ktor.request.uri
import io.ktor.routing.route
import io.ktor.routing.routing
import io.github.servb.eShop.route.product.v1.addRoutes as addProductRoutesV1

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        jackson()
    }

    install(CallLogging) {
        format { call ->
            buildString {
                append(call.request.httpMethod.value)
                append(" ")
                append(call.request.uri)
                append(" - ")
                append(call.response.status())
            }
        }
    }

    routing {
        route("v1") {
            addProductRoutesV1()
        }
    }
}
