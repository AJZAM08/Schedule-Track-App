# Architecture.md - Arsitektur Aplikasi Pelacak Status LHP

> **Sistem:** Aplikasi Penjadwalan & Pelacak Status LHP (K3)  
> **Tech Stack:** Kotlin, Jetpack Compose, Supabase BaaS (Postgres, Auth, Storage, Realtime)  
> **Pola Arsitektur:** Clean Architecture + MVVM + Unidirectional Data Flow (UDF)  
> **Lead Architect:** Ajiz Abdul Majid  

---

## 1. Topologi Sistem Tingkat Tinggi (High-Level Topology)

```
+------------------------------------------------------------------------------------+
|                         CLIENT: ANDROID APPLICATION (KOTLIN)                       |
|                                                                                    |
|  +------------------------------------------------------------------------------+  |
|  |                          UI LAYER (Jetpack Compose)                          |  |
|  |   - AuthScreens         - KanbanBoardScreen       - CalendarScheduleScreen   |  |
|  |   - LhpDetailScreen     - CreateEditLhpScreen     - ReviewAhliK3Screen       |  |
|  +------------------------------------------------------------------------------+  |
|                                         | Observes UiState (StateFlow)             |
|                                         v Emits UiIntent / User Events             |
|  +------------------------------------------------------------------------------+  |
|  |                      VIEWMODEL LAYER (Android Jetpack ViewModel)             |  |
|  |   - AuthViewModel       - KanbanViewModel         - ScheduleViewModel        |  |
|  |   - LhpDetailViewModel  - ManageLhpViewModel      - NotificationSyncManager  |  |
|  +------------------------------------------------------------------------------+  |
|                                         | Interacts with Use Cases                 |
|                                         v                                          |
|  +------------------------------------------------------------------------------+  |
|  |                                DOMAIN LAYER                                  |  |
|  |   Use Cases: GetKanbanBoardUseCase, UpdateLhpStatusUseCase, ListenRealtime...|  |
|  |   Models: LhpRecord, StatusStage, ClientCompany, UserProfile                 |  |
|  |   Repository Interfaces: LhpRepository, AuthRepository, StorageRepository    |  |
|  +------------------------------------------------------------------------------+  |
|                                         | Implements                               |
|                                         v                                          |
|  +------------------------------------------------------------------------------+  |
|  |                                  DATA LAYER                                  |  |
|  |   Repositories: LhpRepositoryImpl, AuthRepositoryImpl                        |  |
|  |   Local Cache: Room Database / DataStore (Offline Cache & Session)           |  |
|  |   Background Sync: Android Jetpack WorkManager (Deadline alerts check)       |  |
|  |   Remote Data Source: Supabase-kt Client SDK                                 |  |
|  +------------------------------------------------------------------------------+  |
+------------------------------------------------------------------------------------+
                                    |
                    HTTPS (REST API) | WSS (Postgres Realtime Changes)
                                    v
+------------------------------------------------------------------------------------+
|                                SUPABASE BACKEND (BaaS)                             |
|                                                                                    |
|  +---------------------+  +------------------------+  +-------------------------+  |
|  |    Supabase Auth    |  |  PostgreSQL Database   |  |    Supabase Realtime    |  |
|  |  (Email / Password, |  |  - Row Level Security  |  |  (CDC / Realtime PubSub |  |
|  |   JWT Tokens, RBAC) |  |  - Stored Procedures   |  |   Broadcast Status LHP) |  |
|  +---------------------+  +------------------------+  +-------------------------+  |
|                                                                                    |
|  +-------------------------------------------------+  +-------------------------+  |
|  |                Supabase Storage                 |  |  Postgres Cron Triggers |  |
|  |  (Buckets: 'lhp-drafts', 'sertifikat-disnaker') |  |  (SLA & Overdue Alert)  |  |
|  +-------------------------------------------------+  +-------------------------+  |
+------------------------------------------------------------------------------------+
```

---

## 2. Pilihan Library & Dependencies Android

| Layer / Kebutuhan | Library Rekomendasi | Alasan |
| :--- | :--- | :--- |
| **Language & Concurrency** | Kotlin 2.0+, Coroutines, Flow | Standar industri modern, asynchronous non-blocking lancar. |
| **UI Toolkit** | Jetpack Compose, Material 3 | Desain modern, deklaratif, mudah membangun custom Kanban board & timeline status. |
| **Backend SDK** | `io.github.jan-tennert.supabase:postgrest-kt`, `auth-kt`, `realtime-kt`, `storage-kt` | SDK resmi Supabase Kotlin Multiplatform yang idiomatis dan modular. |
| **Dependency Injection** | Hilt (Dagger) | Standar resmi Google, integrasi mulus dengan Android Jetpack ViewModel. |
| **Local Persistence** | Room Database | Menyimpan cache lokal berkas LHP saat teknisi berada di area pabrik tanpa sinyal internet (*offline-first read*). |
| **Image & Doc Loading** | Coil Compose | Asynchronous image loader ringan untuk thumbnail foto alat dan preview berkas. |
| **Background Scheduling**| Android WorkManager | Menjalankan pengecekan tenggat waktu (*deadline*) dan memicu notifikasi lokal secara akurat. |

---

## 3. Strategi Realtime & Offline Synchronization

1. **Supabase Realtime Channel (`postgres_changes`):**
   - Aplikasi subscribe ke channel tabel `lhp_records`:
     ```kotlin
     val channel = supabase.realtime.channel("lhp_kanban_updates")
     val changeFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
         table = "lhp_records"
     }
     changeFlow.onEach { action ->
         // Emit perubahan ke ViewModel -> StateFlow -> Compose UI update otomatis
     }.launchIn(viewModelScope)
     ```
2. **Optimistic UI Updates:**
   - Ketika status berkas digeser ke kolom baru di Kanban board, UI seketika mengubah posisi kartu.
   - Request update dikirim ke Supabase PostgREST di background.
   - Jika gagal (koneksi putus), posisi kartu di-rollback dan muncul snackbar error.
3. **Offline Read via Room DB:**
   - Seluruh daftar riksa uji disimpan di database Room lokal. Saat inspektor berada di basement pabrik tanpa sinyal, data riksa tetap dapat dibaca.

---

## 4. Keamanan & Akses Data (Security & RBAC)

1. **Row Level Security (RLS) di Supabase:**
   - Hak akses tidak bergantung pada klien, melainkan dikunci langsung di level database PostgreSQL melalui token JWT Supabase.
2. **Audit Trail Otomatis:**
   - Menggunakan PostgreSQL Triggers pada tabel `lhp_status_logs`. Setiap pergantian status merekam `user_id`, `from_status`, `to_status`, dan `changed_at` secara permanen untuk akuntabilitas.
