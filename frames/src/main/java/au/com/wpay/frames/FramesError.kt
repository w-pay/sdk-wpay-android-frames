package au.com.wpay.frames

@Suppress("MemberVisibilityCanBePrivate")
sealed class FramesError(
    val errorCode: ErrorCode,
    val errorMessage: String?
) {
    override fun toString(): String = "[$errorCode]: ${errorMessage ?: ""}"
}

enum class ErrorCode {
    FATAL_ERROR,
    NETWORK_ERROR,
    TIMEOUT_ERROR,
    FORM_ERROR,
    EVAL_ERROR,
    SDK_INIT_ERROR
}

class FatalError(errorMessage: String?) : FramesError(ErrorCode.FATAL_ERROR, errorMessage)
class NetworkTimeoutError : FramesError(ErrorCode.TIMEOUT_ERROR, "The request timed out.")
class NetworkError(errorMessage: String?) : FramesError(ErrorCode.NETWORK_ERROR, errorMessage)
class FormError(errorMessage: String) : FramesError(ErrorCode.FORM_ERROR, errorMessage)
class EvalError(errorMessage: String) : FramesError(ErrorCode.EVAL_ERROR, errorMessage)
class SdkInitError(errorMessage: String) : FramesError(ErrorCode.SDK_INIT_ERROR, errorMessage)