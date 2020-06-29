package ar.edu.unq.pdes.myprivateblog.services

import android.annotation.SuppressLint
import android.content.Context
import android.util.Base64
import android.widget.Toast
import androidx.core.content.res.TypedArrayUtils
import ar.edu.unq.pdes.myprivateblog.R
import timber.log.Timber
import java.io.InputStream
import java.io.OutputStream
import java.security.SecureRandom
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class EncryptionService @Inject constructor(val context: Context, val authenticationService: AuthenticationService) {

    private val algorithm: String = "AES"
    private val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")

    fun encrypt(inputStream: InputStream, outputStream: OutputStream) {
        val password = retrievePassword()

        val salt = ByteArray(cipher.blockSize)
        SecureRandom().nextBytes(salt)

        val iv = ByteArray(cipher.blockSize)
        SecureRandom().nextBytes(iv)

        val secretKey = generateSecretKey(password, salt)

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))

        val output = CipherOutputStream(outputStream, cipher)

        outputStream.use {
            it.write(salt)
            it.write(iv)
        }

        output.use {
            inputStream.use {
                it.copyTo(output)
            }
        }
    }

    fun decrypt(encryptedInput: InputStream, outputStream: OutputStream) {
        val password = retrievePassword()

        val salt = ByteArray(cipher.blockSize)
        val iv = ByteArray(cipher.blockSize)

        encryptedInput.use {
            it.read(salt, 0, cipher.blockSize)
            it.read(iv, 0, cipher.blockSize)
        }

        val secretKey = generateSecretKey(password, salt)

        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))

        val cipherInput = CipherInputStream(encryptedInput, cipher)

        cipherInput.use {
            it.copyTo(outputStream)
        }
    }

    private fun generateSecretKey(password: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val spec =  PBEKeySpec(password.toCharArray(), salt, 1536, 256)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, algorithm)
    }

    fun retrievePassword(): String{
        return authenticationService.retrievePassword()
    }

}