package au.com.wpay.frames.dto

import org.json.JSONObject
import java.io.Serializable

data class ValidateCardResponse(
    val challengeResponse: ChallengeResponse?
) : Serializable {
    companion object {
        fun fromJson(json: String) =
            fromJson(JSONObject(json))

        fun fromJson(json: JSONObject): ValidateCardResponse =
            ValidateCardResponse(
                json.optJSONObject("challengeResponse")?.let { ChallengeResponse.fromJson(it) }
            )
    }
}