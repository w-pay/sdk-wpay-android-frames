package au.com.wpay.frames.dto

import org.json.JSONObject

data class Status(
    val auditID: String?,
    val error: Error?,
    val esResponse: Any?,
    val messageId: Any?,
    val responseCode: String?,
    val responseText: String?,
    val txnTime: Long?
) {
    companion object {
        fun fromJson(json: String) =
            fromJson(JSONObject(json))

        fun fromJson(json: JSONObject): Status =
            Status(
                json.optString("auditID"),
                json.optJSONObject("error")?.let { Error.fromJson(it) },
                json.opt("esResponse"),
                json.opt("messageId"),
                json.optString("responseCode"),
                json.optString("responseText"),
                json.optLong("txnTime")
            )
    }
}