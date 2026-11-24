package com.klinik.app.utils;

/**
 * Delegator untuk backward compatibility.
 * Kode baru disarankan menggunakan com.klinik.app.util.PasswordUtil.
 */
public class PasswordUtil {

    public static String hashPassword(String password) {
        return com.klinik.app.util.PasswordUtil.hashPassword(password);
    }

    public static boolean checkPassword(String plainPassword, String storedHash) {
        return com.klinik.app.util.PasswordUtil.checkPassword(plainPassword, storedHash);
    }
}
