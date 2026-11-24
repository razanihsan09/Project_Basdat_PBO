package com.klinik.app.model;

import java.time.LocalDateTime;

public class RekamMedis {
    private int rekamMedisId;
    private int pasienId;
    private Integer dokterId; 
    private Integer antreanId;
    private LocalDateTime tanggalPeriksa;
    private String keluhan;
    private String diagnosa;
    private String tindakan;

    // Atribut tambahan untuk menampilkan data di TableView UI
    private String namaPasien;
    private String namaDokter;

    public RekamMedis() {}

    public RekamMedis(int rekamMedisId, int pasienId, Integer dokterId, Integer antreanId, 
                      LocalDateTime tanggalPeriksa, String keluhan, String diagnosa, String tindakan) {
        this.rekamMedisId = rekamMedisId;
        this.pasienId = pasienId;
        this.dokterId = dokterId;
        this.antreanId = antreanId;
        this.tanggalPeriksa = tanggalPeriksa;
        this.keluhan = keluhan;
        this.diagnosa = diagnosa;
        this.tindakan = tindakan;
    }

    public int getRekamMedisId() { return rekamMedisId; }
    public void setRekamMedisId(int rekamMedisId) { this.rekamMedisId = rekamMedisId; }

    public int getPasienId() { return pasienId; }
    public void setPasienId(int pasienId) { this.pasienId = pasienId; }

    public Integer getDokterId() { return dokterId; }
    public void setDokterId(Integer dokterId) { this.dokterId = dokterId; }

    public Integer getAntreanId() { return antreanId; }
    public void setAntreanId(Integer antreanId) { this.antreanId = antreanId; }

    public LocalDateTime getTanggalPeriksa() { return tanggalPeriksa; }
    public void setTanggalPeriksa(LocalDateTime tanggalPeriksa) { this.tanggalPeriksa = tanggalPeriksa; }

    public String getKeluhan() { return keluhan; }
    public void setKeluhan(String keluhan) { this.keluhan = keluhan; }

    public String getDiagnosa() { return diagnosa; }
    public void setDiagnosa(String diagnosa) { this.diagnosa = diagnosa; }

    public String getTindakan() { return tindakan; }
    public void setTindakan(String tindakan) { this.tindakan = tindakan; }

    public String getNamaPasien() { return namaPasien; }
    public void setNamaPasien(String namaPasien) { this.namaPasien = namaPasien; }

    public String getNamaDokter() { return namaDokter; }
    public void setNamaDokter(String namaDokter) { this.namaDokter = namaDokter; }
}