package danvim.qrsign.utils

class Message(
    val content: String,
    val date: String,
    val keyType: KeyType,
    val publicKeyLocation: String
) {
    val validator: String
        get() {
            return listOf(content, date, publicKeyLocation).joinToString("\n")
        }
}