package au.com.wpay.frames.dto

import org.json.JSONObject
import java.io.Serializable

data class ChallengeResponse(
    val instrumentId: String?,
    val reference: String?,
    val token: String?,
    val type: String?
) : Serializable {
    companion object {
        fun fromJson(json: String) =
            fromJson(JSONObject(json))

        fun fromJson(json: JSONObject): ChallengeResponse =
            ChallengeResponse(
                json.optString("instrumentId"),
                json.optString("reference"),
                json.optString("token"),
                json.optString("type")
            )

    }

    fun toJson(): JSONObject =
        JSONObject().apply {
            instrumentId?.let { put("instrumentId", it) }
            reference?.let { put("reference", it) }
            token?.let { put("token", it) }
            type?.let { put("type", it) }
        }
}