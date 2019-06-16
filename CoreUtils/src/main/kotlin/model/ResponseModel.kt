package model

import com.beust.klaxon.Json

data class ResponseModel(@Json(name = "response") val response: String)
