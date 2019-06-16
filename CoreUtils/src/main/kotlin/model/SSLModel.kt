package model

import com.beust.klaxon.Json

data class SSLModel(@Json(name = "host") val host: String,
                    @Json(name = "certificate") val certificate: String)