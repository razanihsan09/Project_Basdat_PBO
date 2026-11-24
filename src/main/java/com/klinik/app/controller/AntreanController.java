package com.klinik.app.controller;

import com.klinik.app.dao.AntreanDAO;
import com.klinik.app.dao.DokterDAO;
import com.klinik.app.dao.PasienDAO;
import com.klinik.app.model.AdminSystem;
import com.klinik.app.model.Antrean;
import com.klinik.app.model.Dokter;
import com.klinik.app.model.Pasien;
import com.klinik.app.util.UserSession;
import javafx.application.Platform;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AntreanController {

    @FXML private ComboBox<Pasien> cbPasien;
    @FXML private ComboBox<Dokter> cbDokter;
    @FXML private TextField txtNoAntrean;
    @FXML private ComboBox<String> cbTingkatUrgensi;
    @FXML private Button btnTambahAntrean;
    @FXML private Label lblPasienBerikutnya;
    @FXML private Label lblQueueStats;
    @FXML private Label lblAutoRefreshStatus;
    @FXML private VBox boxAdminInputs;
    @FXML private Button btnPanggil;

    @FXML private TableView<Antrean> tableAntrean;
    @FXML private TableColumn<Antrean, Integer> colNoAntrean;
    @FXML private TableColumn<Antrean, String> colNamaPasien;
    @FXML private TableColumn<Antrean, String> colNamaDokter;
    @FXML private TableColumn<Antrean, String> colTingkatUrgensi;
    @FXML private TableColumn<Antrean, String> colStatus;
    @FXML private TableColumn<Antrean, String> colKeluhan;
    @FXML private Button btnHalamanSebelumnya;
    @FXML private Button btnHalamanBerikutnya;
    @FXML private Label lblInfoHalaman;

    private final AntreanDAO antreanDAO = new AntreanDAO();
    private final PasienDAO pasienDAO = new PasienDAO();
    private final DokterDAO dokterDAO = new DokterDAO();

    private final ObservableList<Antrean> listTableAntrean = FXCollections.observableArrayList();
    private static final int PAGE_SIZE = 20;
    private int currentPage = 0;
    private Antrean antreanBerikutnya;
    private PauseTransition patientSearchDelay;
    private long patientSearchRequestId;
    private long dataLoadRequestId;
    private ScheduledExecutorService autoRefreshScheduler;

    @FXML
    public void initialize() {
        if (cbTingkatUrgensi != null) {
            cbTingkatUrgensi.setItems(FXCollections.observableArrayList("HIJAU", "KUNING", "MERAH"));
            cbTingkatUrgensi.setValue("HIJAU");
        }

        if (colNoAntrean != null) colNoAntrean.setCellValueFactory(new PropertyValueFactory<>("noAntrean"));
        if (colNamaPasien != null) colNamaPasien.setCellValueFactory(new PropertyValueFactory<>("namaPasien"));
        if (colNamaDokter != null) colNamaDokter.setCellValueFactory(new PropertyValueFactory<>("namaDokter"));
        if (colTingkatUrgensi != null) colTingkatUrgensi.setCellValueFactory(new PropertyValueFactory<>("tingkatUrgensi"));
        if (colStatus != null) colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        if (colKeluhan != null) colKeluhan.setCellValueFactory(new PropertyValueFactory<>("keluhan"));

        // Format tampilan badge tabel
        setupTableStyling();

        UserSession session = UserSession.getInstance();
        if (session != null && session.isDokter()) {
            disableAntreanInputs();
        }

        setupDropdowns();
        loadDataAntrean();
        startAutoRefresh();
    }

    private void setupTableStyling() {
        if (colTingkatUrgensi != null) {
            colTingkatUrgensi.setCellFactory(col -> new TableCell<Antrean, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                        setStyle("");
                    } else {
                        setText(item);
                        switch (item.toUpperCase()) {
                            case "MERAH":
                                setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #b91c1c; -fx-font-weight: bold; -fx-alignment: CENTER; -fx-background-radius: 4;");
                                break;
                            case "KUNING":
                                setStyle("-fx-background-color: #fef3c7; -fx-text-fill: #b45309; -fx-font-weight: bold; -fx-alignment: CENTER; -fx-background-radius: 4;");
                                break;
                            default:
                                setStyle("-fx-background-color: #d1fae5; -fx-text-fill: #047857; -fx-font-weight: bold; -fx-alignment: CENTER; -fx-background-radius: 4;");
                                break;
                        }
                    }
                }
            });
        }

        if (colStatus != null) {
            colStatus.setCellFactory(col -> new TableCell<Antrean, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        switch (item.toUpperCase()) {
                            case "DIPERIKSA":
                                setStyle("-fx-background-color: #ffedd5; -fx-text-fill: #c2410c; -fx-font-weight: bold; -fx-alignment: CENTER; -fx-background-radius: 4;");
                                break;
                            case "SELESAI":
                                setStyle("-fx-background-color: #ecfdf5; -fx-text-fill: #065f46; -fx-font-weight: bold; -fx-alignment: CENTER; -fx-background-radius: 4;");
                                break;
                            default:
                                setStyle("-fx-background-color: #e0f2fe; -fx-text-fill: #0369a1; -fx-font-weight: bold; -fx-alignment: CENTER; -fx-background-radius: 4;");
                                break;
                        }
                    }
                }
            });
        }
    }

    private void setupDropdowns() {
        new Thread(() -> {
            List<Dokter> listDokter = dokterDAO.getAllDokter();
            List<Pasien> pasienAwal = pasienDAO.searchPasienForSelector("", 50);

            Platform.runLater(() -> {
                if (cbPasien != null) {
                    cbPasien.setEditable(true);
                    cbPasien.setItems(FXCollections.observableArrayList(pasienAwal));
                    cbPasien.setConverter(new StringConverter<Pasien>() {
                        @Override
                        public String toString(Pasien p) {
                            if (p == null) return "";
                            return p.getNamaPasien() + " (NIK: " + p.getNik() + ")";
                        }
                        @Override
                        public Pasien fromString(String string) { return null; }
                    });
                    configurePatientComboSearch();
                }

                if (cbDokter != null) {
                    cbDokter.setItems(FXCollections.observableArrayList(listDokter));
                    cbDokter.setConverter(new StringConverter<Dokter>() {
                        @Override
                        public String toString(Dokter d) {
                            if (d == null) return "";
                            return d.getNamaLengkap() + " (" + d.getSpesialisasi() + ")";
                        }
                        @Override
                        public Dokter fromString(String string) { return null; }
                    });

                    cbDokter.valueProperty().addListener((obs, oldDokter, newDokter) -> {
                        if (newDokter != null && txtNoAntrean != null) {
                            new Thread(() -> {
                                int nextNo = antreanDAO.getNomorAntreanBerikutnya(newDokter.getPegawaiId());
                                Platform.runLater(() -> txtNoAntrean.setText(String.valueOf(nextNo)));
                            }).start();
                        }
                    });
                }
            });
        }).start();
    }

    private void configurePatientComboSearch() {
        if (cbPasien == null) return;
        patientSearchDelay = new PauseTransition(Duration.millis(300));
        cbPasien.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            String keyword = newText == null ? "" : newText.trim();
            if (cbPasien.getValue() != null && keyword.equals(cbPasien.getConverter().toString(cbPasien.getValue()))) return;
            if (!keyword.isEmpty() && keyword.length() < 2) return;
            patientSearchDelay.stop();
            patientSearchDelay.setOnFinished(event -> {
                long requestId = ++patientSearchRequestId;
                new Thread(() -> {
                    List<Pasien> results = pasienDAO.searchPasienForSelector(keyword, 50);
                    Platform.runLater(() -> {
                        if (requestId == patientSearchRequestId && cbPasien != null) {
                            cbPasien.setItems(FXCollections.observableArrayList(results));
                            if (!results.isEmpty() && cbPasien.isFocused()) cbPasien.show();
                        }
                    });
                }).start();
            });
            patientSearchDelay.playFromStart();
        });
    }

    private void disableAntreanInputs() {
        if (boxAdminInputs != null) {
            boxAdminInputs.setVisible(false);
            boxAdminInputs.setManaged(false);
        }
        if (cbPasien != null) cbPasien.setDisable(true);
        if (cbDokter != null) cbDokter.setDisable(true);
        if (txtNoAntrean != null) txtNoAntrean.setDisable(true);
        if (cbTingkatUrgensi != null) cbTingkatUrgensi.setDisable(true);
        if (btnTambahAntrean != null) btnTambahAntrean.setDisable(true);
    }

    @FXML
    public void loadDataAntrean() {
        UserSession session = UserSession.getInstance();
        Integer dokterIdLogin = (session != null && session.isDokter()) ? session.getDokterPegawaiId() : null;
        int requestedPage = currentPage;
        long requestId = ++dataLoadRequestId;

        new Thread(() -> {
            int total = antreanDAO.countAntreanAktif(dokterIdLogin);
            int totalPages = Math.max(1, (int) Math.ceil(total / (double) PAGE_SIZE));
            int page = Math.max(0, Math.min(requestedPage, totalPages - 1));
            List<Antrean> pageRows = antreanDAO.getAntreanAktifPage(dokterIdLogin, PAGE_SIZE, page * PAGE_SIZE);
            Antrean next = antreanDAO.getAntreanBerikutnya(dokterIdLogin);

            final int resolvedPage = page;
            Platform.runLater(() -> {
                if (requestId != dataLoadRequestId) return;
                currentPage = resolvedPage;
                updateTableAndLabel(pageRows, total, next);
            });
        }).start();
    }

    private void updateTableAndLabel(List<Antrean> pageRows, int total, Antrean next) {
        listTableAntrean.setAll(pageRows != null ? pageRows : List.of());
        if (tableAntrean != null) tableAntrean.setItems(listTableAntrean);
        antreanBerikutnya = next;

        if (lblPasienBerikutnya != null) {
            if (antreanBerikutnya != null) {
                lblPasienBerikutnya.setText("Pasien Prioritas Berikutnya: No. " + antreanBerikutnya.getNoAntrean() + " • " +
                        antreanBerikutnya.getNamaPasien() + " [" + antreanBerikutnya.getTingkatUrgensi() + "]");
                String warna = "MERAH".equalsIgnoreCase(antreanBerikutnya.getTingkatUrgensi()) ? "#dc2626" :
                        ("KUNING".equalsIgnoreCase(antreanBerikutnya.getTingkatUrgensi()) ? "#d97706" : "#059669");
                lblPasienBerikutnya.setStyle("-fx-font-weight: bold; -fx-text-fill: " + warna);
            } else {
                lblPasienBerikutnya.setText("Tidak ada antrean pasien menunggu");
                lblPasienBerikutnya.setStyle("-fx-font-weight: normal; -fx-text-fill: #64748b;");
            }
        }

        if (lblQueueStats != null) lblQueueStats.setText("Total Pasien Aktif: " + total + " orang");
        updatePaginationControls(total);

        if (lblAutoRefreshStatus != null) {
            String timeNow = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            lblAutoRefreshStatus.setText("● Auto-refresh (5s) • " + timeNow);
        }
    }

    private void updatePaginationControls(int total) {
        int totalPages = (int) Math.ceil(total / (double) PAGE_SIZE);
        if (lblInfoHalaman != null) {
            lblInfoHalaman.setText(total == 0 ? "Tidak ada data" :
                    "Halaman " + (currentPage + 1) + " dari " + totalPages + " • " + total + " data");
        }
        if (btnHalamanSebelumnya != null) btnHalamanSebelumnya.setDisable(currentPage <= 0 || total == 0);
        if (btnHalamanBerikutnya != null) btnHalamanBerikutnya.setDisable(total == 0 || currentPage >= totalPages - 1);
    }

    @FXML
    private void handleHalamanSebelumnya() {
        if (currentPage > 0) {
            currentPage--;
            loadDataAntrean();
        }
    }

    @FXML
    private void handleHalamanBerikutnya() {
        currentPage++;
        loadDataAntrean();
    }

    @FXML
    private void handleTambahAntrean() {
        UserSession session = UserSession.getInstance();
        if (session != null && session.isDokter()) {
            showAlert(Alert.AlertType.ERROR, "Akses Ditolak", "Dokter tidak diperkenankan menambahkan antrean baru!");
            return;
        }

        Pasien pasienPilihan = cbPasien != null ? cbPasien.getValue() : null;
        Dokter dokterPilihan = cbDokter != null ? cbDokter.getValue() : null;

        if (pasienPilihan == null || dokterPilihan == null) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gagal", "Silakan pilih Pasien dan Dokter Tujuan!");
            return;
        }

        try {
            int pasienId = pasienPilihan.getPasienId();
            int dokterId = dokterPilihan.getPegawaiId();

            String strNoAntrean = txtNoAntrean != null ? txtNoAntrean.getText().trim() : "";
            int noAntrean = strNoAntrean.isEmpty() ? 0 : Integer.parseInt(strNoAntrean);

            int adminId = (session != null && session.isAdmin() && session.getAdmin() != null)
                    ? session.getAdmin().getPegawaiId() : 1;

            String triase = (cbTingkatUrgensi != null && cbTingkatUrgensi.getValue() != null)
                    ? cbTingkatUrgensi.getValue() : "HIJAU";

            if (antreanDAO.prosesAntreanOlehAdmin(pasienId, dokterId, adminId, noAntrean, triase)) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", 
                        "Antrean untuk pasien " + pasienPilihan.getNamaPasien() + " berhasil ditambahkan!");
                loadDataAntrean();
                if (cbPasien != null) cbPasien.setValue(null);
                if (cbDokter != null) cbDokter.setValue(null);
                if (txtNoAntrean != null) txtNoAntrean.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menyimpan antrean ke database!");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Input Tidak Valid", "Nomor Antrean harus berupa angka!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error Database", e.getMessage());
        }
    }

    @FXML
    private void handlePanggilPasienBerikutnya() {
        Antrean dipanggil = antreanBerikutnya;

        if (dipanggil != null) {
            boolean berhasil = antreanDAO.updateStatus(dipanggil.getAntreanId(), "DIPERIKSA");
            if (!berhasil) {
                showAlert(Alert.AlertType.ERROR, "Gagal Memanggil", "Status antrean gagal diperbarui. Silakan refresh data.");
                loadDataAntrean();
                return;
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Panggilan Pasien");
            alert.setHeaderText("MEMANGGIL PASIEN KE RUANG PEMERIKSAAN");
            alert.setContentText("Nomor Antrean: " + dipanggil.getNoAntrean() + 
                    "\nNama Pasien: " + dipanggil.getNamaPasien() +
                    "\nTriase: " + dipanggil.getTingkatUrgensi() +
                    (dipanggil.getKeluhan() != null ? "\nKeluhan: " + dipanggil.getKeluhan() : ""));
            alert.show();

            loadDataAntrean();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Tidak ada antrean pasien yang berstatus MENUNGGU!");
            alert.setTitle("Antrean Kosong");
            alert.setHeaderText(null);
            alert.show();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void startAutoRefresh() {
        autoRefreshScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Antrean-AutoRefresh-Thread");
            t.setDaemon(true); // otomatis berhenti saat aplikasi ditutup
            return t;
        });

        autoRefreshScheduler.scheduleAtFixedRate(() -> {
            // Hentikan thread otomatis jika user sudah logout atau keluar dari halaman antrean
            if (UserSession.getInstance() == null || tableAntrean == null || tableAntrean.getScene() == null) {
                autoRefreshScheduler.shutdown();
                return;
            }
            loadDataAntrean();
        }, 5, 5, TimeUnit.SECONDS);
    }
}
