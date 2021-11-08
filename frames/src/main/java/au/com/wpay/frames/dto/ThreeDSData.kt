package au.com.wpay.frames.dto

import org.json.JSONObject
import java.io.Serializable

data class ThreeDSData(
    val actionCode: String?,
    val errorDescription: String?,
    val errorNumber: Int?,
    val payment: Payment?,
    val validated: Boolean?
) : Serializable {
    companion object {
        fun fromJson(json: String) =
            fromJson(JSONObject(json))

        fun fromJson(json: JSONObject): ThreeDSData =
            ThreeDSData(
                json.optString("ActionCode"),
                json.optString("ErrorDescription"),
                json.optInt("ErrorNumber"),
                json.optJSONObject("Payment")?.let { Payment.fromJson(it) },
                json.optBoolean("Validated")
            )
    }
}