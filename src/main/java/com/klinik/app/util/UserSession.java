package com.klinik.app.util;

import com.klinik.app.model.AdminSystem;
import com.klinik.app.model.Dokter;
import com.klinik.app.model.JenisPegawai;
import com.klinik.app.model.Pasien;

public class UserSession {
    private static UserSession instance;
    private final Object currentUser;
    private final String role;

    private UserSession(Object currentUser, String role) {
        this.currentUser = currentUser;
        this.role = role != null ? role.toUpperCase() : "";
    }

    public static synchronized void setSession(Object currentUser, String role) {
        instance = new UserSession(currentUser, role);
    }

    public static synchronized UserSession getInstance() {
        return instance;
    }

    public static synchronized void clearSession() {
        instance = null;
    }

    public Object getCurrentUser() {
        return currentUser;
    }

    public String getRoleString() {
        return role;
    }

    public JenisPegawai getRole() {
        if ("ADMIN".equalsIgnoreCase(role)) return JenisPegawai.ADMIN;
        if ("DOKTER".equalsIgnoreCase(role)) return JenisPegawai.DOKTER;
        return null;
    }

    public Dokter getDokter() {
        return (currentUser instanceof Dokter) ? (Dokter) currentUser : null;
    }

    public AdminSystem getAdmin() {
        return (currentUser instanceof AdminSystem) ? (AdminSystem) currentUser : null;
    }

    public Pasien getPasien() {
        return (currentUser instanceof Pasien) ? (Pasien) currentUser : null;
    }

    public Integer getDokterPegawaiId() {
        Dokter d = getDokter();
        return d != null ? d.getPegawaiId() : null;
    }

    public String getNamaPengguna() {
        if (currentUser instanceof Dokter) {
            Dokter d = (Dokter) currentUser;
            return d.getNamaLengkap() != null && !d.getNamaLengkap().isEmpty() ? d.getNamaLengkap() : d.getUsername();
        } else if (currentUser instanceof AdminSystem) {
            AdminSystem a = (AdminSystem) currentUser;
            return a.getNamaLengkap() != null && !a.getNamaLengkap().isEmpty() ? a.getNamaLengkap() : a.getUsername();
        } else if (currentUser instanceof Pasien) {
            Pasien p = (Pasien) currentUser;
            return p.getNamaPasien();
        }
        return "Pengguna";
    }

    public String getSubInfo() {
        if (currentUser instanceof Dokter) {
            Dokter d = (Dokter) currentUser;
            String sp = d.getSpesialisasi() != null ? d.getSpesialisasi() : "Dokter";
            String sip = d.getNoSip() != null ? " • " + d.getNoSip() : "";
            return sp + sip;
        } else if (currentUser instanceof AdminSystem) {
            return "Administrator Sistem Klinik";
        } else if (currentUser instanceof Pasien) {
            Pasien p = (Pasien) currentUser;
            return "NIK: " + p.getNik();
        }
        return "";
    }

    public boolean isPasien() {
        return "PASIEN".equalsIgnoreCase(role);
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public boolean isDokter() {
        return "DOKTER".equalsIgnoreCase(role);
    }
}
