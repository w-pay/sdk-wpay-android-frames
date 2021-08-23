package au.com.wpay.frames.dto

import org.json.JSONObject

data class Payment(
    val extendedData: ExtendedData?,
    val processorTransactionId: String?,
    val type: String?
){
    companion object {
        fun fromJson(json: String) =
            fromJson(JSONObject(json))

        fun fromJson(json: JSONObject): Payment =
            Payment(
                json.optJSONObject("ExtendedData")?.let { ExtendedData.fromJson(it) },
                json.optString("ProcessorTransactionId"),
                json.optString("Type")
            )
    }
}