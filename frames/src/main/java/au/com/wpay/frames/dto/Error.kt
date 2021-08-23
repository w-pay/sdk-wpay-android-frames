package au.com.wpay.frames.dto

import org.json.JSONObject

data class Error(
    val context: String?,
    val correction: String?,
    val description: String?
){
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