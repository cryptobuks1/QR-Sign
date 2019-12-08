package danvim.qrsign.utils

import com.google.crypto.tink.subtle.Ed25519Sign

class Signer {
    fun sign(message: Message, privateKey: String): SignedMessage {
        return SignedMessage(
            message,
            Ed25519Sign(privateKey.toByteArray()).sign(message.validator.toByteArray()).toString()
        )
    }
}