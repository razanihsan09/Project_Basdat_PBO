package com.klinik.app.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Dokter extends Pegawai {

    private final StringProperty username;
    private final StringProperty passwordHash;
    private final StringProperty noSip;
    private final StringProperty spesialisasi;

    public Dokter() {
        super();
        setJenisPegawai(JenisPegawai.DOKTER);
        this.username = new SimpleStringProperty(this, "username");
        this.passwordHash = new SimpleStringProperty(this, "passwordHash");
        this.noSip = new SimpleStringProperty(this, "noSip");
        this.spesialisasi = new SimpleStringProperty(this, "spesialisasi");
    }

    public Dokter(int pegawaiId, String username, String passwordHash, String noSip, String spesialisasi) {
        this();
        setPegawaiId(pegawaiId);
        setUsername(username);
        setPasswordHash(passwordHash);
        setNoSip(noSip);
        setSpesialisasi(spesialisasi);
    }

    public Dokter(int pegawaiId, String namaLengkap, String noHp, String username, String passwordHash, String noSip, String spesialisasi) {
        super(pegawaiId, namaLengkap, noHp, JenisPegawai.DOKTER);
        this.username = new SimpleStringProperty(this, "username", username);
        this.passwordHash = new SimpleStringProperty(this, "passwordHash", passwordHash);
        this.noSip = new SimpleStringProperty(this, "noSip", noSip);
        this.spesialisasi = new SimpleStringProperty(this, "spesialisasi", spesialisasi);
    }

    // --- Getter, Setter, & Property Methods khusus Dokter ---
    public String getUsername() { return username.get(); }
    public void setUsername(String username) { this.username.set(username); }
    public StringProperty usernameProperty() { return username; }

    public String getPasswordHash() { return passwordHash.get(); }
    public void setPasswordHash(String passwordHash) { this.passwordHash.set(passwordHash); }
    public StringProperty passwordHashProperty() { return passwordHash; }

    public String getNoSip() { return noSip.get(); }
    public void setNoSip(String noSip) { this.noSip.set(noSip); }
    public StringProperty noSipProperty() { return noSip; }

    public String getSpesialisasi() { return spesialisasi.get(); }
    public void setSpesialisasi(String spesialisasi) { this.spesialisasi.set(spesialisasi); }
    public StringProperty spesialisasiProperty() { return spesialisasi; }
}