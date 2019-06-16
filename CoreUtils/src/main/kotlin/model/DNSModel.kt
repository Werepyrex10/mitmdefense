package model

import com.beust.klaxon.Json

data class DNSModel(@Json(name = "host") val host: String,
                    @Json(name = "ipv4") val ipv4: List<String>,
                    @Json(name = "ipv6") val ipv6: List<String>)