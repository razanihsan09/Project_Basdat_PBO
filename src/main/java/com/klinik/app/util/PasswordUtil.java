package com.klinik.app.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

    /**
     * Menghasilkan hash SHA-256 dari string password.
     */
    public static String hashPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password tidak boleh null");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hashBytes.length * 2);
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritma SHA-256 tidak didukung di lingkungan ini", e);
        }
    }

    /**
     * Memverifikasi apakah plainPassword cocok dengan hashedPassword.
     * Menggunakan MessageDigest.isEqual untuk mencegah timing attack.
     * Tidak menerima perbandingan plaintext secara terbuka.
     */
    public static boolean checkPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null || storedHash.trim().isEmpty()) {
            return false;
        }

        String computedHash = hashPassword(plainPassword);
        return MessageDigest.isEqual(
                computedHash.getBytes(StandardCharsets.UTF_8),
                storedHash.trim().toLowerCase().getBytes(StandardCharsets.UTF_8)
        );
    }
}
