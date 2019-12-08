package danvim.qrsign

import com.google.crypto.tink.subtle.Base64
import org.junit.Test

import org.junit.Assert.*
import com.google.crypto.tink.subtle.Ed25519Sign
import com.google.crypto.tink.subtle.Ed25519Verify

class TinkTest {
    companion object {
        const val publicKey = "v2BKmzP1yvIFCW0qc48KShXToHgyCo/Now3wnOHgJyY="
        const val privateKey = "henL6au7mGATgCFoVGAZQwzqkeCY8/8d8032TtG+p3w="
        const val message = "Test"
        const val signature = "UsNhLUPxdgr6Vb0DhsTld9VNY7gBbBhrZ1+3vzMQU7+deo1zxl8GaymS+0wD7ZMTgPyxpkfPQfjuxELlp/W7BA=="
    }

    @Test
    fun verifySignatureGeneration() {
        val testSignature = Ed25519Sign(Base64.decode(privateKey)).sign(message.toByteArray())
        assertEquals(signature, Base64.encode(testSignature))
    }

    @Test
    fun verifySignatureMessage() {
        Ed25519Verify(Base64.decode(publicKey)).verify(Base64.decode(signature), message.toByteArray())
    }

    @Test
    fun generateKeyPair() {
        val keyPair = Ed25519Sign.KeyPair.newKeyPair()
        println(Base64.encode(keyPair.publicKey))
        println(Base64.encode(keyPair.privateKey))
    }
}