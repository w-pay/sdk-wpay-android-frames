package au.com.wpay.frames.dto

import org.json.JSONObject
import java.io.Serializable

data class ExtendedData(
    val amount: String?,
    val cAVV: String?,
    val currencyCode: String?,
    val eCIFlag: String?,
    val pAResStatus: String?,
    val signatureVerification: String?,
    val threeDSVersion: String?
) : Serializable {
    companion object {
        fun fromJson(json: String) =
            fromJson(JSONObject(json))

        fun fromJson(json: JSONObject): ExtendedData =
            ExtendedData(
                json.optString("Amount"),
                json.optString("CAVV"),
                json.optString("CurrencyCode"),
                json.optString("ECIFlag"),
                json.optString("PAResStatus"),
                json.optString("SignatureVerification"),
                json.optString("ThreeDSVersion")
            )
    }
}