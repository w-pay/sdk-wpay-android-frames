package au.com.wpay.frames.dto

import org.json.JSONObject
import java.io.Serializable

data class CardCaptureResponse(
    val fraudResponse: FraudResponse?,
    val paymentInstrument: PaymentInstrument?,
    val status: Status?,
    val stepUpToken: String?,
    val threeDSError: ThreeDSError?,
    val message: String?,
    val threeDSToken: String?
) : Serializable {
    companion object {
        fun fromJson(json: String): CardCaptureResponse =
            fromJson(JSONObject(json))

        fun fromJson(json: JSONObject): CardCaptureResponse =
            CardCaptureResponse(
                json.optJSONObject("fraudResponse")?.let { FraudResponse.fromJson(it) },
                json.optJSONObject("paymentInstrument")?.let { PaymentInstrument.fromJson(it) },
                json.optJSONObject("status")?.let { Status.fromJson(it) },
                json.optString("stepUpToken"),
                json.optString("errorCode")?.let { ThreeDSError.fromCode(it) },
                json.optString("message"),
                json.optString("token")
            )
    }
}





