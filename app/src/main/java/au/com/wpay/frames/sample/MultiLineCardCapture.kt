package au.com.wpay.frames.sample

import android.view.View
import au.com.wpay.frames.*
import au.com.wpay.frames.types.ActionType
import au.com.wpay.frames.types.ControlType

class MultiLineCardCapture : FramesHost(HTML) {
    companion object {
        private const val CARD_NO_DOM_ID = "cardNoElement"
        private const val CARD_EXPIRY_DOM_ID = "cardExpiryElement"
        private const val CARD_CVV_DOM_ID = "cardCvvElement"

        const val ACTION_NAME = "multiLineCardCapture"
        const val HTML = """
           <html>
              <body>
                <div id="$CARD_NO_DOM_ID"></div>
                <div>
                  <div id="$CARD_EXPIRY_DOM_ID" style="display: inline-block; width: 50%"></div>
                  <div id="$CARD_CVV_DOM_ID" style="display: inline-block; width: 40%; float: right;"></div>
                </div>
              </body>
            </html>
       """
    }

    override fun onPageLoaded() {
        super.onPageLoaded()

        /*
         * Step 3.
         *
         * Add a multi line card group to the page
         */
        post(BuildFramesCommand(
            ActionType.CaptureCard(cardCaptureOptions()).toCommand(ACTION_NAME),
            StartActionCommand(ACTION_NAME),
            CreateActionControlCommand(ACTION_NAME, ControlType.CARD_NUMBER, CARD_NO_DOM_ID),
            CreateActionControlCommand(ACTION_NAME, ControlType.CARD_EXPIRY, CARD_EXPIRY_DOM_ID),
            CreateActionControlCommand(ACTION_NAME, ControlType.CARD_CVV, CARD_CVV_DOM_ID)
        ))
    }

    override fun onSubmit(view: View) {
        super.onSubmit(view)

        post(SubmitFormCommand(SingleCardCapture.ACTION_NAME))
    }

    override fun onClear(view: View) {
        super.onClear(view)

        post(ClearFormCommand(SingleCardCapture.ACTION_NAME))
    }
}