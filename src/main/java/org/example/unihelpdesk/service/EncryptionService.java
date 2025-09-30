package org.example.unihelpdesk.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES";
    private final SecretKeySpec secretKey;

    // Secret key එක application.properties file එකෙන් ගන්නවා
    public EncryptionService(@Value("${encryption.secret.key}") String key) {
        // The key must be 16, 24, or 32 bytes long for AES
        this.secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
    }

    // String එකක් encrypt කරන method එක
    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while encrypting data", e);
        }
    }

    // Encrypt කරපු string එකක් decrypt කරන method එක
    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            // Decryption failed (e.g., wrong key or corrupted data)
            // Log the error and return a placeholder or re-throw
            // For security, don't reveal too much detail in the exception
            System.err.println("Decryption failed for data snippet: " + encryptedData.substring(0, Math.min(encryptedData.length(), 10)) + "...");
            return "[Unable to decrypt message]";
        }
    }
}