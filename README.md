## News Reader App - Kotlin Multiplatform (KMP) ##

## Identitas Mahasiswa ##

Nama : Khairul Rijal Syauqi
NIM  : 123140143

## Deskripsi ##

Aplikasi pembaca berita yang dibangun menggunakan Kotlin Multiplatform dan Compose Multiplatform untuk menampilkan artikel berita secara real-time.

API yang Digunakan

Aplikasi ini menggunakan layanan dari NewsAPI.org:

- item Endpoint: v2/top-headlines

- item Parameter:

    - item category: technology

    - item language: en

- item Library Networking: Ktor Client

- item Library Serialization: Kotlinx Serialization

## Screenshot States ##

1. Loading State

Kondisi saat aplikasi sedang melakukan request data ke server menggunakan CircularProgressIndicator.

![image alt](https://github.com/32gz/Tugas6-NewsReader-Khairul-Rijal-Syauqi/blob/main/Screenshot%202026-05-31%20180143.png)

2. Success State

Kondisi saat data berita berhasil diambil dan ditampilkan dalam daftar menggunakan LazyColumn.

![image alt](https://github.com/32gz/Tugas6-NewsReader-Khairul-Rijal-Syauqi/blob/main/Screenshot%202026-05-31%20180200.png)

3. Detail State

Tampilan konten lengkap (gambar, judul, author, dan isi berita) setelah salah satu berita dipilih.

![image alt](https://github.com/32gz/Tugas6-NewsReader-Khairul-Rijal-Syauqi/blob/main/Screenshot%202026-05-31%20180248.png)

4. Error State

Tampilan saat terjadi kegagalan jaringan, API Key salah, atau data kosong. Dilengkapi tombol Coba Lagi.

![image alt](https://github.com/32gz/Tugas6-NewsReader-Khairul-Rijal-Syauqi/blob/main/Screenshot%202026-05-31%20180219.png)

## Video Demo ##

[Video Demo](https://drive.google.com/file/d/1R07eu6LfmWuAjN0aqTNR8X0Nr617PFnA/view?usp=sharing)

