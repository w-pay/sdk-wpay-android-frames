package au.com.wpay.frames.sample

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import au.com.wpay.frames.*
import au.com.wpay.frames.dto.CardCaptureResponse
import au.com.wpay.frames.types.ActionType
import au.com.wpay.frames.types.FramesConfig
import au.com.wpay.frames.types.LogLevel

open class FramesHost(private val html: String) : Fragment(R.layout.frames_host), FramesView.Callback {
    private lateinit var messageView: TextView
    private lateinit var framesView: FramesView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        messageView = view.findViewById(R.id.messageText)
        framesView = view.findViewById(R.id.framesView)

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
                 *
                 * Note: The SDK will inject a default <meta> tag setting the viewport if no <meta>
                 * tag for the viewport is provided in the host HTML.
                 */
                html = this.html
            ),
            callback = this,
            logger = DebugLogger()
        )

        view.findViewById<Button>(R.id.loadBtn).setOnClickListener {
            messageView.text = ""

            /*
             * Step 2.
             *
             * Load the Frames SDK into the HTML page.
             */
            framesView.loadFrames(
                FramesConfig(
                    apiKey = "95udD3oX82JScUQ1qyACSOMysyAl93Gb",
                    authToken = "Bearer jbst7UCKR695D93j8tfAd5fG7k2m",
                    apiBase = "https://dev.mobile-api.woolworths.com.au/wow/v1/pay/instore",
                    logLevel = LogLevel.DEBUG
                )
            )
        }

        view.findViewById<Button>(R.id.submitBtn).setOnClickListener(::onSubmit)
        view.findViewById<Button>(R.id.clearBtn).setOnClickListener(::onClear)
    }

    override fun onComplete(response: String) {
        debug("onComplete(response: $response)")

        val data = CardCaptureResponse.fromJson(response)
        val id: String? = when {
            data.paymentInstrument?.itemId != null -> { data.paymentInstrument?.itemId }
            else -> { data.itemId }
        }

        val message = "${data.status?.responseText ?: ""} - ${id!!}"

        messageView.text = message
    }

    override fun onError(error: FramesError) {
        debug("onError(error: $error)")

        messageView.text = error.errorMessage.toString()
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
         * Override this to add card controls to action
         */
    }

    override fun onRendered(id: String) {
        debug("onRendered($id)")
    }

    override fun onRemoved(id: String) {
        debug("onRemoved($id)")
    }

    open fun onSubmit(view: View) {
        // do nothing by default
    }

    open fun onClear(view: View) {
        messageView.text = ""
    }

    fun post(command: JavascriptCommand) =
        command.post(framesView)

    open fun cardCaptureOptions() =
        ActionType.CaptureCard.Payload(
            verify = true,
            save = true
        )
}

fun debug(message: String) {
    Log.d("[Callback]", message)
}