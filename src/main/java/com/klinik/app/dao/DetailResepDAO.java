package com.klinik.app.dao;

import com.klinik.app.config.Database;
import com.klinik.app.model.DetailResep;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetailResepDAO {

    public List<DetailResep> getResepByRekamMedis(int rekamMedisId) {
        List<DetailResep> list = new ArrayList<>();
        String sql = "SELECT dr.*, o.nama_obat, o.satuan " +
                     "FROM detail_resep dr " +
                     "JOIN obat o ON dr.obat_id = o.obat_id " +
                     "WHERE dr.rekam_medis_id = ? " +
                     "ORDER BY dr.detail_resep_id ASC";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rekamMedisId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetailResep dr = new DetailResep(
                        rs.getInt("detail_resep_id"),
                        rs.getInt("rekam_medis_id"),
                        rs.getInt("obat_id"),
                        rs.getInt("jumlah"),
                        rs.getString("dosis")
                    );
                    dr.setNamaObat(rs.getString("nama_obat"));
                    dr.setSatuan(rs.getString("satuan"));
                    list.add(dr);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DetailResepDAO] Error getResepByRekamMedis: " + e.getMessage());
        }
        return list;
    }

    /**
     * Menyimpan resep ke database di dalam koneksi transaksi yang sudah ada.
     */
    public static void saveBatchInTransaction(Connection conn, int rekamMedisId, List<DetailResep> listResep) throws SQLException {
        if (listResep == null || listResep.isEmpty()) return;

        String sqlResep = "INSERT INTO detail_resep (rekam_medis_id, obat_id, jumlah, dosis) VALUES (?, ?, ?, ?)";
        try (PreparedStatement psResep = conn.prepareStatement(sqlResep, Statement.RETURN_GENERATED_KEYS)) {
            for (DetailResep dr : listResep) {
                psResep.setInt(1, rekamMedisId);
                psResep.setInt(2, dr.getObatId());
                psResep.setInt(3, dr.getJumlah());
                psResep.setString(4, dr.getDosis());
                psResep.addBatch();
            }
            psResep.executeBatch();
        }
    }

    public boolean saveBatch(int rekamMedisId, List<DetailResep> listResep) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                saveBatchInTransaction(conn, rekamMedisId, listResep);
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
