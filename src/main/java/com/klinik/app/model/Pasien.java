package com.klinik.app.model;

import java.time.LocalDate;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Pasien {

    private final IntegerProperty pasienId;
    private final StringProperty nik;
    private final StringProperty passwordHash;
    private final StringProperty namaPasien;
    private final ObjectProperty<LocalDate> tanggalLahir;
    private final ObjectProperty<JenisKelamin> jenisKelamin;
    private final StringProperty alamat;
    private final StringProperty noHp;
    private final StringProperty alergiObat;
    
    // Properti baru untuk integrasi antrean hari ini
    private final StringProperty dokterTujuan;
    private final StringProperty keluhanUtama;

    public Pasien() {
        this.pasienId = new SimpleIntegerProperty(this, "pasienId");
        this.nik = new SimpleStringProperty(this, "nik");
        this.passwordHash = new SimpleStringProperty(this, "passwordHash");
        this.namaPasien = new SimpleStringProperty(this, "namaPasien");
        this.tanggalLahir = new SimpleObjectProperty<>(this, "tanggalLahir");
        this.jenisKelamin = new SimpleObjectProperty<>(this, "jenisKelamin");
        this.alamat = new SimpleStringProperty(this, "alamat");
        this.noHp = new SimpleStringProperty(this, "noHp");
        this.alergiObat = new SimpleStringProperty(this, "alergiObat");
        this.dokterTujuan = new SimpleStringProperty(this, "dokterTujuan");
        this.keluhanUtama = new SimpleStringProperty(this, "keluhanUtama");
    }

    public Pasien(int pasienId, String nik, String passwordHash, String namaPasien, LocalDate tanggalLahir,
                  JenisKelamin jenisKelamin, String alamat, String noHp, String alergiObat) {
        this();
        setPasienId(pasienId);
        setNik(nik);
        setPasswordHash(passwordHash);
        setNamaPasien(namaPasien);
        setTanggalLahir(tanggalLahir);
        setJenisKelamin(jenisKelamin);
        setAlamat(alamat);
        setNoHp(noHp);
        setAlergiObat(alergiObat);
    }

    public int getPasienId() { return pasienId.get(); }
    public void setPasienId(int pasienId) { this.pasienId.set(pasienId); }
    public IntegerProperty pasienIdProperty() { return pasienId; }

    public String getNik() { return nik.get(); }
    public void setNik(String nik) { this.nik.set(nik); }
    public StringProperty nikProperty() { return nik; }

    public String getPasswordHash() { return passwordHash.get(); }
    public void setPasswordHash(String passwordHash) { this.passwordHash.set(passwordHash); }
    public StringProperty passwordHashProperty() { return passwordHash; }

    public String getNamaPasien() { return namaPasien.get(); }
    public void setNamaPasien(String namaPasien) { this.namaPasien.set(namaPasien); }
    public StringProperty namaPasienProperty() { return namaPasien; }

    public LocalDate getTanggalLahir() { return tanggalLahir.get(); }
    public void setTanggalLahir(LocalDate tanggalLahir) { this.tanggalLahir.set(tanggalLahir); }
    public ObjectProperty<LocalDate> tanggalLahirProperty() { return tanggalLahir; }

    public JenisKelamin getJenisKelamin() { return jenisKelamin.get(); }
    public void setJenisKelamin(JenisKelamin jenisKelamin) { this.jenisKelamin.set(jenisKelamin); }
    public ObjectProperty<JenisKelamin> jenisKelaminProperty() { return jenisKelamin; }

    public String getAlamat() { return alamat.get(); }
    public void setAlamat(String alamat) { this.alamat.set(alamat); }
    public StringProperty alamatProperty() { return alamat; }

    public String getNoHp() { return noHp.get(); }
    public void setNoHp(String noHp) { this.noHp.set(noHp); }
    public StringProperty noHpProperty() { return noHp; }

    public String getAlergiObat() { return alergiObat.get(); }
    public void setAlergiObat(String alergiObat) { this.alergiObat.set(alergiObat); }
    public StringProperty alergiObatProperty() { return alergiObat; }

    public String getDokterTujuan() { return dokterTujuan.get(); }
    public void setDokterTujuan(String dokterTujuan) { this.dokterTujuan.set(dokterTujuan); }
    public StringProperty dokterTujuanProperty() { return dokterTujuan; }

    public String getKeluhanUtama() { return keluhanUtama.get(); }
    public void setKeluhanUtama(String keluhanUtama) { this.keluhanUtama.set(keluhanUtama); }
    public StringProperty keluhanUtamaProperty() { return keluhanUtama; }
}