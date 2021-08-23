package au.com.wpay.frames.dto

import org.json.JSONObject

data class CardUpdateResponse(
    val fraudResponse: FraudResponse?,
    val itemId: Int?,
    val status: Status?,
    val stepUpToken: String?
){
    companion object {
        fun fromJson(json: String) =
            fromJson(JSONObject(json))

        fun fromJson(json: JSONObject): CardUpdateResponse =
            CardUpdateResponse(
                json.optJSONObject("fraudResponse")?.let { FraudResponse.fromJson(it) },
                json.optInt("itemId"),
                json.optJSONObject("status")?.let { Status.fromJson(it) },
                json.optString("stepUpToken")
            )
    }
}