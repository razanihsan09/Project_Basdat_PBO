/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.klinik.app.model;

/**
 *
 * @author Acer
 */

import java.time.LocalDate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model (POJO) untuk tabel `obat`.
 * Menggunakan JavaFX Properties agar dapat langsung di-bind ke TableView atau
 * komponen UI JavaFX lainnya (TextField, Spinner, DatePicker, dll).
 */
public class Obat {

    // ===================== Fields (JavaFX Properties) =====================
    private final IntegerProperty obatId;             // PK, AUTO_INCREMENT
    private final StringProperty namaObat;              // VARCHAR(100) NOT NULL
    private final StringProperty kategori;              // VARCHAR(50)
    private final IntegerProperty stok;                 // INT NOT NULL DEFAULT 0
    private final StringProperty satuan;                // VARCHAR(20) NOT NULL
    private final ObjectProperty<LocalDate> tglExpired; // DATE NOT NULL

    // ===================== Constructor Kosong (Default) =====================
    public Obat() {
        this.obatId = new SimpleIntegerProperty(this, "obatId");
        this.namaObat = new SimpleStringProperty(this, "namaObat");
        this.kategori = new SimpleStringProperty(this, "kategori");
        this.stok = new SimpleIntegerProperty(this, "stok", 0); // default 0 sesuai DDL
        this.satuan = new SimpleStringProperty(this, "satuan");
        this.tglExpired = new SimpleObjectProperty<>(this, "tglExpired");
    }

    // ===================== Constructor Lengkap (Semua Parameter) =====================
    public Obat(int obatId, String namaObat, String kategori, int stok, String satuan, LocalDate tglExpired) {
        this.obatId = new SimpleIntegerProperty(this, "obatId", obatId);
        this.namaObat = new SimpleStringProperty(this, "namaObat", namaObat);
        this.kategori = new SimpleStringProperty(this, "kategori", kategori);
        this.stok = new SimpleIntegerProperty(this, "stok", stok);
        this.satuan = new SimpleStringProperty(this, "satuan", satuan);
        this.tglExpired = new SimpleObjectProperty<>(this, "tglExpired", tglExpired);
    }

    // ===================== Getter =====================
    public int getObatId() {
        return obatId.get();
    }

    public String getNamaObat() {
        return namaObat.get();
    }

    public String getKategori() {
        return kategori.get();
    }

    public int getStok() {
        return stok.get();
    }

    public String getSatuan() {
        return satuan.get();
    }

    public LocalDate getTglExpired() {
        return tglExpired.get();
    }

    // ===================== Setter =====================
    public void setObatId(int obatId) {
        this.obatId.set(obatId);
    }

    public void setNamaObat(String namaObat) {
        this.namaObat.set(namaObat);
    }

    public void setKategori(String kategori) {
        this.kategori.set(kategori);
    }

    public void setStok(int stok) {
        this.stok.set(stok);
    }

    public void setSatuan(String satuan) {
        this.satuan.set(satuan);
    }

    public void setTglExpired(LocalDate tglExpired) {
        this.tglExpired.set(tglExpired);
    }

    // ===================== Property Method (untuk binding TableView/UI) =====================
    public IntegerProperty obatIdProperty() {
        return obatId;
    }

    public StringProperty namaObatProperty() {
        return namaObat;
    }

    public StringProperty kategoriProperty() {
        return kategori;
    }

    public IntegerProperty stokProperty() {
        return stok;
    }

    public StringProperty satuanProperty() {
        return satuan;
    }

    public ObjectProperty<LocalDate> tglExpiredProperty() {
        return tglExpired;
    }

    @Override
    public String toString() {
        return "Obat{" +
                "obatId=" + getObatId() +
                ", namaObat='" + getNamaObat() + '\'' +
                ", stok=" + getStok() +
                ", satuan='" + getSatuan() + '\'' +
                ", tglExpired=" + getTglExpired() +
                '}';
    }
}
