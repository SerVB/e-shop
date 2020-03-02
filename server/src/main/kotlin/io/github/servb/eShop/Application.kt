package io.github.servb.eShop

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.openAPIGen
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.route
import io.ktor.application.Application
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.request.httpMethod
import io.ktor.request.uri
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.get
import io.ktor.routing.routing
import io.github.servb.eShop.route.product.v1.addRoutes as addProductRoutesV1

private const val OPEN_API_JSON_PATH = "/openapi.json"

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

    install(OpenAPIGen) {
        info {
            version = "1.0-SNAPSHOT"
            title = "e-shop"
            description =
                "This is a project to learn modern web technologies. There is the API description on this page. You can find the sources of the project on [GitHub](https://github.com/SerVB/e-shop)."
        }

        server("http://localhost:8080/") {
            description = "Local server"
        }
    }

    routing {
        get(OPEN_API_JSON_PATH) {
            call.respond(application.openAPIGen.api)
        }

        get("swagger-ui") {
            call.respondRedirect("/swagger-ui/index.html?url=$OPEN_API_JSON_PATH", true)
        }
    }

    apiRouting {
        route("v1") {
            addProductRoutesV1()
        }
    }
}
