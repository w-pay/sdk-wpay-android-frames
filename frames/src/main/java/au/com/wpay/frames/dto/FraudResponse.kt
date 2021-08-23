package au.com.wpay.frames.dto

import org.json.JSONObject

data class FraudResponse(
    val fraudClientId: Any?,
    val fraudDecision: Any?,
    val fraudReasonCd: Any?
) {
    companion object {
        fun fromJson(json: String): FraudResponse =
            fromJson(JSONObject(json))

        fun fromJson(json: JSONObject): FraudResponse =
            FraudResponse(
                json.opt("fraudClientId"),
                json.opt("fraudDecision"),
                json.opt("fraudReasonCd")
            )
    }
}