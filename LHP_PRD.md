# Product Requirement Document (PRD)
## Aplikasi Penjadwalan & Pelacak Status LHP (Laporan Hasil Pemeriksaan K3)

> **Status:** Draft / Approved for Development  
> **Versi:** 1.0.0  
> **Product Owner & Architect:** Ajiz Abdul Majid  
> **Platform Target:** Android Native (Kotlin, Jetpack Compose)  
> **Backend / BaaS:** Supabase (Auth, Postgres, Realtime, Storage, Edge Functions)  
> **Terakhir Diperbarui:** 2026/09/12  

---

## 1. Ringkasan Eksekutif (Executive Summary)
Pengurusan **Laporan Hasil Pemeriksaan (LHP)** K3 (Keselamatan dan Kesehatan Kerja) dan pengesahan sertifikat/SKK/Suket Disnaker/Kemenaker memiliki tahapan regulasi yang panjang dan ketat: mulai dari penjadwalan riksa uji lapangan, input data teknis/drafting LHP, peninjauan Ahli K3 Spesialis, cetak berkas, hingga pengesahan dan penerbitan sertifikasi dinas terkait.

- **Pernyataan Masalah (Problem Statement):**
  1. Pelacakan puluhan berkas LHP antar tim (Inspektor Lapangan, Drafter, Ahli K3, dan Admin Operasional) masih tercecer di spreadsheet atau chat WhatsApp grup tanpa status terpusat.
  2. Kerap terjadi keterlambatan (*bottleneck*) tanpa diketahui di tahap mana berkas tertahan (apakah di review Ahli K3 atau proses antrian dinas).
  3. Terlewatnya tenggat waktu (*deadline*) penyerahan dokumen ke klien perusahaan karena ketiadaan pengingat terstruktur.
- **Solusi yang Diajukan (Proposed Solution):**
  Aplikasi Android mobile-first untuk internal PJK3 (Perusahaan Jasa K3) yang menggabungkan:
  - **Kanban Board & List Tracker:** Alur status LHP visual real-time (`Jadwal Lapangan` -> `Pemeriksaan Selesai` -> `Draft LHP Dibuat` -> `Review Ahli K3` -> `Proses Disnaker/Kemenaker` -> `Penerbitan Sertifikat / Selesai`).
  - **Penjadwalan & Pengingat Tenggat Waktu (Deadline Management):** Integrasi kalender riksa uji, pelacak SLA per tahapan berkas, dan notifikasi pengingat otomatis.
- **Value Proposition:** Memangkas resiko denda/komplain keterlambatan LHP hingga 90%, visibilitas real-time status berkas via ponsel, dan transparansi akuntabilitas kerja antar personil K3.

---

## 2. Tujuan & Metrik Keberhasilan (Goals & Success Metrics)

### 2.1 Sasaran Bisnis & Produk (Goals)
- Memusatkan seluruh pelacakan berkas LHP K3 dalam satu genggaman di aplikasi Android.
- Menyediakan papan Kanban mobile yang interaktif dan sinkron secara real-time antar staf lapangan dan kantor.
- Memberikan peringatan otomatis H-3, H-1, dan hari-H sebelum tenggat waktu SLA penyerahan dokumen ke klien.
- Memungkinkan upload dan preview lampiran berkas draft/suket/foto alat secara cepat via Supabase Storage.

### 2.2 Bukan Sasaran (Non-Goals / Out of Scope v1)
- Tidak membuat portal login publik untuk klien luar (hanya untuk internal tim PJK3 & inspektor pada v1).
- Tidak mengotomatisasi cetak fisik printer dari handphone (pencetakan fisik tetap dilakukan dari PC kantor).
- Tidak mengintegrasikan API resmi Kemenaker TemanK3 secara direct (karena keterbatasan API pihak ketiga; status Disnaker dicatat manual oleh admin/liason officer).

### 2.3 Metrik Keberhasilan (KPIs)
| Metrik | Target Pasca Rilis | Periode Evaluasi |
| :--- | :---: | :--- |
| **Peningkatan Ketepatan Waktu LHP (On-Time SLA)** | $\ge 95\%$ berkas selesai sebelum deadline klien | 60 hari |
| **Realtime Sync Latency** | $< 1.5$ detik saat kartu status bergeser | Pasca rilis |
| **Waktu Update Status Berkas** | $\le 15$ detik per dokumen oleh personil | 30 hari |
| **Tingkat Adopsi Tim Internal (Daily Active Users)** | $100\%$ personil operasional PJK3 | 30 hari |

---

## 3. Persona Pengguna & Peran (User Roles)

1. **Inspektor / Ahli K3 Lapangan (Doni):**
   - Melakukan inspeksi teknis alat/lingkungan K3 ke pabrik klien.
   - Perlu melihat jadwal inspeksi mingguan dan mengunggah status selesai periksa langsung dari HP.
2. **Ahli K3 Reviewer / Penanggung Jawab Teknis (Ir. Hendra):**
   - Melakukan verifikasi perhitungan teknik, regulasi perundangan, dan rekomendasi K3.
   - Perlu notifikasi instan saat ada draft baru yang siap di-review dan menyetujui/menolak revisi draft.
3. **Admin Operasional / Liason Officer Disnaker (Siti):**
   - Mengurus antrian pengesahan ke kantor Disnaker/Kemenaker, mencetak sertifikat, dan mengirimkan ke klien.
   - Mengelola tenggat waktu, kartu Kanban, dan pembagian tugas personil.

---

## 4. Alur Pengguna & Fitur Utama (User Stories)

### Epic 1: Autentikasi & Role-Based Access Control
- **US-01:** Sebagai staf internal, saya dapat login menggunakan email & password akun kantor agar data berkas perusahaan terlindungi.
- **US-02:** Sistem mengatur akses menu berdasarkan role (`ADMIN`, `INSPECTOR`, `AHLI_K3`, `OPERATIONAL`).

### Epic 2: Papan Kanban & Pelacak Status LHP Real-Time
- **US-03:** Pengguna dapat melihat daftar berkas LHP dalam tampilan papan Kanban kolom status:
  `Jadwal Lapangan` $ightarrow$ `Pemeriksaan Selesai` $ightarrow$ `Draft LHP Dibuat` $ightarrow$ `Review Ahli K3` $ightarrow$ `Proses Disnaker` $ightarrow$ `Penerbitan Sertifikat / Selesai`.
- **US-04:** Pengguna dapat mengubah status berkas (drag-and-drop atau melalui selector status) dengan riwayat histori perubahan tercatat otomatis (*audit trail*).
- **US-05:** Status kartu otomatis terupdate secara langsung (Supabase Realtime) di layar pengguna lain tanpa perlu refresh manual.

### Epic 3: Penjadwalan & Deadline Tracker
- **US-06:** Admin dapat menambahkan berkas LHP baru dengan menyertakan nama perusahaan klien, jenis riksa uji (misal: Bejana Tekan, Pesawat Angkat Angkut, Instalasi Listrik, Damkar), personil PIC, tanggal periksa, dan tenggat waktu (*deadline* penyerahan).
- **US-07:** Pengguna dapat melihat tampilan Kalender agenda inspeksi lapangan dan tenggat waktu pengesahan.
- **US-08:** Sistem menampilkan label status tenggat waktu warna: *Aman (Hijau)*, *Mendekati Deadline $\le 3$ hari (Kuning)*, *Terlambat/Overdue (Merah)*.

### Epic 4: Manajemen Dokumen & Catatan Review
- **US-09:** Staf dapat mengunggah file PDF draft laporan atau foto bukti lapangan ke Supabase Storage.
- **US-10:** Ahli K3 dapat memberikan catatan review revisi atau *approval* langsung dari aplikasi.

---

## 5. Tahapan Rilis (Milestones)
1. **Sprint 1 (Fondasi):** Inisiasi Project Android Kotlin Compose, Supabase Auth, Desain Database PostgreSQL, RLS Policies.
2. **Sprint 2 (Core Tracking):** Implementasi Kanban Board Compose, CRUD Berkas LHP, Realtime Channel listener.
3. **Sprint 3 (Scheduling & Docs):** Kalender agenda, Deadline warning, integrasi upload lampiran berkas PDF/Foto Supabase Storage.
4. **Sprint 4 (Testing & Polish):** Notifikasi lokal/push (WorkManager / FCM), hardening security, UAT internal tim PJK3.
