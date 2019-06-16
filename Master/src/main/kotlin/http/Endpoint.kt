package http

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import metrics.Metrics

class Endpoint(port: Int) {
    private val server = embeddedServer(Netty, port = port) {
        routing {
            get("/metrics") {
                println("Got metrics request")
                call.respondText(Metrics.registry.scrape(), ContentType.Text.Plain)
            }
        }
    }

    fun start() {
        server.start()
    }
}
