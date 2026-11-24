package com.klinik.app.dao;

import com.klinik.app.config.Database;
import com.klinik.app.model.AdminSystem;
import com.klinik.app.model.Dokter;
import com.klinik.app.model.JenisPegawai;
import com.klinik.app.model.Pasien;
import com.klinik.app.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthDAO {

    private String lastErrorMessage = "";

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public Object login(String inputUser, String passwordMentah) {
        lastErrorMessage = "";

        if (inputUser == null || passwordMentah == null || inputUser.trim().isEmpty() || passwordMentah.trim().isEmpty()) {
            lastErrorMessage = "Username dan password wajib diisi!";
            return null;
        }

        String userTrimmed = inputUser.trim();
        String passTrimmed = passwordMentah.trim();

        try {
            // 1. Cek Admin System (via Username)
            AdminSystem admin = loginAdmin(userTrimmed, passTrimmed);
            if (admin != null) return admin;

            // 2. Cek Dokter (via Username)
            Dokter dokter = loginDokter(userTrimmed, passTrimmed);
            if (dokter != null) return dokter;

            // 3. Cek Pasien (via NIK)
            Pasien pasien = loginPasien(userTrimmed, passTrimmed);
            if (pasien != null) return pasien;

        } catch (SQLException e) {
            e.printStackTrace();
            lastErrorMessage = "Gagal terhubung ke database: " + e.getMessage();
            return null;
        }

        lastErrorMessage = "Username atau password salah!";
        return null;
    }

    private boolean verifyPassword(String inputPassword, String storedPassword) {
        if (storedPassword == null || storedPassword.trim().isEmpty()) return false;
        // Hanya verifikasi via hash SHA-256 yang aman, hilangkan perbandingan plaintext
        return PasswordUtil.checkPassword(inputPassword, storedPassword);
    }

    private AdminSystem loginAdmin(String username, String passwordMentah) throws SQLException {
        String sql = "SELECT a.pegawai_id, a.username, a.password_hash, p.nama_lengkap, p.no_hp " +
                     "FROM admin_system a " +
                     "JOIN pegawai p ON a.pegawai_id = p.pegawai_id " +
                     "WHERE a.username = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (verifyPassword(passwordMentah, storedHash)) {
                        AdminSystem admin = new AdminSystem();
                        admin.setPegawaiId(rs.getInt("pegawai_id"));
                        admin.setUsername(rs.getString("username"));
                        admin.setPasswordHash(storedHash);
                        admin.setNamaLengkap(rs.getString("nama_lengkap"));
                        admin.setNoHp(rs.getString("no_hp"));
                        return admin;
                    }
                }
            }
        }
        return null;
    }

    private Dokter loginDokter(String username, String passwordMentah) throws SQLException {
        String sql = "SELECT d.pegawai_id, d.username, d.password_hash, d.no_sip, d.spesialisasi, " +
                     "p.nama_lengkap, p.no_hp " +
                     "FROM dokter d " +
                     "JOIN pegawai p ON d.pegawai_id = p.pegawai_id " +
                     "WHERE d.username = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (verifyPassword(passwordMentah, storedHash)) {
                        Dokter d = new Dokter();
                        d.setPegawaiId(rs.getInt("pegawai_id"));
                        d.setUsername(rs.getString("username"));
                        d.setPasswordHash(storedHash);
                        d.setNoSip(rs.getString("no_sip"));
                        d.setSpesialisasi(rs.getString("spesialisasi"));
                        d.setNamaLengkap(rs.getString("nama_lengkap"));
                        d.setNoHp(rs.getString("no_hp"));
                        d.setJenisPegawai(JenisPegawai.DOKTER);
                        return d;
                    }
                }
            }
        }
        return null;
    }

    private Pasien loginPasien(String nik, String passwordMentah) throws SQLException {
        String sql = "SELECT * FROM pasien WHERE nik = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nik);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (verifyPassword(passwordMentah, storedHash)) {
                        Pasien p = new Pasien();
                        p.setPasienId(rs.getInt("pasien_id"));
                        p.setNik(rs.getString("nik"));
                        p.setNamaPasien(rs.getString("nama_pasien"));
                        p.setPasswordHash(storedHash);
                        java.sql.Date tgl = rs.getDate("tanggal_lahir");
                        if (tgl != null) p.setTanggalLahir(tgl.toLocalDate());
                        p.setNoHp(rs.getString("no_hp"));
                        p.setAlamat(rs.getString("alamat"));
                        p.setAlergiObat(rs.getString("alergi_obat"));
                        return p;
                    }
                }
            }
        }
        return null;
    }
}
