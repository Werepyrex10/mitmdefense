package resolver

import java.net.URL
import java.security.MessageDigest
import java.util.*
import javax.net.ssl.HttpsURLConnection


class SSLResolver {
    private val hasher  = MessageDigest.getInstance("SHA-256")
    private val encoder = Base64.getEncoder()

    fun getCertificateOfHost(host: String): String {
        try {
            val url = URL(host)
            val urlConnection = url.openConnection()
            urlConnection.connect()

            var certs = ByteArray(0)

            if (urlConnection is HttpsURLConnection) {
                certs =
                    urlConnection.serverCertificates.map { it.publicKey.encoded }.fold(ByteArray(0), ByteArray::plus)
            }

            val inputStream = urlConnection.getInputStream()

            return encode(certs)
        } catch (e: Exception) {
            println(e.localizedMessage)
            return ""
        }
    }

    private fun encode(data: ByteArray): String {
        return encoder.encodeToString(hasher.digest(data))
    }
}
