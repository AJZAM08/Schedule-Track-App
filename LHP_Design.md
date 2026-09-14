# Design.md - UI/UX Specification & Android Jetpack Compose System

> **Sistem:** Aplikasi Penjadwalan & Pelacak Status LHP K3  
> **Platform:** Android Mobile (Material Design 3)  
> **Filosofi UI/UX:** *Industrial-Grade, High-Density Information, Glanceable & Action-Oriented*  

---

## 1. Prinsip Desain UI/UX

1. **Glanceable Status (Sekali Lirik Paham):**
   - Menggunakan kode warna status dan penanda tenggat waktu yang tegas agar staf langsung tahu berkas mana yang mendesak.
2. **Dual-View Ergonomics:**
   - **Kanban Board:** Menggeser kartu antar tahapan proses dengan cepat secara visual.
   - **List/Calendar View:** Untuk memantau tanggal agenda lapangan dan deadline yang jatuh tempo minggu ini.
3. **One-Tap Quick Actions:**
   - Dari kartu LHP, pengguna dapat langsung menelepon PIC klien, membuka dokumen draft, atau memajukan status ke tahap berikutnya.

---

## 2. Design System & Tokens (Material Design 3)

### 2.1 Palet Warna Status LHP (Industrial K3 Theme)
| Elemen / Token | Hex Code | Makna / Penggunaan |
| :--- | :--- | :--- |
| **Status: Scheduled** | `#3B82F6` (Blue) | Tahap awal penjadwalan riksa uji lapangan |
| **Status: Inspection Done**| `#6366F1` (Indigo) | Pemeriksaan fisik selesai, menunggu drafting |
| **Status: Draft Created** | `#8B5CF6` (Purple) | Draft LHP telah dibuat oleh drafter |
| **Status: Ahli K3 Review** | `#F59E0B` (Amber) | Sedang diverifikasi & tanda tangan teknis Ahli K3 |
| **Status: Disnaker Process**| `#EC4899` (Pink/Magenta)| Dokumen dalam antrian pengesahan dinas tenaga kerja |
| **Status: Certificate Issued**| `#10B981` (Emerald Green)| Selesai / Sertifikat resmi terbit |
| **Deadline: Overdue (<0d)**| `#EF4444` (High Red) | Tenggat waktu penyerahan terlewat |
| **Deadline: Warning (≤3d)** | `#F97316` (Vivid Orange)| Mendekati batas penyerahan |
| **Deadline: Safe (>3d)** | `#64748B` (Slate Gray) | Masih dalam batas waktu pengerjaan |

---

## 3. Wireframe & Desain Layar Aplikasi Android

### 3.1 Layar 1: Papan Kanban LHP (`KanbanScreen.kt`)
Header dengan filter kategori dan tab/kolom horizontal yang dapat di-*swipe*.

```
+-------------------------------------------------------------+
| [=] Status LHP K3               [Filter] [Search]  (Avatar) |
+-------------------------------------------------------------+
| Kategori: [ Semua ] [ Forklift ] [ Boiler ] [ Listrik ]     |
+-------------------------------------------------------------+
| < [ REVIEW AHLI K3 (8) ] >       (Geser Kolom: -> Disnaker) |
|-------------------------------------------------------------|
| +---------------------------------------------------------+ |
| | LHP/2026/09/0142               [ ⚠️ Sisa 2 Hari ]       | |
| | PT Semen Sentosa Abadi                                  | |
| | Alat: Overhead Crane 15 Ton (Unit #03)                  | |
| | PIC: Bpk. Bambang | Ahli K3: Ir. Hendra                 | |
| |---------------------------------------------------------| |
| | [📄 Draft_v2.pdf]       [ Catatan: Rekomendasi Wire ]   | |
| | [ Setujui Review -> ]              [ Ajukan Revisi ]    | |
| +---------------------------------------------------------+ |
|                                                             |
| +---------------------------------------------------------+ |
| | LHP/2026/09/0138               [ 🟢 Sisa 6 Hari ]       | |
| | PT Indofood Sukses                                      | |
| | Alat: Bejana Tekan Kompresor 10 Bar                     | |
| | PIC: Bpk. Surya   | Ahli K3: Ir. Hendra                 | |
| |---------------------------------------------------------| |
| | [📄 Draft_v1.pdf]       [ Menunggu pengecekan kalkulasi]| |
| | [ Setujui Review -> ]              [ Ajukan Revisi ]    | |
| +---------------------------------------------------------+ |
|                                                             |
| [ FAB: + Buat Berkas Baru ]                                 |
+-------------------------------------------------------------+
| [ Papan Kanban ]     [ Kalender Agenda ]    [ Notifikasi ]  |
+-------------------------------------------------------------+
```

### 3.2 Layar 2: Kalender Agenda & Deadline Tracker (`ScheduleScreen.kt`)
Tampilan untuk melacak jadwal lapangan dan pengingat tanggal penyerahan.

```
+-------------------------------------------------------------+
| [=] Jadwal & Deadline                      [ Bulan: Sep v ] |
+-------------------------------------------------------------+
|   SEN    SEL    RAB    KAM    JUM    SAB    MIN             |
|   07     08     09     10     11     12    [13]             |
|   --     --     --     --     --     --     --              |
|   (2)    (4)    (1)    (3)    (5)    (1)    (0)             |
+-------------------------------------------------------------+
| AGENDA HARI INI & MENDATANG:                                |
|                                                             |
| 📅 HARI INI: 12 Sep 2026                                    |
| [Jadwal Riksa Lapangan]                                     |
| • 09:00 WIB - PT Krakatau Tirta                             |
|   Riksa Uji Instalasi Penyalur Petir (3 Tower)              |
|   Inspektor: Doni Pratama                                   |
|                                                             |
| ⚠️ DEADLINE MINGGU INI (Tenggat Penyerahan Sertifikat):     |
| • 14 Sep 2026 (Senin)                                       |
|   [🚨 DEADLINE] PT Semen Sentosa - LHP Overhead Crane       |
|   Status Saat Ini: Review Ahli K3                           |
|   SLA: Klien meminta dokumen final sebelum tgl 15.          |
|                                                             |
| • 15 Sep 2026 (Selasa)                                       |
|   [🚨 DEADLINE] PT Chandra Asri - 2 Unit Boiler             |
|   Status Saat Ini: Proses Pengesahan Disnaker Serang        |
+-------------------------------------------------------------+
```

### 3.3 Layar 3: Detail Berkas & Timeline Progres LHP (`LhpDetailScreen.kt`)

```
+-------------------------------------------------------------+
| <- No: LHP/2026/09/0142                         [Edit] [:]  |
+-------------------------------------------------------------+
| PT SEMEN SENTOSA ABADI                                      |
| Overhead Crane 15 Ton - No Seri: CR-2021-99                 |
| Kategori: Pesawat Angkat & Angkut                           |
|-------------------------------------------------------------|
| STATUS SAAT INI:                                            |
| [ 🟧 REVIEW AHLI K3 ] -> Next: [ Kirim ke Disnaker ]        |
| Tenggat Waktu Klien: 14 September 2026 (Tinggal 2 Hari Lagi)|
|                                                             |
| TIMELINE PROGRES PENGERJAAN:                                |
| [x] 1. Penjadwalan Dibuat      (01 Sep - Siti Admin)        |
| [x] 2. Lapangan Selesai Riksa  (04 Sep - Doni Inspektor)    |
| [x] 3. Draft LHP Dibuat        (07 Sep - Rian Drafter)      |
| [o] 4. Review Ahli K3          (Sedang Berjalan...)         |
| [ ] 5. Pengesahan Disnaker     (Belum Dimulai)              |
| [ ] 6. Sertifikat & Penyerahan (Belum Dimulai)              |
|-------------------------------------------------------------|
| BERKAS & LAMPIRAN:                                          |
| • [PDF] Draft_LHP_Rev1.pdf (2.4 MB)          [Unduh/Buka]   |
| • [IMG] Foto_Temuan_Kawat_Seling.jpg         [Lihat]        |
| [+ Tambah Lampiran / Upload File Baru]                      |
|-------------------------------------------------------------|
| CATATAN TIM:                                                |
| "Ir. Hendra: Mohon safety factor kawat seling dihitung ulang|
| sesuai Permenaker No 8 Tahun 2020 pasal 12."                |
|                                                             |
| [ Buka WhatsApp Klien ]          [ Update Status Berkas ]   |
+-------------------------------------------------------------+
```

---

## 4. Pola Navigasi & Komponen Jetpack Compose

1. **Jetpack Compose Navigation:**
   - Menggunakan Kotlin Serialization untuk type-safe routes (`@Serializable data class LhpDetail(val id: String)`).
2. **Bottom Navigation Bar:**
   - `KanbanBoard` (Papan status utama).
   - `CalendarSchedule` (Jadwal riksa & deadline tracker).
   - `CompanyDirectory` (Daftar kontak klien K3).
3. **Android Jetpack WorkManager Integration:**
   - Background worker memeriksa berkas LHP berstatus mendekati deadline ($\le 3$ hari) setiap jam 08:00 pagi dan memicu Android Local Notification berprioritas tinggi (`NotificationCompat.PRIORITY_HIGH`).
