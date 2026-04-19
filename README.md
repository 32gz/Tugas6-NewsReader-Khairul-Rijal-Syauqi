News Reader App - Kotlin Multiplatform (KMP)

Aplikasi pembaca berita yang dibangun menggunakan Kotlin Multiplatform dan Compose Multiplatform untuk menampilkan artikel berita secara real-time.

API yang Digunakan

Aplikasi ini menggunakan layanan dari NewsAPI.org:

- item Endpoint: v2/top-headlines

- item Parameter:

- item category: technology

- item language: en

- item Library Networking: Ktor Client

- item Library Serialization: Kotlinx Serialization

Screenshot States

1. Loading State

Kondisi saat aplikasi sedang melakukan request data ke server menggunakan CircularProgressIndicator.

2. Success State

Kondisi saat data berita berhasil diambil dan ditampilkan dalam daftar menggunakan LazyColumn.

3. Detail State

Tampilan konten lengkap (gambar, judul, author, dan isi berita) setelah salah satu berita dipilih.

4. Error State

Tampilan saat terjadi kegagalan jaringan, API Key salah, atau data kosong. Dilengkapi tombol Coba Lagi.
