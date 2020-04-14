package io.github.servb.eShop

import com.papsign.ktor.openapigen.APITag
import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.openAPIGen
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.tag
import com.papsign.ktor.openapigen.schema.namer.DefaultSchemaNamer
import com.papsign.ktor.openapigen.schema.namer.SchemaNamer
import io.github.servb.eShop.route.product.v1.addProductV1Routes
import io.github.servb.eShop.util.logRequests
import io.github.servb.eShop.util.logResponses
import io.ktor.application.Application
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import kotlin.math.roundToInt
import kotlin.reflect.KType

private const val OPEN_API_JSON_PATH = "/openapi.json"

private val exampleServiceStatusUsable = ServiceStatusUsable(name = "my-service", uptime = "123s")

private const val SERVICE_TITLE = "e-shop-product"

const val DB_PORT_ENV_NAME = "DB_PORT"
const val DB_USER_ENV_NAME = "DB_USER"
const val DB_PASSWORD_ENV_NAME = "DB_PASSWORD"
const val DB_HOST_ENV_NAME = "DB_HOST"
const val DB_DB_ENV_NAME = "DB_DB"
const val AUTH_PORT_ENV_NAME = "AUTH_PORT"
const val AUTH_HOST_ENV_NAME = "AUTH_HOST"

@Suppress("unused") // Referenced in application.conf
fun Application.module() = module(
    dbPort = System.getenv(DB_PORT_ENV_NAME)!!.toInt(),
    dbUser = System.getenv(DB_USER_ENV_NAME)!!,
    dbPassword = System.getenv(DB_PASSWORD_ENV_NAME)!!,
    dbHost = System.getenv(DB_HOST_ENV_NAME)!!,
    dbDb = System.getenv(DB_DB_ENV_NAME)!!,
    authPort = System.getenv(AUTH_PORT_ENV_NAME)!!.toInt(),
    authHost = System.getenv(AUTH_HOST_ENV_NAME)!!
)

@OptIn(KtorExperimentalAPI::class)
fun Application.module(
    dbPort: Int,
    dbUser: String,
    dbPassword: String,
    dbHost: String,
    dbDb: String,
    authPort: Int,
    authHost: String
) {
    val serviceStartMillis = System.currentTimeMillis()

    val connection = DatabaseConnection(
        dbPort = dbPort,
        dbUser = dbUser,
        dbPassword = dbPassword,
        dbHost = dbHost,
        dbDb = dbDb
    )

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

        //rename DTOs from java type name to generator compatible form
        replaceModule(DefaultSchemaNamer, object : SchemaNamer {
            val regex = Regex("[A-Za-z0-9_.]+")

            override fun get(type: KType): String {
                return type.toString().replace(regex) { it.value.split(".").last() }.replace(Regex(">|<|, "), "_")
            }
        })
    }

    logRequests()
    logResponses()

    val httpClient = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
    }
    val authBaseUrl = "http://$authHost:$authPort"

    routing {
        get(OPEN_API_JSON_PATH) {
            call.respond(application.openAPIGen.api.serialize())
        }

        get("swagger-ui") {
            call.respondRedirect("/swagger-ui/index.html?url=$OPEN_API_JSON_PATH", true)
        }
    }

    apiRouting {
        tag(Tag.Misc) {
            get<Unit, ServiceStatusUsable>(
                info(
                    summary = "Get service status.",
                    description = "Health check: returns the name and uptime."
                ),
                example = exampleServiceStatusUsable
            ) {
                val uptimeS = System.currentTimeMillis().minus(serviceStartMillis).toDouble().div(1000).roundToInt()

                respond(ServiceStatusUsable(name = SERVICE_TITLE, uptime = "${uptimeS}s"))
            }
        }

        tag(Tag.V1) {
            route("v1") {
                addProductV1Routes(connection.database, httpClient, authBaseUrl)
            }
        }
    }
}

@Response("A Service Status Response.")
data class ServiceStatusUsable(val name: String, val uptime: String)

enum class Tag(override val description: String) : APITag {

    V1("Version 1 API."),
    Misc("Unclassified API."),
}
