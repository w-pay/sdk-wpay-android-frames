package au.com.wpay.frames.dto

import org.json.JSONObject

data class Status(
    val auditID: String?,
    val error: Error?,
    val esResponse: EsResponse?,
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
                json.optJSONObject("esResponse")?.let { EsResponse.fromJson(it) },
                json.optString("responseCode"),
                json.optString("responseText"),
                json.optLong("txnTime")
            )
    }
}