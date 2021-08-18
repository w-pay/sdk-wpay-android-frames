package au.com.wpay.frames

import org.json.JSONObject

const val JS_SDK_VERSION = "2.0.2"

/**
 * A "Javascript command" is a piece of Javascript that can be evaluated inside the [FramesView]
 */
open class JavascriptCommand(val command: String) {
    /**
     * Syntactic sugar for [FramesView#postCommand]
     */
    fun post(view: FramesView, callback: ((String) -> Unit)? = null) {
        view.postCommand(this, callback)
    }

    companion object {
        /**
         * Used to namespace functions in the JS runtime where the implementation is in native
         * code.
         */
        const val JS_NAMESPACE = "android"
    }
}

/**
 * Javascript to add the Frames JS SDK to the host HTML page.
 */
object FramesSDKLoadCommand : JavascriptCommand(
    """
    let frames = { "controls": [] };
    
    frames.init = function() {
       var tag = document.createElement("script");
       tag.src = "https://assets/framesSDK-$JS_SDK_VERSION.js";
       tag.type = "text/javascript";
       tag.onload = function() { $JS_NAMESPACE.handleFramesSDKLoaded(FRAMES !== undefined) }
       document.getElementsByTagName("head")[0].appendChild(tag);
    }

    frames.init();
    """.trimMargin()
)

class InstantiateFramesSDKCommand(payload: JSONObject) :
    JavascriptCommand("frames.sdk = new FRAMES.FramesSDK($payload);")