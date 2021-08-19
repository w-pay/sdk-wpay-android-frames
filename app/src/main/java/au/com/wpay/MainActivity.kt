package au.com.wpay

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import au.com.wpay.frames.*
import au.com.wpay.frames.types.ActionType
import au.com.wpay.frames.types.ControlType
import au.com.wpay.frames.types.FramesConfig
import au.com.wpay.frames.types.LogLevel

class MainActivity : AppCompatActivity(), FramesView.Callback {
    private lateinit var errorTextView: TextView
    private lateinit var framesView: FramesView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        errorTextView = findViewById(R.id.errorText)
        framesView = findViewById(R.id.framesView)

        /*
         * Step 1.
         *
         * We need to configure the SDK to provide the "bridge" between the native and web worlds.
         */
        framesView.configure(
            config = FramesView.FramesViewConfig(
                /*
                 * Note: The SDK will add the required <script> tag to inject the JS SDK into the
                 * host page. Applications can however add other web content to the page to aid
                 * in styling.
                 */
                html = "<html><head></head><body><div id='cardElement'></div></body></html>"
            ),
            callback = this,
            logger = DebugLogger()
        )

        findViewById<Button>(R.id.loadBtn).setOnClickListener {
            /*
             * Step 2.
             *
             * Load the Frames SDK into the HTML page.
             */
            framesView.loadFrames(
                FramesConfig(
                    apiKey = "ukUfDqw5A39a2rKIxCMVuNvg3IBA7oHv",
                    authToken = "Bearer cauPxIUvA3AAGDmERgfV13bSxCWE",
                    apiBase = "https://dev.mobile-api.woolworths.com.au/wow/v1/pay/instore",
                    logLevel = LogLevel.DEBUG
                )
            )
        }
    }

    override fun onComplete(response: String) {
        debug("onComplete(response: $response)")
    }

    override fun onError(error: FramesError) {
        debug("onError(error: $error)")

        errorTextView.text = error.errorMessage.toString()
    }

    override fun onProgressChanged(progress: Int) {
        debug("onProgressChanged(progress: $progress)")
    }

    override fun onValidationChange(domId: String, isValid: Boolean) {
        debug("onValidationChange($domId, isValid: $isValid)")
    }

    override fun onFocusChange(domId: String, isFocussed: Boolean) {
        debug("onFocusChange($domId, isFocussed: $isFocussed)")
    }

    override fun onPageLoaded() {
        debug("onPageLoaded()")

        /*
         * Step 3.
         *
         * Add a single line card group to the page
         */
        framesView.postCommand(BuildFramesCommand(
            ActionType.CaptureCard().toCommand(),
            StartActionCommand,
            CreateActionControlCommand(ControlType.CARD_GROUP,"cardElement")
        ))
    }

    override fun onRendered() {
        debug("onRendered()")
    }
}

fun debug(message: String) {
    Log.d("[Callback]", message)
}
