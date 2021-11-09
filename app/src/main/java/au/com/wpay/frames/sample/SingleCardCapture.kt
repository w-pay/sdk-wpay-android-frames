package au.com.wpay.frames.sample

import android.view.View
import au.com.wpay.frames.*
import au.com.wpay.frames.types.ActionType
import au.com.wpay.frames.types.ControlType

class SingleCardCapture: FramesHost(HTML) {
    companion object {
        private const val DOM_ID = "cardElement"

        const val ACTION_NAME = "singleCardCapture"
        const val HTML = """<html><body><div id="$DOM_ID"></div></body></html>"""
    }

    override fun onPageLoaded() {
        super.onPageLoaded()

        /*
         * Step 3.
         *
         * Add a single line card group to the page
         */
        post(BuildFramesCommand(
            ActionType.CaptureCard(cardCaptureOptions()).toCommand(ACTION_NAME),
            StartActionCommand(ACTION_NAME),
            CreateActionControlCommand(ACTION_NAME, ControlType.CARD_GROUP, DOM_ID)
        ))
    }

    override fun onSubmit(view: View) {
        super.onSubmit(view)

        post(SubmitFormCommand(ACTION_NAME))
    }

    override fun onClear(view: View) {
        super.onClear(view)

        post(ClearFormCommand(ACTION_NAME))
    }
}