package au.com.wpay.frames.dto

import org.json.JSONObject
import java.io.Serializable

data class PaymentInstrument(
    val bin: String?,
    val created: Long?,
    val expiryMonth: String?,
    val expiryYear: String?,
    val itemId: String?,
    val nickname: String?,
    val paymentToken: String?,
    val scheme: String?,
    val status: String?,
    val suffix: String?
) : Serializable {
    companion object {
        fun fromJson(json: String) =
            fromJson(JSONObject(json))

        fun fromJson(json: JSONObject): PaymentInstrument =
            PaymentInstrument(
                json.optString("bin"),
                json.optLong("created"),
                json.optString("expiryMonth"),
                json.optString("expiryYear"),
                json.optString("itemId"),
                json.optString("nickname"),
                json.optString("paymentToken"),
                json.optString("scheme"),
                json.optString("status"),
                json.optString("suffix")
            )
    }
}