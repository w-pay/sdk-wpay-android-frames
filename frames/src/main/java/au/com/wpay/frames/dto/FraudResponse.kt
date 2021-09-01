package au.com.wpay.frames.dto

import org.json.JSONObject

data class FraudResponse(
    val fraudClientId: String?,
    val fraudDecision: String?,
    val fraudReasonCd: String?
) {
    companion object {
        fun fromJson(json: String): FraudResponse =
            fromJson(JSONObject(json))

        fun fromJson(json: JSONObject): FraudResponse =
            FraudResponse(
                json.optString("fraudClientId"),
                json.optString("fraudDecision"),
                json.optString("fraudReasonCd")
            )
    }
}