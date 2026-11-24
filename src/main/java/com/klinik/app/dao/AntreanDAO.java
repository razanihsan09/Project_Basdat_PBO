package com.klinik.app.dao;

import com.klinik.app.config.Database;
import com.klinik.app.model.Antrean;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AntreanDAO {

    private static final String SELECT_ANTREAN_AKTIF =
            "SELECT a.*, p.nama_pasien, p.nik, p.alergi_obat, peg.nama_lengkap AS nama_dokter " +
            "FROM antrean a " +
            "JOIN pasien p ON a.pasien_id = p.pasien_id " +
            "LEFT JOIN dokter d ON a.dokter_id = d.pegawai_id " +
            "LEFT JOIN pegawai peg ON d.pegawai_id = peg.pegawai_id ";

    /** Mengambil antrean aktif per halaman (paginasi di sisi database). */
    public List<Antrean> getAntreanAktifPage(Integer dokterId, int limit, int offset) {
        List<Antrean> list = new ArrayList<>();
        String sql = SELECT_ANTREAN_AKTIF +
                "WHERE a.tanggal = CURRENT_DATE() AND a.status IN ('MENUNGGU', 'DIPERIKSA') " +
                (dokterId != null ? "AND a.dokter_id = ? " : "") +
                "ORDER BY CASE a.status WHEN 'DIPERIKSA' THEN 0 ELSE 1 END, " +
                "CASE a.tingkat_urgensi WHEN 'MERAH' THEN 1 WHEN 'KUNING' THEN 2 ELSE 3 END, " +
                "a.no_antrean ASC, a.antrean_id ASC LIMIT ? OFFSET ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int index = 1;
            if (dokterId != null) ps.setInt(index++, dokterId);
            ps.setInt(index++, Math.max(1, Math.min(limit, 500)));
            ps.setInt(index, Math.max(0, offset));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToAntrean(rs));
            }
        } catch (SQLException e) {
            System.err.println("[AntreanDAO] Error getAntreanAktifPage: " + e.getMessage());
        }
        return list;
    }

    public int countAntreanAktif(Integer dokterId) {
        String sql = "SELECT COUNT(*) FROM antrean WHERE tanggal = CURRENT_DATE() " +
                "AND status IN ('MENUNGGU', 'DIPERIKSA') " +
                (dokterId != null ? "AND dokter_id = ?" : "");
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (dokterId != null) ps.setInt(1, dokterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[AntreanDAO] Error countAntreanAktif: " + e.getMessage());
        }
        return 0;
    }

    /** Mengambil satu antrean MENUNGGU teratas dari seluruh antrean, bukan hanya halaman aktif. */
    public Antrean getAntreanBerikutnya(Integer dokterId) {
        String sql = SELECT_ANTREAN_AKTIF +
                "WHERE a.tanggal = CURRENT_DATE() AND a.status = 'MENUNGGU' " +
                (dokterId != null ? "AND a.dokter_id = ? " : "") +
                "ORDER BY CASE a.tingkat_urgensi WHEN 'MERAH' THEN 1 WHEN 'KUNING' THEN 2 ELSE 3 END, " +
                "a.no_antrean ASC, a.antrean_id ASC LIMIT 1";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (dokterId != null) ps.setInt(1, dokterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToAntrean(rs);
            }
        } catch (SQLException e) {
            System.err.println("[AntreanDAO] Error getAntreanBerikutnya: " + e.getMessage());
        }
        return null;
    }

    /** Hasil pencarian antrean untuk ComboBox pemeriksaan; dibatasi agar tidak memuat jutaan baris. */
    public List<Antrean> searchAntreanAktifHariIni(Integer dokterId, String keyword, int limit) {
        List<Antrean> list = new ArrayList<>();
        String sql = SELECT_ANTREAN_AKTIF +
                "WHERE a.tanggal = CURRENT_DATE() AND a.status IN ('MENUNGGU', 'DIPERIKSA') " +
                (dokterId != null ? "AND a.dokter_id = ? " : "") +
                "AND (? = '' OR p.nama_pasien LIKE ? OR p.nik LIKE ? OR CAST(a.no_antrean AS CHAR) LIKE ?) " +
                "ORDER BY CASE a.tingkat_urgensi WHEN 'MERAH' THEN 1 WHEN 'KUNING' THEN 2 ELSE 3 END, " +
                "a.no_antrean ASC, a.antrean_id ASC LIMIT ?";
        String term = keyword == null ? "" : keyword.trim();
        String like = "%" + term + "%";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int index = 1;
            if (dokterId != null) ps.setInt(index++, dokterId);
            ps.setString(index++, term);
            ps.setString(index++, like);
            ps.setString(index++, like);
            ps.setString(index++, like);
            ps.setInt(index, Math.max(1, Math.min(limit, 200)));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultSetToAntrean(rs));
            }
        } catch (SQLException e) {
            System.err.println("[AntreanDAO] Error searchAntreanAktifHariIni: " + e.getMessage());
        }
        return list;
    }

    public List<Antrean> getAllAntreanHariIni() {
        List<Antrean> list = new ArrayList<>();
        String sql = "SELECT a.*, p.nama_pasien, p.nik, p.alergi_obat, peg.nama_lengkap AS nama_dokter " +
                     "FROM antrean a " +
                     "JOIN pasien p ON a.pasien_id = p.pasien_id " +
                     "LEFT JOIN dokter d ON a.dokter_id = d.pegawai_id " +
                     "LEFT JOIN pegawai peg ON d.pegawai_id = peg.pegawai_id " +
                     "WHERE a.tanggal = CURRENT_DATE() AND a.status IN ('MENUNGGU', 'DIPERIKSA') " +
                     "ORDER BY CASE a.tingkat_urgensi " +
                     "    WHEN 'MERAH' THEN 1 " +
                     "    WHEN 'KUNING' THEN 2 " +
                     "    WHEN 'HIJAU' THEN 3 END ASC, a.no_antrean ASC";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Antrean a = mapResultSetToAntrean(rs);
                list.add(a);
            }
        } catch (SQLException e) {
            System.err.println("[AntreanDAO] Error getAllAntreanHariIni: " + e.getMessage());
        }
        return list;
    }

    public List<Antrean> getAntreanDokterHariIni(int dokterId) {
        List<Antrean> list = new ArrayList<>();
        String sql = "SELECT a.*, p.nama_pasien, p.nik, p.alergi_obat, peg.nama_lengkap AS nama_dokter " +
                     "FROM antrean a " +
                     "JOIN pasien p ON a.pasien_id = p.pasien_id " +
                     "LEFT JOIN dokter d ON a.dokter_id = d.pegawai_id " +
                     "LEFT JOIN pegawai peg ON d.pegawai_id = peg.pegawai_id " +
                     "WHERE a.tanggal = CURRENT_DATE() AND a.dokter_id = ? AND a.status IN ('MENUNGGU', 'DIPERIKSA') " +
                     "ORDER BY CASE a.tingkat_urgensi " +
                     "    WHEN 'MERAH' THEN 1 " +
                     "    WHEN 'KUNING' THEN 2 " +
                     "    WHEN 'HIJAU' THEN 3 END ASC, a.no_antrean ASC";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dokterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAntrean(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[AntreanDAO] Error getAntreanDokterHariIni: " + e.getMessage());
        }
        return list;
    }

    public List<Antrean> getAntreanPendingHariIni() {
        List<Antrean> list = new ArrayList<>();
        String sql = "SELECT a.*, p.nama_pasien, p.nik, p.alergi_obat, '-' AS nama_dokter " +
                     "FROM antrean a " +
                     "JOIN pasien p ON a.pasien_id = p.pasien_id " +
                     "WHERE a.tanggal = CURRENT_DATE() AND a.status = 'PENDING' " +
                     "ORDER BY a.antrean_id ASC";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToAntrean(rs));
            }
        } catch (SQLException e) {
            System.err.println("[AntreanDAO] Error getAntreanPendingHariIni: " + e.getMessage());
        }
        return list;
    }

    private Antrean mapResultSetToAntrean(ResultSet rs) throws SQLException {
        Antrean a = new Antrean();
        a.setAntreanId(rs.getInt("antrean_id"));
        a.setPasienId(rs.getInt("pasien_id"));

        int adminId = rs.getInt("admin_id");
        a.setAdminId(rs.wasNull() ? 0 : adminId);

        int dokterId = rs.getInt("dokter_id");
        a.setDokterId(rs.wasNull() ? 0 : dokterId);

        Date tgl = rs.getDate("tanggal");
        if (tgl != null) a.setTanggal(tgl.toLocalDate());

        a.setNoAntrean(rs.getInt("no_antrean"));
        a.setTingkatUrgensi(rs.getString("tingkat_urgensi"));
        a.setStatus(rs.getString("status"));
        a.setNamaPasien(rs.getString("nama_pasien"));
        a.setNamaDokter(rs.getString("nama_dokter"));

        try {
            a.setKeluhan(rs.getString("keluhan"));
        } catch (SQLException ignored) {}

        return a;
    }

    public boolean isPasienSudahAntreHariIni(int pasienId) {
        String sql = "SELECT COUNT(*) FROM antrean WHERE pasien_id = ? AND tanggal = CURRENT_DATE() AND status IN ('PENDING', 'MENUNGGU', 'DIPERIKSA')";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pasienId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("[AntreanDAO] Error isPasienSudahAntreHariIni: " + e.getMessage());
        }
        return false;
    }

    /**
     * Mengambil nomor antrean berikutnya untuk dokter tertentu pada hari ini.
     * Mencakup SELURUH antrean hari ini agar tidak bertabrakan dengan constraint uq_antrean_harian.
     */
    public int getNomorAntreanBerikutnya(int dokterId) {
        String sql = "SELECT COALESCE(MAX(no_antrean), 0) + 1 AS next_no " +
                     "FROM antrean WHERE dokter_id = ? AND tanggal = CURRENT_DATE()";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dokterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("next_no");
                }
            }
        } catch (SQLException e) {
            System.err.println("[AntreanDAO] Error getNomorAntreanBerikutnya: " + e.getMessage());
        }
        return 1;
    }

    public boolean insertAntreanPending(int pasienId, String keluhan) throws SQLException {
        // Cek admin_id default yang valid
        int adminId = 1;
        String sqlAdmin = "SELECT pegawai_id FROM admin_system LIMIT 1";

        try (Connection conn = Database.getConnection()) {
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sqlAdmin)) {
                if (rs.next()) adminId = rs.getInt(1);
            }

            String sql = "INSERT INTO antrean (pasien_id, admin_id, dokter_id, tanggal, no_antrean, tingkat_urgensi, keluhan, status) " +
                         "VALUES (?, ?, NULL, CURRENT_DATE(), 0, 'HIJAU', ?, 'PENDING')";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, pasienId);
                ps.setInt(2, adminId);
                ps.setString(3, keluhan);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                // Fallback jika dokter_id NOT NULL
                String sqlFallback = "INSERT INTO antrean (pasien_id, admin_id, dokter_id, tanggal, no_antrean, tingkat_urgensi, keluhan, status) " +
                                     "VALUES (?, ?, (SELECT pegawai_id FROM dokter LIMIT 1), CURRENT_DATE(), 0, 'HIJAU', ?, 'PENDING')";
                try (PreparedStatement ps2 = conn.prepareStatement(sqlFallback)) {
                    ps2.setInt(1, pasienId);
                    ps2.setInt(2, adminId);
                    ps2.setString(3, keluhan);
                    return ps2.executeUpdate() > 0;
                }
            }
        }
    }

    public boolean prosesAntreanOlehAdmin(int pasienId, int dokterId, int adminId, int noAntrean, String triase) throws SQLException {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Cek apakah ada antrean PENDING
                String sqlCek = "SELECT antrean_id FROM antrean WHERE pasien_id = ? AND tanggal = CURRENT_DATE() AND status = 'PENDING' FOR UPDATE";
                int pendingId = -1;
                try (PreparedStatement psCek = conn.prepareStatement(sqlCek)) {
                    psCek.setInt(1, pasienId);
                    try (ResultSet rs = psCek.executeQuery()) {
                        if (rs.next()) {
                            pendingId = rs.getInt("antrean_id");
                        }
                    }
                }

                // Jika nomor antrean 0 atau otomatis diminta, hitung nomor terbaru di dalam transaksi
                if (noAntrean <= 0) {
                    String sqlMax = "SELECT COALESCE(MAX(no_antrean), 0) + 1 FROM antrean WHERE dokter_id = ? AND tanggal = CURRENT_DATE() FOR UPDATE";
                    try (PreparedStatement psMax = conn.prepareStatement(sqlMax)) {
                        psMax.setInt(1, dokterId);
                        try (ResultSet rs = psMax.executeQuery()) {
                            if (rs.next()) noAntrean = rs.getInt(1);
                        }
                    }
                }

                if (pendingId > 0) {
                    String sqlUpdate = "UPDATE antrean SET dokter_id = ?, admin_id = ?, no_antrean = ?, tingkat_urgensi = ?, status = 'MENUNGGU' WHERE antrean_id = ?";
                    try (PreparedStatement psUp = conn.prepareStatement(sqlUpdate)) {
                        psUp.setInt(1, dokterId);
                        psUp.setInt(2, adminId);
                        psUp.setInt(3, noAntrean);
                        psUp.setString(4, triase != null ? triase : "HIJAU");
                        psUp.setInt(5, pendingId);
                        psUp.executeUpdate();
                    }
                } else {
                    String sqlInsert = "INSERT INTO antrean (pasien_id, admin_id, dokter_id, tanggal, no_antrean, tingkat_urgensi, status) VALUES (?, ?, ?, CURRENT_DATE(), ?, ?, 'MENUNGGU')";
                    try (PreparedStatement psIns = conn.prepareStatement(sqlInsert)) {
                        psIns.setInt(1, pasienId);
                        psIns.setInt(2, adminId);
                        psIns.setInt(3, dokterId);
                        psIns.setInt(4, noAntrean);
                        psIns.setString(5, triase != null ? triase : "HIJAU");
                        psIns.executeUpdate();
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public boolean updateStatus(int antreanId, String newStatus) {
        String sql = "UPDATE antrean SET status = ? WHERE antrean_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, antreanId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AntreanDAO] Error updateStatus: " + e.getMessage());
            return false;
        }
    }
}
