package com.scheduletrackapp.domain.model

enum class LhpStatus {
    SCHEDULED,            // 1. Penjadwalan Lapangan
    INSPECTION_COMPLETED, // 2. Pemeriksaan Selesai
    DRAFT_CREATED,        // 3. Draft LHP Dibuat
    AHLI_K3_REVIEW,       // 4. Review Ahli K3
    DISNAKER_PROCESS,     // 5. Proses Pengesahan Disnaker
    CERTIFICATE_ISSUED,   // 6. Sertifikat Terbit
    DELIVERED_TO_CLIENT   // 7. Diserahkan ke Klien (Selesai 100%) 🏁
}