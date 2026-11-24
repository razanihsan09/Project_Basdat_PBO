package com.klinik.app.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DetailResep {

    private final IntegerProperty detailResepId;
    private final IntegerProperty rekamMedisId;
    private final IntegerProperty obatId;
    private final IntegerProperty jumlah;
    private final StringProperty dosis;

    // Properti bantuan untuk tampilan UI & cetak struk
    private final StringProperty namaObat;
    private final StringProperty satuan;

    public DetailResep() {
        this.detailResepId = new SimpleIntegerProperty(this, "detailResepId", 0);
        this.rekamMedisId = new SimpleIntegerProperty(this, "rekamMedisId", 0);
        this.obatId = new SimpleIntegerProperty(this, "obatId", 0);
        this.jumlah = new SimpleIntegerProperty(this, "jumlah", 0);
        this.dosis = new SimpleStringProperty(this, "dosis", "");
        this.namaObat = new SimpleStringProperty(this, "namaObat", "");
        this.satuan = new SimpleStringProperty(this, "satuan", "");
    }

    public DetailResep(int detailResepId, int rekamMedisId, int obatId, int jumlah, String dosis) {
        this();
        setDetailResepId(detailResepId);
        setRekamMedisId(rekamMedisId);
        setObatId(obatId);
        setJumlah(jumlah);
        setDosis(dosis);
    }

    public DetailResep(int obatId, String namaObat, int jumlah, String satuan, String dosis) {
        this();
        setObatId(obatId);
        setNamaObat(namaObat);
        setJumlah(jumlah);
        setSatuan(satuan);
        setDosis(dosis);
    }

    public int getDetailResepId() { return detailResepId.get(); }
    public void setDetailResepId(int val) { this.detailResepId.set(val); }
    public IntegerProperty detailResepIdProperty() { return detailResepId; }

    public int getRekamMedisId() { return rekamMedisId.get(); }
    public void setRekamMedisId(int val) { this.rekamMedisId.set(val); }
    public IntegerProperty rekamMedisIdProperty() { return rekamMedisId; }

    public int getObatId() { return obatId.get(); }
    public void setObatId(int val) { this.obatId.set(val); }
    public IntegerProperty obatIdProperty() { return obatId; }

    public int getJumlah() { return jumlah.get(); }
    public void setJumlah(int val) { this.jumlah.set(val); }
    public IntegerProperty jumlahProperty() { return jumlah; }

    public String getDosis() { return dosis.get(); }
    public void setDosis(String val) { this.dosis.set(val); }
    public StringProperty dosisProperty() { return dosis; }

    public String getNamaObat() { return namaObat.get(); }
    public void setNamaObat(String val) { this.namaObat.set(val); }
    public StringProperty namaObatProperty() { return namaObat; }

    public String getSatuan() { return satuan.get(); }
    public void setSatuan(String val) { this.satuan.set(val); }
    public StringProperty satuanProperty() { return satuan; }

    @Override
    public String toString() {
        return (getNamaObat().isEmpty() ? "Obat #" + getObatId() : getNamaObat()) +
                " (" + getJumlah() + " " + getSatuan() + ") - " + getDosis();
    }
}
