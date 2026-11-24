/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.klinik.app.model;

/**
 *
 * @author Acer
 */

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model (POJO) untuk tabel `pegawai`.
 * <p>
 * Tabel ini merupakan tabel induk (parent) untuk relasi 1-to-1 dengan
 * tabel `admin_system` dan `dokter` (pola: Single Table Inheritance / Class Table Inheritance).
 * Menggunakan JavaFX Properties agar dapat langsung di-bind ke TableView atau
 * komponen UI JavaFX lainnya (TextField, ComboBox, dll).
 * </p>
 */
public class Pegawai {

    // ===================== Fields (JavaFX Properties) =====================
    private final IntegerProperty pegawaiId;          // PK, AUTO_INCREMENT
    private final StringProperty namaLengkap;          // VARCHAR(100) NOT NULL
    private final StringProperty noHp;                 // VARCHAR(15)
    private final ObjectProperty<JenisPegawai> jenisPegawai; // ENUM('ADMIN','DOKTER') NOT NULL

    // ===================== Constructor Kosong (Default) =====================
    public Pegawai() {
        this.pegawaiId = new SimpleIntegerProperty(this, "pegawaiId");
        this.namaLengkap = new SimpleStringProperty(this, "namaLengkap");
        this.noHp = new SimpleStringProperty(this, "noHp");
        this.jenisPegawai = new SimpleObjectProperty<>(this, "jenisPegawai");
    }

    // ===================== Constructor Lengkap (Semua Parameter) =====================
    public Pegawai(int pegawaiId, String namaLengkap, String noHp, JenisPegawai jenisPegawai) {
        this.pegawaiId = new SimpleIntegerProperty(this, "pegawaiId", pegawaiId);
        this.namaLengkap = new SimpleStringProperty(this, "namaLengkap", namaLengkap);
        this.noHp = new SimpleStringProperty(this, "noHp", noHp);
        this.jenisPegawai = new SimpleObjectProperty<>(this, "jenisPegawai", jenisPegawai);
    }

    // ===================== Getter =====================
    public int getPegawaiId() {
        return pegawaiId.get();
    }

    public String getNamaLengkap() {
        return namaLengkap.get();
    }

    public String getNoHp() {
        return noHp.get();
    }

    public JenisPegawai getJenisPegawai() {
        return jenisPegawai.get();
    }

    // ===================== Setter =====================
    public void setPegawaiId(int pegawaiId) {
        this.pegawaiId.set(pegawaiId);
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap.set(namaLengkap);
    }

    public void setNoHp(String noHp) {
        this.noHp.set(noHp);
    }

    public void setJenisPegawai(JenisPegawai jenisPegawai) {
        this.jenisPegawai.set(jenisPegawai);
    }

    // ===================== Property Method (untuk binding TableView/UI) =====================
    public IntegerProperty pegawaiIdProperty() {
        return pegawaiId;
    }

    public StringProperty namaLengkapProperty() {
        return namaLengkap;
    }

    public StringProperty noHpProperty() {
        return noHp;
    }

    public ObjectProperty<JenisPegawai> jenisPegawaiProperty() {
        return jenisPegawai;
    }

    @Override
    public String toString() {
        return "Pegawai{" +
                "pegawaiId=" + getPegawaiId() +
                ", namaLengkap='" + getNamaLengkap() + '\'' +
                ", noHp='" + getNoHp() + '\'' +
                ", jenisPegawai=" + getJenisPegawai() +
                '}';
    }
}

