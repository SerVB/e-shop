package io.github.servb.eShop

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.openAPIGen
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import io.github.servb.eShop.util.logRequests
import io.github.servb.eShop.util.logResponses
import io.ktor.application.Application
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlin.math.roundToInt
import io.github.servb.eShop.route.product.v1.addRoutes as addProductRoutesV1

private const val OPEN_API_JSON_PATH = "/openapi.json"

private val exampleServiceStatusUsable = ServiceStatusUsable(name = "my-service", uptime = "123s")

private const val SERVICE_TITLE = "e-shop"

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(inMemoryStorage: Boolean = false) {
    val serviceStartMillis = System.currentTimeMillis()

    storage = when (inMemoryStorage) {
        true -> InMemory()

        false -> Db(
            dbPort = System.getenv("DB_PORT")!!.toInt(),
            dbUser = System.getenv("DB_USER")!!,
            dbPassword = System.getenv("DB_PASSWORD")!!,
            dbHost = System.getenv("DB_HOST")!!,
            dbDb = System.getenv("DB_DB")!!
        )
    }

    install(ContentNegotiation) {
        jackson()
    }

    install(OpenAPIGen) {
        info {
            version = "1.0-SNAPSHOT"
            title = SERVICE_TITLE
            description =
                "This is a project to learn modern web technologies. There is the API description on this page. You can find the sources of the project on [GitHub](https://github.com/SerVB/e-shop)."
        }

        server("http://localhost:8080/") {
            description = "Local server"
        }
    }

    logRequests()
    logResponses()

    routing {
        get(OPEN_API_JSON_PATH) {
            call.respond(application.openAPIGen.api)
        }

        get("swagger-ui") {
            call.respondRedirect("/swagger-ui/index.html?url=$OPEN_API_JSON_PATH", true)
        }
    }

    apiRouting {
        get<Unit, ServiceStatusUsable>(
            info(
                summary = "Get service status.",
                description = "Returns the name and uptime."
            ),
            example = exampleServiceStatusUsable
        ) {
            val uptimeS = System.currentTimeMillis().minus(serviceStartMillis).toDouble().div(1000).roundToInt()

            respond(ServiceStatusUsable(name = SERVICE_TITLE, uptime = "${uptimeS}s"))
        }

        route("v1") {
            addProductRoutesV1()
        }
    }
}

@Response("A Service Status Response.")
data class ServiceStatusUsable(val name: String, val uptime: String)
