package http

import com.beust.klaxon.Klaxon
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import kotlinx.coroutines.*
import metrics.Metrics
import model.DNSModel
import model.ResponseModel
import model.SSLModel
import resolver.DNSResolver
import resolver.SSLResolver
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

class Client (private val port: Int) {
    private val klaxon = Klaxon()
    private val client = HttpClient()
    private val dnsResolver = DNSResolver()
    private val sslResolver = SSLResolver()

    private suspend fun dnsFlow(host: String) {
        val duration = measureTimeMillis {
            val dnsResult = dnsResolver.getIPOfHost(host).withDefault { "" }
            checkDNS(host, listOf(dnsResult.getValue("ipv4")), listOf(dnsResult.getValue("ipv6")))
        }
        Metrics.timerDNSRequest.set(duration)
    }

    private suspend fun sslFlow(host: String) {
        val duration = measureTimeMillis {
            val certificate = sslResolver.getCertificateOfHost(host)
            checkSSL(host, certificate)
        }

        Metrics.timerSSLRequest.set(duration)
    }

    private suspend fun checkDNS(host: String, ipv4: List<String>, ipv6: List<String>): Boolean {
        val model = DNSModel(host, ipv4, ipv6)
        val body = klaxon.toJsonString(model)
        val response = klaxon.parse<ResponseModel>(client.post<String>(
            port = port,
            path = "/checkDNS",
            body = body
        ))

        if (response!!.response == "success") {
            println("DNS SUCCESS!")
            return true
        } else {
            return false
            println("DNS HACKED!")
        }
    }

    private suspend fun checkSSL(host: String, certificate: String) {
        val model = SSLModel(host, certificate)
        val body = klaxon.toJsonString(model)

        try {
            val response = klaxon.parse<ResponseModel>(
                client.post<String>(
                    port = port,
                    path = "/checkSSL",
                    body = body
                )
            )

            if (response!!.response == "success") {
                println("SSL SUCCESS!")
            } else {
                println("SSL HACKED!")
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
        }
    }

    private suspend fun doWork(host: String, times: Int) {
        for(i in 1..times) {
            try {
                val result = client.get<String>(
                    host = host
                )
            } catch (e: Exception) {

            }
        }
    }

    fun start(endpoints: List<String>, check: Boolean, work: Int) {

           while(true) {
               runBlocking {
                endpoints.forEach {
                    val dnsHost = it
                    val sslHost = "https://$dnsHost"

                    println("Start $sslHost")

                    val duration = measureNanoTime {
                        if (check) {
                            dnsFlow(dnsHost)
                            sslFlow(sslHost)
                        }
                        doWork(sslHost, work)
                    }

                    Metrics.timerOverall.set(duration)

                    println("End $sslHost")
                }
            }
        }
    }
}
