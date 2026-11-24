package com.klinik.app.controller;

import com.klinik.app.dao.ObatDAO;
import com.klinik.app.model.Obat;
import com.klinik.app.util.UserSession;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class ObatController {

    @FXML private TextField txtSearch;
    @FXML private TextField txtNamaObat;
    @FXML private TextField txtKategori;
    @FXML private TextField txtStok;
    @FXML private TextField txtSatuan;
    @FXML private DatePicker dpTglExpired;

    @FXML private Button btnSimpan;
    @FXML private Button btnUpdate;
    @FXML private Button btnHapus;
    @FXML private Button btnClear;
    @FXML private Label lblInfoRole;

    @FXML private TableView<Obat> tvObat;
    @FXML private TableColumn<Obat, Integer> colObatId;
    @FXML private TableColumn<Obat, String> colNamaObat;
    @FXML private TableColumn<Obat, String> colKategori;
    @FXML private TableColumn<Obat, Integer> colStok;
    @FXML private TableColumn<Obat, String> colSatuan;
    @FXML private TableColumn<Obat, Object> colTglExpired;

    private final ObatDAO obatDAO = new ObatDAO();
    private final ObservableList<Obat> masterObat = FXCollections.observableArrayList();
    private FilteredList<Obat> filteredObat;
    private Obat selectedObat;

    @FXML
    public void initialize() {
        if (colObatId != null) colObatId.setCellValueFactory(new PropertyValueFactory<>("obatId"));
        if (colNamaObat != null) colNamaObat.setCellValueFactory(new PropertyValueFactory<>("namaObat"));
        if (colKategori != null) colKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        if (colStok != null) colStok.setCellValueFactory(new PropertyValueFactory<>("stok"));
        if (colSatuan != null) colSatuan.setCellValueFactory(new PropertyValueFactory<>("satuan"));
        if (colTglExpired != null) colTglExpired.setCellValueFactory(new PropertyValueFactory<>("tglExpired"));

        // FAIL-CLOSED: Dokter hanya boleh melihat katalog obat (Read-Only)
        UserSession session = UserSession.getInstance();
        if (session == null || !session.isAdmin()) {
            disableFormInputs();
            if (lblInfoRole != null) {
                lblInfoRole.setText("Mode Baca Saja (Hanya Administrator yang dapat menambah/mengedit obat)");
                lblInfoRole.setVisible(true);
            }
        }

        setupSearchFilter();
        loadTableData();

        if (tvObat != null) {
            tvObat.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedObat = newSelection;
                    populateForm(selectedObat);
                }
            });
        }
    }

    private void setupSearchFilter() {
        filteredObat = new FilteredList<>(masterObat, o -> true);

        if (txtSearch != null) {
            txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredObat.setPredicate(obat -> {
                    if (newVal == null || newVal.trim().isEmpty()) return true;
                    String filter = newVal.toLowerCase().trim();
                    if (obat.getNamaObat() != null && obat.getNamaObat().toLowerCase().contains(filter)) return true;
                    if (obat.getKategori() != null && obat.getKategori().toLowerCase().contains(filter)) return true;
                    if (obat.getSatuan() != null && obat.getSatuan().toLowerCase().contains(filter)) return true;
                    return false;
                });
            });
        }

        if (tvObat != null) {
            tvObat.setItems(filteredObat);
        }
    }

    private void disableFormInputs() {
        if (txtNamaObat != null) txtNamaObat.setDisable(true);
        if (txtKategori != null) txtKategori.setDisable(true);
        if (txtStok != null) txtStok.setDisable(true);
        if (txtSatuan != null) txtSatuan.setDisable(true);
        if (dpTglExpired != null) dpTglExpired.setDisable(true);
        if (btnSimpan != null) btnSimpan.setDisable(true);
        if (btnUpdate != null) btnUpdate.setDisable(true);
        if (btnHapus != null) btnHapus.setDisable(true);
        if (btnClear != null) btnClear.setDisable(true);
    }

    private void loadTableData() {
        new Thread(() -> {
            List<Obat> list = obatDAO.getAllObat();
            Platform.runLater(() -> masterObat.setAll(list));
        }).start();
    }

    private void populateForm(Obat o) {
        if (txtNamaObat != null) txtNamaObat.setText(o.getNamaObat());
        if (txtKategori != null) txtKategori.setText(o.getKategori());
        if (txtStok != null) txtStok.setText(String.valueOf(o.getStok()));
        if (txtSatuan != null) txtSatuan.setText(o.getSatuan());
        if (dpTglExpired != null) dpTglExpired.setValue(o.getTglExpired());
    }

    @FXML
    private void handleSimpanObat() {
        UserSession session = UserSession.getInstance();
        if (session == null || !session.isAdmin()) {
            showAlert(Alert.AlertType.ERROR, "Akses Ditolak", "Hanya Administrator yang berwenang menambah data obat!");
            return;
        }

        String nama = txtNamaObat != null ? txtNamaObat.getText().trim() : "";
        String stokStr = txtStok != null ? txtStok.getText().trim() : "";
        String satuan = txtSatuan != null ? txtSatuan.getText().trim() : "";

        if (nama.isEmpty() || stokStr.isEmpty() || satuan.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gagal", "Nama Obat, Satuan, dan Stok wajib diisi!");
            return;
        }

        int stok;
        try {
            stok = Integer.parseInt(stokStr);
            if (stok < 0) {
                showAlert(Alert.AlertType.WARNING, "Nilai Tidak Valid", "Stok obat tidak boleh bernilai negatif!");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Format Angka Salah", "Stok harus berupa bilangan bulat!");
            return;
        }

        if (dpTglExpired != null && dpTglExpired.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gagal", "Tanggal Kedaluwarsa obat wajib diisi!");
            return;
        }

        Obat o = new Obat();
        o.setNamaObat(nama);
        o.setKategori(txtKategori != null ? txtKategori.getText().trim() : "-");
        o.setStok(stok);
        o.setSatuan(satuan);
        o.setTglExpired(dpTglExpired.getValue());

        try {
            if (obatDAO.insertObat(o)) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data obat '" + nama + "' berhasil disimpan!");
                loadTableData();
                handleClearObat();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menyimpan data obat ke database.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error Database", e.getMessage());
        }
    }

    @FXML
    private void handleUpdateObat() {
        UserSession session = UserSession.getInstance();
        if (session == null || !session.isAdmin()) {
            showAlert(Alert.AlertType.ERROR, "Akses Ditolak", "Hanya Administrator yang berwenang mengubah data obat!");
            return;
        }

        if (selectedObat == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih data obat yang ingin diperbarui dari tabel!");
            return;
        }

        String nama = txtNamaObat != null ? txtNamaObat.getText().trim() : "";
        String stokStr = txtStok != null ? txtStok.getText().trim() : "";
        String satuan = txtSatuan != null ? txtSatuan.getText().trim() : "";

        if (nama.isEmpty() || stokStr.isEmpty() || satuan.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gagal", "Nama Obat, Satuan, dan Stok wajib diisi!");
            return;
        }

        int stok;
        try {
            stok = Integer.parseInt(stokStr);
            if (stok < 0) {
                showAlert(Alert.AlertType.WARNING, "Nilai Tidak Valid", "Stok obat tidak boleh bernilai negatif!");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Format Angka Salah", "Stok harus berupa bilangan bulat!");
            return;
        }

        selectedObat.setNamaObat(nama);
        selectedObat.setKategori(txtKategori != null ? txtKategori.getText().trim() : "-");
        selectedObat.setStok(stok);
        selectedObat.setSatuan(satuan);
        selectedObat.setTglExpired(dpTglExpired != null ? dpTglExpired.getValue() : null);

        try {
            if (obatDAO.updateObat(selectedObat)) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data obat berhasil diperbarui!");
                loadTableData();
                handleClearObat();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal memperbarui data obat.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error Database", e.getMessage());
        }
    }

    @FXML
    private void handleHapusObat() {
        UserSession session = UserSession.getInstance();
        if (session == null || !session.isAdmin()) {
            showAlert(Alert.AlertType.ERROR, "Akses Ditolak", "Hanya Administrator yang berwenang menghapus data obat!");
            return;
        }

        if (selectedObat == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih data obat yang ingin dihapus dari tabel!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
                "Apakah Anda yakin ingin menghapus obat '" + selectedObat.getNamaObat() + "'?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText(null);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            try {
                if (obatDAO.deleteObat(selectedObat.getObatId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data obat berhasil dihapus!");
                    loadTableData();
                    handleClearObat();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menghapus data obat.");
                }
            } catch (SQLException e) {
                if (e.getErrorCode() == 1451) {
                    showAlert(Alert.AlertType.ERROR, "Tidak Dapat Dihapus", "Obat ini sudah pernah diresepkan dalam rekam medis pasien!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error Database", e.getMessage());
                }
            }
        }
    }

    @FXML
    private void handleClearObat() {
        selectedObat = null;
        if (txtNamaObat != null) txtNamaObat.clear();
        if (txtKategori != null) txtKategori.clear();
        if (txtStok != null) txtStok.clear();
        if (txtSatuan != null) txtSatuan.clear();
        if (dpTglExpired != null) dpTglExpired.setValue(null);
        if (tvObat != null) tvObat.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
