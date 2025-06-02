package com.example.videoStreaming.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AesTokenUtils {

    public String encrypt(String secretKey, String ip) throws Exception {
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encrypted = cipher.doFinal(ip.getBytes());
        return Base64.getUrlEncoder().encodeToString(encrypted); // URL-safe token
    }

    public String decrypt(String secretKey, String token) throws Exception {
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decoded = Base64.getUrlDecoder().decode(token);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }
}
