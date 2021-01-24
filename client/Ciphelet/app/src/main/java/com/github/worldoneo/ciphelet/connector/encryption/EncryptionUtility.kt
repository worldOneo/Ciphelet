package com.github.worldoneo.ciphelet.connector.encryption

import android.util.Base64
import com.iwebpp.crypto.TweetNaclFast
import java.security.GeneralSecurityException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.jvm.Throws

object EncryptionUtility {
    const val AES_ALGORITHM = "AES/GCM/NoPadding"
    fun GenerateKeypair(): TweetNaclFast.Box.KeyPair {
        return TweetNaclFast.Box.keyPair()
    }

    fun encryptNaCL(plainText: ByteArray?, theirPublicKey: ByteArray?, ourPrivateKey: ByteArray?): ByteArray {
        val box = TweetNaclFast.Box(theirPublicKey, ourPrivateKey)
        val nonce = TweetNaclFast.makeBoxNonce()
        val encrypted = box.box(plainText, nonce)
        val out = ByteArray(nonce.size + encrypted.size)
        System.arraycopy(nonce, 0, out, 0, nonce.size)
        System.arraycopy(encrypted, 0, out, nonce.size, encrypted.size)
        return out
    }

    @JvmStatic
    fun decryptNaCL(cipherTextArray: ByteArray, theirPublicKey: ByteArray?, ourPrivateKey: ByteArray?): ByteArray {
        val box = TweetNaclFast.Box(theirPublicKey, ourPrivateKey)
        val nonce = Arrays.copyOfRange(cipherTextArray, 0, TweetNaclFast.Box.nonceLength)
        val message = Arrays.copyOfRange(cipherTextArray, TweetNaclFast.Box.nonceLength, cipherTextArray.size)
        return box.open(message, nonce)
    }

    fun encodeKey(key: ByteArray?): String {
        return Base64.encodeToString(key, Base64.DEFAULT or Base64.NO_WRAP)
    }

    @JvmStatic
    fun decodeKey(key: String?): ByteArray {
        return Base64.decode(key, Base64.DEFAULT or Base64.NO_WRAP)
    }

    fun getKeyFromPassword(password: String, salt: ByteArray): SecretKey? {
        try {
            val factory = SecretKeyFactory.getInstance("PBKDF2withHmacSHA1")
            val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt, 65536, 256)
            return SecretKeySpec(factory.generateSecret(spec)
                    .encoded, "AES")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: InvalidKeySpecException) {
            e.printStackTrace()
        }
        return null
    }

    @JvmStatic
    fun randomBytes(length: Int): ByteArray {
        val bytes = ByteArray(length)
        SecureRandom().nextBytes(bytes)
        return bytes
    }

    @JvmStatic
    fun generateIv(): IvParameterSpec {
        return IvParameterSpec(randomBytes(16))
    }

    @JvmStatic
    @Throws(GeneralSecurityException::class)
    fun encryptAES(input: String, key: SecretKey?, iv: IvParameterSpec?): String {
        val cipher = Cipher.getInstance(AES_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)
        val cipherText = cipher.doFinal(input.toByteArray())
        return Base64.encodeToString(cipherText, Base64.DEFAULT)
    }

    @JvmStatic
    @Throws(GeneralSecurityException::class)
    fun decryptAES(cipherText: String?, key: SecretKey?, iv: IvParameterSpec?): String {
        val cipher = Cipher.getInstance(AES_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, key, iv)
        val plainText = cipher.doFinal(Base64.decode(cipherText, Base64.DEFAULT))
        return String(plainText)
    }
}