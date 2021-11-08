package au.com.wpay.frames.dto

import org.json.JSONObject
import java.io.Serializable

data class EsResponse(
    val code: String?,
    val text: String?
) : Serializable {
    companion object {
        fun fromJson(json: String) =
            fromJson(JSONObject(json))

        fun fromJson(json: JSONObject): EsResponse =
            EsResponse(
                json.optString("code"),
                json.optString("text")
            )
    }
}