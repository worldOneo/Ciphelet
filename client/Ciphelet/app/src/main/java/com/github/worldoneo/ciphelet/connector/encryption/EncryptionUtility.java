package com.github.worldoneo.ciphelet.connector.encryption;

import java.security.*;
import java.security.spec.MGF1ParameterSpec;

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
}
