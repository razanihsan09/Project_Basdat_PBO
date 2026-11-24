package com.klinik.app.controller;

import com.klinik.app.config.Database;
import com.klinik.app.dao.AuthDAO;
import com.klinik.app.model.AdminSystem;
import com.klinik.app.model.Dokter;
import com.klinik.app.model.Pasien;
import com.klinik.app.util.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.net.URL;

public class LoginController {

    @FXML private TextField txtUsernameOrNik;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblStatusDb;
    @FXML private Button btnLogin;

    private final AuthDAO authDAO = new AuthDAO();

    @FXML
    public void initialize() {
        // Set event listener Enter untuk login cepat
        if (txtPassword != null) {
            txtPassword.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    handleLogin();
                }
            });
        }
        if (txtUsernameOrNik != null) {
            txtUsernameOrNik.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    if (txtPassword != null) txtPassword.requestFocus();
                    else handleLogin();
                }
            });
        }

        // Cek status koneksi DB di background thread agar UI tidak freeze saat startup
        new Thread(() -> {
            String status = Database.checkConnectionStatus();
            Platform.runLater(() -> {
                if (lblStatusDb != null) {
                    if (status.startsWith("OK")) {
                        lblStatusDb.setText("● Terhubung: " + Database.getActiveDbName());
                        lblStatusDb.setStyle("-fx-text-fill: #10b981; -fx-font-size: 11px;");
                    } else {
                        lblStatusDb.setText("⚠ " + status);
                        lblStatusDb.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 11px;");
                    }
                }
            });
        }).start();
    }

    @FXML
    private void handleLogin() {
        if (txtUsernameOrNik == null || txtPassword == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Komponen UI FXML belum terhubung!");
            return;
        }

        String inputUser = txtUsernameOrNik.getText() != null ? txtUsernameOrNik.getText().trim() : "";
        String password = txtPassword.getText() != null ? txtPassword.getText().trim() : "";

        if (inputUser.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Username dan password wajib diisi!");
            return;
        }

        if (btnLogin != null) btnLogin.setDisable(true);

        new Thread(() -> {
            Object akun = authDAO.login(inputUser, password);
            String errorMsg = authDAO.getLastErrorMessage();

            Platform.runLater(() -> {
                if (btnLogin != null) btnLogin.setDisable(false);

                if (akun instanceof AdminSystem) {
                    AdminSystem admin = (AdminSystem) akun;
                    UserSession.setSession(admin, "ADMIN");
                    bukaHalamanUtama("/fxml/MainView.fxml", "MYCLINIC - Portal Administrator (" + admin.getNamaLengkap() + ")");

                } else if (akun instanceof Dokter) {
                    Dokter dokter = (Dokter) akun;
                    UserSession.setSession(dokter, "DOKTER");
                    bukaHalamanUtama("/fxml/MainView.fxml", "MYCLINIC - Portal Dokter (" + dokter.getNamaLengkap() + ")");

                } else if (akun instanceof Pasien) {
                    Pasien p = (Pasien) akun;
                    showAlert(Alert.AlertType.INFORMATION, "Informasi Pasien",
                            "Halo " + p.getNamaPasien() + "!\n\n" +
                            "Portal ini dikhususkan untuk Staf Medis & Admin.\n" +
                            "Silakan gunakan menu 'Anjungan Pendaftaran Mandiri' di bawah untuk mengambil nomor antrean berobat.");
                    txtPassword.clear();

                } else {
                    showAlert(Alert.AlertType.ERROR, "Login Gagal", errorMsg != null && !errorMsg.isEmpty() ? errorMsg : "Username atau password salah!");
                }
            });
        }).start();
    }

    private void bukaHalamanUtama(String pathFxml, String title) {
        try {
            URL resource = getClass().getResource(pathFxml);
            if (resource == null) {
                resource = getClass().getResource("/com/klinik/app/fxml/MainView.fxml");
            }

            if (resource == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "File " + pathFxml + " tidak ditemukan!");
                return;
            }

            Parent root = FXMLLoader.load(resource);
            Stage stage = (Stage) txtUsernameOrNik.getScene().getWindow();
            Scene scene = new Scene(root, 1100, 720);

            // Pasang stylesheet modern jika ada
            URL cssResource = getClass().getResource("/css/style.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }

            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman utama: " + e.getMessage());
        }
    }

    @FXML
    private void handleKeHalamanDaftarPasien() {
        try {
            URL resource = getClass().getResource("/fxml/RegisterPasienView.fxml");
            if (resource == null) {
                resource = getClass().getResource("/com/klinik/app/fxml/RegisterPasienView.fxml");
            }

            if (resource == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "File RegisterPasienView.fxml tidak ditemukan!");
                return;
            }

            Parent root = FXMLLoader.load(resource);
            Stage stage = (Stage) txtUsernameOrNik.getScene().getWindow();
            Scene scene = new Scene(root, 650, 750);

            URL cssResource = getClass().getResource("/css/style.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }

            stage.setScene(scene);
            stage.setTitle("Anjungan Pendaftaran & Ambil Antrean Mandiri Pasien");
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman pendaftaran: " + e.getMessage());
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
