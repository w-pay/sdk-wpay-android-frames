package au.com.wpay.frames.sample

import au.com.wpay.frames.BuildFramesCommand
import au.com.wpay.frames.CreateActionControlCommand
import au.com.wpay.frames.StartActionCommand
import au.com.wpay.frames.types.ActionType
import au.com.wpay.frames.types.ControlType

class SingleCardCapture: FramesHost(HTML) {
    companion object {
        private const val DOM_ID = "cardElement"

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
            ActionType.CaptureCard(cardCaptureOptions()).toCommand(),
            StartActionCommand,
            CreateActionControlCommand(ControlType.CARD_GROUP, DOM_ID)
        ))
    }
}