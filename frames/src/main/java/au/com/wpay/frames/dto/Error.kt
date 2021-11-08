package au.com.wpay.frames.dto

import org.json.JSONObject
import java.io.Serializable

data class Error(
    val context: String?,
    val correction: String?,
    val description: String?
) : Serializable {
    companion object {
        fun fromJson(json: String) =
            fromJson(JSONObject(json))

        fun fromJson(json: JSONObject): Error =
            Error(
                json.optString("context"),
                json.optString("correction"),
                json.optString("description")
            )
    }
}