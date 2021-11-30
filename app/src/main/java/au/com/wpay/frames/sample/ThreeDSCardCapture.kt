package au.com.wpay.frames.sample

import android.view.View
import au.com.wpay.frames.*
import au.com.wpay.frames.dto.CardCaptureResponse
import au.com.wpay.frames.dto.ThreeDSError
import au.com.wpay.frames.dto.ValidateCardResponse
import au.com.wpay.frames.types.ActionType
import au.com.wpay.frames.types.ControlType
import au.com.wpay.frames.types.ThreeDSEnv
import org.json.JSONArray

/*
 * When a Frames SDK action completes, we want to handle the result differently based on the command
 * that was being executed
 */
typealias FramesActionHandler = (String) -> Unit

/*
 * In order for 3DS to work the Merchant (API key) has to support it.
 * If you get a 400 response when trying to capture a card, then the Merchant
 * doesn't support 3DS.
 */
class ThreeDSCardCapture: FramesHost(HTML) {
    companion object {
        const val CARD_ID = "cardElement"
        const val CHALLENGE_ID = "challengeElement"

        const val CARD_CAPTURE_ACTION_NAME = "singleCardCapture"
        const val VALIDATE_CARD_ACTION_NAME = "validateCard"

        const val HTML = """
            <html>
              <body>
                <div id="$CARD_ID"></div>
                <!-- we show the challenge content when required -->
                <div id="$CHALLENGE_ID" style="display: none"></div>
              </body>
            </html>
            """
    }

    private var framesHandler: FramesActionHandler = ::onCardCapture

    override fun onComplete(response: String) {
        debug("onComplete(response: $response)")

        /*
         * Delegate to the correct handler based on what response we expect from the
         * Frames SDK.
         */
        framesHandler(response)
    }

    override fun onPageLoaded() {
        super.onPageLoaded()

        /*
         * Step 3.
         *
         * Add a single line card group to the page
         */
        post(BuildFramesCommand(
            ActionType.CaptureCard(cardCaptureOptions()).toCommand(CARD_CAPTURE_ACTION_NAME),
            StartActionCommand(CARD_CAPTURE_ACTION_NAME),
            CreateActionControlCommand(CARD_CAPTURE_ACTION_NAME, ControlType.CARD_GROUP, CARD_ID)
        ))
    }

    override fun onRendered(id: String) {
        super.onRendered(id)

        when(id) {
            VALIDATE_CARD_ACTION_NAME -> post(ShowValidationChallenge)
        }
    }

    override fun onRemoved(id: String) {
        super.onRemoved(id)

        when(id) {
            VALIDATE_CARD_ACTION_NAME -> post(HideValidationChallenge)
        }
    }

    override fun onSubmit(view: View) {
        super.onSubmit(view)

        post(SubmitFormCommand(CARD_CAPTURE_ACTION_NAME))
    }

    override fun onClear(view: View) {
        super.onClear(view)

        post(ClearFormCommand(CARD_CAPTURE_ACTION_NAME))
    }

    override fun cardCaptureOptions(): ActionType.CaptureCard.Payload {
        return ActionType.CaptureCard.Payload(
            verify = true,
            save = true,
            env3DS = ThreeDSEnv.STAGING
        )
    }

    private fun cardValidateCommand(sessionId: String): JavascriptCommand =
        GroupCommand("validateCard",
            ActionType.ValidateCard(validateCardOptions(sessionId)).toCommand(VALIDATE_CARD_ACTION_NAME),
            StartActionCommand(VALIDATE_CARD_ACTION_NAME),
            CreateActionControlCommand(VALIDATE_CARD_ACTION_NAME, ControlType.VALIDATE_CARD, CHALLENGE_ID),
            CompleteActionCommand(VALIDATE_CARD_ACTION_NAME)
        )

    private fun validateCardOptions(sessionId: String) =
        ActionType.ValidateCard.Payload(
            sessionId = sessionId,
            acsWindowSize = ActionType.AcsWindowSize.ACS_250x400,
            env3DS = ThreeDSEnv.STAGING
        )

    private fun onCardCapture(data: String) {
        val response = CardCaptureResponse.fromJson(data)

        if (response.threeDSError == ThreeDSError.TOKEN_REQUIRED) {
            validateCard(response.threeDSToken!!)

            return
        }

        if (response.threeDSError == null) {
            super.onComplete(data)

            return
        }

        onError(FatalError("3DS error - ${response.threeDSError!!.code}"))
    }

    private fun validateCard(threeDSToken: String) {
        framesHandler = ::onCardValidation

        post(cardValidateCommand(threeDSToken))
    }

    private fun onCardValidation(data: String) {
        val response = ValidateCardResponse.fromJson(data)

        framesHandler = ::onCardCapture

        post(GroupCommand("completeCardCapture",
            CompleteActionCommand(CARD_CAPTURE_ACTION_NAME, JSONArray().apply {
                response.challengeResponse?.let { put(it.toJson()) }
            })
        ))
    }
}

object ShowValidationChallenge : JavascriptCommand(
    """
      frames.showValidationChallenge = function() {
        const cardCapture = document.getElementById('${ThreeDSCardCapture.CARD_ID}');
        cardCapture.style.display = "none";
        
        const challenge = document.getElementById('${ThreeDSCardCapture.CHALLENGE_ID}');
        challenge.style.display = "block";
      };
      
      frames.showValidationChallenge();
    """.trimMargin()
)

object HideValidationChallenge : JavascriptCommand(
    """
      frames.showValidationChallenge = function() {
        const cardCapture = document.getElementById('${ThreeDSCardCapture.CARD_ID}');
        cardCapture.style.display = "block";
        
        const challenge = document.getElementById('${ThreeDSCardCapture.CHALLENGE_ID}');
        challenge.style.display = "none";
      };
      
      frames.showValidationChallenge();
    """.trimMargin()
)