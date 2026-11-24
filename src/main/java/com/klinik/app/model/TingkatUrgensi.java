package com.klinik.app.model;

public enum TingkatUrgensi {
    MERAH(1),    // Prioritas 1 (Gawat Darurat)
    KUNING(2),   // Prioritas 2 (Mendesak)
    HIJAU(3);    // Prioritas 3 (Biasa/Rutin)

    private final int nilaiPrioritas;

    TingkatUrgensi(int nilaiPrioritas) {
        this.nilaiPrioritas = nilaiPrioritas;
    }

    public int getNilaiPrioritas() {
        return nilaiPrioritas;
    }

    public static TingkatUrgensi fromString(String text) {
        if (text == null) return HIJAU;
        try {
            return TingkatUrgensi.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return HIJAU;
        }
    }
}