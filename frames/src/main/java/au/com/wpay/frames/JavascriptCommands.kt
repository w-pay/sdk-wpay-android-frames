package au.com.wpay.frames

import au.com.wpay.frames.types.ControlType
import org.json.JSONObject
import java.io.Serializable

const val JS_SDK_VERSION = "2.0.2"

/**
 * A "Javascript command" is a piece of Javascript that can be evaluated inside the [FramesView]
 */
open class JavascriptCommand(val command: String) : Serializable {
    /**
     * Allows commands to define how their posted into a [FramesView]
     *
     * This method should be called on commands, instead of [FramesView#postCommand]
     *
     * If there is a JS evaluation error, the callback will still be invoked.
     * Use logcat with the "chromium" tag to search for evaluation errors if the callback
     * doesn't get the right value.
     */
    open fun post(view: FramesView, callback: EvalCallback? = null) {
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
 * Android's WebView executes JS asynchronously, and doesn't wait for asynchronous JS to complete
 * before returning to native code.
 *
 * Therefore if we want to compose JS async behaviour we have to create a wrapper function
 * that can be called as part of a JS composition.
 *
 * @see [BuildFramesCommand]
 */
open class DelayedJavascriptCommand(
    val functionName: String,
    command: String
) : JavascriptCommand(command)

/**
 * Builds the view that is shown to the user.
 *
 * Combines [DelayedJavascriptCommand]s into an command and executes the result. Each command is
 * executed first to add the async function to the web view.
 *
 * If any error is thrown, it is logged and the Error's `message` is returned to the app.
 * Looking for "chromium" errors in the logs can aid developers debugging why JS code threw
 * errors.
 *
 * On success the FramesView is notified that content has been rendered in the web view.
 */
class BuildFramesCommand(
    private val commands: List<DelayedJavascriptCommand>
) : JavascriptCommand(
    """
    frames.build = async function() {
        try {
            ${commands.joinToString("\n") { "await frames.${it.functionName}();" }}
        
            $JS_NAMESPACE.handleOnRendered();
        }
        catch(e) {
            frames.handleError('build', e)
        }
    };
    
    frames.build();
    """.trimMargin()
) {
    constructor(vararg commands: DelayedJavascriptCommand) : this(commands.asList())

    override fun post(view: FramesView, callback: EvalCallback?) {
        commands.forEach { it.post(view) }

        super.post(view, callback)
    }
}

/**
 * Javascript to add the Frames JS SDK to the host HTML page.
 */
object FramesSDKLoadCommand : JavascriptCommand(
    """
    const frames = {
        handleError: function(fnName, err) {
            console.error('frames.' + fnName + ': ' + err)
            
            $JS_NAMESPACE.handleOnError(err.message);
        },
        
        actions: {}
    };
    
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

object AddDefaultViewportCommand: JavascriptCommand(
    """
    const head = document.getElementsByTagName("head")[0];
    const metas = Array.prototype.slice.call(head.getElementsByTagName("meta"))
    const viewport = metas.find((el) => el.getAttribute("name") === "viewport")

    if (!viewport) {
      const meta = document.createElement("meta")
      meta.setAttribute("name", "viewport")
      meta.setAttribute("content", "width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no")

      head.appendChild(meta)
    }
    """.trimMargin()
)

class InstantiateFramesSDKCommand(payload: JSONObject) :
    JavascriptCommand("frames.sdk = new FRAMES.FramesSDK($payload);")

/**
 * Creates an SDK Action in the web view.
 *
 * Note that this will overwrite any previous action, so make sure each action is completed.
 */
class CreateActionCommand(
    name: String,
    action: String,
    payload: JSONObject?
) : DelayedJavascriptCommand(
    "createAction_$name",
    """
    frames.createAction_$name = async function() {
        frames.actions.$name = frames.sdk.createAction(FRAMES.ActionTypes.$action${payload?.let { ", $it" } ?: ""});
    };
    """.trimMargin()
)

class CreateActionControlCommand(
    actionName: String,
    controlType: String,
    domId: String,
    payload: JSONObject?
) : DelayedJavascriptCommand(
    "createActionControl_$domId",
    """
    frames.createActionControl_$domId = async function() {
        frames.actions.$actionName.createFramesControl('$controlType', '$domId'${payload?.let { ", $it" } ?: ""});
        
        const element = document.getElementById('$domId');
        element.addEventListener(FRAMES.FramesEventType.OnValidated, () => { $JS_NAMESPACE.handleOnValidated('$domId', JSON.stringify(frames.actions.$actionName.errors())) });
        element.addEventListener(FRAMES.FramesEventType.OnBlur, () => { $JS_NAMESPACE.handleOnBlur('$domId') });
        element.addEventListener(FRAMES.FramesEventType.OnFocus, () => { $JS_NAMESPACE.handleOnFocus('$domId') });
    }
    """.trimMargin()
) {
    constructor(actionName: String, controlType: String, domId: String)
        : this(actionName, controlType, domId, null)

    constructor(actionName: String, controlType: ControlType, domId: String, payload: JSONObject?)
        : this(actionName, controlType.type, domId, payload)

    constructor(actionName: String, controlType: ControlType, domId: String)
        : this(actionName, controlType.type, domId)
}

class StartActionCommand(
    name: String
) : DelayedJavascriptCommand(
    "startAction_$name",
    """
    frames.startAction_$name = async function() {
        await frames.actions.$name.start();
    }
    """.trimMargin()
)

class ClearFormCommand(
    name: String
) : JavascriptCommand(
    "frames.actions.$name.clear()"
)

class SubmitFormCommand(
    name: String
) : JavascriptCommand(
    """
    frames.submit = async function() {
        try {
            await this.actions.$name.submit()
            
            const response = await this.actions.$name.complete()
            $JS_NAMESPACE.handleOnComplete(JSON.stringify(response))
        }
        catch(e) {
            frames.handleError('submit', e)
        }
    }
    
    frames.submit();
    """.trimMargin()
)