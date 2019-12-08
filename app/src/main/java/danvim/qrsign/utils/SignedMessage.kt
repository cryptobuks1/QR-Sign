package danvim.qrsign.utils

data class SignedMessage(
    val message: Message,
    val signature: String
)