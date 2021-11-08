package au.com.wpay.frames.dto

import org.json.JSONObject
import java.io.Serializable

data class ChallengeResponse(
    val reference: String?,
    val token: String?,
    val type: String?
) : Serializable {
    companion object {
        fun fromJson(json: String) =
            fromJson(JSONObject(json))

        fun fromJson(json: JSONObject): ChallengeResponse =
            ChallengeResponse(
                json.optString("reference"),
                json.optString("token"),
                json.optString("type")
            )
    }
}