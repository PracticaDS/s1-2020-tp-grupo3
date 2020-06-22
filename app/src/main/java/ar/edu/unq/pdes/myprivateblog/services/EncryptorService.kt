package ar.edu.unq.pdes.myprivateblog.services

import android.content.Context
import android.util.Base64
import androidx.core.content.edit
import ar.edu.unq.pdes.myprivateblog.R
import java.io.InputStream
import java.io.OutputStream
import java.security.SecureRandom
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class EncryptionService @Inject constructor(val context: Context) {

    private val algorithm: String = "AES"
    private val transformation: String = "AES/CBC/PKCS5PADDING"
    private val cipher = Cipher.getInstance(transformation)

    fun encrypt(key: SecretKey, plainText: InputStream, outputStream: OutputStream) {

        val salt = ByteArray(cipher.blockSize)
        SecureRandom().nextBytes(salt)

        val data = key.encoded
        val skeySpec = SecretKeySpec(data, 0, data.size, algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, IvParameterSpec(salt))

        val output = CipherOutputStream(outputStream,cipher)

        outputStream.use {
            it.write(salt)
        }

        output.use {
            plainText.use {
                it.copyTo(output)
            }
        }
    }

    fun decrypt(key: SecretKey, encryptedInput: InputStream, outputStream: OutputStream) {

        val salt = ByteArray(cipher.blockSize)
        encryptedInput.use {
            it.read(salt,0,cipher.blockSize)
        }

        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(salt))

        val cipherInput = CipherInputStream(encryptedInput,cipher)

        cipherInput.use {
            it.copyTo(outputStream)
        }
    }

    fun generateSecretKey(): SecretKey? {
        val secureRandom = SecureRandom()
        val keyGenerator = KeyGenerator.getInstance(algorithm)
        keyGenerator?.init(256, secureRandom)
        return keyGenerator?.generateKey()
    }

    private fun encodeSecretKey(secretKey: SecretKey): String = Base64.encodeToString(secretKey.encoded, Base64.NO_WRAP)

    private fun decodeSecretKey(key: String): SecretKey {
        val decodedKey = Base64.decode(key, Base64.NO_WRAP)
        return SecretKeySpec(decodedKey, 0, decodedKey.size, algorithm)
    }

    fun storeSecretKey(secretKey: SecretKey) {
        val encodedSecretKey = encodeSecretKey(secretKey)
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        sharedPreferences.edit {
            this.putString(context.getString(R.string.secret_key), encodedSecretKey)
            this.commit()
        }
    }

    fun retrieveSecretKey(): SecretKey? {
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        val encodedSecretKey = sharedPreferences.getString(context.getString(R.string.secret_key), null)
        return if (encodedSecretKey != null) {
            decodeSecretKey(encodedSecretKey)
        } else {
            null
        }
    }
}