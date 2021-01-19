package com.github.worldoneo.ciphelet.connector.encryption;

import android.util.Base64;

import com.iwebpp.crypto.TweetNaclFast;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtility {
    public static final String AES_ALGORITHM = "AES/GCM/NoPadding";

    public static TweetNaclFast.Box.KeyPair GenerateKeypair() {
        return TweetNaclFast.Box.keyPair();
    }

    public static byte[] encryptNaCL(byte[] plainText, byte[] theirPublicKey, byte[] ourPrivateKey) {
        TweetNaclFast.Box box = new TweetNaclFast.Box(theirPublicKey, ourPrivateKey);
        byte[] nonce = TweetNaclFast.makeBoxNonce();
        byte[] encrypted = box.box(plainText, nonce);
        byte[] out = new byte[nonce.length+encrypted.length];
        System.arraycopy(nonce, 0, out, 0, nonce.length);
        System.arraycopy(encrypted, 0, out, nonce.length, encrypted.length);
        return out;
    }

    public static byte[] decryptNaCL(byte[] cipherTextArray, byte[] theirPublicKey, byte[] ourPrivateKey) {
        TweetNaclFast.Box box = new TweetNaclFast.Box(theirPublicKey, ourPrivateKey);
        byte[] nonce = Arrays.copyOfRange(cipherTextArray, 0, TweetNaclFast.Box.nonceLength);
        byte[] message = Arrays.copyOfRange(cipherTextArray, TweetNaclFast.Box.nonceLength, cipherTextArray.length);
        return box.open(message, nonce);
    }

    public static String encodeKey(byte[] key) {
        return Base64.encodeToString(key, Base64.DEFAULT|Base64.NO_WRAP);
    }

    public static byte[] decodeKey(String key) {
        return Base64.decode(key, Base64.DEFAULT|Base64.NO_WRAP);
    }

    public static SecretKey getKeyFromPassword(String password, byte[] salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2withHmacSHA1");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
            return new SecretKeySpec(factory.generateSecret(spec)
                    .getEncoded(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
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
