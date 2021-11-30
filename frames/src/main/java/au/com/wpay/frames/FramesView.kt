package au.com.wpay.frames

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.net.http.SslError
import android.util.AttributeSet
import android.webkit.*
import au.com.wpay.frames.types.FramesConfig
import org.json.JSONArray
import java.io.IOException
import java.net.URLConnection

typealias EvalCallback = ((String) -> Unit)

/**
 * Hosts the Frames JS SDK inside an Android WebView.
 *
 * Allows applications to receive messages from the JS SDK via a [FramesView.Callback], and to send
 * the JS SDK commands via posting [JavascriptCommand]s
 *
 * Using the Frames JS SDK follows the following steps
 * 1. The host application configures the view
 * 2. The host HTML "page" is "loaded" into the web view
 * 3. Other web resources (ie: the Frames JS) are "loaded" into the host HTML page.
 *
 * The FrameView is now considered "loaded" that is all resources are available for use. SDK Actions
 * can be posted into the view.
 *
 * 4. HTML elements are "rendered" on the page. Once rendered, the elements can be interacted with
 * by the user and events are emitted.
 *
 * 5. As the user interacts with the web elements, events are emitted and passed to the the callback
 * 6. The user can submit the form, or clear the form.
 * 7. After form submission, the application should complete the action.
 */
class FramesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {
    /**
     * Configuration about how the Application wants to host the SDK.
     */
    data class FramesViewConfig(
        /** @property html The host HTML for the SDK. */
        val html: String,

        /** @property allowInvalidSsl Whether assets can still be loaded if SSL validation fails. */
        val allowInvalidSsl: Boolean = false
    )

    /**
     * Allows an Application to receives messages from the JS SDK/WebView
     */
    interface Callback {
        fun onComplete(response: String)

        /**
         * Called when an error occurs.
         */
        fun onError(error: FramesError)

        /**
         * Called as the progress changes loading the web content.
         */
        fun onProgressChanged(progress: Int)

        /**
         * Called when the validation status of an element has changed.
         *
         * @param domId The ID of the element in the HTML DOM tree
         * @param isValid Whether the contents of the element is valid or not.
         */
        fun onValidationChange(domId: String, isValid: Boolean)

        /**
         * Called when the focus has changed on an element
         *
         * @param domId The ID of the element in the HTML DOM tree
         * @param isFocussed Whether the element is focussed or not
         */
        fun onFocusChange(domId: String, isFocussed: Boolean)

        /**
         * Called when all the web content has been loaded and the Frames JS SDK is ready to
         * use.
         */
        fun onPageLoaded()

        /**
         * Called when JS SDK Action has added content to the host page.
         *
         * @param id A command specific ID to identify the content that was just rendered.
         */
        fun onRendered(id: String)

        /**
         * Called when JS SDK Action has removed content from the host page.
         *
         * @param id A command specific ID to identify the content that was just removed.
         */
        fun onRemoved(id: String)
    }

    /**
     * Allows applications to decide the fate of log messages.
     */
    interface Logger {
        fun log(tag: String, message: String)
    }

    private lateinit var config: FramesViewConfig
    private lateinit var sdkConfig: FramesConfig

    private var callback: Callback? = null
    private var logger: Logger? = null

    init {
        setWebContentsDebuggingEnabled(true)
        initialiseWebView()
    }

    /**
     * The starting point for Applications to configure the interaction between native code
     * and the JS SDK. It must be called first.
     *
     * @param config Configuration about how to host the SDK.
     * @param callback Optional callback to receive messages from the SDK.
     * @param logger Optional logger instance.
     */
    fun configure(
        config: FramesViewConfig,
        callback: Callback? = null,
        logger: Logger? = null
    ) {
        this.config = config
        this.callback = callback
        this.logger = logger
    }

    /**
     * Load the SDK into the host page.
     *
     * This will create a new instance of the JS SDK inside the host page.
     *
     * @param config Configuration for the JS SDK.
     */
    fun loadFrames(
        config: FramesConfig,
    ) {
        this.sdkConfig = config

        /*
         * Load the host HTML page. If the page is loaded successfully, handlePageLoaded() will be
         * called, else an error will be passed to the callback.
         */
        loadDataWithBaseURL("https://assets/", this.config.html, "text/html; charset=utf-8", "utf8", null)
    }

    /**
     * Evaluates the Javascript command in the WebView
     *
     * Can be used from any thread.
     *
     * @param command The command to execute
     * @param callback If the JS returns a result, the callback will receive it.
     */
    fun postCommand(command: JavascriptCommand, callback: EvalCallback? = null) {
        val js = command.command

        log("JavascriptCommand: $js")

        post {
            evaluateJavascript(js, callback)
        }
    }

    /**
     * The behaviour in the callback is often coupled to the type of actions being run
     * by the Javascript SDK. Being able to set the callback allows applications to swap
     * how the application will respond to events without having to implement complex
     * Strategies or switching.
     */
    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    /**
     * Hook to allow the SDK to know when the JS SDK is loaded into the host HTML page.
     */
    @JavascriptInterface
    fun handleFramesSDKLoaded(framesLoaded: String) {
        when {
            framesLoaded.toBoolean() -> {
                AddDefaultViewportCommand.post(this)
                InstantiateFramesSDKCommand(this.sdkConfig.toJson()).post(this)

                callback?.onPageLoaded()
            }
            else -> callback?.onError(SdkInitError("FRAMES not found in window"))
        }
    }

    @JavascriptInterface
    fun handleOnBlur(domId: String) {
        log("handleOnBlur($domId)")

        post {
            callback?.onFocusChange(domId, isFocussed = false)
        }
    }

    @JavascriptInterface
    fun handleOnComplete(jsonString: String) {
        log("handleOnComplete($jsonString)")

        post {
            callback?.onComplete(jsonString)
        }
    }

    @JavascriptInterface
    fun handleOnError(message: String) {
        log("handleOnError($message)")

        post {
            callback?.onError(EvalError(message))
        }
    }

    @JavascriptInterface
    fun handleOnFocus(domId: String) {
        log("handleOnFocus($domId)")

        post {
            callback?.onFocusChange(domId, isFocussed = true)
        }
    }

    @JavascriptInterface
    fun handleOnRendered(id: String) {
        log("handleOnRendered($id)")

        post {
            callback?.onRendered(id)
        }
    }

    @JavascriptInterface
    fun handleOnRemoved(id: String) {
        log("handleOnRemoved($id)")

        post {
            callback?.onRemoved(id)
        }
    }

    @JavascriptInterface
    fun handleOnValidated(domId: String, jsonString: String) {
        log("handleOnValidated($domId, $jsonString)")

        val errors = JSONArray(jsonString)
        when {
            errors.length() > 0 -> post {
                callback?.onValidationChange(domId, isValid = false)
                callback?.onError(FormError(errors.optString(0)))
            }
            else -> post {
                callback?.onValidationChange(domId, isValid = true)
            }
        }
    }

    private fun initialiseWebView() {
        webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                post {
                    callback?.onProgressChanged(newProgress)
                }
            }
        }

        webViewClient = FramesWebViewClient()

        @SuppressLint("SetJavaScriptEnabled")
        settings.javaScriptEnabled = true
        addJavascriptInterface(this, JavascriptCommand.JS_NAMESPACE)

        setLayerType(LAYER_TYPE_SOFTWARE, null)

        isFocusable = true
        isFocusableInTouchMode = true
        requestFocus(FOCUS_DOWN)
    }

    /*
     * Called by the [FramesWebViewClient] when the host HTML page is loaded.
     */
    private fun handlePageLoaded() {
        log("Page Loaded")
        FramesSDKLoadCommand.post(this)
    }

    private fun log(msg: String) = logger?.log("[FramesView]", msg)

    internal class FramesWebViewClient: WebViewClient() {
        override fun shouldInterceptRequest(
            view: WebView,
            request: WebResourceRequest
        ): WebResourceResponse? {
            val uri: Uri = request.url
            val framesView = view as FramesView

            if (uri.host.equals("assets")) {
                try {
                    val filename = uri.toString().substring(15)

                    return WebResourceResponse(
                        URLConnection.guessContentTypeFromName(uri.path),
                        "utf-8",
                        framesView.context.assets.open(filename)
                    )
                }
                catch (e: IOException) {
                    if (e.message?.contains("favicon.ico") == true) {
                        /*
                         * Ignore. The WebView automatically tries to load the favicon.
                         * However by returning null here, the WebView will still try again
                         * so we will have to handle the error again. Sigh.
                         */
                        return null
                    }

                    framesView.post {
                        framesView.callback?.onError(NetworkError(e.message))
                    }
                }
            }

            return null
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            super.onReceivedError(view, request, error)

            this.onReceivedError(
                view as FramesView,
                request.url.toString(),

                error.errorCode,
                error.description.toString()
            )
        }

        override fun onReceivedError(
            view: WebView,
            errorCode: Int,
            description: String,
            failingUrl: String
        ) {
            super.onReceivedError(view, errorCode, description, failingUrl)

            this.onReceivedError(
                view as FramesView,
                failingUrl,
                errorCode,
                description
            )
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)

            (view as? FramesView)?.handlePageLoaded()
        }

        override fun onReceivedSslError(
            view: WebView,
            handler: SslErrorHandler,
            error: SslError
        ) {
            val framesView = view as FramesView
            framesView.log("onReceivedSslError $error")

            when {
                framesView.config.allowInvalidSsl -> handler.proceed()
                else -> handler.cancel()
            }
        }

        override fun onReceivedHttpError(
            view: WebView,
            request: WebResourceRequest,
            errorResponse: WebResourceResponse
        ) {
            val framesView = view as FramesView
            val message =
                when {
                    !errorResponse.reasonPhrase.isNullOrBlank() -> errorResponse.reasonPhrase
                    errorResponse.statusCode == 401 -> "Authentication token is invalid"
                    else -> errorResponse.statusCode.toString()
                }

            framesView.log("onReceivedHttpError $message")
            framesView.callback?.onError(NetworkError(message))
        }

        private fun onReceivedError(
            framesView: FramesView,
            url: String,
            errorCode: Int,
            description: String
        ) {
            framesView.log("onReceivedError [$errorCode] $url: $description")

            /*
             * The WebView automatically tries to load a favicon from the assets folder,
             * which we don't need to care about.
             */
            if (url.contains("favicon.ico")) {
                return
            }

            framesView.post {
                framesView.callback?.onError(toFramesError(errorCode, description))
            }
        }

        private fun toFramesError(errorCode: Int, description: String): FramesError =
            when (errorCode) {
                ERROR_TIMEOUT -> NetworkTimeoutError()
                else -> NetworkError(description)
            }
    }
}