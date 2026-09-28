# Graph Report - ScheduleTrackApp  (2026-09-14)

## Corpus Check
- Corpus is ~6,454 words - fits in a single context window. You may not need a graph.

## Summary
- 57 nodes · 41 edges · 25 communities (9 shown, 16 thin omitted)
- Extraction: 88% EXTRACTED · 12% INFERRED · 0% AMBIGUOUS · INFERRED: 5 edges (avg confidence: 0.93)
- Token cost: 6,454 input · 1,200 output

## Community Hubs (Navigation)
- Data, Backend & Schema Foundation
- MainActivity & Compose UI Theme
- Android Screen Specifications & UI Layer
- Gradle Wrapper Script
- Android Instrumented Tests
- Unit Testing Suite
- PJK3 Workflow & Status Pipeline
- HDPI Round App Launcher Asset
- HDPI Standard App Launcher Asset
- MDPI Round App Launcher Asset
- MDPI Standard App Launcher Asset
- XHDPI Round App Launcher Asset
- XHDPI Standard App Launcher Asset
- XXHDPI Round App Launcher Asset
- XXHDPI Standard App Launcher Asset
- XXXHDPI Round App Launcher Asset
- XXXHDPI Standard App Launcher Asset
- Domain Layer Architecture
- System Topology Overview
- ViewModel StateFlow Architecture

## God Nodes (most connected - your core abstractions)
1. `lhp_records Database Table` - 7 edges
2. `Greeting()` - 4 edges
3. `ScheduleTrackAppTheme()` - 4 edges
4. `MainActivity` - 3 edges
5. `GreetingPreview()` - 3 edges
6. `Jetpack Compose UI Layer` - 3 edges
7. `ExampleInstrumentedTest` - 2 edges
8. `ExampleUnitTest` - 2 edges
9. `Data Layer & Repositories` - 2 edges
10. `Kanban Board Screen Specification` - 2 edges

## Surprising Connections (you probably didn't know these)
- `PJK3 LHP Inspection & Certification Workflow` --semantically_similar_to--> `lhp_status Pipeline Enum`  [INFERRED] [semantically similar]
  LHP_PRD.md → LHP_Schema.md
- `Data Layer & Repositories` --references--> `lhp_records Database Table`  [INFERRED]
  LHP_Architecture.md → LHP_Schema.md
- `WorkManager SLA Notification System` --references--> `lhp_records Database Table`  [INFERRED]
  LHP_Design.md → LHP_Schema.md
- `Supabase BaaS Engine` --references--> `lhp_records Database Table`  [EXTRACTED]
  LHP_Architecture.md → LHP_Schema.md
- `LHP SLA & Deadline Tracking Goals` --conceptually_related_to--> `WorkManager SLA Notification System`  [INFERRED]
  LHP_PRD.md → LHP_Design.md

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **PJK3 Clean Architecture & UDF Stack** — lhp_architecture_ui_layer, lhp_architecture_viewmodel_layer, lhp_architecture_domain_layer, lhp_architecture_data_layer [EXTRACTED 1.00]
- **End-to-End LHP Status & Audit Pipeline** — lhp_prd_pjk3_workflow, lhp_schema_lhp_status_enum, lhp_schema_lhp_records_table, lhp_schema_lhp_status_logs_table, lhp_design_kanban_screen [INFERRED 0.95]

## Communities (25 total, 16 thin omitted)

### Community 0 - "Data, Backend & Schema Foundation"
Cohesion: 0.17
Nodes (12): Data Layer & Repositories, Offline-First Room Caching Rationale, Supabase BaaS Engine, WorkManager SLA Notification System, LHP SLA & Deadline Tracking Goals, PJK3 User Roles & RBAC (Admin, Inspector, Ahli K3), client_companies Database Table, lhp_attachments Storage Table (+4 more)

### Community 1 - "MainActivity & Compose UI Theme"
Cohesion: 0.36
Nodes (7): Greeting(), GreetingPreview(), MainActivity, ScheduleTrackAppTheme(), Bundle, ComponentActivity, Modifier

### Community 2 - "Android Screen Specifications & UI Layer"
Cohesion: 0.40
Nodes (5): Optimistic UI Updates Rationale, Jetpack Compose UI Layer, LHP Detail Screen Specification, Kanban Board Screen Specification, Schedule & Deadline Screen Specification

### Community 3 - "Gradle Wrapper Script"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **23 isolated node(s):** `LHP System Overview & Topology`, `ViewModel & StateFlow Layer`, `Domain Layer & Use Cases`, `Supabase BaaS Engine`, `Schedule & Deadline Screen Specification` (+18 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **16 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Are the 2 inferred relationships involving `lhp_records Database Table` (e.g. with `Data Layer & Repositories` and `WorkManager SLA Notification System`) actually correct?**
  _`lhp_records Database Table` has 2 INFERRED edges - model-reasoned connections that need verification._
- **What connects `LHP System Overview & Topology`, `ViewModel & StateFlow Layer`, `Domain Layer & Use Cases` to the rest of the system?**
  _23 weakly-connected nodes found - possible documentation gaps or missing edges._