package au.com.wpay.frames.types

import au.com.wpay.frames.CreateActionCommand
import org.json.JSONObject
import java.io.Serializable

/**
 * Helper to create an instance of [CreateActionCommand] with some type safety.
 */
sealed class ActionType(
    private val type: String,
    private val payload: Payload? = null
) : Serializable {
    interface Payload {
        fun toJson(): JSONObject
    }

    /**
     * Converts an [ActionType] to a [CreateActionCommand]
     */
    fun toCommand(name: String): CreateActionCommand =
        CreateActionCommand(name, type, payload?.toJson())

    /**
     * This matches the `CaptureCard` action type in the JS SDK.
     */
    class CaptureCard(
        payload: Payload? = null
    ) : ActionType("CaptureCard", payload), Serializable {
        /**
         * Options for the action.
         *
         * By passing null for `env3DS` 3DS won't be used. Passing a value will see the action
         * use 3DS.
         */
        data class Payload(
            val verify: Boolean,
            val save: Boolean,
            val useEverydayPay: Boolean = false,
            val env3DS: ThreeDSEnv? = null
        ) : ActionType.Payload, Serializable {
            override fun toJson() = JSONObject().apply {
                put("verify", verify)
                put("save", save)
                put("useEverydayPay", useEverydayPay)

                env3DS?.let {
                    put("threeDS", JSONObject().apply {
                        put("requires3DS", true)
                        put("env", it.env)
                    })
                }
            }
        }
    }

    class StepUp(
        payload: Payload?
    ) : ActionType("StepUp", payload), Serializable {
        data class Payload(
            val paymentInstrumentId: String,
            val scheme: String
        ) : ActionType.Payload, Serializable {
            override fun toJson() = JSONObject().apply {
                put("paymentInstrumentId", paymentInstrumentId)
                put("scheme", scheme)
            }
        }
    }

    class UpdateCard(
        payload: Payload?
    ) : ActionType("UpdateCard", payload), Serializable {
        data class Payload(
            val paymentInstrumentId: String,
            val scheme: String
        ) : ActionType.Payload, Serializable {
            override fun toJson() = JSONObject().apply {
                put("paymentInstrumentId", paymentInstrumentId)
                put("scheme", scheme)
            }
        }
    }

    class ValidateCard(
        payload: Payload?
    ) : ActionType("ValidateCard", payload), Serializable {
        data class Payload(
            val sessionId: String,
            val env3DS: ThreeDSEnv,
            val acsWindowSize: AcsWindowSize = AcsWindowSize.ACS_250x400
        ) : ActionType.Payload, Serializable {
            override fun toJson() = JSONObject().apply {
                put("sessionId", sessionId)
                put("threeDS", JSONObject().apply {
                    put("env", env3DS)
                    put("consumerAuthenticationInformation", JSONObject().apply {
                        put("acsWindowSize", acsWindowSize.code)
                    })
                })
            }
        }
    }

    object ValidatePayment : ActionType("ValidatePayment")

    enum class AcsWindowSize(val code: String) {
        ACS_250x400("01"),
        ACS_390x400("02"),
        ACS_500x600("03"),
        ACS_600x400("04"),
        ACS_FULL_PAGE("05")
    }
}