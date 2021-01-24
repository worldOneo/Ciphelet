package com.github.worldoneo.ciphelet.storage

import android.content.SharedPreferences
import android.util.Base64
import com.github.worldoneo.ciphelet.connector.Connector.Companion.gson
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility.decryptAES
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility.encryptAES
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility.generateIv
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility.randomBytes
import java.security.GeneralSecurityException
import javax.crypto.SecretKey

class SecureStorage(private val sharedPreferences: SharedPreferences, private val key: SecretKey) {
    fun store(key: String?, value: Any?): Boolean {
        try {
            val iv = generateIv()
            val encrypted = encryptAES(gson.toJson(value), this.key, iv)
            val json = StorageBlock(encrypted, iv).serialize()
            sharedPreferences.edit().putString(key, json).apply()
            return true
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        }
        return false
    }

    operator fun <T> get(key: String?, classOffT: Class<T>?): T? {
        try {
            val json = sharedPreferences.getString(key, "")
            if (json == "") {
                return null
            }
            val storageBlock: StorageBlock = StorageBlock.deserialize(json)
            val decrypted = decryptAES(storageBlock.json, this.key, storageBlock.iv)
            return gson.fromJson(decrypted, classOffT)
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        }
        return null
    }

    companion object {
        fun getSalt(preferences: SharedPreferences): ByteArray {
            if (preferences.contains("storagesalt")) {
                return Base64.decode(preferences.getString("storagesalt", ""), Base64.URL_SAFE)
            }
            val salt = randomBytes(32)
            preferences.edit().putString("storagesalt", Base64.encodeToString(salt, Base64.URL_SAFE)).apply()
            return salt
        }
    }

}