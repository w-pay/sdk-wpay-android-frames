package au.com.wpay.frames.types

enum class ControlType(val type: String) {
    CARD_GROUP("CardGroup"),
    CARD_NUMBER("CardNo"),
    CARD_EXPIRY("CardExpiry"),
    CARD_CVV("CardCVV"),
    VALIDATE_CARD("ValidateCard")
}