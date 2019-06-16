package http

import com.beust.klaxon.Klaxon
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.request.receiveText
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import model.DNSModel
import model.ResponseModel
import model.SSLModel
import resolver.DNSResolver
import resolver.SSLResolver

class Endpoint(port: Int) {
    private val dnsResolver = DNSResolver()
    private val sslResolver = SSLResolver()
    private val klaxon = Klaxon()

    private val metricsServer = embeddedServer(Netty, port = 9330) {
        routing {
            get("/metrics") {

            }
        }
    }

    private val server = embeddedServer(Netty, port = port) {
        routing {
            post("/checkDNS") {
                val body = call.receiveText()
                val masterDNSResult = klaxon.parse<DNSModel>(body)!!

                val result = dnsResolver.getIPOfHost(masterDNSResult.host)

                val matches = masterDNSResult.ipv4.filter { it != result["ipv4"] }.asSequence() +
                        masterDNSResult.ipv6.filter { it != result["ipv6"] }

                var response = "failed"

                if (matches.toList().isEmpty()) {
                    response = "success"
                }

                call.respondText(klaxon.toJsonString(ResponseModel(response)), ContentType.Application.Json)
            }
            post("/checkSSL") {
                val body = call.receiveText()
                val masterSSLResult = klaxon.parse<SSLModel>(body)!!

                val result = sslResolver.getCertificateOfHost(masterSSLResult.host)

                var response = "failed"

                if (result== masterSSLResult.certificate) {
                    response = "success"
                }

                call.respondText(klaxon.toJsonString(ResponseModel(response)), ContentType.Application.Json)
            }
        }
    }

    fun start() {
        server.start(wait = true)
    }
}
