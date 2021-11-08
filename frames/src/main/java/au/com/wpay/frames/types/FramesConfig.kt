package au.com.wpay.frames.types

import org.json.JSONObject
import java.io.Serializable

data class FramesConfig(
    val apiKey: String,
    val authToken: String,
    val apiBase: String,
    val logLevel: LogLevel = LogLevel.ERROR
) : Serializable {
    fun toJson(): JSONObject =
        JSONObject().apply {
            put("apiKey", apiKey)
            put("authToken", authToken)
            put("apiBase", apiBase)
            put("logLevel", logLevel.level)
        }
}