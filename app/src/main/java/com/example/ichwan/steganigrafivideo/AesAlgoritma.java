package com.example.ichwan.steganigrafivideo;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by ichwan on 11/2/2017.
 */

public class AesAlgoritma {

    static String AES = "AES";
    private static byte[] key = {
            0x2d, 0x2a, 0x2d, 0x42, 0x55, 0x49, 0x4c, 0x44, 0x41, 0x43, 0x4f, 0x44, 0x45, 0x2d, 0x2a, 0x2d
    };


   public String Encrypt(String Data, String Password) throws Exception {

        SecretKeySpec key = generateKey(Password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = android.util.Base64.encodeToString(encVal, android.util.Base64.DEFAULT);
        return encryptedValue;
    }

    public String decrypt(String OutputString, String Password) throws Exception {
        SecretKeySpec key = generateKey(Password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodeValue = android.util.Base64.decode(OutputString, android.util.Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodeValue);
        String decryptedValue = new String(decValue);
        return  decryptedValue;
    }


    public SecretKeySpec generateKey(String Password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = Password.getBytes("UTF-8");
        // byte[] bytes = new byte[16];
        digest.update(bytes, 0, bytes.length);
        //  byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        //SecretKeySpec secretKeySpec = new SecretKeySpec(new byte[16], "AES");
        return secretKeySpec;
    }



}
