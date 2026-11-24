package com.klinik.app.controller;

import com.klinik.app.dao.DokterDAO;
import com.klinik.app.dao.PegawaiDAO;
import com.klinik.app.model.Dokter;
import com.klinik.app.model.JenisPegawai;
import com.klinik.app.model.Pegawai;
import com.klinik.app.util.UserSession;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class PegawaiController {

    @FXML private TextField txtSearch;
    @FXML private TextField txtNamaLengkap;
    @FXML private TextField txtNoHp;
    @FXML private ComboBox<JenisPegawai> cbJenisPegawai;

    @FXML private VBox boxDokterFields;
    @FXML private Label lblUsername;
    @FXML private TextField txtUsername;
    @FXML private Label lblPassword;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblSip;
    @FXML private TextField txtNoSip;
    @FXML private Label lblSpesialisasi;
    @FXML private TextField txtSpesialisasi;

    @FXML private Button btnSimpan;
    @FXML private Button btnUpdate;
    @FXML private Button btnHapus;

    @FXML private TableView<Pegawai> tvPegawai;
    @FXML private TableColumn<Pegawai, Integer> colPegawaiId;
    @FXML private TableColumn<Pegawai, String> colNamaLengkap;
    @FXML private TableColumn<Pegawai, String> colNoHp;
    @FXML private TableColumn<Pegawai, JenisPegawai> colJenisPegawai;

    private final PegawaiDAO pegawaiDAO = new PegawaiDAO();
    private final DokterDAO dokterDAO = new DokterDAO();
    private final ObservableList<Pegawai> masterPegawai = FXCollections.observableArrayList();
    private FilteredList<Pegawai> filteredPegawai;
    private Pegawai selectedPegawai;

    @FXML
    public void initialize() {
        UserSession session = UserSession.getInstance();

        // FAIL-CLOSED SECURITY: Jika bukan Admin, matikan seluruh kontrol pegawai
        if (session == null || !session.isAdmin()) {
            disableAllControls();
            return;
        }

        if (cbJenisPegawai != null) {
            cbJenisPegawai.setItems(FXCollections.observableArrayList(JenisPegawai.values()));
            cbJenisPegawai.valueProperty().addListener((obs, oldVal, newVal) -> {
                boolean isDokter = newVal == JenisPegawai.DOKTER;
                setDokterFieldsVisible(isDokter);
            });
        }

        if (colPegawaiId != null) colPegawaiId.setCellValueFactory(new PropertyValueFactory<>("pegawaiId"));
        if (colNamaLengkap != null) colNamaLengkap.setCellValueFactory(new PropertyValueFactory<>("namaLengkap"));
        if (colNoHp != null) colNoHp.setCellValueFactory(new PropertyValueFactory<>("noHp"));
        if (colJenisPegawai != null) colJenisPegawai.setCellValueFactory(new PropertyValueFactory<>("jenisPegawai"));

        setupSearchFilter();
        loadTableData();

        if (tvPegawai != null) {
            tvPegawai.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedPegawai = newSelection;
                    populateForm(selectedPegawai);
                }
            });
        }

        // Default sembunyikan field dokter sampai jenis DOKTER dipilih
        setDokterFieldsVisible(false);
    }

    private void setDokterFieldsVisible(boolean visible) {
        if (boxDokterFields != null) {
            boxDokterFields.setVisible(visible);
            boxDokterFields.setManaged(visible);
        }
        if (lblUsername != null) lblUsername.setVisible(visible);
        if (txtUsername != null) txtUsername.setVisible(visible);
        if (lblPassword != null) lblPassword.setVisible(visible);
        if (txtPassword != null) txtPassword.setVisible(visible);
        if (lblSip != null) lblSip.setVisible(visible);
        if (txtNoSip != null) txtNoSip.setVisible(visible);
        if (lblSpesialisasi != null) lblSpesialisasi.setVisible(visible);
        if (txtSpesialisasi != null) txtSpesialisasi.setVisible(visible);
    }

    private void setupSearchFilter() {
        filteredPegawai = new FilteredList<>(masterPegawai, p -> true);

        if (txtSearch != null) {
            txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredPegawai.setPredicate(pegawai -> {
                    if (newVal == null || newVal.trim().isEmpty()) return true;
                    String f = newVal.toLowerCase().trim();
                    if (pegawai.getNamaLengkap() != null && pegawai.getNamaLengkap().toLowerCase().contains(f)) return true;
                    if (pegawai.getNoHp() != null && pegawai.getNoHp().contains(f)) return true;
                    if (pegawai.getJenisPegawai() != null && pegawai.getJenisPegawai().name().toLowerCase().contains(f)) return true;
                    return false;
                });
            });
        }

        if (tvPegawai != null) {
            tvPegawai.setItems(filteredPegawai);
        }
    }

    private void disableAllControls() {
        if (tvPegawai != null) tvPegawai.setDisable(true);
        if (txtNamaLengkap != null) txtNamaLengkap.setDisable(true);
        if (txtNoHp != null) txtNoHp.setDisable(true);
        if (cbJenisPegawai != null) cbJenisPegawai.setDisable(true);
        if (btnSimpan != null) btnSimpan.setDisable(true);
        if (btnUpdate != null) btnUpdate.setDisable(true);
        if (btnHapus != null) btnHapus.setDisable(true);
        setDokterFieldsVisible(false);
    }

    private void loadTableData() {
        new Thread(() -> {
            List<Pegawai> list = pegawaiDAO.getAllPegawai();
            Platform.runLater(() -> masterPegawai.setAll(list));
        }).start();
    }

    private void populateForm(Pegawai p) {
        if (txtNamaLengkap != null) txtNamaLengkap.setText(p.getNamaLengkap());
        if (txtNoHp != null) txtNoHp.setText(p.getNoHp());
        if (cbJenisPegawai != null) cbJenisPegawai.setValue(p.getJenisPegawai());

        if (p.getJenisPegawai() == JenisPegawai.DOKTER) {
            setDokterFieldsVisible(true);
            new Thread(() -> {
                Dokter d = dokterDAO.getDokterById(p.getPegawaiId());
                Platform.runLater(() -> {
                    if (d != null) {
                        if (txtUsername != null) txtUsername.setText(d.getUsername());
                        if (txtNoSip != null) txtNoSip.setText(d.getNoSip());
                        if (txtSpesialisasi != null) txtSpesialisasi.setText(d.getSpesialisasi());
                    }
                    if (txtPassword != null) txtPassword.clear();
                });
            }).start();
        } else {
            setDokterFieldsVisible(false);
            if (txtUsername != null) txtUsername.clear();
            if (txtPassword != null) txtPassword.clear();
            if (txtNoSip != null) txtNoSip.clear();
            if (txtSpesialisasi != null) txtSpesialisasi.clear();
        }
    }

    @FXML
    private void handleSimpanPegawai() {
        UserSession session = UserSession.getInstance();
        if (session == null || !session.isAdmin()) {
            showAlert(Alert.AlertType.ERROR, "Akses Ditolak", "Hanya Administrator yang memiliki wewenang mengelola data Pegawai & Dokter!");
            return;
        }

        String nama = txtNamaLengkap != null ? txtNamaLengkap.getText().trim() : "";
        JenisPegawai jp = cbJenisPegawai != null ? cbJenisPegawai.getValue() : null;

        if (nama.isEmpty() || jp == null) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gagal", "Nama Lengkap dan Jenis Pegawai wajib diisi!");
            return;
        }

        Pegawai p = new Pegawai();
        p.setNamaLengkap(nama);
        p.setNoHp(txtNoHp != null ? txtNoHp.getText().trim() : "");
        p.setJenisPegawai(jp);

        try {
            if (jp == JenisPegawai.DOKTER) {
                String username = txtUsername != null ? txtUsername.getText().trim() : "";
                String password = txtPassword != null ? txtPassword.getText().trim() : "";
                String noSip = txtNoSip != null ? txtNoSip.getText().trim() : "";
                String spesialisasi = txtSpesialisasi != null ? txtSpesialisasi.getText().trim() : "Dokter Umum";

                if (username.isEmpty() || password.isEmpty() || noSip.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Validasi Dokter", "Username, Password, dan No. SIP wajib diisi untuk Dokter!");
                    return;
                }

                Dokter d = new Dokter();
                d.setUsername(username);
                d.setNoSip(noSip);
                d.setSpesialisasi(spesialisasi);

                if (dokterDAO.buatAkunDokter(p, d, password)) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Akun dan Data Dokter " + nama + " berhasil dibuat!");
                    loadTableData();
                    handleClearPegawai();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menyimpan akun Dokter.");
                }
            } else {
                if (pegawaiDAO.insertPegawai(p)) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data Pegawai " + nama + " berhasil disimpan!");
                    loadTableData();
                    handleClearPegawai();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menyimpan data pegawai.");
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                showAlert(Alert.AlertType.ERROR, "Data Duplikat", "Username atau No. SIP sudah digunakan oleh pegawai lain!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error Database", e.getMessage());
            }
        }
    }

    @FXML
    private void handleUpdatePegawai() {
        UserSession session = UserSession.getInstance();
        if (session == null || !session.isAdmin()) {
            showAlert(Alert.AlertType.ERROR, "Akses Ditolak", "Hanya Administrator yang memiliki wewenang mengubah data Pegawai & Dokter!");
            return;
        }

        if (selectedPegawai == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih data pegawai dari tabel yang ingin diperbarui!");
            return;
        }

        String nama = txtNamaLengkap != null ? txtNamaLengkap.getText().trim() : "";
        if (nama.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gagal", "Nama Lengkap wajib diisi!");
            return;
        }

        selectedPegawai.setNamaLengkap(nama);
        selectedPegawai.setNoHp(txtNoHp != null ? txtNoHp.getText().trim() : "");

        try {
            if (selectedPegawai.getJenisPegawai() == JenisPegawai.DOKTER) {
                String username = txtUsername != null ? txtUsername.getText().trim() : "";
                String password = txtPassword != null ? txtPassword.getText().trim() : "";
                String noSip = txtNoSip != null ? txtNoSip.getText().trim() : "";
                String spesialisasi = txtSpesialisasi != null ? txtSpesialisasi.getText().trim() : "Dokter Umum";

                if (username.isEmpty() || noSip.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Validasi Gagal", "Username dan No SIP wajib diisi untuk Dokter!");
                    return;
                }

                Dokter d = new Dokter();
                d.setPegawaiId(selectedPegawai.getPegawaiId());
                d.setUsername(username);
                d.setNoSip(noSip);
                d.setSpesialisasi(spesialisasi);

                if (dokterDAO.updateAkunDokter(selectedPegawai, d, password)) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data Dokter berhasil diperbarui!");
                    loadTableData();
                    handleClearPegawai();
                }
            } else {
                if (pegawaiDAO.updatePegawai(selectedPegawai)) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data Pegawai berhasil diperbarui!");
                    loadTableData();
                    handleClearPegawai();
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error Database", e.getMessage());
        }
    }

    @FXML
    private void handleHapusPegawai() {
        UserSession session = UserSession.getInstance();
        if (session == null || !session.isAdmin()) {
            showAlert(Alert.AlertType.ERROR, "Akses Ditolak", "Hanya Administrator yang memiliki wewenang menghapus data Pegawai!");
            return;
        }

        if (selectedPegawai == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih data pegawai dari tabel yang ingin dihapus!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
                "Apakah Anda yakin ingin menghapus " + selectedPegawai.getNamaLengkap() + " (" + selectedPegawai.getJenisPegawai() + ")?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText(null);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            try {
                if (pegawaiDAO.deletePegawai(selectedPegawai.getPegawaiId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data pegawai berhasil dihapus!");
                    loadTableData();
                    handleClearPegawai();
                }
            } catch (SQLException e) {
                if (e.getErrorCode() == 1451) {
                    showAlert(Alert.AlertType.ERROR, "Tidak Dapat Dihapus", "Pegawai/Dokter ini memiliki riwayat antrean atau rekam medis terkait!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error Database", e.getMessage());
                }
            }
        }
    }

    @FXML
    private void handleClearPegawai() {
        selectedPegawai = null;
        if (txtNamaLengkap != null) txtNamaLengkap.clear();
        if (txtNoHp != null) txtNoHp.clear();
        if (cbJenisPegawai != null) cbJenisPegawai.setValue(null);
        if (txtUsername != null) txtUsername.clear();
        if (txtPassword != null) txtPassword.clear();
        if (txtNoSip != null) txtNoSip.clear();
        if (txtSpesialisasi != null) txtSpesialisasi.clear();
        setDokterFieldsVisible(false);
        if (tvPegawai != null) tvPegawai.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
