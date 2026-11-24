package com.klinik.app.dao;

import com.klinik.app.config.Database;
import com.klinik.app.model.Dokter;
import com.klinik.app.model.JenisPegawai;
import com.klinik.app.model.Pegawai;
import com.klinik.app.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DokterDAO {

    public List<Dokter> getAllDokter() {
        List<Dokter> listDokter = new ArrayList<>();
        String sql = "SELECT d.pegawai_id, p.nama_lengkap, p.no_hp, p.jenis_pegawai, " +
                     "d.username, d.no_sip, d.spesialisasi " +
                     "FROM dokter d " +
                     "JOIN pegawai p ON d.pegawai_id = p.pegawai_id " +
                     "ORDER BY d.pegawai_id ASC";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Dokter d = new Dokter();
                d.setPegawaiId(rs.getInt("pegawai_id"));
                d.setUsername(rs.getString("username"));
                d.setNoSip(rs.getString("no_sip"));
                d.setSpesialisasi(rs.getString("spesialisasi"));
                d.setNamaLengkap(rs.getString("nama_lengkap"));
                d.setNoHp(rs.getString("no_hp"));
                d.setJenisPegawai(JenisPegawai.DOKTER);

                listDokter.add(d);
            }
        } catch (SQLException e) {
            System.err.println("[DokterDAO] Error getAllDokter: " + e.getMessage());
        }
        return listDokter;
    }

    public Dokter getDokterById(int pegawaiId) {
        String sql = "SELECT d.pegawai_id, p.nama_lengkap, p.no_hp, p.jenis_pegawai, " +
                     "d.username, d.no_sip, d.spesialisasi " +
                     "FROM dokter d " +
                     "JOIN pegawai p ON d.pegawai_id = p.pegawai_id " +
                     "WHERE d.pegawai_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pegawaiId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Dokter d = new Dokter();
                    d.setPegawaiId(rs.getInt("pegawai_id"));
                    d.setUsername(rs.getString("username"));
                    d.setNoSip(rs.getString("no_sip"));
                    d.setSpesialisasi(rs.getString("spesialisasi"));
                    d.setNamaLengkap(rs.getString("nama_lengkap"));
                    d.setNoHp(rs.getString("no_hp"));
                    d.setJenisPegawai(JenisPegawai.DOKTER);
                    return d;
                }
            }
        } catch (SQLException e) {
            System.err.println("[DokterDAO] Error getDokterById: " + e.getMessage());
        }
        return null;
    }

    public boolean buatAkunDokter(Pegawai p, Dokter d, String rawPassword) throws SQLException {
        String sqlPegawai = "INSERT INTO pegawai (nama_lengkap, no_hp, jenis_pegawai) VALUES (?, ?, ?)";
        String sqlDokter = "INSERT INTO dokter (pegawai_id, username, password_hash, no_sip, spesialisasi) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            int generatedPegawaiId = -1;
            try (PreparedStatement stmtP = conn.prepareStatement(sqlPegawai, Statement.RETURN_GENERATED_KEYS)) {
                stmtP.setString(1, p.getNamaLengkap());
                stmtP.setString(2, p.getNoHp());
                stmtP.setString(3, JenisPegawai.DOKTER.name());
                stmtP.executeUpdate();

                try (ResultSet rsKeys = stmtP.getGeneratedKeys()) {
                    if (rsKeys.next()) {
                        generatedPegawaiId = rsKeys.getInt(1);
                    }
                }
            }

            if (generatedPegawaiId == -1) {
                conn.rollback();
                throw new SQLException("Gagal membuat record pegawai untuk dokter!");
            }

            try (PreparedStatement stmtD = conn.prepareStatement(sqlDokter)) {
                stmtD.setInt(1, generatedPegawaiId);
                stmtD.setString(2, d.getUsername());
                stmtD.setString(3, PasswordUtil.hashPassword(rawPassword));
                stmtD.setString(4, d.getNoSip());
                stmtD.setString(5, d.getSpesialisasi());
                stmtD.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public boolean updateAkunDokter(Pegawai p, Dokter d, String newRawPassword) throws SQLException {
        String sqlPegawai = "UPDATE pegawai SET nama_lengkap = ?, no_hp = ? WHERE pegawai_id = ?";
        String sqlDokter = (newRawPassword != null && !newRawPassword.trim().isEmpty())
                ? "UPDATE dokter SET username = ?, password_hash = ?, no_sip = ?, spesialisasi = ? WHERE pegawai_id = ?"
                : "UPDATE dokter SET username = ?, no_sip = ?, spesialisasi = ? WHERE pegawai_id = ?";

        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmtP = conn.prepareStatement(sqlPegawai)) {
                stmtP.setString(1, p.getNamaLengkap());
                stmtP.setString(2, p.getNoHp());
                stmtP.setInt(3, p.getPegawaiId());
                stmtP.executeUpdate();
            }

            try (PreparedStatement stmtD = conn.prepareStatement(sqlDokter)) {
                stmtD.setString(1, d.getUsername());
                if (newRawPassword != null && !newRawPassword.trim().isEmpty()) {
                    stmtD.setString(2, PasswordUtil.hashPassword(newRawPassword.trim()));
                    stmtD.setString(3, d.getNoSip());
                    stmtD.setString(4, d.getSpesialisasi());
                    stmtD.setInt(5, p.getPegawaiId());
                } else {
                    stmtD.setString(2, d.getNoSip());
                    stmtD.setString(3, d.getSpesialisasi());
                    stmtD.setInt(4, p.getPegawaiId());
                }
                stmtD.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }
}
