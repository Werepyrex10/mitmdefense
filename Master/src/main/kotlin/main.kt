import com.typesafe.config.ConfigFactory
import http.Client
import http.Endpoint

fun main() {
    val config = ConfigFactory.load()

    val client = Client(1337)
    val metricsServer = Endpoint(config.getInt("metrics.port"))

    metricsServer.start()
    client.start(config.getStringList("endpoints"), config.getBoolean("check"), config.getInt("workload"))
}
