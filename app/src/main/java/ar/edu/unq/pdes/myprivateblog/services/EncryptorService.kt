package ar.edu.unq.pdes.myprivateblog.services

import android.content.Context
import android.util.Base64
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
            it.read(salt,0,cipher.blockSize)
            it.read(iv,0,cipher.blockSize)
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

    fun storePassword(password: String){
        authenticationService.storePassword(password)
    }

    //Not used methods

    private fun encodeSecretKey(secretKey: SecretKey): String = Base64.encodeToString(secretKey.encoded, Base64.NO_WRAP)

    private fun decodeSecretKey(key: String): SecretKey {
        val decodedKey = Base64.decode(key, Base64.NO_WRAP)
        return SecretKeySpec(decodedKey, 0, decodedKey.size, algorithm)
    }

}