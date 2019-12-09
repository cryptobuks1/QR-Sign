package danvim.qrsign

import com.google.crypto.tink.subtle.Base64
import com.google.crypto.tink.subtle.Ed25519Sign
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import danvim.qrsign.exceptions.InvalidMessageFormatException
import danvim.qrsign.exceptions.PublicKeyNotFoundException
import danvim.qrsign.utils.*
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.lang.IllegalArgumentException
import java.net.URL

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ValidatorTest {
    private val keyPair = Ed25519Sign.KeyPair.newKeyPair()

    @Test
    fun validKeyPair() {
        val isValid = Validator().validateKeyPair(
            Base64.encode(keyPair.publicKey),
            Base64.encode(keyPair.privateKey)
        )
        assertTrue(isValid)
    }

    @Test
    fun invalidKeyPair() {
        val isValid = Validator().validateKeyPair(
            Base64.encode(keyPair.publicKey),
            ""
        )
        assertFalse(isValid)
    }

    @Nested
    inner class ParseMessage {
        private val message = Message(
            "Test",
            "2012-12-12",
            KeyType.URL,
            "http://example.com"
        )
        val publicKey = "v2BKmzP1yvIFCW0qc48KShXToHgyCo/Now3wnOHgJyY="
        val privateKey = "henL6au7mGATgCFoVGAZQwzqkeCY8/8d8032TtG+p3w="
        private val signature = "6o7kLAcaybD7pZ0OBB4PBDSHcdxg7UhKWETUWYSaW9qq1OeWgJTxTtJr2WV5RcI1G9zmXnXIFEW/eMCd7c7oBw=="

        @Test
        fun validMessage() {
            Validator().getMessage("${message.content}\n${message.date}\n${message.publicKeyLocation}\n$signature")
        }

        @Test
        fun invalidLength() {
            val ex = assertThrows(InvalidMessageFormatException::class.java) {
                Validator().getMessage("")
            }
            assertEquals("SignedMessage item length incorrect.", ex.message)
        }

        @Test
        fun invalidDateFormat() {
            val ex = assertThrows(InvalidMessageFormatException::class.java) {
                Validator().getMessage("${message.content}\n2012-12-12-12\n${message.publicKeyLocation}\n$signature")
            }
            assertEquals("Date format must be YYYY-MM-DD.", ex.message)
        }

        @Test
        fun invalidKeyType() {
            val ex = assertThrows(InvalidMessageFormatException::class.java) {
                Validator().getMessage("${message.content}\n${message.date}\nftp://example.com\n$signature")
            }
            assertEquals("Key type invalid.", ex.message)
        }

        @Test
        fun invalidSignature() {
            val ex = assertThrows(InvalidMessageFormatException::class.java) {
                Validator().getMessage("${message.content}\n${message.date}\n${message.publicKeyLocation}\nSW52YWxpZA==")
            }
            assertEquals("Signature must be length of 88.", ex.message)
        }
    }

    @Nested
    inner class ScrapeWebTest {
        private val signedMessage: SignedMessage
        private val publicKeyURL = "http://example.com"
        private val url = URL(publicKeyURL)
        private val publicKey64 = Base64.encode(keyPair.publicKey)

        init {
            val message = Message("Text", "2012-12-12", KeyType.URL, publicKeyURL)
            signedMessage = Signer().sign(message, Base64.encode(keyPair.privateKey))
        }

        @Test
        fun foundMeta() {
            val pageContent = "<!DOCTYPE html><html><head><meta name=\"qr-sign\" content=\"$publicKey64\"></head></html>"
            val mockPageReader: PageReaderInterface = mock {
                //language=HTML
                on { readPage(url) } doReturn pageContent
            }
            val validator = Validator(mockPageReader)
            val scrapeResult = validator.scrapePublicKey(signedMessage)
            assertEquals(publicKey64, scrapeResult.key)
            assertEquals(pageContent, scrapeResult.pageContent)
        }

        @Test
        fun notFoundMeta() {
            val mockPageReader: PageReaderInterface = mock {
                //language=HTML
                on { readPage(url) } doReturn "<!DOCTYPE html><html><head></head></html>"
            }
            assertThrows(PublicKeyNotFoundException::class.java) {
                Validator(mockPageReader).scrapePublicKey(signedMessage)
            }
        }

        @Test
        fun validMessage() {
            val validationResult = Validator().validateMessage(signedMessage, publicKey64, null)
            assertTrue(validationResult.isWellSigned)
            assertNull(validationResult.isVerified)
        }

        @Test
        fun invalidMessage() {
            val validationResult = Validator().validateMessage(
                signedMessage,
                Base64.encode(Ed25519Sign.KeyPair.newKeyPair().publicKey),
                null
            )
            assertFalse(validationResult.isWellSigned)
            assertFalse(validationResult.isVerified!!)
        }

        @Test
        fun notProperlySignedMessage() {
            assertThrows(IllegalArgumentException::class.java) {
                Validator().validateMessage(signedMessage, "", null).isWellSigned
            }
        }
    }

    @Nested
    inner class ScrapeFacebookTest {
        private val signedMessage: SignedMessage
        private val pageId = "1000"
        private val url = URL("https://www.facebook.com/$pageId/about")
        private val publicKey64 = Base64.encode(keyPair.publicKey)

        init {
            val message = Message("Text", "2012-12-12", KeyType.FACEBOOK, "FB:$pageId")
            signedMessage = Signer().sign(message, Base64.encode(keyPair.privateKey))
        }

        @Test
        fun foundFBAbout() {
            val mockPageReader: PageReaderInterface = mock {
                //language=HTML
                on { readPage(url) } doReturn "\\u003Cspan>A blue verification badge confirms that this is an authentic Page for this public figure, media company or brand.\\u003C/span>\nQRSign&lt;$publicKey64&gt;"
            }
            val validator = Validator(mockPageReader)
            val scrapeResult = validator.scrapePublicKey(signedMessage)
            assertEquals(publicKey64, scrapeResult.key)
            assertTrue(scrapeResult.isVerified!!)

            val validationResult = validator.validateMessage(signedMessage, publicKey64, scrapeResult.isVerified)
            assertTrue(validationResult.isVerified!!)
        }

        @Test
        fun notFoundFBAbout() {
            val mockPageReader: PageReaderInterface = mock {
                //language=HTML
                on { readPage(url) } doReturn ""
            }
            assertThrows(PublicKeyNotFoundException::class.java) {
                Validator(mockPageReader).scrapePublicKey(signedMessage)
            }
        }
    }
}