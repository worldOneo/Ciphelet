package com.github.worldoneo.ciphelet.storage;

import android.util.Base64;

import com.github.worldoneo.ciphelet.connector.Connector;

import javax.crypto.spec.IvParameterSpec;

public class StorageBlock {
    public final String json;
    public final IvParameterSpec iv;

    public StorageBlock(String json, IvParameterSpec iv) {
        this.json = json;
        this.iv = iv;
    }

    private final class SerializeableBlock {
        public final String iv;
        public final String json;

        private SerializeableBlock(String iv, String json) {
            this.iv = iv;
            this.json = json;
        }

        public String serialize() {
            return Connector.gson.toJson(this);
        }
    }

    public String serialize() {
        String iv = Base64.encodeToString(this.iv.getIV(), Base64.URL_SAFE);
        return Connector.gson.toJson(new SerializeableBlock(iv, this.json));
    }

    public static StorageBlock deserialize(String json)  {
        SerializeableBlock serializeableBlock = Connector.gson.fromJson(json, SerializeableBlock.class);
        byte[] iv = Base64.decode(serializeableBlock.iv, Base64.URL_SAFE);
        return new StorageBlock(serializeableBlock.json, new IvParameterSpec(iv));
    }
}
