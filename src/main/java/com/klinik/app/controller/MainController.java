package com.klinik.app.controller;

import com.klinik.app.util.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;

public class MainController {

    @FXML private TabPane mainTabPane;
    @FXML private Tab tabPasien;
    @FXML private Tab tabObat;
    @FXML private Tab tabPegawai;
    @FXML private Tab tabAntrean;
    @FXML private Tab tabRekamMedis;

    @FXML private Label lblCurrentUserName;
    @FXML private Label lblCurrentUserRole;
    @FXML private Label lblCurrentUserSub;

    @FXML
    public void initialize() {
        UserSession session = UserSession.getInstance();

        // KEAMANAN FAIL-CLOSED: Jika tidak ada session yang aktif, tolak akses dan kembali ke Login
        if (session == null) {
            Platform.runLater(() -> {
                showAlert(Alert.AlertType.ERROR, "Akses Ditolak", "Sesi login tidak valid. Silakan login terlebih dahulu.");
                handleLogout();
            });
            return;
        }

        // Tampilkan identitas pengguna yang sedang login di header
        if (lblCurrentUserName != null) {
            lblCurrentUserName.setText(session.getNamaPengguna());
        }
        if (lblCurrentUserRole != null) {
            lblCurrentUserRole.setText(session.getRoleString());
            if (session.isDokter()) {
                lblCurrentUserRole.setStyle("-fx-background-color: #0284c7; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 11px;");
            } else if (session.isAdmin()) {
                lblCurrentUserRole.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 11px;");
            }
        }
        if (lblCurrentUserSub != null) {
            lblCurrentUserSub.setText(session.getSubInfo());
        }

        // Terapkan hak akses tab berdasarkan peran (Role-Based Access Control)
        if (session.isDokter()) {
            // DOKTER: Mengelola Antrean & Pemeriksaan Pasien, serta melihat Katalog Obat
            if (tabPegawai != null) mainTabPane.getTabs().remove(tabPegawai);
            if (tabPasien != null) mainTabPane.getTabs().remove(tabPasien);

            // Buka langsung tab Antrean atau Rekam Medis untuk kenyamanan Dokter
            if (tabAntrean != null) {
                mainTabPane.getSelectionModel().select(tabAntrean);
            }

        } else if (session.isAdmin()) {
            // ADMIN: Mengelola Data Pasien, Pegawai & Dokter, Obat, serta Penjadwalan Antrean
            // Diagnosa & tindakan resep obat hanya boleh diakses oleh Dokter
            if (tabRekamMedis != null) mainTabPane.getTabs().remove(tabRekamMedis);

            if (tabAntrean != null) {
                mainTabPane.getSelectionModel().select(tabAntrean);
            }
        }
    }

    @FXML
    private void handleLogout() {
        UserSession.clearSession();

        try {
            URL resource = getClass().getResource("/fxml/LoginView.fxml");
            if (resource == null) {
                resource = getClass().getResource("/com/klinik/app/fxml/LoginView.fxml");
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Stage stage = (Stage) mainTabPane.getScene().getWindow();
            Scene scene = new Scene(root, 420, 520);

            URL cssResource = getClass().getResource("/css/style.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }

            stage.setScene(scene);
            stage.setTitle("Login - Sistem MYCLINIC");
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal kembali ke halaman login: " + e.getMessage());
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
