# Schema.md - Database Schema & Supabase Configuration

> **Sistem:** Aplikasi Penjadwalan & Pelacak Status LHP  
> **Database:** PostgreSQL 15+ (Supabase Managed Engine)  
> **Fitur Database:** Row Level Security (RLS), Triggers, Realtime Publication, Storage Buckets  

---

## 1. Diagram Relasi Entitas (ERD)

```
 +------------------------+              +------------------------+
 |      user_profiles     | 1          * |      lhp_records       |
 +------------------------+--------------+------------------------+
 | id (PK, UUID -> auth)  | PIC/Inspector| id (PK, UUID)          |
 | full_name              |              | lhp_number (UQ)        |
 | role (ENUM)            |              | client_company_id (FK) |
 | phone_number           |              | inspection_type (ENUM) |
 | created_at             |              | current_status (ENUM)  |
 +------------------------+              | inspection_date        |
             |                           | client_deadline        |
             |                           | inspector_id (FK)      |
             |                           | reviewer_id (FK)       |
             |                           | notes                  |
             | 1                         +------------------------+
             |                                       |
             |                                       | 1
             | *                                     |
 +------------------------+                          | *
 |   client_companies     | 1                      * v
 +------------------------+--------------+------------------------+
 | id (PK, UUID)          |              |    lhp_status_logs     |
 | company_name           |              +------------------------+
 | pic_name               |              | id (PK, UUID)          |
 | pic_phone              |              | lhp_id (FK)            |
 | address                |              | previous_status        |
 | created_at             |              | new_status             |
 +------------------------+              | changed_by (FK users)  |
                                         | notes / review_comment |
                                         | created_at             |
                                         +------------------------+
                                                     |
                                                     | 1
                                                     |
                                                     | *
                                         +------------------------+
                                         |     lhp_attachments    |
                                         +------------------------+
                                         | id (PK, UUID)          |
                                         | lhp_id (FK)            |
                                         | file_name              |
                                         | file_path (Storage)    |
                                         | file_type (DRAFT/CERT) |
                                         | uploaded_by (FK)       |
                                         | uploaded_at            |
                                         +------------------------+
```

---

## 2. Struktur Enum & Definisi Tabel SQL

```sql
-- 1. ENUMS
CREATE TYPE user_role AS ENUM (
    'ADMIN',
    'INSPECTOR',
    'AHLI_K3',
    'OPERATIONAL'
);

CREATE TYPE lhp_status AS ENUM (
    'SCHEDULED',           -- Penjadwalan Lapangan
    'INSPECTION_COMPLETED',-- Pemeriksaan Selesai
    'DRAFT_CREATED',       -- Draft LHP Dibuat
    'AHLI_K3_REVIEW',      -- Review Ahli K3
    'DISNAKER_PROCESS',    -- Proses Pengesahan Disnaker/Kemenaker
    'CERTIFICATE_ISSUED'   -- Sertifikat Terbit / Selesai
);

CREATE TYPE k3_inspection_category AS ENUM (
    'PESAWAT_ANGKAT_ANGKUT', -- Forklift, Crane, Hoist
    'BEJANA_TEKAN_TANGKI',   -- Boiler, Compressor, Tangki Timbun
    'INSTALASI_LISTRIK_PETIR',-- Listrik & Penangkal Petir
    'PROTEKSI_KEBAKARAN',    -- Hydrant, Alarm, APAR
    'ELEVATOR_ESKALATOR',    -- Lift & Eskalator
    'LINGKUNGAN_KERJA'       -- Ergonomi, Fisika, Kimia
);

-- 2. USER PROFILES (Ekstensi dari Supabase auth.users)
CREATE TABLE public.user_profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    full_name VARCHAR(150) NOT NULL,
    role user_role NOT NULL DEFAULT 'INSPECTOR',
    phone_number VARCHAR(25),
    signature_url TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 3. DATA PERUSAHAAN KLIEN
CREATE TABLE public.client_companies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_name VARCHAR(255) NOT NULL,
    pic_name VARCHAR(150) NOT NULL,
    pic_phone VARCHAR(25) NOT NULL,
    address TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 4. TABEL UTAMA: LHP RECORDS
CREATE TABLE public.lhp_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lhp_number VARCHAR(100) UNIQUE NOT NULL,
    client_company_id UUID NOT NULL REFERENCES public.client_companies(id) ON DELETE RESTRICT,
    category k3_inspection_category NOT NULL,
    unit_description VARCHAR(255) NOT NULL, -- Contoh: "Forklift Komatsu 3 Ton No. Unit 02"
    current_status lhp_status NOT NULL DEFAULT 'SCHEDULED',
    inspection_date DATE NOT NULL,
    client_deadline DATE NOT NULL,
    inspector_id UUID REFERENCES public.user_profiles(id) ON DELETE SET NULL,
    reviewer_id UUID REFERENCES public.user_profiles(id) ON DELETE SET NULL,
    notes TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 5. RIWAYAT PERUBAHAN STATUS (AUDIT LOGS)
CREATE TABLE public.lhp_status_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lhp_id UUID NOT NULL REFERENCES public.lhp_records(id) ON DELETE CASCADE,
    previous_status lhp_status,
    new_status lhp_status NOT NULL,
    changed_by UUID NOT NULL REFERENCES public.user_profiles(id),
    review_notes TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 6. LAMPIRAN DOKUMEN & FOTO (STORAGE METADATA)
CREATE TABLE public.lhp_attachments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lhp_id UUID NOT NULL REFERENCES public.lhp_records(id) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    storage_path TEXT NOT NULL,
    file_type VARCHAR(50) NOT NULL, -- 'DRAFT_PDF', 'EVIDENCE_PHOTO', 'FINAL_CERT'
    uploaded_by UUID NOT NULL REFERENCES public.user_profiles(id),
    uploaded_at TIMESTAMPTZ DEFAULT NOW()
);

-- INDEXES UNTUK KINERJA QUERY & REALTIME
CREATE INDEX idx_lhp_status ON public.lhp_records(current_status);
CREATE INDEX idx_lhp_deadline ON public.lhp_records(client_deadline);
CREATE INDEX idx_lhp_inspector ON public.lhp_records(inspector_id);
CREATE INDEX idx_logs_lhpid ON public.lhp_status_logs(lhp_id);
```

---

## 3. Database Triggers & Otomasi

```sql
-- Trigger untuk mencatat riwayat perubahan status secara otomatis
CREATE OR REPLACE FUNCTION log_lhp_status_transition()
RETURNS TRIGGER AS $$
BEGIN
    IF (OLD.current_status IS DISTINCT FROM NEW.current_status) THEN
        INSERT INTO public.lhp_status_logs (
            lhp_id,
            previous_status,
            new_status,
            changed_by,
            review_notes
        ) VALUES (
            NEW.id,
            OLD.current_status,
            NEW.current_status,
            auth.uid(),
            'Status diperbarui melalui aplikasi'
        );
        NEW.updated_at = NOW();
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE TRIGGER trigger_log_lhp_status
BEFORE UPDATE ON public.lhp_records
FOR EACH ROW EXECUTE FUNCTION log_lhp_status_transition();
```

---

## 4. Konfigurasi Realtime & Storage di Supabase
- **Realtime Publication:**
  ```sql
  ALTER PUBLICATION supabase_realtime ADD TABLE public.lhp_records;
  ALTER PUBLICATION supabase_realtime ADD TABLE public.lhp_status_logs;
  ```
- **Supabase Storage Buckets:**
  - `lhp-documents`: Private bucket untuk file PDF draft dan sertifikat pengesahan Disnaker.
  - `inspection-photos`: Private bucket untuk foto pemeriksaan fisik alat K3.
