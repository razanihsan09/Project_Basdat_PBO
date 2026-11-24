package com.klinik.app.dao;

import com.klinik.app.config.Database;
import com.klinik.app.model.Obat;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ObatDAO {

    public List<Obat> getAllObat() {
        List<Obat> list = new ArrayList<>();
        String sql = "SELECT * FROM obat ORDER BY obat_id ASC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Date exp = rs.getDate("tgl_expired");
                Obat o = new Obat(
                    rs.getInt("obat_id"),
                    rs.getString("nama_obat"),
                    rs.getString("kategori"),
                    rs.getInt("stok"),
                    rs.getString("satuan"),
                    exp != null ? exp.toLocalDate() : null
                );
                list.add(o);
            }
        } catch (SQLException e) {
            System.err.println("[ObatDAO] Error getAllObat: " + e.getMessage());
        }
        return list;
    }

    public Obat getObatById(int obatId) {
        String sql = "SELECT * FROM obat WHERE obat_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, obatId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date exp = rs.getDate("tgl_expired");
                    return new Obat(
                        rs.getInt("obat_id"),
                        rs.getString("nama_obat"),
                        rs.getString("kategori"),
                        rs.getInt("stok"),
                        rs.getString("satuan"),
                        exp != null ? exp.toLocalDate() : null
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[ObatDAO] Error getObatById: " + e.getMessage());
        }
        return null;
    }

    public boolean insertObat(Obat o) throws SQLException {
        String sql = "INSERT INTO obat (nama_obat, kategori, stok, satuan, tgl_expired) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, o.getNamaObat());
            ps.setString(2, o.getKategori());
            ps.setInt(3, o.getStok());
            ps.setString(4, o.getSatuan());
            ps.setDate(5, o.getTglExpired() != null ? Date.valueOf(o.getTglExpired()) : null);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        o.setObatId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public boolean updateObat(Obat o) throws SQLException {
        String sql = "UPDATE obat SET nama_obat=?, kategori=?, stok=?, satuan=?, tgl_expired=? WHERE obat_id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, o.getNamaObat());
            ps.setString(2, o.getKategori());
            ps.setInt(3, o.getStok());
            ps.setString(4, o.getSatuan());
            ps.setDate(5, o.getTglExpired() != null ? Date.valueOf(o.getTglExpired()) : null);
            ps.setInt(6, o.getObatId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteObat(int obatId) throws SQLException {
        String sql = "DELETE FROM obat WHERE obat_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, obatId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Mengurangi stok obat secara aman di dalam sebuah transaksi yang sedang berjalan.
     * Menggunakan query atomik "UPDATE obat SET stok = stok - ? WHERE obat_id = ? AND stok >= ?"
     * Jika stok tidak cukup, executeUpdate() menghasilkan 0 dan melempar SQLException.
     */
    public static boolean kurangiStokDalamTransaksi(Connection conn, int obatId, int jumlah) throws SQLException {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah obat yang diresepkan harus lebih dari 0!");
        }

        String sql = "UPDATE obat SET stok = stok - ? WHERE obat_id = ? AND stok >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jumlah);
            ps.setInt(2, obatId);
            ps.setInt(3, jumlah);

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Stok obat ID #" + obatId + " tidak mencukupi untuk jumlah " + jumlah + "!");
            }
            return true;
        }
    }
}
