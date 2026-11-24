package com.klinik.app.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Antrean {

    private final IntegerProperty antreanId = new SimpleIntegerProperty(this, "antreanId");
    private final IntegerProperty pasienId = new SimpleIntegerProperty(this, "pasienId");
    private final IntegerProperty adminId = new SimpleIntegerProperty(this, "adminId");
    private final IntegerProperty dokterId = new SimpleIntegerProperty(this, "dokterId");
    private final ObjectProperty<LocalDate> tanggal = new SimpleObjectProperty<>(this, "tanggal");
    private final IntegerProperty noAntrean = new SimpleIntegerProperty(this, "noAntrean");
    private final StringProperty tingkatUrgensi = new SimpleStringProperty(this, "tingkatUrgensi");
    private final StringProperty keluhan = new SimpleStringProperty(this, "keluhan"); // FIELD BARU
    private final StringProperty status = new SimpleStringProperty(this, "status");
    private final StringProperty namaPasien = new SimpleStringProperty(this, "namaPasien");
    private final StringProperty namaDokter = new SimpleStringProperty(this, "namaDokter");

    public Antrean() {}

    // --- Getter, Setter, Property untuk Keluhan ---
    public String getKeluhan() { return keluhan.get(); }
    public void setKeluhan(String value) { this.keluhan.set(value); }
    public StringProperty keluhanProperty() { return keluhan; }

    // --- Getter & Setter Lainya ---
    public int getAntreanId() { return antreanId.get(); }
    public void setAntreanId(int value) { this.antreanId.set(value); }
    public IntegerProperty antreanIdProperty() { return antreanId; }

    public int getPasienId() { return pasienId.get(); }
    public void setPasienId(int value) { this.pasienId.set(value); }
    public IntegerProperty pasienIdProperty() { return pasienId; }

    public int getAdminId() { return adminId.get(); }
    public void setAdminId(int value) { this.adminId.set(value); }
    public IntegerProperty adminIdProperty() { return adminId; }

    public int getDokterId() { return dokterId.get(); }
    public void setDokterId(int value) { this.dokterId.set(value); }
    public IntegerProperty dokterIdProperty() { return dokterId; }

    public LocalDate getTanggal() { return tanggal.get(); }
    public void setTanggal(LocalDate value) { this.tanggal.set(value); }
    public ObjectProperty<LocalDate> tanggalProperty() { return tanggal; }

    public int getNoAntrean() { return noAntrean.get(); }
    public void setNoAntrean(int value) { this.noAntrean.set(value); }
    public IntegerProperty noAntreanProperty() { return noAntrean; }

    public String getTingkatUrgensi() { return tingkatUrgensi.get(); }
    public void setTingkatUrgensi(String value) { this.tingkatUrgensi.set(value); }
    public StringProperty tingkatUrgensiProperty() { return tingkatUrgensi; }

    public String getStatus() { return status.get(); }
    public void setStatus(String value) { this.status.set(value); }
    public StringProperty statusProperty() { return status; }

    public String getNamaPasien() { return namaPasien.get(); }
    public void setNamaPasien(String value) { this.namaPasien.set(value); }
    public StringProperty namaPasienProperty() { return namaPasien; }

    public String getNamaDokter() { return namaDokter.get(); }
    public void setNamaDokter(String value) { this.namaDokter.set(value); }
    public StringProperty namaDokterProperty() { return namaDokter; }
}