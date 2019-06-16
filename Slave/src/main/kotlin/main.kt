import http.Endpoint

fun main() {
    val server = Endpoint(1337)
    server.start()
}
