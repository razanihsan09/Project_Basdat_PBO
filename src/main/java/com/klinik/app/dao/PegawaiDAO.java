package com.klinik.app.dao;

import com.klinik.app.config.Database;
import com.klinik.app.model.JenisPegawai;
import com.klinik.app.model.Pegawai;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PegawaiDAO {

    public List<Pegawai> getAllPegawai() {
        List<Pegawai> list = new ArrayList<>();
        String sql = "SELECT * FROM pegawai ORDER BY pegawai_id ASC";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pegawai p = new Pegawai();
                p.setPegawaiId(rs.getInt("pegawai_id"));
                p.setNamaLengkap(rs.getString("nama_lengkap"));
                p.setNoHp(rs.getString("no_hp"));
                p.setJenisPegawai(JenisPegawai.valueOf(rs.getString("jenis_pegawai")));
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("[PegawaiDAO] Error getAllPegawai: " + e.getMessage());
        }
        return list;
    }

    public boolean insertPegawai(Pegawai p) throws SQLException {
        String sql = "INSERT INTO pegawai (nama_lengkap, no_hp, jenis_pegawai) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNamaLengkap());
            ps.setString(2, p.getNoHp());
            ps.setString(3, p.getJenisPegawai().name());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setPegawaiId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean updatePegawai(Pegawai p) throws SQLException {
        String sql = "UPDATE pegawai SET nama_lengkap = ?, no_hp = ?, jenis_pegawai = ? WHERE pegawai_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNamaLengkap());
            ps.setString(2, p.getNoHp());
            ps.setString(3, p.getJenisPegawai().name());
            ps.setInt(4, p.getPegawaiId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deletePegawai(int pegawaiId) throws SQLException {
        String sql = "DELETE FROM pegawai WHERE pegawai_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pegawaiId);
            return ps.executeUpdate() > 0;
        }
    }
}
