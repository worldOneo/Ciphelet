package com.github.worldoneo.ciphelet.connector.encryption;

import android.util.Base64;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtility {
    public static final String AES_ALGORITHM = "AES/GCM/NoPadding";

    public static KeyPair GenerateKeypair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException ignored) {
            return null;
        }
    }

    public static byte[] encryptRSA(byte[] plainText, Key publicKey) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
        //Initialize Cipher for DECRYPT_MODE
        OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams);

        return cipher.doFinal(plainText);
    }

    public static byte[] decryptRSA(byte[] cipherTextArray, Key privateKey) throws GeneralSecurityException {
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

    public static SecretKey getKeyFromPassword(String password, byte[] salt )
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2withHmacSHA1");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
        return secret;
    }

    public static byte[] randomBytes(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

    public static IvParameterSpec generateIv() {
        return new IvParameterSpec(randomBytes(16));
    }

    public static String encryptAES(String input, SecretKey key, IvParameterSpec iv) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.encodeToString(cipherText, Base64.DEFAULT);
    }

    public static String decryptAES(String cipherText, SecretKey key, IvParameterSpec iv) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.decode(cipherText, Base64.DEFAULT));
        return new String(plainText);
    }

}
