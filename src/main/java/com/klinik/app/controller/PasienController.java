package com.klinik.app.controller;

import com.klinik.app.dao.PasienDAO;
import com.klinik.app.model.JenisKelamin;
import com.klinik.app.model.Pasien;
import com.klinik.app.util.PasswordUtil;
import com.klinik.app.util.UserSession;
import javafx.application.Platform;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.List;

public class PasienController {

    @FXML private TextField txtSearch; 
    @FXML private TextField txtNik;
    @FXML private TextField txtNamaPasien;
    @FXML private DatePicker dpTanggalLahir;
    @FXML private ComboBox<JenisKelamin> cbJenisKelamin;
    @FXML private TextField txtNoHp;
    @FXML private TextArea txtAlamat;
    @FXML private TextField txtAlergiObat;

    @FXML private Button btnSimpan;
    @FXML private Button btnUpdate;
    @FXML private Button btnHapus;
    @FXML private Label lblTotalPasien;

    @FXML private TableView<Pasien> tvPasien;
    @FXML private TableColumn<Pasien, Integer> colPasienId;
    @FXML private TableColumn<Pasien, String> colNik;
    @FXML private TableColumn<Pasien, String> colNamaPasien;
    @FXML private TableColumn<Pasien, String> colKeluhanUtama;
    @FXML private TableColumn<Pasien, Object> colTanggalLahir;
    @FXML private TableColumn<Pasien, JenisKelamin> colJenisKelamin;
    @FXML private TableColumn<Pasien, String> colNoHp;
    @FXML private TableColumn<Pasien, String> colAlamat;
    @FXML private TableColumn<Pasien, String> colAlergiObat;
    @FXML private Button btnHalamanSebelumnya;
    @FXML private Button btnHalamanBerikutnya;
    @FXML private Label lblInfoHalaman;

    private final PasienDAO pasienDAO = new PasienDAO();
    private final ObservableList<Pasien> masterData = FXCollections.observableArrayList();
    private static final int PAGE_SIZE = 20;
    private int currentPage = 0;
    private int totalPasien = 0;
    private long dataLoadRequestId = 0;
    private PauseTransition searchDelay;
    private Pasien selectedPasien;

    @FXML
    public void initialize() {
        if (cbJenisKelamin != null) {
            cbJenisKelamin.setItems(FXCollections.observableArrayList(JenisKelamin.values()));
        }

        if (colPasienId != null) colPasienId.setCellValueFactory(new PropertyValueFactory<>("pasienId"));
        if (colNik != null) colNik.setCellValueFactory(new PropertyValueFactory<>("nik"));
        if (colNamaPasien != null) colNamaPasien.setCellValueFactory(new PropertyValueFactory<>("namaPasien"));
        if (colKeluhanUtama != null) colKeluhanUtama.setCellValueFactory(new PropertyValueFactory<>("keluhanUtama"));
        if (colTanggalLahir != null) colTanggalLahir.setCellValueFactory(new PropertyValueFactory<>("tanggalLahir"));
        if (colJenisKelamin != null) colJenisKelamin.setCellValueFactory(new PropertyValueFactory<>("jenisKelamin"));
        if (colNoHp != null) colNoHp.setCellValueFactory(new PropertyValueFactory<>("noHp"));
        if (colAlamat != null) colAlamat.setCellValueFactory(new PropertyValueFactory<>("alamat"));
        if (colAlergiObat != null) colAlergiObat.setCellValueFactory(new PropertyValueFactory<>("alergiObat"));

        // FAIL-CLOSED SECURITY: Hanya Admin yang dapat memanipulasi data master pasien
        UserSession session = UserSession.getInstance();
        if (session == null || !session.isAdmin()) {
            disableFormInputs();
        }

        setupPencarian();
        loadTableData();

        if (tvPasien != null) {
            tvPasien.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedPasien = newSelection;
                    populateForm(selectedPasien);
                }
            });
        }
    }

    private void setupPencarian() {
        if (tvPasien != null) tvPasien.setItems(masterData);
        searchDelay = new PauseTransition(Duration.millis(300));
        searchDelay.setOnFinished(event -> {
            currentPage = 0;
            loadTableData();
        });

        if (txtSearch != null) {
            txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
                searchDelay.stop();
                searchDelay.playFromStart();
            });
        }
    }

    private void loadTableData() {
        String keyword = txtSearch != null ? txtSearch.getText().trim() : "";
        int requestedPage = currentPage;
        long requestId = ++dataLoadRequestId;
        new Thread(() -> {
            int total = pasienDAO.countPasien(keyword);
            int totalPages = Math.max(1, (int) Math.ceil(total / (double) PAGE_SIZE));
            int page = Math.max(0, Math.min(requestedPage, totalPages - 1));
            List<Pasien> data = pasienDAO.getPasienPage(keyword, PAGE_SIZE, page * PAGE_SIZE);

            final int resolvedPage = page;
            Platform.runLater(() -> {
                if (requestId != dataLoadRequestId) return;
                currentPage = resolvedPage;
                totalPasien = total;
                masterData.setAll(data);
                updatePaginationControls();
                updateTotalLabel();
            });
        }).start();
    }

    private void updateTotalLabel() {
        if (lblTotalPasien != null) {
            String formatted = NumberFormat.getIntegerInstance(new Locale("id", "ID")).format(totalPasien);
            String keyword = txtSearch != null ? txtSearch.getText().trim() : "";
            lblTotalPasien.setText("Total Pasien: " + formatted + (keyword.isEmpty() ? "" : " hasil"));
        }
    }

    private void updatePaginationControls() {
        int totalPages = (int) Math.ceil(totalPasien / (double) PAGE_SIZE);
        if (lblInfoHalaman != null) {
            lblInfoHalaman.setText(totalPasien == 0 ? "Tidak ada data" :
                    "Halaman " + (currentPage + 1) + " dari " + totalPages + " • " +
                    masterData.size() + " dari " + NumberFormat.getIntegerInstance(new Locale("id", "ID")).format(totalPasien) + " data");
        }
        if (btnHalamanSebelumnya != null) btnHalamanSebelumnya.setDisable(currentPage <= 0 || totalPasien == 0);
        if (btnHalamanBerikutnya != null) btnHalamanBerikutnya.setDisable(totalPasien == 0 || currentPage >= totalPages - 1);
    }

    @FXML
    private void handleHalamanSebelumnya() {
        if (currentPage > 0) {
            currentPage--;
            loadTableData();
        }
    }

    @FXML
    private void handleHalamanBerikutnya() {
        if ((currentPage + 1) * PAGE_SIZE < totalPasien) {
            currentPage++;
            loadTableData();
        }
    }

    private void disableFormInputs() {
        if (txtNik != null) txtNik.setDisable(true);
        if (txtNamaPasien != null) txtNamaPasien.setDisable(true);
        if (dpTanggalLahir != null) dpTanggalLahir.setDisable(true);
        if (cbJenisKelamin != null) cbJenisKelamin.setDisable(true);
        if (txtNoHp != null) txtNoHp.setDisable(true);
        if (txtAlamat != null) txtAlamat.setDisable(true);
        if (txtAlergiObat != null) txtAlergiObat.setDisable(true);
        if (btnSimpan != null) btnSimpan.setDisable(true);
        if (btnUpdate != null) btnUpdate.setDisable(true);
        if (btnHapus != null) btnHapus.setDisable(true);
    }

    private void populateForm(Pasien p) {
        if (txtNik != null) txtNik.setText(p.getNik());
        if (txtNamaPasien != null) txtNamaPasien.setText(p.getNamaPasien());
        if (dpTanggalLahir != null) dpTanggalLahir.setValue(p.getTanggalLahir());
        if (cbJenisKelamin != null) cbJenisKelamin.setValue(p.getJenisKelamin());
        if (txtNoHp != null) txtNoHp.setText(p.getNoHp());
        if (txtAlamat != null) txtAlamat.setText(p.getAlamat());
        if (txtAlergiObat != null) txtAlergiObat.setText(p.getAlergiObat());
    }

    @FXML
    private void handleSimpan() {
        UserSession session = UserSession.getInstance();
        if (session == null || !session.isAdmin()) {
            showAlert(Alert.AlertType.ERROR, "Akses Ditolak", "Hanya Administrator yang memiliki wewenang mendaftarkan pasien baru!");
            return;
        }

        String nik = txtNik != null ? txtNik.getText().trim() : "";
        String nama = txtNamaPasien != null ? txtNamaPasien.getText().trim() : "";

        if (nik.isEmpty() || nama.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gagal", "NIK dan Nama Pasien wajib diisi!");
            return;
        }

        if (!nik.matches("\\d{16}")) {
            showAlert(Alert.AlertType.WARNING, "Format NIK Salah", "NIK harus terdiri dari 16 digit angka!");
            return;
        }

        Pasien p = new Pasien();
        p.setNik(nik);
        p.setPasswordHash(PasswordUtil.hashPassword("123456")); // default password terhash aman
        p.setNamaPasien(nama);
        p.setTanggalLahir(dpTanggalLahir != null ? dpTanggalLahir.getValue() : null);
        p.setJenisKelamin(cbJenisKelamin != null ? cbJenisKelamin.getValue() : null);
        p.setNoHp(txtNoHp != null ? txtNoHp.getText().trim() : "");
        p.setAlamat(txtAlamat != null ? txtAlamat.getText().trim() : "");
        p.setAlergiObat(txtAlergiObat != null ? txtAlergiObat.getText().trim() : "-");

        try {
            if (pasienDAO.insertPasien(p)) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data pasien baru berhasil disimpan!");
                currentPage = 0;
                loadTableData();
                handleClear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menyimpan pasien ke database.");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                showAlert(Alert.AlertType.ERROR, "Duplikat NIK", "Pasien dengan NIK " + nik + " sudah terdaftar di sistem!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error Database", e.getMessage());
            }
        }
    }

    @FXML
    private void handleUpdate() {
        UserSession session = UserSession.getInstance();
        if (session == null || !session.isAdmin()) {
            showAlert(Alert.AlertType.ERROR, "Akses Ditolak", "Hanya Administrator yang berwenang mengubah data pasien!");
            return;
        }

        if (selectedPasien == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih data pasien dari tabel yang ingin diperbarui!");
            return;
        }

        String nik = txtNik != null ? txtNik.getText().trim() : "";
        String nama = txtNamaPasien != null ? txtNamaPasien.getText().trim() : "";

        if (nik.isEmpty() || nama.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gagal", "NIK dan Nama Pasien wajib diisi!");
            return;
        }

        if (!nik.matches("\\d{16}")) {
            showAlert(Alert.AlertType.WARNING, "Format NIK Salah", "NIK harus terdiri dari 16 digit angka!");
            return;
        }

        selectedPasien.setNik(nik);
        selectedPasien.setNamaPasien(nama);
        selectedPasien.setTanggalLahir(dpTanggalLahir != null ? dpTanggalLahir.getValue() : null);
        selectedPasien.setJenisKelamin(cbJenisKelamin != null ? cbJenisKelamin.getValue() : null);
        selectedPasien.setNoHp(txtNoHp != null ? txtNoHp.getText().trim() : "");
        selectedPasien.setAlamat(txtAlamat != null ? txtAlamat.getText().trim() : "");
        selectedPasien.setAlergiObat(txtAlergiObat != null ? txtAlergiObat.getText().trim() : "-");

        try {
            if (pasienDAO.updatePasien(selectedPasien)) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data pasien berhasil diperbarui!");
                loadTableData();
                handleClear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal memperbarui data pasien.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error Database", e.getMessage());
        }
    }

    @FXML
    private void handleHapus() {
        UserSession session = UserSession.getInstance();
        if (session == null || !session.isAdmin()) {
            showAlert(Alert.AlertType.ERROR, "Akses Ditolak", "Hanya Administrator yang berwenang menghapus data pasien!");
            return;
        }

        if (selectedPasien == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih data pasien dari tabel yang ingin dihapus!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
                "Apakah Anda yakin ingin menghapus data pasien " + selectedPasien.getNamaPasien() + " (NIK: " + selectedPasien.getNik() + ")?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText(null);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            try {
                if (pasienDAO.deletePasien(selectedPasien.getPasienId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data pasien berhasil dihapus!");
                    loadTableData();
                    handleClear();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menghapus data pasien.");
                }
            } catch (SQLException e) {
                if (e.getErrorCode() == 1451) {
                    showAlert(Alert.AlertType.ERROR, "Tidak Dapat Dihapus", "Pasien ini sudah memiliki riwayat antrean atau rekam medis sehingga tidak dapat dihapus!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error Database", e.getMessage());
                }
            }
        }
    }

    @FXML
    private void handleClear() {
        selectedPasien = null;
        if (txtNik != null) txtNik.clear();
        if (txtNamaPasien != null) txtNamaPasien.clear();
        if (dpTanggalLahir != null) dpTanggalLahir.setValue(null);
        if (cbJenisKelamin != null) cbJenisKelamin.setValue(null);
        if (txtNoHp != null) txtNoHp.clear();
        if (txtAlamat != null) txtAlamat.clear();
        if (txtAlergiObat != null) txtAlergiObat.clear();
        if (tvPasien != null) tvPasien.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
