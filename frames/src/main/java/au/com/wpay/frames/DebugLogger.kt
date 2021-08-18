package au.com.wpay.frames

import android.util.Log

/**
 * Simple logger that uses Android's Log
 */
class DebugLogger : FramesView.Logger {
    override fun log(tag: String, message: String) {
        Log.d(tag, message)
    }
}