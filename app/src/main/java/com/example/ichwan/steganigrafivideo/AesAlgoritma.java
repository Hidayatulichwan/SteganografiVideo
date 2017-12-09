package com.example.ichwan.steganigrafivideo;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by ichwan on 11/2/2017.
 */

public class AesAlgoritma {

    String DES="DES";
    private String decrypt(String OutputString, String Password) throws Exception{
        SecretKeySpec key = generateKey(Password);
        Cipher c = Cipher.getInstance(DES);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodeValue= android.util.Base64.decode(OutputString, android.util.Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodeValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;

    }

    private String Encrypt(String Data, String Password) throws Exception {

        SecretKeySpec key = generateKey(Password);
        Cipher c = Cipher.getInstance(DES);
        c.init(Cipher.ENCRYPT_MODE,key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = android.util.Base64.encodeToString(encVal, android.util.Base64.DEFAULT);
        return encryptedValue;

    }

    private SecretKeySpec generateKey(String Password) throws Exception{
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes =  Password.getBytes("UTF-8");
        digest.update(bytes, 0,bytes.length);
        byte[] key= digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }

}
