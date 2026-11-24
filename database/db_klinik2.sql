-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.4.3 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- Dumping database structure for db_klinik2
DROP DATABASE IF EXISTS `db_klinik2`;
CREATE DATABASE IF NOT EXISTS `db_klinik2` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `db_klinik2`;

-- Dumping structure for table db_klinik2.admin_system
DROP TABLE IF EXISTS `admin_system`;
CREATE TABLE IF NOT EXISTS `admin_system` (
  `pegawai_id` int NOT NULL,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  PRIMARY KEY (`pegawai_id`),
  UNIQUE KEY `username` (`username`),
  CONSTRAINT `admin_system_ibfk_1` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`pegawai_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table db_klinik2.admin_system
INSERT INTO `admin_system` (`pegawai_id`, `username`, `password_hash`) VALUES
	(1, 'admin1', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92'),
	(2, 'admin2', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92'),
	(9, 'admin3', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92'),
	(10, 'admin4', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92'),
	(11, 'admin5', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92');

-- Dumping structure for table db_klinik2.antrean
DROP TABLE IF EXISTS `antrean`;
CREATE TABLE IF NOT EXISTS `antrean` (
  `antrean_id` int NOT NULL AUTO_INCREMENT,
  `pasien_id` int NOT NULL,
  `admin_id` int NOT NULL,
  `dokter_id` int DEFAULT NULL,
  `tanggal` date NOT NULL,
  `no_antrean` int NOT NULL,
  `tingkat_urgensi` enum('MERAH','KUNING','HIJAU') NOT NULL DEFAULT 'HIJAU',
  `keluhan` text,
  `status` enum('PENDING','MENUNGGU','DIPERIKSA','SELESAI','BATAL') DEFAULT 'PENDING',
  PRIMARY KEY (`antrean_id`),
  UNIQUE KEY `uq_antrean_harian` (`tanggal`,`dokter_id`,`no_antrean`),
  UNIQUE KEY `uq_antrean_pasien_dokter` (`antrean_id`,`pasien_id`,`dokter_id`),
  KEY `pasien_id` (`pasien_id`),
  KEY `admin_id` (`admin_id`),
  KEY `dokter_id` (`dokter_id`),
  CONSTRAINT `antrean_ibfk_1` FOREIGN KEY (`pasien_id`) REFERENCES `pasien` (`pasien_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `antrean_ibfk_2` FOREIGN KEY (`admin_id`) REFERENCES `admin_system` (`pegawai_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `antrean_ibfk_3` FOREIGN KEY (`dokter_id`) REFERENCES `dokter` (`pegawai_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table db_klinik2.antrean (sudah dirapikan no_antrean unik per dokter_id)
INSERT INTO `antrean` (`antrean_id`, `pasien_id`, `admin_id`, `dokter_id`, `tanggal`, `no_antrean`, `tingkat_urgensi`, `keluhan`, `status`) VALUES
	(1, 1, 1, 3, CURDATE(), 1, 'KUNING', 'Demam tinggi sudah 3 hari disertai pusing dan mual.', 'MENUNGGU'),
	(2, 2, 1, 5, CURDATE(), 1, 'HIJAU', 'Telinga kanan berdenging dan pusing berputar saat bangun tidur.', 'MENUNGGU'),
	(3, 3, 1, 6, CURDATE(), 1, 'HIJAU', 'Gatal-gatal serta timbul bintik merah di area leher dan tangan', 'MENUNGGU'),
	(4, 4, 1, 7, CURDATE(), 1, 'KUNING', 'Sakit Kepala kunang kunang', 'MENUNGGU'),
	(5, 5, 1, 7, CURDATE(), 2, 'HIJAU', 'Demam ringan dan pegal-pegal sejak kemarin', 'MENUNGGU'),
	(6, 6, 1, 7, CURDATE(), 3, 'HIJAU', 'Batuk kering berturut-turut lebih dari 3 hari', 'MENUNGGU'),
	(7, 7, 1, 3, CURDATE(), 2, 'KUNING', 'Nyeri lambung / perih ulu hati setelah makan pedas', 'MENUNGGU'),
	(8, 8, 1, 7, CURDATE(), 4, 'KUNING', 'Pusing berputar dan lemas saat berdiri mendadak', 'MENUNGGU'),
	(9, 9, 1, 5, CURDATE(), 2, 'HIJAU', 'Sakit tenggorokan dan sulit menelan makanan', 'MENUNGGU'),
	(10, 10, 1, 6, CURDATE(), 2, 'HIJAU', 'Gatal-gatal di area lengan dan leher', 'MENUNGGU'),
	(11, 11, 1, 7, CURDATE(), 5, 'HIJAU', 'Pemeriksaan rutin tekanan darah dan kontrol kesehatan', 'MENUNGGU'),
	(12, 12, 1, 7, CURDATE(), 6, 'HIJAU', 'Flu berat, hidung tersumbat disertai bersin-bersin', 'MENUNGGU'),
	(13, 13, 1, 7, CURDATE(), 7, 'HIJAU', 'Nyeri pada persendian lutut saat berjalan jauh', 'MENUNGGU'),
	(14, 14, 1, 8, CURDATE(), 1, 'KUNING', 'Sakit gigi geraham bagian bawah sebelah kanan', 'MENUNGGU'),
	(15, 15, 1, 7, CURDATE(), 8, 'HIJAU', 'Mual dan muntah ringan sejak tadi pagi', 'MENUNGGU'),
	(16, 16, 1, 7, CURDATE(), 9, 'HIJAU', 'Mata merah dan berair karena iritasi debu', 'MENUNGGU'),
	(17, 17, 1, 7, CURDATE(), 10, 'HIJAU', 'Pegal linu di punggung belakang setelah bekerja berat', 'MENUNGGU'),
	(18, 18, 1, 7, CURDATE(), 11, 'MERAH', 'Nyeri dada ringan bila bernapas dalam-dalam', 'MENUNGGU'),
	(19, 19, 1, 7, CURDATE(), 12, 'KUNING', 'Sensasi terbakar di dada setelah tidur terlentang', 'MENUNGGU'),
	(20, 20, 1, 8, CURDATE(), 2, 'HIJAU', 'Sariawan meluas di area lidah dan gusi', 'MENUNGGU'),
	(21, 21, 1, 3, CURDATE(), 3, 'MERAH', 'Sesak napas ringan setelah beraktivitas fisik', 'MENUNGGU'),
	(22, 22, 1, 6, CURDATE(), 3, 'HIJAU', 'Bintik merah disertai rasa gatal di punggung', 'MENUNGGU'),
	(23, 23, 1, 7, CURDATE(), 13, 'KUNING', 'Sakit kepala sebelah (migrain) bagian kiri', 'MENUNGGU'),
	(24, 24, 1, 7, CURDATE(), 14, 'HIJAU', 'Nyeri tenggorokan dan badan meriang', 'MENUNGGU'),
	(25, 25, 1, 5, CURDATE(), 3, 'HIJAU', 'Telinga terasa tersumbat dan agak nyeri', 'MENUNGGU'),
	(26, 26, 1, 7, CURDATE(), 15, 'HIJAU', 'Pemeriksaan lab darah rutin bulanan', 'MENUNGGU'),
	(27, 27, 1, 7, CURDATE(), 16, 'KUNING', 'Diare 3 kali sejak subuh tadi', 'MENUNGGU'),
	(28, 28, 1, 7, CURDATE(), 17, 'HIJAU', 'Kram otot pada kaki sebelah kanan', 'MENUNGGU'),
	(29, 29, 1, 7, CURDATE(), 18, 'HIJAU', 'Kulit kering dan bersisik di area wajah', 'MENUNGGU'),
	(30, 30, 1, 7, CURDATE(), 19, 'HIJAU', 'Insomnia dan sering merasa cemas saat malam', 'MENUNGGU'),
	(31, 31, 1, 3, CURDATE(), 4, 'HIJAU', 'Nyeri dada ringan saat kelelahan', 'MENUNGGU'),
	(32, 32, 9, 4, CURDATE(), 1, 'HIJAU', 'Mual dan muntah disertai nyeri perut', 'MENUNGGU'),
	(33, 33, 2, 5, CURDATE(), 4, 'KUNING', 'Sesak napas berat sejak 2 jam lalu', 'MENUNGGU'),
	(34, 34, 11, 6, CURDATE(), 4, 'HIJAU', 'Batuk pilek disertai demam', 'MENUNGGU'),
	(35, 35, 10, 7, CURDATE(), 20, 'HIJAU', 'Demam tinggi 39C', 'MENUNGGU'),
	(36, 36, 1, 8, CURDATE(), 3, 'HIJAU', 'Ruam merah gatal di kulit', 'MENUNGGU'),
	(37, 37, 11, 12, CURDATE(), 1, 'KUNING', 'Telinga keluar cairan dan nyeri', 'MENUNGGU'),
	(38, 38, 11, 13, CURDATE(), 1, 'HIJAU', 'Tenggorokan gatal dan suara serak', 'MENUNGGU'),
	(39, 39, 11, 14, CURDATE(), 1, 'HIJAU', 'Jerawat parah di area wajah', 'MENUNGGU'),
	(40, 40, 10, 15, CURDATE(), 1, 'HIJAU', 'Alergi makanan laut disertai bengkak', 'MENUNGGU'),
	(41, 41, 1, 16, CURDATE(), 1, 'KUNING', 'Pusing dan badan terasa lemas', 'MENUNGGU'),
	(42, 42, 2, 17, CURDATE(), 1, 'KUNING', 'Kontrol rutin kesehatan harian', 'MENUNGGU'),
	(43, 43, 9, 18, CURDATE(), 1, 'HIJAU', 'Demam 3 hari naik turun', 'MENUNGGU'),
	(44, 44, 2, 19, CURDATE(), 1, 'MERAH', 'Sakit gigi melilit hingga ke kepala', 'MENUNGGU'),
	(45, 45, 1, 20, CURDATE(), 1, 'HIJAU', 'Gusi berdarah saat sikat gigi', 'MENUNGGU'),
	(46, 46, 10, 21, CURDATE(), 1, 'HIJAU', 'Nyeri dada menjalar ke lengan kiri', 'MENUNGGU'),
	(47, 47, 2, 22, CURDATE(), 1, 'HIJAU', 'Jantung berdebar cepat saat istirahat', 'MENUNGGU'),
	(48, 48, 9, 23, CURDATE(), 1, 'HIJAU', 'Pemeriksaan rutin tekanan darah', 'MENUNGGU'),
	(49, 49, 11, 24, CURDATE(), 1, 'HIJAU', 'Migrain parah sebelah kanan', 'MENUNGGU'),
	(50, 50, 2, 25, CURDATE(), 1, 'HIJAU', 'Kesemutan di jari tangan dan kaki', 'MENUNGGU'),
	(51, 51, 10, 3, CURDATE(), 5, 'KUNING', 'Kejang berulang singkat', 'MENUNGGU'),
	(52, 52, 9, 4, CURDATE(), 2, 'HIJAU', 'Mata merah dan gatal iritasi debu', 'MENUNGGU'),
	(53, 53, 9, 5, CURDATE(), 5, 'HIJAU', 'Penglihatan mendadak kabur', 'MENUNGGU'),
	(54, 54, 11, 6, CURDATE(), 5, 'HIJAU', 'Pemeriksaan kehamilan rutin bulanan', 'MENUNGGU'),
	(55, 55, 11, 7, CURDATE(), 21, 'HIJAU', 'Nyeri perut bawah saat haid', 'MENUNGGU'),
	(56, 56, 10, 8, CURDATE(), 4, 'KUNING', 'Pendarahan melimpah saat hamil muda', 'MENUNGGU'),
	(57, 57, 10, 12, CURDATE(), 2, 'KUNING', 'Nyeri pergelangan kaki setelah terkilir', 'MENUNGGU'),
	(58, 58, 9, 13, CURDATE(), 2, 'HIJAU', 'Nyeri lutut saat menaiki tangga', 'MENUNGGU'),
	(59, 59, 10, 14, CURDATE(), 2, 'HIJAU', 'Batuk berdarah disertai sesak napas', 'MENUNGGU'),
	(60, 60, 10, 15, CURDATE(), 2, 'HIJAU', 'Batuk menetap lebih dari 2 minggu', 'MENUNGGU'),
	(61, 61, 1, 16, CURDATE(), 2, 'HIJAU', 'Nyeri pinggang saat buang air kecil', 'MENUNGGU'),
	(62, 62, 9, 17, CURDATE(), 2, 'HIJAU', 'Urin berwarna agak keruh', 'MENUNGGU'),
	(63, 63, 11, 18, CURDATE(), 2, 'HIJAU', 'Kekakuan otot bahu pasca cedera', 'MENUNGGU'),
	(64, 64, 2, 19, CURDATE(), 2, 'HIJAU', 'Latihan fisioterapi pasca operasi', 'MENUNGGU'),
	(65, 65, 11, 20, CURDATE(), 2, 'MERAH', 'Konsultasi pre-operasi kecil', 'MENUNGGU'),
	(66, 66, 11, 21, CURDATE(), 2, 'KUNING', 'Jadwal rontgen dada rutin', 'MENUNGGU'),
	(67, 67, 2, 22, CURDATE(), 2, 'MERAH', 'Pemeriksaan kolesterol dan gula darah', 'MENUNGGU'),
	(68, 68, 11, 23, CURDATE(), 2, 'HIJAU', 'Nyeri leher hebat tidak bisa digerakkan', 'MENUNGGU'),
	(69, 69, 10, 24, CURDATE(), 2, 'KUNING', 'Pengambilan sampel patologi kulit', 'MENUNGGU'),
	(70, 70, 11, 25, CURDATE(), 2, 'HIJAU', 'Pemeriksaan kebugaran fisik', 'MENUNGGU'),
	(71, 71, 9, 3, CURDATE(), 6, 'MERAH', 'Nyeri ulu hati ringan', 'MENUNGGU'),
	(72, 72, 10, 4, CURDATE(), 3, 'KUNING', 'Anak lemas dan sulit makan', 'MENUNGGU'),
	(73, 73, 1, 5, CURDATE(), 6, 'HIJAU', 'Telinga terasa penuh dan kurang mendengar', 'MENUNGGU'),
	(74, 74, 11, 6, CURDATE(), 6, 'MERAH', 'Bintik berair di sekujur tubuh', 'MENUNGGU'),
	(75, 75, 9, 7, CURDATE(), 22, 'HIJAU', 'Flu berat dan hidung tersumbat', 'MENUNGGU'),
	(76, 76, 2, 8, CURDATE(), 5, 'HIJAU', 'Pembersihan karang gigi', 'MENUNGGU'),
	(77, 77, 2, 12, CURDATE(), 3, 'KUNING', 'Nyeri dada tembus ke punggung', 'MENUNGGU'),
	(78, 78, 10, 13, CURDATE(), 3, 'HIJAU', 'Kebas di telapak kaki kanan', 'MENUNGGU'),
	(79, 79, 2, 14, CURDATE(), 3, 'HIJAU', 'Mata berair dan terasa mengganjal', 'MENUNGGU'),
	(80, 80, 10, 15, CURDATE(), 3, 'HIJAU', 'Konsultasi KB dan kesehatan reproduksi', 'MENUNGGU'),
	(81, 81, 10, 16, CURDATE(), 3, 'KUNING', 'Patah tulang jari manis tangan', 'MENUNGGU'),
	(82, 82, 11, 17, CURDATE(), 3, 'HIJAU', 'Nyeri saat bernapas dalam', 'MENUNGGU'),
	(83, 83, 2, 18, CURDATE(), 3, 'HIJAU', 'Tidak bisa buang air kecil sejak kemarin', 'MENUNGGU'),
	(84, 84, 9, 19, CURDATE(), 3, 'HIJAU', 'Nyeri sendi siku pasca olahraga', 'MENUNGGU'),
	(85, 85, 11, 20, CURDATE(), 3, 'KUNING', 'Konsultasi manajemen nyeri kronis', 'MENUNGGU'),
	(86, 86, 9, 21, CURDATE(), 3, 'HIJAU', 'Pemeriksaan USG Abdomen', 'MENUNGGU'),
	(87, 87, 10, 22, CURDATE(), 3, 'HIJAU', 'Cek laboratorium asam urat', 'MENUNGGU'),
	(88, 88, 1, 23, CURDATE(), 3, 'KUNING', 'Pusing hebat serasa berputar', 'MENUNGGU'),
	(89, 89, 1, 24, CURDATE(), 3, 'KUNING', 'Pemeriksaan sampel benjolan', 'MENUNGGU'),
	(90, 90, 11, 25, CURDATE(), 3, 'HIJAU', 'Kram otot paha saat berlari', 'MENUNGGU'),
	(91, 91, 11, 3, CURDATE(), 7, 'KUNING', 'Diare 4 kali sehari disertai lemas', 'MENUNGGU'),
	(92, 92, 9, 4, CURDATE(), 4, 'HIJAU', 'Sensasi terbakar ringan di dada', 'MENUNGGU'),
	(93, 93, 10, 5, CURDATE(), 7, 'HIJAU', 'Sariawan meluas pada gusi', 'MENUNGGU'),
	(94, 94, 1, 6, CURDATE(), 7, 'MERAH', 'Pegal-pegal seluruh badan', 'MENUNGGU'),
	(95, 95, 9, 7, CURDATE(), 23, 'MERAH', 'Sakit kepala tidak tertahankan', 'MENUNGGU'),
	(96, 96, 2, 8, CURDATE(), 6, 'HIJAU', 'Mata terasa perih dan kering', 'MENUNGGU'),
	(97, 97, 1, 12, CURDATE(), 4, 'KUNING', 'Batuk berkepanjangan 4 hari', 'MENUNGGU'),
	(98, 98, 9, 13, CURDATE(), 4, 'KUNING', 'Nyeri pada persendian tangan', 'MENUNGGU'),
	(99, 99, 11, 14, CURDATE(), 4, 'KUNING', 'Insomnia dan sulit tidur malam', 'MENUNGGU'),
	(100, 100, 2, 15, CURDATE(), 4, 'HIJAU', 'Nyeri lambung berulang', 'MENUNGGU');

-- Dumping structure for table db_klinik2.detail_resep
DROP TABLE IF EXISTS `detail_resep`;
CREATE TABLE IF NOT EXISTS `detail_resep` (
  `detail_resep_id` int NOT NULL AUTO_INCREMENT,
  `rekam_medis_id` int NOT NULL,
  `obat_id` int NOT NULL,
  `jumlah` int NOT NULL,
  `dosis` varchar(50) NOT NULL,
  PRIMARY KEY (`detail_resep_id`),
  UNIQUE KEY `uq_resep_obat` (`rekam_medis_id`,`obat_id`),
  KEY `obat_id` (`obat_id`),
  CONSTRAINT `detail_resep_ibfk_1` FOREIGN KEY (`rekam_medis_id`) REFERENCES `rekam_medis` (`rekam_medis_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `detail_resep_ibfk_2` FOREIGN KEY (`obat_id`) REFERENCES `obat` (`obat_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping structure for table db_klinik2.dokter
DROP TABLE IF EXISTS `dokter`;
CREATE TABLE IF NOT EXISTS `dokter` (
  `pegawai_id` int NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `password_hash` varchar(255) DEFAULT NULL,
  `no_sip` varchar(50) NOT NULL,
  `spesialisasi` varchar(100) NOT NULL,
  PRIMARY KEY (`pegawai_id`),
  UNIQUE KEY `no_sip` (`no_sip`),
  UNIQUE KEY `username` (`username`),
  CONSTRAINT `dokter_ibfk_1` FOREIGN KEY (`pegawai_id`) REFERENCES `pegawai` (`pegawai_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table db_klinik2.dokter
INSERT INTO `dokter` (`pegawai_id`, `username`, `password_hash`, `no_sip`, `spesialisasi`) VALUES
	(3, 'dokter1', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SIP/2023/1001', 'Spesialis Penyakit Dalam'),
	(4, 'dokter2', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SIP/2023/1002', 'Spesialis Anak'),
	(5, 'dokter3', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SIP/2023/1003', 'Spesialis THT'),
	(6, 'dokter4', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SIP/2023/1004', 'Spesialis Kulit dan Kelamin'),
	(7, 'dokter5', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SIP/2023/1005', 'Dokter Umum'),
	(8, 'dokter6', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SIP/2023/1006', 'Spesialis Gigi'),
	(12, 'dokter7', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SIP/2023/1007', 'Spesialis Jantung dan Pembuluh Darah'),
	(13, 'dokter8', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SIP/2023/1008', 'Spesialis Saraf'),
	(14, 'dokter9', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SIP/2023/1009', 'Spesialis Mata'),
	(15, 'dokter10', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SIP/2023/1010', 'Spesialis Obstetri dan Ginekologi'),
	(16, 'dokter11', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SIP/2023/1011', 'Spesialis Bedah Tulang (Ortopedi)'),
	(17, 'dokter12', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SIP/2023/1012', 'Spesialis Paru'),
	(18, 'dokter13', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SIP/2023/1013', 'Spesialis Urologi'),
	(19, 'dokter14', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SIP/2023/1014', 'Spesialis Kedokteran Fisik dan Rehabilitasi'),
	(20, 'dokter15', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SIP/2023/1015', 'Spesialis Anestesiologi'),
	(21, 'dokter16', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SIP/2023/1016', 'Spesialis Radiologi'),
	(22, 'dokter17', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SIP/2023/1017', 'Spesialis Patologi Klinik'),
	(23, 'dokter18', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SIP/2023/1018', 'Spesialis Bedah Saraf'),
	(24, 'dokter19', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SIP/2023/1019', 'Spesialis Patologi Anatomi'),
	(25, 'dokter20', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'SIP/2023/1020', 'Spesialis Kedokteran Olahraga');

-- Dumping structure for table db_klinik2.obat
DROP TABLE IF EXISTS `obat`;
CREATE TABLE IF NOT EXISTS `obat` (
  `obat_id` int NOT NULL AUTO_INCREMENT,
  `nama_obat` varchar(100) NOT NULL,
  `kategori` varchar(50) DEFAULT NULL,
  `stok` int NOT NULL DEFAULT '0',
  `satuan` varchar(20) NOT NULL,
  `tgl_expired` date NOT NULL,
  PRIMARY KEY (`obat_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table db_klinik2.obat
INSERT INTO `obat` (`obat_id`, `nama_obat`, `kategori`, `stok`, `satuan`, `tgl_expired`) VALUES
	(1, 'Paracetamol 500mg', 'Analgetik', 78, 'Tablet', '2026-12-31'),
	(2, 'Amoxicillin 500mg', 'Antibiotik', 50, 'Kaplet', '2026-10-15'),
	(3, 'Antasida Doen', 'Maag', 65, 'Tablet', '2027-05-20'),
	(4, 'Ibuprofen 400mg', 'Antiinflamasi', 50, 'Tablet', '2026-08-10'),
	(5, 'CTM 4mg', 'Antihistamin', 120, 'Tablet', '2027-01-01'),
	(6, 'Omeprazole 20mg', 'Maag', 100, 'Kapsul', '2027-03-15'),
	(7, 'Cetirizine 10mg', 'Antihistamin', 150, 'Tablet', '2027-06-30'),
	(8, 'Cefadroxil 500mg', 'Antibiotik', 80, 'Kapsul', '2026-11-20'),
	(9, 'Asam Mefenamat 500mg', 'Analgetik', 120, 'Kaplet', '2027-08-10'),
	(10, 'Metformin 500mg', 'Antidiabetes', 200, 'Tablet', '2027-12-01'),
	(11, 'Amlodipine 5mg', 'Antihipertensi', 180, 'Tablet', '2028-01-15'),
	(12, 'Simvastatin 10mg', 'Antihiperlipidemia', 90, 'Tablet', '2027-04-20'),
	(13, 'Salbutamol 2mg', 'Bronkodilator', 110, 'Tablet', '2026-12-10'),
	(14, 'Neurobion Forte', 'Vitamin', 250, 'Tablet', '2028-05-18'),
	(15, 'Loperamide 2mg', 'Antidiare', 100, 'Tablet', '2027-09-05');

-- Dumping structure for table db_klinik2.pasien
DROP TABLE IF EXISTS `pasien`;
CREATE TABLE IF NOT EXISTS `pasien` (
  `pasien_id` int NOT NULL AUTO_INCREMENT,
  `nik` varchar(16) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `nama_pasien` varchar(100) NOT NULL,
  `tanggal_lahir` date NOT NULL,
  `jenis_kelamin` enum('L','P') NOT NULL,
  `alamat` text,
  `no_hp` varchar(15) DEFAULT NULL,
  `alergi_obat` text,
  PRIMARY KEY (`pasien_id`),
  UNIQUE KEY `nik` (`nik`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table db_klinik2.pasien
INSERT INTO `pasien` (`pasien_id`, `nik`, `password_hash`, `nama_pasien`, `tanggal_lahir`, `jenis_kelamin`, `alamat`, `no_hp`, `alergi_obat`) VALUES
	(1, '3305011205950001', '136661bca0c3e614251414b0e5bad5a1c979f8660c0e94ce3c104b4035ddc690', 'Maya Imamori', '2007-06-05', 'P', 'Jl. Pemuda No. 12, Kebumen', '089762783592', 'Amoxicillin'),
	(2, '3305014508980002', 'd9e1e434e64d5107fc92b430318b73f9cbaf53d5dfea0df27e41604b60a9d09a', 'Aya Emori', '2004-08-26', 'P', 'Jl. Yos Sudarso No. 45, Gombong', '082658461046', '-'),
	(3, '3305012010900003', 'cf80e25ee961e35968b9743840265ce2f48e6f56c19e175fe4adfff0c5e9b004', 'Iori Yagami', '2002-03-20', 'L', 'Jl. Pantai No. 88, Klirong', '0892645184', '-'),
	(4, '3305122894653200', 'e48b969894dd8d9952b9f5ba7a8f8bf72d7c595d19ecf3f7dcc2bf67d67a6609', 'Budi Santoso', '2002-01-23', 'L', 'Jl. Veteran No. 14, Kebumen', '086847310074', '-'),
	(5, '3305011101950005', '38d0d964a99c064c10d24618c76fbb96e0542fa1aae8892951987c833965fa6a', 'Aditya Pratama', '1995-01-11', 'L', 'Jl. Pahlawan No. 05, Kebumen', '081234567005', '-'),
	(6, '3305015202980006', '2fe697a33e7777d7e5fa5ba25a807e8f3036a23416120e25f7d3a7551db47e50', 'Anisa Rahmawati', '1998-02-12', 'P', 'Jl. Pemuda No. 18, Kebumen', '081234567006', 'Aspirin'),
	(7, '3305012303010007', '702d272591682d818d9ac13d08f91728cf9217d769e767f7e25d21e2b176dd7a', 'Bambang Sudibyo', '2001-03-23', 'L', 'Jl. Merdeka No. 42, Pejagoan', '081234567007', '-'),
	(8, '3305016404990008', '0d0ba18a9da30af0986147c0dc4030dfd8ccdbd66a3ec39c635a936d3e0e98d8', 'Citra Dewi', '1999-04-24', 'P', 'Jl. Ahmad Yani No. 09, Gombong', '081234567008', 'Penicillin'),
	(9, '3305011505020009', '9f184da236b970424c2a8d0c5a60bded78d288bc994167edea4f4b27106195b2', 'Deni Setiawan', '2002-05-15', 'L', 'Jl. Gatot Subroto No. 33, Kebumen', '081234567009', '-'),
	(10, '3305015606970010', '5260b23c33e18ceb2cbc5dc9e6e7239d1f60be4e304df2805e7592adfde31032', 'Eka Nurjanah', '1997-06-16', 'P', 'Jl. Tentara Pelajar No. 21, Kebumen', '081234567010', '-'),
	(11, '3305012707000011', '73f0f4d0ffaab03aaf446db5b73decf8b6e166eac5537ed8aefb1f79cfe281e1', 'Fajar Nugroho', '2000-07-27', 'L', 'Jl. Yos Sudarso No. 102, Gombong', '081234567011', 'Sulfa'),
	(12, '3305016808030012', 'c824e957441c8927578f2350935d9c87df33140e581637b71b90889413c682c3', 'Gita Gutawa', '2003-08-28', 'P', 'Jl. Pahlawan No. 77, Kebumen', '081234567012', '-'),
	(13, '3305011909940013', '80481b965d7c60270508eae7002dcf9d93017e0daa23be47f486f8ebcd2ebe8f', 'Hadi Wijaya', '1994-09-19', 'L', 'Jl. Stasiun No. 04, Kutowinangun', '081234567013', '-'),
	(14, '3305015010960014', 'd1a888b00c80d1aa42fb18ca0168777e8cad897af4a345c4934d178457f6fb87', 'Indah Permata', '1996-10-10', 'P', 'Jl. Raya Sruweng No. 15, Sruweng', '081234567014', 'Paracetamol'),
	(15, '3305012111010015', 'bc9a3a8f253f39896cb0368dd16ebfe84cb8543249cd8ddd6a7ebdf467e50e67', 'Joko Susilo', '2001-11-21', 'L', 'Jl. Diponegoro No. 88, Kebumen', '081234567015', '-'),
	(16, '3305016212990016', '789a181d7698a7a935754e86f947006b7ca510085af387c7ac5659139365225f', 'Kartika Sari', '1999-12-22', 'P', 'Jl. Panjaitan No. 11, Pejagoan', '081234567016', '-'),
	(17, '3305011301930017', '8bad94f9652f42034ef5473ec023b5f447d1b6810a52bf1108a435fc57b752d3', 'Lukman Hakim', '1993-01-13', 'L', 'Jl. Kolonel Sugiono No. 03, Kebumen', '081234567017', '-'),
	(18, '3305015402000018', '18da00edcce8f4fe9f588ee1aa89e7d331f81f83ec293fb12f54fff1be410672', 'Mega Utami', '2000-02-14', 'P', 'Jl. Sarbini No. 50, Kebumen', '081234567018', 'Ibuprofen'),
	(19, '3305012503970019', '3dd71e4675f688f1ab797fc507a7c833015452eb241f62b0df941161763941b2', 'Naufal Rizky', '1997-03-25', 'L', 'Jl. Cendrawasih No. 27, Kebumen', '081234567019', '-'),
	(20, '3305016604020020', '00e0e46283d4308fdde3d17d6d05cc4cacda7f789f3d82a68ddaf0eb84edcdc4', 'Olivia Zalianty', '2002-04-26', 'P', 'Jl. Kusuma No. 08, Kebumen', '081234567020', '-'),
	(21, '3305011705960021', 'a4a109ec46ebe81df9ba6a2d0a4eb6b52468eaba14e9371509eadf93d7fb3078', 'Panji Ahmad', '1996-05-17', 'L', 'Jl. Kaswari No. 19, Kebumen', '081234567021', '-'),
	(22, '3305015806010022', '413d4e0b5dacd58f6a0efc4bc1e6378d27b88dc3e1708dadfda6b7735c549d4f', 'Qonita Aulia', '2001-06-18', 'P', 'Jl. Kenanga No. 02, Gombong', '081234567022', 'Amoxicillin'),
	(23, '3305012907980023', '05d6cc04f864635d86f347228a3e5c3c12d8689fc06a5b9c2d4f746beaef263d', 'Rian Hidayat', '1998-07-29', 'L', 'Jl. Mawar No. 44, Kutowinangun', '081234567023', '-'),
	(24, '3305017008040024', '56cdecf05fe61ad01d0fe48eb1bcbbafe2ceade00062ca7938807b38b33bc22a', 'Siti Nurhaliza', '2004-08-30', 'P', 'Jl. Melati No. 12, Klirong', '081234567024', '-'),
	(25, '3305011109950025', '6d660327af6f6b8fa5b2bb0fafa1f3e9a96ef1fb0f9b21547bd08084e69f597f', 'Taufik Hidayat', '1995-09-11', 'L', 'Jl. Anggrek No. 06, Kebumen', '081234567025', '-'),
	(26, '3305015210020026', '83ae279c28359e01128557764e8e4acf5acf6517b0e9e7ada960c66a24812b95', 'Ufa Syahira', '2002-10-12', 'P', 'Jl. Flamboyan No. 31, Pejagoan', '081234567026', '-'),
	(27, '3305012311970027', '16cdfd2273e0201212f5c6e761b4bc59872c01f5d37f80f41ecc8a2bc2473646', 'Vino G Bastian', '1997-11-23', 'L', 'Jl. Rajawali No. 15, Gombong', '081234567027', '-'),
	(28, '3305016412000028', 'd8caae6f927ba7a2a860b67fc2a054e2c2c6fb683836f20092b763fce7d0d033', 'Winda Viska', '2000-12-24', 'P', 'Jl. Garuda No. 07, Kebumen', '081234567028', '-'),
	(29, '3305011501990029', 'ba7bf6f94a979fbb2aade3df42668e17d0d83245f1b5d3150d9a363e36ddf42d', 'Xavier Gunawan', '1999-01-15', 'L', 'Jl. Ahmad Yani No. 55, Kebumen', '081234567029', '-'),
	(30, '3305015602030030', 'ec2a2da3d886539b63c920dd87296b3c69ada41a1492d264f2ca5474f6c3c590', 'Yulia Anggraini', '2003-02-16', 'P', 'Jl. Pemuda No. 89, Kebumen', '081234567030', '-'),
	(31, '3305011101010031', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Agus Setiawan', '1991-01-10', 'L', 'Jl. Merbabu No. 10, Kebumen', '081234567031', '-'),
	(32, '3305015202020032', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Bella Safitri', '1995-02-15', 'P', 'Jl. Sumbing No. 22, Pejagoan', '081234567032', 'Paracetamol'),
	(33, '3305011303030033', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Candra Wijaya', '1988-03-20', 'L', 'Jl. Slamet No. 05, Gombong', '081234567033', '-'),
	(34, '3305015404040034', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Dina Mariya', '2000-04-12', 'P', 'Jl. Sindoro No. 14, Kebumen', '081234567034', 'Amoxicillin'),
	(35, '3305011505050035', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Eko Prasetyo', '1993-05-18', 'L', 'Jl. Lawu No. 30, Sruweng', '081234567035', '-'),
	(36, '3305015606060036', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Fitriani Lestari', '1997-06-25', 'P', 'Jl. Merapi No. 08, Kutowinangun', '081234567036', '-'),
	(37, '3305011707070037', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Gilang Permana', '1985-07-30', 'L', 'Jl. Dieng No. 17, Klirong', '081234567037', 'Penicillin'),
	(38, '3305015808080038', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Hani Novita', '2002-08-08', 'P', 'Jl. Rinjani No. 03, Kebumen', '081234567038', '-'),
	(39, '3305011909090039', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Irfan Hakim', '1990-09-14', 'L', 'Jl. Krakatau No. 12, Kebumen', '081234567039', '-'),
	(40, '3305016010100040', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Julia Rahma', '1998-10-02', 'P', 'Jl. Bromo No. 41, Gombong', '081234567040', 'Aspirin'),
	(41, '3305012111110041', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Kiki Ardiansyah', '1996-11-11', 'L', 'Jl. Semeru No. 19, Pejagoan', '081234567041', '-'),
	(42, '3305016212120042', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Lia Amelia', '2001-12-05', 'P', 'Jl. Tambora No. 27, Kebumen', '081234567042', '-'),
	(43, '3305012301130043', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'M. Maulana', '1987-01-22', 'L', 'Jl. Jayawijaya No. 02, Kutowinangun', '081234567043', 'Sulfa'),
	(44, '3305016402140044', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Nia Ramadhani', '1994-02-17', 'P', 'Jl. Gamalama No. 15, Sruweng', '081234567044', '-'),
	(45, '3305012503150045', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Oky Pratama', '1999-03-29', 'L', 'Jl. Argopuro No. 09, Kebumen', '081234567045', '-'),
	(46, '3305016604160046', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Putri Ayu', '2003-04-04', 'P', 'Jl. Salak No. 33, Kebumen', '081234567046', 'Ibuprofen'),
	(47, '3305012705170047', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Qomaruddin', '1982-05-09', 'L', 'Jl. Patuha No. 11, Gombong', '081234567047', '-'),
	(48, '3305016806180048', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Rina Anggraini', '1992-06-16', 'P', 'Jl. Ciremai No. 20, Klirong', '081234567048', '-'),
	(49, '3305012907190049', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Setyo Budiman', '1989-07-21', 'L', 'Jl. Galunggung No. 04, Kebumen', '081234567049', '-'),
	(50, '3305017008200050', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Tania Putri', '2004-08-13', 'P', 'Jl. Tangkuban No. 50, Pejagoan', '081234567050', '-'),
	(51, '3305013109210051', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Umar Faruq', '1991-09-03', 'L', 'Jl. Kelud No. 18, Kebumen', '081234567051', 'Amoxicillin'),
	(52, '3305017210220052', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Vina Panduwinata', '1997-10-28', 'P', 'Jl. Wilis No. 07, Kutowinangun', '081234567052', '-'),
	(53, '3305011311230053', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Wahyu Hidayat', '1986-11-19', 'L', 'Jl. Kawi No. 25, Gombong', '081234567053', '-'),
	(54, '3305015412240054', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Xena Warrior', '2000-12-31', 'P', 'Jl. Raung No. 13, Sruweng', '081234567054', '-'),
	(55, '3305011501250055', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Yusuf Mansur', '1995-01-08', 'L', 'Jl. Ijen No. 36, Kebumen', '081234567055', 'Penicillin'),
	(56, '3305015602260056', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Zahra Nabila', '2002-02-14', 'P', 'Jl. Agung No. 01, Pejagoan', '081234567056', '-'),
	(57, '3305011703270057', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Ahmad Dani', '1984-03-24', 'L', 'Jl. Marapi No. 29, Kebumen', '081234567057', '-'),
	(58, '3305015804280058', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Bunga Citra', '1999-04-10', 'P', 'Jl. Kerinci No. 44, Gombong', '081234567058', '-'),
	(59, '3305011905290059', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Cak Lontong', '1980-05-15', 'L', 'Jl. Dempo No. 16, Sruweng', '081234567059', 'Aspirin'),
	(60, '3305016006300060', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Dewi Sandra', '1993-06-07', 'P', 'Jl. Sinabung No. 06, Kebumen', '081234567060', '-'),
	(61, '3305012107310061', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Erlangga Putra', '1996-07-19', 'L', 'Jl. Soputan No. 28, Klirong', '081234567061', '-'),
	(62, '3305016208320062', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Fani Febriana', '2001-08-23', 'P', 'Jl. Gamkonora No. 31, Pejagoan', '081234567062', '-'),
	(63, '3305012309330063', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Gatot Kaca', '1983-09-01', 'L', 'Jl. Lokon No. 09, Kutowinangun', '081234567063', 'Paracetamol'),
	(64, '3305016410340064', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Hilda Fitria', '1998-10-14', 'P', 'Jl. Dukono No. 21, Kebumen', '081234567064', '-'),
	(65, '3305012511350065', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Iwan Fals', '1987-11-26', 'L', 'Jl. Karangetang No. 35, Gombong', '081234567065', '-'),
	(66, '3305016612360066', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Jessica Mila', '2003-12-09', 'P', 'Jl. Ibu No. 12, Kebumen', '081234567066', '-'),
	(67, '3305012701370067', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Kunto Aji', '1992-01-31', 'L', 'Jl. Gamalama No. 04, Sruweng', '081234567067', 'Ibuprofen'),
	(68, '3305016802380068', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Luna Maya', '1995-02-20', 'P', 'Jl. Banda No. 18, Kebumen', '081234567068', '-'),
	(69, '3305012903390069', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'M. Natsir', '1981-03-12', 'L', 'Jl. Serayu No. 23, Pejagoan', '081234567069', '-'),
	(70, '3305017004400070', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Nabila Syakieb', '1994-04-05', 'P', 'Jl. Progo No. 02, Kutowinangun', '081234567070', '-'),
	(71, '3305013105410071', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Opick Tombo', '1986-05-22', 'L', 'Jl. Opak No. 15, Kebumen', '081234567071', 'Penicillin'),
	(72, '3305017206420072', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Pevita Pearce', '2001-06-17', 'P', 'Jl. Elo No. 30, Gombong', '081234567072', '-'),
	(73, '3305013307430073', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Raffi Ahmad', '1990-07-28', 'L', 'Jl. Bengawan No. 08, Klirong', '081234567073', '-'),
	(74, '3305017408440074', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Raisa Andriana', '1993-08-11', 'P', 'Jl. Solo No. 42, Kebumen', '081234567074', '-'),
	(75, '3305013509450075', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Sule Sutisna', '1983-09-09', 'L', 'Jl. Brantas No. 19, Pejagoan', '081234567075', 'Amoxicillin'),
	(76, '3305017610460076', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Titi Kamal', '1996-10-23', 'P', 'Jl. Citanduy No. 05, Kebumen', '081234567076', '-'),
	(77, '3305013711470077', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Uus Rizky', '1992-11-04', 'L', 'Jl. Cisadane No. 27, Sruweng', '081234567077', '-'),
	(78, '3305017812480078', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Vanesha Prescilla', '2002-12-18', 'P', 'Jl. Citarum No. 11, Gombong', '081234567078', '-'),
	(79, '3305013901490079', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Wendi Cagur', '1988-01-16', 'L', 'Jl. Barito No. 34, Kebumen', '081234567079', 'Sulfa'),
	(80, '3305018002500080', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Yuni Shara', '1985-02-02', 'P', 'Jl. Kapuas No. 03, Kutowinangun', '081234567080', '-'),
	(81, '3305014103510081', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Zaskia Gotik', '1997-03-27', 'P', 'Jl. Mahakam No. 17, Klirong', '081234567081', '-'),
	(82, '3305018204520082', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Anang Hermansyah', '1980-04-14', 'L', 'Jl. Musi No. 22, Kebumen', '081234567082', '-'),
	(83, '3305014305530083', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Baim Wong', '1989-05-01', 'L', 'Jl. Batanghari No. 06, Pejagoan', '081234567083', 'Paracetamol'),
	(84, '3305018406540084', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Cinta Laura', '1999-06-20', 'P', 'Jl. Indragiri No. 38, Gombong', '081234567084', '-'),
	(85, '3305014507550085', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Deddy Corbuzier', '1984-07-07', 'L', 'Jl. Kampar No. 14, Sruweng', '081234567085', '-'),
	(86, '3305018608560086', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Enzy Storia', '2000-08-10', 'P', 'Jl. Siak No. 29, Kebumen', '081234567086', '-'),
	(87, '3305014709570087', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Fiersa Besari', '1991-09-25', 'L', 'Jl. Asahan No. 01, Kutowinangun', '081234567087', 'Aspirin'),
	(88, '3305018810580088', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Gisella Anastasia', '1996-10-15', 'P', 'Jl. Memberamo No. 12, Kebumen', '081234567088', '-'),
	(89, '3305014911590089', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Hesti Purwadinata', '1987-11-03', 'P', 'Jl. Digul No. 26, Klirong', '081234567089', '-'),
	(90, '3305019012600090', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Indra Bekti', '1983-12-29', 'L', 'Jl. Fly No. 07, Gombong', '081234567090', '-'),
	(91, '3305011101610091', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Jefri Nichol', '2001-01-15', 'L', 'Jl. Kahayan No. 10, Kebumen', '081234567091', 'Penicillin'),
	(92, '3305015202620092', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Keanu Angelo', '1998-02-22', 'L', 'Jl. Mahakam No. 33, Pejagoan', '081234567092', '-'),
	(93, '3305011303630093', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Livy Renata', '2004-03-08', 'P', 'Jl. Martapura No. 04, Kebumen', '081234567093', '-'),
	(94, '3305015404640094', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Mamat Alkatiri', '1993-04-19', 'L', 'Jl. Barito No. 20, Sruweng', '081234567094', '-'),
	(95, '3305011505650095', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Najwa Shihab', '1982-05-11', 'P', 'Jl. Mentaya No. 15, Gombong', '081234567095', 'Ibuprofen'),
	(96, '3305015606660096', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Onadio Leonardo', '1990-06-03', 'L', 'Jl. Kapuas No. 28, Kebumen', '081234567096', '-'),
	(97, '3305011707670097', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Prilly Latuconsina', '2002-07-26', 'P', 'Jl. Sampit No. 02, Kutowinangun', '081234567097', '-'),
	(98, '3305015808680098', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Raditya Dika', '1989-08-17', 'L', 'Jl. Pawan No. 19, Pejagoan', '081234567098', '-'),
	(99, '3305011909690099', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Soimah Pancawati', '1986-09-05', 'P', 'Jl. Landak No. 11, Kebumen', '081234567099', 'Amoxicillin'),
	(100, '3305016010700100', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Tretan Muslim', '1995-10-21', 'L', 'Jl. Sambas No. 08, Klirong', '081234567100', '-');

-- Dumping structure for table db_klinik2.pegawai
DROP TABLE IF EXISTS `pegawai`;
CREATE TABLE IF NOT EXISTS `pegawai` (
  `pegawai_id` int NOT NULL AUTO_INCREMENT,
  `nama_lengkap` varchar(100) NOT NULL,
  `no_hp` varchar(15) DEFAULT NULL,
  `jenis_pegawai` enum('ADMIN','DOKTER') NOT NULL,
  PRIMARY KEY (`pegawai_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table db_klinik2.pegawai
INSERT INTO `pegawai` (`pegawai_id`, `nama_lengkap`, `no_hp`, `jenis_pegawai`) VALUES
	(1, 'Budi Santoso, S.Kom', '081234567890', 'ADMIN'),
	(2, 'Siti Rahmawati, A.Md', '081234567891', 'ADMIN'),
	(3, 'dr. Ahmad Dahlan, Sp.PD', '081234567892', 'DOKTER'),
	(4, 'dr. Citra Lestari, Sp.A', '081234567893', 'DOKTER'),
	(5, 'dr. Eka Wijaya, Sp.THT-KL', '081234567894', 'DOKTER'),
	(6, 'dr. Maya Putri, Sp.DVE', '081234567895', 'DOKTER'),
	(7, 'dr. Hendra Kurniawan, Sp.B', '081234567896', 'DOKTER'),
	(8, 'dr. Sarah Amalia, Sp.KG', '081234567897', 'DOKTER'),
	(9, 'Dwi Cahyono, A.Md', '081234567898', 'ADMIN'),
	(10, 'Eka Prasetya, S.Kom', '081234567899', 'ADMIN'),
	(11, 'Fitri Handayani, S.AB', '081234567900', 'ADMIN'),
	(12, 'dr. Gilang Ramadhan, Sp.JP', '081234567901', 'DOKTER'),
	(13, 'dr. Hana Pertiwi, Sp.N', '081234567902', 'DOKTER'),
	(14, 'dr. Indra Wijaya, Sp.M', '081234567903', 'DOKTER'),
	(15, 'dr. Julia Perez, Sp.OG', '081234567904', 'DOKTER'),
	(16, 'dr. Kevin Sanjaya, Sp.OT', '081234567905', 'DOKTER'),
	(17, 'dr. Larasati Putri, Sp.P', '081234567906', 'DOKTER'),
	(18, 'dr. Muhammad Rizky, Sp.U', '081234567907', 'DOKTER'),
	(19, 'dr. Nadia Saphira, Sp.KFR', '081234567908', 'DOKTER'),
	(20, 'dr. Oscar Lawalata, Sp.An', '081234567909', 'DOKTER'),
	(21, 'dr. Putri Marino, Sp.Rad', '081234567910', 'DOKTER'),
	(22, 'dr. Qory Sandioriva, Sp.PK', '081234567911', 'DOKTER'),
	(23, 'dr. Reza Rahadian, Sp.BS', '081234567912', 'DOKTER'),
	(24, 'dr. Siska Kohl, Sp.PA', '081234567913', 'DOKTER'),
	(25, 'dr. Tora Sudiro, Sp.JP', '081234567914', 'DOKTER');

-- Dumping structure for table db_klinik2.rekam_medis
DROP TABLE IF EXISTS `rekam_medis`;
CREATE TABLE IF NOT EXISTS `rekam_medis` (
  `rekam_medis_id` int NOT NULL AUTO_INCREMENT,
  `pasien_id` int NOT NULL,
  `dokter_id` int DEFAULT NULL,
  `antrean_id` int DEFAULT NULL,
  `tanggal_periksa` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `keluhan` text NOT NULL,
  `diagnosa` text NOT NULL,
  `tindakan` text,
  PRIMARY KEY (`rekam_medis_id`),
  KEY `pasien_id` (`pasien_id`),
  KEY `dokter_id` (`dokter_id`),
  KEY `fk_rm_antrean_konsisten` (`antrean_id`,`pasien_id`,`dokter_id`),
  CONSTRAINT `fk_rm_antrean_konsisten` FOREIGN KEY (`antrean_id`, `pasien_id`, `dokter_id`) REFERENCES `antrean` (`antrean_id`, `pasien_id`, `dokter_id`),
  CONSTRAINT `rekam_medis_ibfk_1` FOREIGN KEY (`pasien_id`) REFERENCES `pasien` (`pasien_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `rekam_medis_ibfk_2` FOREIGN KEY (`dokter_id`) REFERENCES `dokter` (`pegawai_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping structure for trigger db_klinik2.reduce_stok_obat_after_resep
DROP TRIGGER IF EXISTS `reduce_stok_obat_after_resep`;
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `reduce_stok_obat_after_resep` AFTER INSERT ON `detail_resep` FOR EACH ROW BEGIN
    UPDATE obat 
    SET stok = GREATEST(0, stok - NEW.jumlah)
    WHERE obat_id = NEW.obat_id;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- ============================================================================
-- TAMBAHAN DATA MASSAL (Default Thin: 1.000 pasien + 1.000 antrean)
-- ============================================================================
USE `db_klinik2`;

SET @tanggal_antrean = CURDATE();
SET @pwd_hash = '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92';

DROP TABLE IF EXISTS `tmp_digit`;
DROP TABLE IF EXISTS `tmp_angka`;
DROP TABLE IF EXISTS `tmp_seed`;
DROP TABLE IF EXISTS `tmp_ref_keluhan`;
DROP TABLE IF EXISTS `tmp_ref_alergi`;

CREATE TABLE `tmp_digit` (`d` INT NOT NULL PRIMARY KEY);
INSERT INTO `tmp_digit` (`d`) VALUES (0),(1),(2),(3),(4),(5),(6),(7),(8),(9);

-- angka 1 .. 100.000
CREATE TABLE `tmp_angka` (`n` INT NOT NULL PRIMARY KEY);
INSERT INTO `tmp_angka` (`n`)
SELECT a.d + b.d*10 + c.d*100 + e.d*1000 + f.d*10000 + 1
FROM `tmp_digit` a, `tmp_digit` b, `tmp_digit` c, `tmp_digit` e, `tmp_digit` f;

CREATE TABLE `tmp_seed` (
  `n`  INT NOT NULL PRIMARY KEY,
  `k`  INT NOT NULL,
  `r1` DOUBLE NOT NULL, `r2` DOUBLE NOT NULL, `r3` DOUBLE NOT NULL, `r4` DOUBLE NOT NULL,
  `r5` DOUBLE NOT NULL, `r6` DOUBLE NOT NULL, `r7` DOUBLE NOT NULL, `r8` DOUBLE NOT NULL,
  `r9` DOUBLE NOT NULL, `r10` DOUBLE NOT NULL, `r11` DOUBLE NOT NULL
) ENGINE=InnoDB;

CREATE TABLE `tmp_ref_keluhan` (
  `id`        INT NOT NULL PRIMARY KEY,
  `dokter_id` INT NOT NULL,
  `urgensi`   ENUM('MERAH','KUNING','HIJAU') NOT NULL,
  `keluhan`   VARCHAR(255) NOT NULL,
  `diagnosa`  VARCHAR(255) NOT NULL,
  `tindakan`  VARCHAR(255) NOT NULL,
  `obat1` INT NULL, `jumlah1` INT NULL, `dosis1` VARCHAR(50) NULL,
  `obat2` INT NULL, `jumlah2` INT NULL, `dosis2` VARCHAR(50) NULL,
  UNIQUE KEY `uq_ref_keluhan` (`dokter_id`, `keluhan`)
) ENGINE=InnoDB;

INSERT INTO `tmp_ref_keluhan`
(`id`,`dokter_id`,`urgensi`,`keluhan`,`diagnosa`,`tindakan`,`obat1`,`jumlah1`,`dosis1`,`obat2`,`jumlah2`,`dosis2`) VALUES
(1, 12,'MERAH', 'Nyeri dada menjalar ke lengan kiri disertai keringat dingin', 'Suspek sindrom koroner akut', 'EKG, pemberian oksigen, dan rujuk segera ke IGD', NULL,NULL,NULL, NULL,NULL,NULL),
(2, 12,'HIJAU', 'Kontrol rutin tekanan darah tinggi', 'Hipertensi derajat 1', 'Pengukuran tekanan darah, edukasi diet rendah garam, kontrol ulang 1 bulan', 11,30,'1 x 1 tablet sehari (pagi)', NULL,NULL,NULL),
(3, 17,'MERAH', 'Sesak napas berat disertai batuk berdarah', 'Suspek tuberkulosis paru dengan hemoptisis', 'Pemberian oksigen, foto rontgen thorax, dan rujuk segera ke IGD', NULL,NULL,NULL, NULL,NULL,NULL),
(4, 17,'HIJAU', 'Batuk menetap lebih dari 2 minggu', 'Bronkitis akut berkepanjangan', 'Pemeriksaan paru dan edukasi menghindari paparan asap rokok', 13,15,'3 x 1 tablet sehari', NULL,NULL,NULL),
(5, 13,'MERAH', 'Sakit kepala hebat mendadak disertai kelemahan anggota gerak', 'Suspek stroke', 'Pemeriksaan neurologis, pemantauan tekanan darah, dan rujuk segera ke IGD', NULL,NULL,NULL, NULL,NULL,NULL),
(6, 13,'HIJAU', 'Kesemutan di jari tangan dan kaki', 'Neuropati perifer', 'Pemeriksaan neurologis dan edukasi ergonomi', 14,30,'1 x 1 tablet sehari', NULL,NULL,NULL),
(7, 15,'MERAH', 'Pendarahan hebat saat hamil muda', 'Suspek abortus imminens', 'Observasi, USG kandungan, dan rujuk segera ke IGD kebidanan', NULL,NULL,NULL, NULL,NULL,NULL),
(8, 15,'HIJAU', 'Pemeriksaan kehamilan rutin bulanan', 'Kehamilan normal (antenatal care)', 'Pengukuran tinggi fundus, tekanan darah, dan USG kandungan', NULL,NULL,NULL, NULL,NULL,NULL),
(9, 3,'KUNING', 'Lemas, sering haus, dan sering buang air kecil malam hari', 'Suspek diabetes melitus tipe 2', 'Pemeriksaan gula darah sewaktu dan edukasi pola makan', 10,30,'2 x 1 tablet sehari sesudah makan', NULL,NULL,NULL),
(10, 3,'HIJAU', 'Nyeri ulu hati dan mual setelah makan', 'Dispepsia (gastritis)', 'Edukasi pola makan teratur dan hindari makanan pedas', 6,14,'1 x 1 kapsul sebelum makan pagi', 3,21,'3 x 1 tablet sesudah makan'),
(11, 4,'KUNING', 'Anak demam 3 hari naik turun dan sulit makan', 'Demam (suspek infeksi virus)', 'Pemeriksaan fisik, anjuran banyak minum, dan kompres hangat', 1,6,'3 x 1/2 tablet sehari bila demam', NULL,NULL,NULL),
(12, 4,'HIJAU', 'Imunisasi dan kontrol tumbuh kembang anak', 'Anak sehat (kontrol tumbuh kembang)', 'Penimbangan berat badan, pengukuran tinggi badan, dan imunisasi sesuai jadwal', NULL,NULL,NULL, NULL,NULL,NULL),
(13, 5,'KUNING', 'Telinga keluar cairan disertai nyeri', 'Otitis media akut', 'Pembersihan telinga dan pemeriksaan otoskopi', 8,10,'2 x 1 kapsul sehari selama 5 hari', 1,6,'3 x 1 tablet sehari bila nyeri'),
(14, 5,'HIJAU', 'Hidung tersumbat dan bersin-bersin sejak 3 hari', 'Rinitis alergi', 'Edukasi menghindari pencetus alergi', 7,10,'1 x 1 tablet malam hari', NULL,NULL,NULL),
(15, 7,'KUNING', 'Demam tinggi 3 hari disertai pusing dan mual', 'Suspek demam tifoid', 'Pemeriksaan fisik dan anjuran tes laboratorium darah', 1,9,'3 x 1 tablet sehari bila demam', 6,7,'1 x 1 kapsul sebelum makan pagi'),
(16, 7,'HIJAU', 'Batuk pilek disertai demam ringan', 'Infeksi saluran pernapasan atas (ISPA)', 'Istirahat cukup dan perbanyak minum air putih', 1,6,'3 x 1 tablet sehari bila demam', 5,10,'3 x 1 tablet sehari'),
(17, 8,'KUNING', 'Sakit gigi berlubang disertai gusi bengkak', 'Pulpitis dengan abses gusi', 'Pembersihan dan perawatan saluran akar sementara', 8,10,'2 x 1 kapsul sehari selama 5 hari', 9,10,'3 x 1 kaplet sehari bila nyeri'),
(18, 8,'HIJAU', 'Pembersihan karang gigi', 'Kalkulus gigi (karang gigi)', 'Scaling (pembersihan karang gigi)', NULL,NULL,NULL, NULL,NULL,NULL),
(19, 14,'KUNING', 'Mata merah, perih, dan berair sejak kemarin', 'Konjungtivitis', 'Pemeriksaan mata dan edukasi kebersihan tangan', 7,10,'1 x 1 tablet malam hari', NULL,NULL,NULL),
(20, 14,'HIJAU', 'Mata terasa kering dan lelah setelah menatap layar', 'Sindrom mata kering', 'Edukasi aturan 20-20-20 dan istirahat mata', NULL,NULL,NULL, NULL,NULL,NULL),
(21, 16,'KUNING', 'Nyeri pergelangan kaki setelah terkilir', 'Cedera ligamen pergelangan kaki (sprain)', 'Pembidaian, kompres dingin, dan istirahat', 9,10,'3 x 1 kaplet sehari sesudah makan', NULL,NULL,NULL),
(22, 16,'HIJAU', 'Nyeri lutut saat menaiki tangga', 'Osteoartritis lutut', 'Edukasi pengurangan beban sendi dan latihan penguatan otot paha', 9,10,'3 x 1 kaplet sehari sesudah makan', 14,30,'1 x 1 tablet sehari'),
(23, 18,'KUNING', 'Nyeri saat buang air kecil dan sering ingin berkemih', 'Infeksi saluran kemih', 'Pemeriksaan urinalisis dan anjuran banyak minum air putih', 8,10,'2 x 1 kapsul sehari selama 5 hari', 1,6,'3 x 1 tablet sehari bila nyeri'),
(24, 18,'HIJAU', 'Urin berwarna keruh', 'Dehidrasi ringan', 'Pemeriksaan urinalisis dan anjuran minum air putih minimal 2 liter per hari', NULL,NULL,NULL, NULL,NULL,NULL),
(25, 23,'KUNING', 'Nyeri leher hebat menjalar ke lengan', 'Radikulopati servikal', 'Pemeriksaan neurologis dan anjuran MRI servikal', 9,10,'3 x 1 kaplet sehari sesudah makan', 14,30,'1 x 1 tablet sehari'),
(26, 23,'KUNING', 'Benjolan di kepala setelah benturan', 'Cedera kepala ringan', 'Observasi neurologis dan anjuran CT scan kepala', 1,6,'3 x 1 tablet sehari bila nyeri', NULL,NULL,NULL),
(27, 20,'KUNING', 'Konsultasi persiapan pembiusan sebelum operasi', 'Evaluasi pra-anestesi', 'Penilaian status fisik (ASA), instruksi puasa pra-operasi, dan pemeriksaan penunjang', NULL,NULL,NULL, NULL,NULL,NULL),
(28, 20,'KUNING', 'Konsultasi manajemen nyeri kronis', 'Nyeri kronis', 'Evaluasi skala nyeri dan terapi nyeri terjadwal', 9,10,'3 x 1 kaplet sehari sesudah makan', 14,30,'1 x 1 tablet sehari'),
(29, 6,'HIJAU', 'Gatal-gatal dan bintik merah di area leher dan tangan', 'Dermatitis alergi', 'Edukasi menghindari pencetus alergi', 7,10,'1 x 1 tablet malam hari', NULL,NULL,NULL),
(30, 6,'HIJAU', 'Kulit wajah kering dan bersisik', 'Dermatitis seboroik', 'Edukasi perawatan kulit dan penggunaan pelembap', NULL,NULL,NULL, NULL,NULL,NULL),
(31, 19,'HIJAU', 'Latihan fisioterapi pasca operasi', 'Rehabilitasi pasca operasi', 'Terapi latihan gerak sendi dan penguatan otot', NULL,NULL,NULL, NULL,NULL,NULL),
(32, 19,'HIJAU', 'Kekakuan otot bahu pasca cedera', 'Sindrom nyeri miofasial bahu', 'Fisioterapi hot pack dan latihan peregangan', 9,10,'3 x 1 kaplet sehari sesudah makan', NULL,NULL,NULL),
(33, 21,'HIJAU', 'Jadwal rontgen dada rutin', 'Hasil rontgen thorax dalam batas normal', 'Rontgen thorax PA dan pembacaan hasil', NULL,NULL,NULL, NULL,NULL,NULL),
(34, 21,'HIJAU', 'Pemeriksaan USG abdomen', 'Hasil USG abdomen dalam batas normal', 'USG abdomen dan pembacaan hasil', NULL,NULL,NULL, NULL,NULL,NULL),
(35, 22,'HIJAU', 'Pemeriksaan laboratorium darah rutin', 'Hasil laboratorium rutin dalam batas normal', 'Pengambilan sampel darah vena dan pemeriksaan darah lengkap', NULL,NULL,NULL, NULL,NULL,NULL),
(36, 22,'HIJAU', 'Cek laboratorium asam urat', 'Hiperurisemia ringan', 'Pengambilan sampel darah dan edukasi diet rendah purin', NULL,NULL,NULL, NULL,NULL,NULL),
(37, 24,'HIJAU', 'Pemeriksaan sampel benjolan jinak', 'Lipoma (tumor jinak jaringan lemak)', 'Biopsi jarum halus dan pemeriksaan sitologi', NULL,NULL,NULL, NULL,NULL,NULL),
(38, 24,'HIJAU', 'Pengambilan sampel jaringan kulit', 'Dermatitis kronis (hasil histopatologi)', 'Biopsi kulit dan pengiriman sampel ke laboratorium', NULL,NULL,NULL, NULL,NULL,NULL),
(39, 25,'HIJAU', 'Pemeriksaan kebugaran fisik', 'Kebugaran fisik baik', 'Pengukuran tekanan darah, denyut nadi, dan uji ketahanan fisik', NULL,NULL,NULL, NULL,NULL,NULL),
(40, 25,'HIJAU', 'Kram otot paha saat berlari', 'Kram otot (muscle cramp)', 'Peregangan, pijat ringan, dan edukasi hidrasi', 14,30,'1 x 1 tablet sehari', NULL,NULL,NULL);

CREATE TABLE `tmp_ref_alergi` (
  `alergen` VARCHAR(100) NOT NULL,
  `obat_id` INT NOT NULL,
  PRIMARY KEY (`alergen`, `obat_id`)
) ENGINE=InnoDB;
INSERT INTO `tmp_ref_alergi` (`alergen`, `obat_id`) VALUES
('Paracetamol', 1),
('Ibuprofen', 4), ('Ibuprofen', 9),
('Aspirin', 4),   ('Aspirin', 9),
('Amoxicillin', 2), ('Amoxicillin', 8),
('Penicillin', 2),  ('Penicillin', 8);

DROP PROCEDURE IF EXISTS `sp_isi_data_massal`;
DELIMITER $$
CREATE PROCEDURE `sp_isi_data_massal`()
BEGIN
  DECLARE v_batch        INT DEFAULT 0;
  DECLARE v_jml_batch    INT DEFAULT 1;
  DECLARE v_ukuran       INT DEFAULT 1000;
  DECLARE v_lo           INT;
  DECLARE v_hi           INT;
  DECLARE v_jml_keluhan  INT;

  SELECT COUNT(*) INTO v_jml_keluhan FROM `tmp_ref_keluhan`;

  WHILE v_batch < v_jml_batch DO
    SET v_lo = v_batch * v_ukuran + 1;
    SET v_hi = (v_batch + 1) * v_ukuran;

    -- (a) bilangan acak hanya sebanyak v_ukuran per batch
    INSERT INTO `tmp_seed` (`n`,`k`,`r1`,`r2`,`r3`,`r4`,`r5`,`r6`,`r7`,`r8`,`r9`,`r10`,`r11`)
    SELECT v_batch * v_ukuran + a.n,
           1 + FLOOR(RAND() * v_jml_keluhan),
           RAND(), RAND(), RAND(), RAND(), RAND(), RAND(),
           RAND(), RAND(), RAND(), RAND(), RAND()
    FROM `tmp_angka` a
    WHERE a.n <= v_ukuran;

    -- (b) pasien
    INSERT INTO `pasien`
      (`pasien_id`,`nik`,`password_hash`,`nama_pasien`,`tanggal_lahir`,`jenis_kelamin`,`alamat`,`no_hp`,`alergi_obat`)
    SELECT 100 + x.n,
           CONCAT('330599', LPAD(x.n, 10, '0')),
           @pwd_hash,
           CONCAT(
             IF(x.jk = 'L',
                ELT(1 + FLOOR(x.r3 * 20), 'Ahmad','Budi','Dedi','Eko','Fajar','Gilang','Hendra','Irfan','Joko','Kevin',
                                           'Lukman','Mulyadi','Nanda','Oki','Putra','Rudi','Slamet','Teguh','Wahyu','Yoga'),
                ELT(1 + FLOOR(x.r3 * 20), 'Siti','Dewi','Rina','Ayu','Fitri','Lestari','Nur','Putri','Sri','Wulan',
                                           'Anisa','Bunga','Citra','Dina','Eka','Intan','Maya','Rani','Tika','Yuni')),
             ' ',
             ELT(1 + FLOOR(x.r4 * 30), 'Santoso','Wijaya','Pratama','Saputra','Hidayat','Nugroho','Setiawan','Kurniawan','Susilo','Hakim',
                                        'Ramadhan','Firmansyah','Purnomo','Wibowo','Maulana','Gunawan','Suryanto','Hartono','Prasetyo','Syahputra',
                                        'Cahyono','Fauzi','Sulistyo','Widodo','Kusuma','Mahendra','Basuki','Wahyudi','Haryanto','Irawan')),
           IF(x.dokter_id = 4,
              DATE_SUB(CURDATE(), INTERVAL FLOOR(x.r2 * 4380) DAY),
              DATE_SUB(CURDATE(), INTERVAL (4745 + FLOOR(x.r2 * 24455)) DAY)),
           x.jk,
           CONCAT('Jl. ',
                  ELT(1 + FLOOR(x.r5 * 20), 'Pemuda','Yos Sudarso','Pantai','Veteran','Pahlawan','Merdeka','Ahmad Yani','Gatot Subroto','Tentara Pelajar','Diponegoro',
                                             'Stasiun','Kolonel Sugiono','Sarbini','Cendrawasih','Kusuma','Kenanga','Mawar','Melati','Anggrek','Flamboyan'),
                  ' No. ', 1 + FLOOR(x.r6 * 150), ', ',
                  ELT(1 + FLOOR(x.r7 * 12), 'Kebumen','Gombong','Pejagoan','Sruweng','Kutowinangun','Klirong',
                                             'Karanganyar','Prembun','Ayah','Buayan','Petanahan','Puring')),
           CONCAT('08', ELT(1 + MOD(x.n, 6), '12','13','21','52','57','81'), LPAD(FLOOR(x.r9 * 100000000), 8, '0')),
           IF(x.r8 < 0.15,
              ELT(1 + FLOOR(x.r11 * 6), 'Amoxicillin','Penicillin','Aspirin','Sulfa','Paracetamol','Ibuprofen'),
              '-')
    FROM (
      SELECT s.n, s.r2, s.r3, s.r4, s.r5, s.r6, s.r7, s.r8, s.r9, s.r11, r.dokter_id,
             IF(r.dokter_id = 15, 'P', IF(s.r1 < 0.5, 'L', 'P')) AS jk
      FROM `tmp_seed` s
      JOIN `tmp_ref_keluhan` r ON r.id = s.k
      WHERE s.n BETWEEN v_lo AND v_hi
    ) x;

    SET v_batch = v_batch + 1;
  END WHILE;

  -- (c) antrean: lanjutkan nomor antrean setelah MAX(no_antrean) yang sudah ada per dokter
  INSERT INTO `antrean`
    (`antrean_id`,`pasien_id`,`admin_id`,`dokter_id`,`tanggal`,`no_antrean`,`tingkat_urgensi`,`keluhan`,`status`)
  SELECT 100 + s.n,
         100 + s.n,
         CAST(ELT(1 + FLOOR(s.r10 * 5), 1, 2, 9, 10, 11) AS UNSIGNED),
         r.dokter_id,
         @tanggal_antrean,
         COALESCE(m.max_no, 0) + ROW_NUMBER() OVER (PARTITION BY r.dokter_id ORDER BY s.n),
         r.urgensi,
         r.keluhan,
         'MENUNGGU'
  FROM `tmp_seed` s
  JOIN `tmp_ref_keluhan` r ON r.id = s.k
  LEFT JOIN (
      SELECT `dokter_id`, MAX(`no_antrean`) AS `max_no`
      FROM `antrean`
      WHERE `tanggal` = @tanggal_antrean
      GROUP BY `dokter_id`
  ) m ON m.dokter_id = r.dokter_id;
END$$
DELIMITER ;

CALL `sp_isi_data_massal`();

-- Rekam medis untuk 30 antrean pertama (antrean_id 101-130)
INSERT INTO `rekam_medis` (`pasien_id`,`dokter_id`,`antrean_id`,`tanggal_periksa`,`keluhan`,`diagnosa`,`tindakan`)
SELECT a.pasien_id,
       a.dokter_id,
       a.antrean_id,
       DATE_ADD(TIMESTAMP(a.tanggal, '08:00:00'), INTERVAL (a.no_antrean - 1) * 15 MINUTE),
       a.keluhan,
       r.diagnosa,
       r.tindakan
FROM `antrean` a
JOIN `tmp_ref_keluhan` r ON r.dokter_id = a.dokter_id AND r.keluhan = a.keluhan
WHERE a.antrean_id BETWEEN 101 AND 130
ORDER BY a.antrean_id;

UPDATE `antrean` SET `status` = 'SELESAI'
WHERE `antrean_id` IN (SELECT `antrean_id` FROM `rekam_medis`);

-- Detail resep
DROP TABLE IF EXISTS `tmp_obat`;
CREATE TABLE `tmp_obat` AS SELECT `obat_id`, `stok`, `tgl_expired` FROM `obat`;

INSERT INTO `detail_resep` (`rekam_medis_id`,`obat_id`,`jumlah`,`dosis`)
SELECT rm.rekam_medis_id, r.obat1, r.jumlah1, r.dosis1
FROM `rekam_medis` rm
JOIN `antrean` a         ON a.antrean_id = rm.antrean_id
JOIN `tmp_ref_keluhan` r ON r.dokter_id = a.dokter_id AND r.keluhan = a.keluhan
JOIN `pasien` p          ON p.pasien_id = rm.pasien_id
JOIN `tmp_obat` o        ON o.obat_id = r.obat1
WHERE r.obat1 IS NOT NULL
  AND o.tgl_expired >= DATE(rm.tanggal_periksa)
  AND o.stok >= r.jumlah1
  AND NOT EXISTS (SELECT 1 FROM `tmp_ref_alergi` al WHERE al.alergen = p.alergi_obat AND al.obat_id = r.obat1);

INSERT INTO `detail_resep` (`rekam_medis_id`,`obat_id`,`jumlah`,`dosis`)
SELECT rm.rekam_medis_id, r.obat2, r.jumlah2, r.dosis2
FROM `rekam_medis` rm
JOIN `antrean` a         ON a.antrean_id = rm.antrean_id
JOIN `tmp_ref_keluhan` r ON r.dokter_id = a.dokter_id AND r.keluhan = a.keluhan
JOIN `pasien` p          ON p.pasien_id = rm.pasien_id
JOIN `tmp_obat` o        ON o.obat_id = r.obat2
WHERE r.obat2 IS NOT NULL
  AND o.tgl_expired >= DATE(rm.tanggal_periksa)
  AND o.stok >= r.jumlah2
  AND NOT EXISTS (SELECT 1 FROM `tmp_ref_alergi` al WHERE al.alergen = p.alergi_obat AND al.obat_id = r.obat2);

-- Bersih-bersih tabel bantu
DROP PROCEDURE IF EXISTS `sp_isi_data_massal`;
DROP TABLE IF EXISTS `tmp_seed`;
DROP TABLE IF EXISTS `tmp_angka`;
DROP TABLE IF EXISTS `tmp_digit`;
DROP TABLE IF EXISTS `tmp_ref_keluhan`;
DROP TABLE IF EXISTS `tmp_ref_alergi`;
DROP TABLE IF EXISTS `tmp_obat`;

-- Ringkasan hasil
SELECT 'pasien' AS keterangan, COUNT(*) AS jumlah FROM `pasien`
UNION ALL SELECT 'antrean (total)',  COUNT(*) FROM `antrean`
UNION ALL SELECT 'antrean MENUNGGU', COUNT(*) FROM `antrean` WHERE `status` = 'MENUNGGU'
UNION ALL SELECT 'antrean SELESAI',  COUNT(*) FROM `antrean` WHERE `status` = 'SELESAI'
UNION ALL SELECT 'urgensi MERAH',    COUNT(*) FROM `antrean` WHERE `tingkat_urgensi` = 'MERAH'
UNION ALL SELECT 'urgensi KUNING',   COUNT(*) FROM `antrean` WHERE `tingkat_urgensi` = 'KUNING'
UNION ALL SELECT 'urgensi HIJAU',    COUNT(*) FROM `antrean` WHERE `tingkat_urgensi` = 'HIJAU'
UNION ALL SELECT 'rekam_medis',      COUNT(*) FROM `rekam_medis`
UNION ALL SELECT 'detail_resep',     COUNT(*) FROM `detail_resep`;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;