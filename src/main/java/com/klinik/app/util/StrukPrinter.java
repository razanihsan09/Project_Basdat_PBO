package com.klinik.app.util;

import com.klinik.app.model.DetailResep;
import com.klinik.app.model.Dokter;
import com.klinik.app.model.Pasien;
import com.klinik.app.model.RekamMedis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StrukPrinter {

    private static final DateTimeFormatter D_FORMAT = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm");

    /**
     * Menghasilkan teks struk lengkap dengan nama pasien, dokter, dan nama obat yang jelas.
     */
    public static String buildReceiptText(Pasien pasien, Dokter dokter, RekamMedis rm, List<DetailResep> resepList) {
        StringBuilder sb = new StringBuilder();

        String tglStr = rm.getTanggalPeriksa() != null 
                ? rm.getTanggalPeriksa().format(D_FORMAT) 
                : LocalDateTime.now().format(D_FORMAT);

        String namaPasien = pasien != null ? pasien.getNamaPasien() : (rm.getNamaPasien() != null ? rm.getNamaPasien() : "Pasien ID #" + rm.getPasienId());
        String nikPasien = pasien != null ? pasien.getNik() : "-";
        String alergiPasien = (pasien != null && pasien.getAlergiObat() != null && !pasien.getAlergiObat().trim().isEmpty() && !"-".equals(pasien.getAlergiObat().trim()))
                ? pasien.getAlergiObat() : "Tidak ada";

        String namaDokter = dokter != null ? dokter.getNamaLengkap() : (rm.getNamaDokter() != null ? rm.getNamaDokter() : "Dokter Umum");
        String sipDokter = dokter != null && dokter.getNoSip() != null ? dokter.getNoSip() : "-";
        String spesialisasi = dokter != null && dokter.getSpesialisasi() != null ? dokter.getSpesialisasi() : "Praktik Umum";

        sb.append("=================================================================\n");
        sb.append("                      MYCLINIC                   \n");
        sb.append("               Jl. Pemuda No. 45 Kebumen - Jawa Tengah           \n");
        sb.append("                       Telp: (0287) 381-999                      \n");
        sb.append("=================================================================\n");
        sb.append(String.format(" No. Rekam Medis : RM-%05d%n", rm.getRekamMedisId()));
        if (rm.getAntreanId() != null && rm.getAntreanId() > 0) {
            sb.append(String.format(" No. Antrean     : %d%n", rm.getAntreanId()));
        }
        sb.append(String.format(" Waktu Periksa   : %s WIB%n", tglStr));
        sb.append("-----------------------------------------------------------------\n");
        sb.append(" DATA PASIEN\n");
        sb.append(String.format("   Nama Lengkap  : %s%n", namaPasien));
        sb.append(String.format("   NIK           : %s%n", nikPasien));
        sb.append(String.format("   Riwayat Alergi: %s%n", alergiPasien));
        sb.append("-----------------------------------------------------------------\n");
        sb.append(" DOKTER PEMERIKSA\n");
        sb.append(String.format("   Dokter        : %s%n", namaDokter));
        sb.append(String.format("   Spesialisasi  : %s%n", spesialisasi));
        sb.append(String.format("   No. SIP       : %s%n", sipDokter));
        sb.append("-----------------------------------------------------------------\n");
        sb.append(" HASIL PEMERIKSAAN MEDIS\n");
        sb.append(String.format("   Keluhan       : %s%n", rm.getKeluhan() != null ? rm.getKeluhan() : "-"));
        sb.append(String.format("   Diagnosa      : %s%n", rm.getDiagnosa() != null ? rm.getDiagnosa() : "-"));
        sb.append(String.format("   Tindakan      : %s%n", rm.getTindakan() != null ? rm.getTindakan() : "-"));
        sb.append("-----------------------------------------------------------------\n");
        sb.append(" RESEP OBAT & ATURAN PAKAI\n");

        if (resepList == null || resepList.isEmpty()) {
            sb.append("   (Tidak ada obat yang diresepkan)\n");
        } else {
            int no = 1;
            for (DetailResep dr : resepList) {
                String namaObat = dr.getNamaObat() != null && !dr.getNamaObat().isEmpty() 
                        ? dr.getNamaObat() 
                        : "Obat ID #" + dr.getObatId();
                String satuan = dr.getSatuan() != null && !dr.getSatuan().isEmpty() 
                        ? dr.getSatuan() 
                        : "Unit";

                sb.append(String.format("   %d. %-28s %3d %-8s | Dosis: %s%n", 
                        no++, namaObat, dr.getJumlah(), satuan, dr.getDosis()));
            }
        }

        sb.append("=================================================================\n");
        sb.append("                Semoga Lekas Sembuh & Sehat Selalu!              \n");
        sb.append("    Harap simpan struk ini sebagai bukti pemeriksaan & tebus obat\n");
        sb.append("=================================================================\n");

        return sb.toString();
    }

    /**
     * Mencetak nota dan menyimpannya ke file teks di folder receipts/
     */
    public static boolean cetakNota(Pasien pasien, Dokter dokter, RekamMedis rm, List<DetailResep> resepList) {
        String receiptContent = buildReceiptText(pasien, dokter, rm, resepList);

        try {
            File receiptDir = new File("receipts");
            if (!receiptDir.exists()) {
                receiptDir.mkdirs();
            }

            File file = new File(receiptDir, "Nota_Pemeriksaan_RM" + rm.getRekamMedisId() + ".txt");
            try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                writer.write(receiptContent);
            }

            // Juga salin ke root project untuk backward compatibility NetBeans runner
            File rootFile = new File("Nota_Pemeriksaan_RM" + rm.getRekamMedisId() + ".txt");
            try (FileWriter writer2 = new FileWriter(rootFile, StandardCharsets.UTF_8)) {
                writer2.write(receiptContent);
            }

            return true;
        } catch (IOException e) {
            System.err.println("[StrukPrinter] Gagal mencetak nota: " + e.getMessage());
            return false;
        }
    }

    /**
     * Backward-compatible overload
     */
    public static boolean cetakNota(String namaPasien, RekamMedis rm, List<DetailResep> resepList) {
        return cetakNota(null, null, rm, resepList);
    }
}
