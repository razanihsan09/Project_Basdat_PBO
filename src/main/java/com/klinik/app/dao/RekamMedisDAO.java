package com.klinik.app.dao;

import com.klinik.app.config.Database;
import com.klinik.app.model.DetailResep;
import com.klinik.app.model.RekamMedis;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RekamMedisDAO {

    /**
     * Mengambil satu halaman riwayat rekam medis langsung dari database.
     */
    public List<RekamMedis> getAllRekamMedis(int limit, int offset) {
        List<RekamMedis> list = new ArrayList<>();
        String sql = "SELECT rm.*, p.nama_pasien, peg.nama_lengkap AS nama_dokter " +
                "FROM rekam_medis rm " +
                "JOIN pasien p ON rm.pasien_id = p.pasien_id " +
                "LEFT JOIN dokter d ON rm.dokter_id = d.pegawai_id " +
                "LEFT JOIN pegawai peg ON d.pegawai_id = peg.pegawai_id " +
                "ORDER BY rm.tanggal_periksa DESC, rm.rekam_medis_id DESC LIMIT ? OFFSET ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, Math.max(1, Math.min(limit, 500)));
            ps.setInt(2, Math.max(0, offset));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToRekamMedis(rs));
            }
        } catch (SQLException e) {
            System.err.println("[RekamMedisDAO] Error getAllRekamMedis(page): " + e.getMessage());
        }
        return list;
    }

    /** Versi tanpa paginasi dipertahankan untuk pemanggil lama; UI menggunakan overload berpaginasi. */
    public List<RekamMedis> getAllRekamMedis() {
        List<RekamMedis> list = new ArrayList<>();
        String sql = "SELECT rm.*, p.nama_pasien, peg.nama_lengkap AS nama_dokter " +
                "FROM rekam_medis rm " +
                "JOIN pasien p ON rm.pasien_id = p.pasien_id " +
                "LEFT JOIN dokter d ON rm.dokter_id = d.pegawai_id " +
                "LEFT JOIN pegawai peg ON d.pegawai_id = peg.pegawai_id " +
                "ORDER BY rm.tanggal_periksa DESC, rm.rekam_medis_id DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapResultSetToRekamMedis(rs));
        } catch (SQLException e) {
            System.err.println("[RekamMedisDAO] Error getAllRekamMedis: " + e.getMessage());
        }
        return list;
    }

    public int countAllRekamMedis() {
        String sql = "SELECT COUNT(*) FROM rekam_medis";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[RekamMedisDAO] Error countAllRekamMedis: " + e.getMessage());
        }
        return 0;
    }

    private RekamMedis mapResultSetToRekamMedis(ResultSet rs) throws SQLException {
        RekamMedis rm = new RekamMedis();
        rm.setRekamMedisId(rs.getInt("rekam_medis_id"));
        rm.setPasienId(rs.getInt("pasien_id"));
        int dokterId = rs.getInt("dokter_id");
        rm.setDokterId(rs.wasNull() ? null : dokterId);
        int antreanId = rs.getInt("antrean_id");
        rm.setAntreanId(rs.wasNull() ? null : antreanId);
        Timestamp tglPeriksa = rs.getTimestamp("tanggal_periksa");
        if (tglPeriksa != null) rm.setTanggalPeriksa(tglPeriksa.toLocalDateTime());
        rm.setKeluhan(rs.getString("keluhan"));
        rm.setDiagnosa(rs.getString("diagnosa"));
        rm.setTindakan(rs.getString("tindakan"));
        rm.setNamaPasien(rs.getString("nama_pasien"));
        rm.setNamaDokter(rs.getString("nama_dokter"));
        return rm;
    }

    public List<RekamMedis> getRekamMedisByPasien(int pasienId) {
        List<RekamMedis> list = new ArrayList<>();
        String sql = "SELECT rm.*, p.nama_pasien, peg.nama_lengkap AS nama_dokter " +
                     "FROM rekam_medis rm " +
                     "JOIN pasien p ON rm.pasien_id = p.pasien_id " +
                     "LEFT JOIN dokter d ON rm.dokter_id = d.pegawai_id " +
                     "LEFT JOIN pegawai peg ON d.pegawai_id = peg.pegawai_id " +
                     "WHERE rm.pasien_id = ? " +
                     "ORDER BY rm.tanggal_periksa DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pasienId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RekamMedis rm = new RekamMedis();
                    rm.setRekamMedisId(rs.getInt("rekam_medis_id"));
                    rm.setPasienId(rs.getInt("pasien_id"));

                    int dokterId = rs.getInt("dokter_id");
                    rm.setDokterId(rs.wasNull() ? null : dokterId);

                    int antreanId = rs.getInt("antrean_id");
                    rm.setAntreanId(rs.wasNull() ? null : antreanId);

                    Timestamp tglPeriksa = rs.getTimestamp("tanggal_periksa");
                    if (tglPeriksa != null) {
                        rm.setTanggalPeriksa(tglPeriksa.toLocalDateTime());
                    }

                    rm.setKeluhan(rs.getString("keluhan"));
                    rm.setDiagnosa(rs.getString("diagnosa"));
                    rm.setTindakan(rs.getString("tindakan"));
                    rm.setNamaPasien(rs.getString("nama_pasien"));
                    rm.setNamaDokter(rs.getString("nama_dokter"));

                    list.add(rm);
                }
            }
        } catch (SQLException e) {
            System.err.println("[RekamMedisDAO] Error getRekamMedisByPasien: " + e.getMessage());
        }
        return list;
    }

    private boolean checkHasStockTrigger(Connection conn) {
        String sql = "SELECT COUNT(*) FROM information_schema.triggers WHERE trigger_schema = DATABASE() AND trigger_name = 'reduce_stok_obat_after_resep'";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (Exception ignored) {}
        return false;
    }

    /**
     * Menyimpan pemeriksaan lengkap dalam SATU TRANSAKSI ATOMIK:
     * 1. Insert rekam_medis
     * 2. Kurangi stok obat di tabel obat secara aman (mencegah double-deduction jika trigger DB aktif)
     * 3. Insert detail_resep
     * 4. Update status antrean menjadi SELESAI
     * Jika terjadi kegagalan pada langkah mana pun, seluruh operasi di-rollback.
     */
    public boolean simpanPemeriksaanLengkap(RekamMedis rm, List<DetailResep> resepList, Integer antreanId) throws SQLException {
        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            // 1. Simpan Rekam Medis
            String sqlRM = "INSERT INTO rekam_medis (pasien_id, dokter_id, antrean_id, tanggal_periksa, keluhan, diagnosa, tindakan) " +
                          "VALUES (?, ?, ?, NOW(), ?, ?, ?)";

            int generatedRmId = -1;
            try (PreparedStatement psRM = conn.prepareStatement(sqlRM, Statement.RETURN_GENERATED_KEYS)) {
                psRM.setInt(1, rm.getPasienId());

                if (rm.getDokterId() == null || rm.getDokterId() == 0) {
                    psRM.setNull(2, Types.INTEGER);
                } else {
                    psRM.setInt(2, rm.getDokterId());
                }

                if (antreanId == null || antreanId == 0) {
                    psRM.setNull(3, Types.INTEGER);
                } else {
                    psRM.setInt(3, antreanId);
                }

                psRM.setString(4, rm.getKeluhan() != null ? rm.getKeluhan() : "-");
                psRM.setString(5, rm.getDiagnosa() != null ? rm.getDiagnosa() : "-");
                psRM.setString(6, rm.getTindakan() != null ? rm.getTindakan() : "-");

                int affected = psRM.executeUpdate();
                if (affected == 0) {
                    throw new SQLException("Gagal membuat data rekam medis baru!");
                }

                try (ResultSet rsKeys = psRM.getGeneratedKeys()) {
                    if (rsKeys.next()) {
                        generatedRmId = rsKeys.getInt(1);
                        rm.setRekamMedisId(generatedRmId);
                    } else {
                        throw new SQLException("Tidak dapat memperoleh ID Rekam Medis baru!");
                    }
                }
            }

            // 2. Kurangi stok dan simpan resep jika ada
            if (resepList != null && !resepList.isEmpty()) {
                boolean hasTrigger = checkHasStockTrigger(conn);

                for (DetailResep dr : resepList) {
                    if (hasTrigger) {
                        // Jika trigger database MySQL sudah ada, pastikan stok cukup terlebih dahulu
                        String sqlCheck = "SELECT stok, nama_obat FROM obat WHERE obat_id = ? FOR UPDATE";
                        try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
                            psCheck.setInt(1, dr.getObatId());
                            try (ResultSet rsCheck = psCheck.executeQuery()) {
                                if (rsCheck.next()) {
                                    int currentStok = rsCheck.getInt("stok");
                                    String nama = rsCheck.getString("nama_obat");
                                    if (currentStok < dr.getJumlah()) {
                                        throw new SQLException("Stok obat '" + nama + "' tidak mencukupi (sisa: " + currentStok + ")!");
                                    }
                                } else {
                                    throw new SQLException("Obat ID #" + dr.getObatId() + " tidak ditemukan!");
                                }
                            }
                        }
                    } else {
                        // Jika tidak ada trigger DB, kurangi stok secara manual melalui aplikasi
                        ObatDAO.kurangiStokDalamTransaksi(conn, dr.getObatId(), dr.getJumlah());
                    }
                }

                // Simpan batch resep (akan memicu trigger DB jika ada)
                DetailResepDAO.saveBatchInTransaction(conn, generatedRmId, resepList);
            }

            // 3. Update status antrean menjadi SELESAI
            if (antreanId != null && antreanId > 0) {
                String sqlAntrean = "UPDATE antrean SET status = 'SELESAI' WHERE antrean_id = ?";
                try (PreparedStatement psAntrean = conn.prepareStatement(sqlAntrean)) {
                    psAntrean.setInt(1, antreanId);
                    psAntrean.executeUpdate();
                }
            }

            // Commit semua perubahan jika berhasil
            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("[RekamMedisDAO] Rollback error: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    public int insertRekamMedis(RekamMedis rm) {
        try {
            boolean ok = simpanPemeriksaanLengkap(rm, null, rm.getAntreanId());
            return ok ? rm.getRekamMedisId() : -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
