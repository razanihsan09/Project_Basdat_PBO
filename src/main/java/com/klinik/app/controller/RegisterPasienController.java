package com.klinik.app.controller;

import com.klinik.app.dao.AntreanDAO;
import com.klinik.app.dao.PasienDAO;
import com.klinik.app.model.JenisKelamin;
import com.klinik.app.model.Pasien;
import com.klinik.app.util.PasswordUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;

public class RegisterPasienController {

    @FXML private TextField txtNik;
    @FXML private TextField txtNamaPasien;
    @FXML private DatePicker dpTanggalLahir;
    @FXML private ComboBox<JenisKelamin> cbJenisKelamin;
    @FXML private TextField txtNoHp;
    @FXML private TextArea txtAlamat;
    @FXML private TextField txtAlergiObat;
    @FXML private TextArea txtKeluhan;
    @FXML private Button btnDaftar;

    private final PasienDAO pasienDAO = new PasienDAO();
    private final AntreanDAO antreanDAO = new AntreanDAO();

    @FXML
    public void initialize() {
        if (cbJenisKelamin != null) {
            cbJenisKelamin.setItems(FXCollections.observableArrayList(JenisKelamin.values()));
        }

        // Auto-check jika NIK sudah terdaftar saat user selesai mengetik NIK
        if (txtNik != null) {
            txtNik.focusedProperty().addListener((obs, oldF, newF) -> {
                if (!newF) { // Saat focus hilang (blur)
                    String nik = txtNik.getText().trim();
                    if (nik.matches("\\d{16}")) {
                        new Thread(() -> {
                            Pasien p = pasienDAO.getPasienByNik(nik);
                            if (p != null) {
                                Platform.runLater(() -> {
                                    if (txtNamaPasien != null) txtNamaPasien.setText(p.getNamaPasien());
                                    if (dpTanggalLahir != null) dpTanggalLahir.setValue(p.getTanggalLahir());
                                    if (cbJenisKelamin != null) cbJenisKelamin.setValue(p.getJenisKelamin());
                                    if (txtNoHp != null) txtNoHp.setText(p.getNoHp());
                                    if (txtAlamat != null) txtAlamat.setText(p.getAlamat());
                                    if (txtAlergiObat != null) txtAlergiObat.setText(p.getAlergiObat());
                                });
                            }
                        }).start();
                    }
                }
            });
        }
    }

    @FXML
    private void handleDaftar() {
        if (txtNik == null || txtNamaPasien == null || txtKeluhan == null) {
            showAlert(Alert.AlertType.ERROR, "Error UI", "Komponen formulir belum terhubung!");
            return;
        }

        String nik = txtNik.getText().trim();
        String nama = txtNamaPasien.getText().trim();
        String keluhan = txtKeluhan.getText().trim();

        if (nik.isEmpty() || nama.isEmpty() || keluhan.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gagal", "NIK, Nama Lengkap, dan Keluhan Utama wajib diisi!");
            return;
        }

        if (!nik.matches("\\d{16}")) {
            showAlert(Alert.AlertType.WARNING, "Format NIK Salah", "NIK harus terdiri dari 16 digit angka sesuai KTP!");
            return;
        }

        if (btnDaftar != null) btnDaftar.setDisable(true);

        new Thread(() -> {
            try {
                // 1. Simpan atau Ambil Profil Pasien
                Pasien pasien = pasienDAO.getPasienByNik(nik);
                if (pasien == null) {
                    pasien = new Pasien();
                    pasien.setNik(nik);
                    pasien.setPasswordHash(PasswordUtil.hashPassword("123456"));
                    pasien.setNamaPasien(nama);
                    pasien.setTanggalLahir(dpTanggalLahir != null ? dpTanggalLahir.getValue() : null);
                    pasien.setJenisKelamin(cbJenisKelamin != null ? cbJenisKelamin.getValue() : null);
                    pasien.setNoHp(txtNoHp != null ? txtNoHp.getText().trim() : "");
                    pasien.setAlamat(txtAlamat != null ? txtAlamat.getText().trim() : "");
                    pasien.setAlergiObat(txtAlergiObat != null ? txtAlergiObat.getText().trim() : "-");

                    if (!pasienDAO.insertPasien(pasien)) {
                        Platform.runLater(() -> {
                            if (btnDaftar != null) btnDaftar.setDisable(false);
                            showAlert(Alert.AlertType.ERROR, "Error Database", "Gagal mendaftarkan data pasien baru.");
                        });
                        return;
                    }
                    pasien = pasienDAO.getPasienByNik(nik);
                }

                if (pasien == null) {
                    Platform.runLater(() -> {
                        if (btnDaftar != null) btnDaftar.setDisable(false);
                        showAlert(Alert.AlertType.ERROR, "Error", "Data pasien tidak dapat diproses.");
                    });
                    return;
                }

                // 2. Cek apakah pasien sudah memiliki antrean aktif hari ini
                if (antreanDAO.isPasienSudahAntreHariIni(pasien.getPasienId())) {
                    final String namaPasien = pasien.getNamaPasien();
                    Platform.runLater(() -> {
                        if (btnDaftar != null) btnDaftar.setDisable(false);
                        showAlert(Alert.AlertType.WARNING, "Pendaftaran Ditolak", 
                                "Pasien " + namaPasien + " sudah terdaftar dalam antrean hari ini!\n" +
                                "Silakan menunggu panggilan petugas/dokter di ruang tunggu.");
                    });
                    return;
                }

                // 3. Simpan Pendaftaran Pendahuluan (Status 'PENDING')
                if (antreanDAO.insertAntreanPending(pasien.getPasienId(), keluhan)) {
                    final String namaP = nama;
                    Platform.runLater(() -> {
                        if (btnDaftar != null) btnDaftar.setDisable(false);
                        showAlert(Alert.AlertType.INFORMATION, "Pendaftaran Berhasil!", 
                                "Terima kasih, pendaftaran mandiri berhasil!\n\n" +
                                "Nama: " + namaP + "\n" +
                                "Status: Menunggu Konfirmasi Dokter oleh Petugas Admin\n\n" +
                                "Silakan melapor ke meja pendaftaran/administrasi untuk konfirmasi dokter dan nomor antrean.");
                        handleKembaliKeLogin();
                    });
                } else {
                    Platform.runLater(() -> {
                        if (btnDaftar != null) btnDaftar.setDisable(false);
                        showAlert(Alert.AlertType.ERROR, "Error", "Gagal menyimpan data pendaftaran ke antrean.");
                    });
                }

            } catch (SQLException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    if (btnDaftar != null) btnDaftar.setDisable(false);
                    showAlert(Alert.AlertType.ERROR, "Error Database", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void handleKembaliKeLogin() {
        try {
            URL resource = getClass().getResource("/fxml/LoginView.fxml");
            if (resource == null) {
                resource = getClass().getResource("/com/klinik/app/fxml/LoginView.fxml");
            }
            if (resource != null) {
                Parent root = FXMLLoader.load(resource);
                Stage stage = (Stage) txtNik.getScene().getWindow();
                Scene scene = new Scene(root, 420, 520);

                URL cssResource = getClass().getResource("/css/style.css");
                if (cssResource != null) {
                    scene.getStylesheets().add(cssResource.toExternalForm());
                }

                stage.setScene(scene);
                stage.setTitle("Login - Sistem MYCLINIC");
                stage.centerOnScreen();
                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
