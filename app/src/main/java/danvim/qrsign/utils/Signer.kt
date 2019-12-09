package danvim.qrsign.utils

import com.google.crypto.tink.subtle.Base64
import com.google.crypto.tink.subtle.Ed25519Sign

class Signer {
    fun sign(message: Message, privateKey: String): SignedMessage {
        return SignedMessage(
            message,
            Base64.encode(Ed25519Sign(Base64.decode(privateKey)).sign(message.validator.toByteArray()))
        )
    }
}