package com.github.worldoneo.ciphelet.connector.encryption;

import android.util.Base64;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

public class EncryptionUtility {
    public static KeyPair GenerateKeypair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(4096);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException ignored) {
            return null;
        }
    }

    public static byte[] encrypt(byte[] plainText, Key publicKey) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
        //Initialize Cipher for DECRYPT_MODE
        OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams);

        return cipher.doFinal(plainText);
    }

    public static byte[] decrypt(byte[] cipherTextArray, Key privateKey) throws GeneralSecurityException {
        System.out.println(cipherTextArray.length);
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
        //Initialize Cipher for DECRYPT_MODE
        OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
        cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);

        return cipher.doFinal(cipherTextArray);
    }

    public static String EncodeKey(PrivateKey key) {
        return Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
    }

    public static PrivateKey DecodeKey(String key) {
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(Base64.decode(key, Base64.DEFAULT));
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(ks);
        } catch (GeneralSecurityException e) {
            return null;
        }
    }
}
