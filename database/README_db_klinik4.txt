PENGATURAN DATABASE PROJECT KLINIK

1. Jalankan MySQL/Laragon.
2. Import file database/db_klinik4_update.sql melalui HeidiSQL, phpMyAdmin, atau mysql CLI.
3. File SQL tersebut membuat database bernama db_klinik4 dan mengisi data contoh dalam jumlah besar.
4. Perhatian: pada bagian awal file terdapat DROP DATABASE IF EXISTS `db_klinik4`.
   Perintah ini menghapus database db_klinik4 yang sudah ada sebelum membuat ulang.
   Backup database terlebih dahulu jika ada data yang ingin dipertahankan.
5. Konfigurasi aplikasi berada di src/main/resources/database.properties.
   Default: localhost:3306, database db_klinik4, user root, password kosong.
6. Ubah db.host, db.port, db.user, dan db.password sesuai konfigurasi MySQL lokal.

Halaman Antrean, Data Pasien, dan Rekam Medis menggunakan paginasi database
(LIMIT/OFFSET) dengan 20 data per halaman. Pencarian pasien dan pilihan pasien
pada ComboBox dilakukan secara bertahap ke database.
