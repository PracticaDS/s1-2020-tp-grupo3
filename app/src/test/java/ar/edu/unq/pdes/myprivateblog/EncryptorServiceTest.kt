package ar.edu.unq.pdes.myprivateblog

import android.content.Context
import ar.edu.unq.pdes.myprivateblog.services.EncryptionService
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class EncryptionServiceTest {

    private lateinit var encryptionService: EncryptionService
    private lateinit var context: Context

    @Before
    fun setup() {
        context = mock(Context::class.java)
        encryptionService = EncryptionService(context)
    }

    @ExperimentalStdlibApi
    @Test
    fun whenEncryptingAString_itShouldBeTheSameAfterDecrypting() {
        val someString = "A String"

        val inputStream = ByteArrayInputStream(
            someString.encodeToByteArray()
        )

        val outputStream = ByteArrayOutputStream()

        val secretKey = encryptionService.generateSecretKey()!!

        encryptionService.encrypt(secretKey, inputStream, outputStream)
        val encodeString = outputStream.toByteArray()

        val decryptInputStream = ByteArrayInputStream(encodeString)

        val decryptOutputStream = ByteArrayOutputStream()

        encryptionService.decrypt(secretKey, decryptInputStream, decryptOutputStream)

        assertEquals(someString, decryptOutputStream.toByteArray().decodeToString())
    }
}