package au.com.wpay.frames.dto

enum class ThreeDSError(val code: String) {
    TOKEN_REQUIRED("3DS_001"),
    INVALID_SESSION("3DS_002"),
    VALIDATION_FAILED("3DS_003"),
    UNSUPPORTED_VERSION("3DS_004"),
    SERVICE_UNAVAILABLE("3DS_005"),
    UNKNOWN_ERROR("3DS_500");

    companion object {
        fun fromCode(code: String): ThreeDSError? =
            values().find { it.code == code }
    }
}