package com.github.worldoneo.ciphelet.storage;

import android.content.SharedPreferences;
import android.util.Base64;

import com.github.worldoneo.ciphelet.connector.Connector;
import com.github.worldoneo.ciphelet.connector.encryption.EncryptionUtility;

import java.security.GeneralSecurityException;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class SecureStorage {
    private final SharedPreferences sharedPreferences;
    private final SecretKey key;

    public SecureStorage(SharedPreferences sharedPreferences, SecretKey key) {
        this.sharedPreferences = sharedPreferences;
        this.key = key;
    }

    public static byte[] getSalt(SharedPreferences preferences) {
        if (preferences.contains("storagesalt")) {
            return Base64.decode(preferences.getString("storagesalt", ""), Base64.URL_SAFE);
        }
        byte[] salt = EncryptionUtility.randomBytes(32);
        preferences.edit().putString("storagesalt", Base64.encodeToString(salt, Base64.URL_SAFE)).apply();
        return salt;
    }

    public boolean store(String key, Object value) {
        try {
            IvParameterSpec iv = EncryptionUtility.generateIv();
            String encrypted = EncryptionUtility.encryptAES(Connector.gson.toJson(value), this.key, iv);
            String json = new StorageBlock(encrypted, iv).serialize();
            sharedPreferences.edit().putString(key, json).apply();
            return true;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return false;
    }

    public <T> T get(String key, Class<T> classOffT) {
        try {
            String json = sharedPreferences.getString(key, "");
            if (json.equals("")) {
                return null;
            }
            StorageBlock storageBlock = StorageBlock.deserialize(json);
            String decrypted = EncryptionUtility.decryptAES(storageBlock.json, this.key, storageBlock.iv);
            return Connector.gson.fromJson(decrypted, classOffT);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
}
