package com.klinik.app.dao;

import com.klinik.app.config.Database;
import com.klinik.app.model.JenisKelamin;
import com.klinik.app.model.Pasien;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PasienDAO {

    public List<Pasien> getAllPasien() {
        List<Pasien> list = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "COALESCE(a.keluhan, '-') AS keluhan_utama " +
                     "FROM pasien p " +
                     "LEFT JOIN antrean a ON p.pasien_id = a.pasien_id " +
                     "    AND a.tanggal = CURRENT_DATE() " +
                     "    AND a.status IN ('PENDING', 'MENUNGGU', 'DIPERIKSA') " +
                     "ORDER BY p.pasien_id DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToPasien(rs));
            }
        } catch (SQLException e) {
            System.err.println("[PasienDAO] Error getAllPasien: " + e.getMessage());
        }
        return list;
    }

    /**
     * Mengambil satu halaman data pasien langsung dari MySQL.
     * Pencarian mencakup nama, NIK, nomor HP, dan keluhan aktif hari ini.
     */
    public List<Pasien> getPasienPage(String keyword, int limit, int offset) {
        List<Pasien> list = new ArrayList<>();
        String sql = "SELECT p.*, " +
                "COALESCE((SELECT a.keluhan FROM antrean a " +
                "WHERE a.pasien_id = p.pasien_id AND a.tanggal = CURRENT_DATE() " +
                "AND a.status IN ('PENDING', 'MENUNGGU', 'DIPERIKSA') " +
                "ORDER BY a.antrean_id DESC LIMIT 1), '-') AS keluhan_utama " +
                "FROM pasien p " +
                "WHERE (? = '' OR p.nama_pasien LIKE ? OR p.nik LIKE ? OR p.no_hp LIKE ? " +
                "OR EXISTS (SELECT 1 FROM antrean ax WHERE ax.pasien_id = p.pasien_id " +
                "AND ax.tanggal = CURRENT_DATE() AND ax.status IN ('PENDING', 'MENUNGGU', 'DIPERIKSA') " +
                "AND ax.keluhan LIKE ?)) " +
                "ORDER BY p.pasien_id DESC LIMIT ? OFFSET ?";

        String term = keyword == null ? "" : keyword.trim();
        String like = "%" + term + "%";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, term);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            ps.setString(5, like);
            ps.setInt(6, Math.max(1, Math.min(limit, 500)));
            ps.setInt(7, Math.max(0, offset));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToPasien(rs));
            }
        } catch (SQLException e) {
            System.err.println("[PasienDAO] Error getPasienPage: " + e.getMessage());
        }
        return list;
    }

    public int countPasien(String keyword) {
        String sql = "SELECT COUNT(*) FROM pasien p " +
                "WHERE (? = '' OR p.nama_pasien LIKE ? OR p.nik LIKE ? OR p.no_hp LIKE ? " +
                "OR EXISTS (SELECT 1 FROM antrean ax WHERE ax.pasien_id = p.pasien_id " +
                "AND ax.tanggal = CURRENT_DATE() AND ax.status IN ('PENDING', 'MENUNGGU', 'DIPERIKSA') " +
                "AND ax.keluhan LIKE ?))";
        String term = keyword == null ? "" : keyword.trim();
        String like = "%" + term + "%";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, term);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            ps.setString(5, like);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[PasienDAO] Error countPasien: " + e.getMessage());
        }
        return 0;
    }

    /** Opsi pasien terbatas untuk ComboBox; pencarian dilakukan di server. */
    public List<Pasien> searchPasienForSelector(String keyword, int limit) {
        List<Pasien> list = new ArrayList<>();
        String sql = "SELECT p.*, '-' AS keluhan_utama FROM pasien p " +
                "WHERE (? = '' OR p.nama_pasien LIKE ? OR p.nik LIKE ? OR p.no_hp LIKE ?) " +
                "ORDER BY p.pasien_id DESC LIMIT ?";
        String term = keyword == null ? "" : keyword.trim();
        String like = "%" + term + "%";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, term);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            ps.setInt(5, Math.max(1, Math.min(limit, 200)));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToPasien(rs));
            }
        } catch (SQLException e) {
            System.err.println("[PasienDAO] Error searchPasienForSelector: " + e.getMessage());
        }
        return list;
    }

    public Pasien getPasienByNik(String nik) {
        String sql = "SELECT p.*, '-' AS keluhan_utama FROM pasien p WHERE p.nik = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nik);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPasien(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[PasienDAO] Error getPasienByNik: " + e.getMessage());
        }
        return null;
    }

    public Pasien getPasienById(int pasienId) {
        String sql = "SELECT p.*, '-' AS keluhan_utama FROM pasien p WHERE p.pasien_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pasienId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPasien(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[PasienDAO] Error getPasienById: " + e.getMessage());
        }
        return null;
    }

    private Pasien mapResultSetToPasien(ResultSet rs) throws SQLException {
        Pasien p = new Pasien();
        p.setPasienId(rs.getInt("pasien_id"));
        p.setNik(rs.getString("nik"));
        p.setPasswordHash(rs.getString("password_hash"));
        p.setNamaPasien(rs.getString("nama_pasien"));

        Date tgl = rs.getDate("tanggal_lahir");
        if (tgl != null) p.setTanggalLahir(tgl.toLocalDate());

        String jk = rs.getString("jenis_kelamin");
        if (jk != null) {
            try {
                p.setJenisKelamin(JenisKelamin.valueOf(jk));
            } catch (Exception ignored) {}
        }

        p.setAlamat(rs.getString("alamat"));
        p.setNoHp(rs.getString("no_hp"));
        p.setAlergiObat(rs.getString("alergi_obat"));

        try {
            p.setKeluhanUtama(rs.getString("keluhan_utama"));
        } catch (SQLException ignored) {}

        return p;
    }

    public boolean insertPasien(Pasien p) throws SQLException {
        String sql = "INSERT INTO pasien (nik, password_hash, nama_pasien, tanggal_lahir, jenis_kelamin, alamat, no_hp, alergi_obat) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNik());
            ps.setString(2, p.getPasswordHash() != null ? p.getPasswordHash() : "");
            ps.setString(3, p.getNamaPasien());
            ps.setDate(4, p.getTanggalLahir() != null ? Date.valueOf(p.getTanggalLahir()) : null);
            ps.setString(5, p.getJenisKelamin() != null ? p.getJenisKelamin().name() : null);
            ps.setString(6, p.getAlamat());
            ps.setString(7, p.getNoHp());
            ps.setString(8, p.getAlergiObat());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rsKeys = ps.getGeneratedKeys()) {
                    if (rsKeys.next()) {
                        p.setPasienId(rsKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public boolean updatePasien(Pasien p) throws SQLException {
        String sql = "UPDATE pasien SET nik = ?, nama_pasien = ?, tanggal_lahir = ?, jenis_kelamin = ?, alamat = ?, no_hp = ?, alergi_obat = ? WHERE pasien_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNik());
            ps.setString(2, p.getNamaPasien());
            ps.setDate(3, p.getTanggalLahir() != null ? Date.valueOf(p.getTanggalLahir()) : null);
            ps.setString(4, p.getJenisKelamin() != null ? p.getJenisKelamin().name() : null);
            ps.setString(5, p.getAlamat());
            ps.setString(6, p.getNoHp());
            ps.setString(7, p.getAlergiObat());
            ps.setInt(8, p.getPasienId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deletePasien(int pasienId) throws SQLException {
        String sql = "DELETE FROM pasien WHERE pasien_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pasienId);
            return ps.executeUpdate() > 0;
        }
    }
}
