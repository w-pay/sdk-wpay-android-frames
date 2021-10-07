package au.com.wpay.frames.sample

import au.com.wpay.frames.BuildFramesCommand
import au.com.wpay.frames.CreateActionControlCommand
import au.com.wpay.frames.StartActionCommand
import au.com.wpay.frames.types.ActionType
import au.com.wpay.frames.types.ControlType

class MultiLineCardCapture : FramesHost(HTML) {
    companion object {
        private const val CARD_NO_DOM_ID = "cardNoElement"
        private const val CARD_EXPIRY_DOM_ID = "cardExpiryElement"
        private const val CARD_CVV_DOM_ID = "cardCvvElement"

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
            ActionType.CaptureCard(cardCaptureOptions()).toCommand(),
            StartActionCommand,
            CreateActionControlCommand(ControlType.CARD_NUMBER, CARD_NO_DOM_ID),
            CreateActionControlCommand(ControlType.CARD_EXPIRY, CARD_EXPIRY_DOM_ID),
            CreateActionControlCommand(ControlType.CARD_CVV, CARD_CVV_DOM_ID)
        ))
    }
}