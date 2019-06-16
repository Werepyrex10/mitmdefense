package metrics

import com.typesafe.config.ConfigFactory
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import java.util.concurrent.atomic.AtomicLong

object Metrics {

    val registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    val timerDNSRequest = registry.gauge("request.dns.time", AtomicLong(0))
    val timerSSLRequest = registry.gauge("request.ssl.time", AtomicLong(0))
    val timerOverall = registry.gauge("request.total.time", AtomicLong(0))

    init {
        val config = ConfigFactory.load()
        registry.config().commonTags(config.getString("metrics.tag"), "mitm")
    }
}
