package resolver

import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress

class DNSResolver {
    fun getIPOfHost(host: String): Map<String, String> {
        try {
            val ipas = InetAddress.getAllByName(host)
            val ips = mutableMapOf<String, String>()
            for (ipa in ipas) {
                when (ipa) {
                    is Inet4Address -> ips["ipv4"] = ipa.hostAddress
                    is Inet6Address -> ips["ipv6"] = ipa.hostAddress
                }
            }
            return ips
        }
        catch (ex: Exception) {
            println(ex.localizedMessage)
            return emptyMap()
        }
    }
}
