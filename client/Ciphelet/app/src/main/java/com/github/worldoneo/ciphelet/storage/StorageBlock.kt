package com.github.worldoneo.ciphelet.storage

import android.util.Base64
import com.github.worldoneo.ciphelet.connector.Connector.Companion.gson
import javax.crypto.spec.IvParameterSpec

class StorageBlock(val json: String, val iv: IvParameterSpec) {

    private inner class SerializeableBlock(val iv: String, val json: String) {
        fun serialize(): String {
            return gson.toJson(this)
        }

    }

    fun serialize(): String {
        val iv = Base64.encodeToString(iv.iv, Base64.URL_SAFE)
        return gson.toJson(SerializeableBlock(iv, json))
    }

    companion object {
        fun deserialize(json: String?): StorageBlock {
            val serializeableBlock = gson.fromJson(json, SerializeableBlock::class.java)
            val iv = Base64.decode(serializeableBlock.iv, Base64.URL_SAFE)
            return StorageBlock(serializeableBlock.json, IvParameterSpec(iv))
        }
    }

}