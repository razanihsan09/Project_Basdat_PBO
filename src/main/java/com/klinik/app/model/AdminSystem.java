package com.klinik.app.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AdminSystem {

    private final IntegerProperty pegawaiId;
    private final StringProperty username;
    private final StringProperty passwordHash;
    private final StringProperty namaLengkap;
    private final StringProperty noHp;

    public AdminSystem() {
        this.pegawaiId = new SimpleIntegerProperty(this, "pegawaiId", 0);
        this.username = new SimpleStringProperty(this, "username", "");
        this.passwordHash = new SimpleStringProperty(this, "passwordHash", "");
        this.namaLengkap = new SimpleStringProperty(this, "namaLengkap", "Administrator");
        this.noHp = new SimpleStringProperty(this, "noHp", "");
    }

    public AdminSystem(int pegawaiId, String username, String passwordHash) {
        this();
        setPegawaiId(pegawaiId);
        setUsername(username);
        setPasswordHash(passwordHash);
    }

    public int getPegawaiId() { return pegawaiId.get(); }
    public void setPegawaiId(int val) { this.pegawaiId.set(val); }
    public IntegerProperty pegawaiIdProperty() { return pegawaiId; }

    public String getUsername() { return username.get(); }
    public void setUsername(String val) { this.username.set(val); }
    public StringProperty usernameProperty() { return username; }

    public String getPasswordHash() { return passwordHash.get(); }
    public void setPasswordHash(String val) { this.passwordHash.set(val); }
    public StringProperty passwordHashProperty() { return passwordHash; }

    public String getNamaLengkap() { return namaLengkap.get(); }
    public void setNamaLengkap(String val) { this.namaLengkap.set(val); }
    public StringProperty namaLengkapProperty() { return namaLengkap; }

    public String getNoHp() { return noHp.get(); }
    public void setNoHp(String val) { this.noHp.set(val); }
    public StringProperty noHpProperty() { return noHp; }

    @Override
    public String toString() {
        return getNamaLengkap() + " (" + getUsername() + ")";
    }
}
