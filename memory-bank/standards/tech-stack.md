# Tech Stack

## Overview
Native Android app (Kotlin, single-module or multi-module Gradle project) using Jetpack Compose for UI, MVVM over Clean Architecture for structure, Room for local persistence, and Hilt for DI. No backend/server — all data stays on-device.

## Languages
Kotlin

Sole language for the entire app (UI, domain, data layers) — idiomatic for modern Android development and required for Jetpack Compose.

## Framework
Jetpack Compose + Android Jetpack (Navigation Compose, ViewModel), Material Design 3, MVVM presentation pattern over Clean Architecture layering (Presentation → Domain → Data)

Compose is Google's current recommended UI toolkit; MVVM + Clean Architecture gives clear separation between UI state, business logic (e.g., the SMS parser engine), and data (Room).

## Dependency Injection
Hilt

Standard DI solution for Android; integrates with `ViewModel`, `WorkManager`, and Compose out of the box.

## Async
Kotlin Coroutines + Flow

Used throughout: background SMS import, live detection processing, and reactive Room queries exposed as `Flow`.

## Persistence
Room (SQLite)
See `standards/data-stack.md` for schema/ORM details.

## Charts
MPAndroidChart, bridged into Compose via `AndroidView`

Chosen over a fully Compose-native charting library because it's mature and covers the full range of chart types required (Pie, Monthly Bar, Daily Line, Category Breakdown, Income vs Expense) with less custom-drawing work. Revisit if the `AndroidView` bridge proves awkward once `003-dashboard-analytics` is underway.

## Settings Storage
Jetpack DataStore (Preferences DataStore)

## Testing
JUnit (unit tests), Espresso (instrumented/UI tests)

## Authentication
None — single-user, on-device app with no login/accounts.

## Infrastructure & Deployment
No backend/server. Personal/sideload distribution (APK) initially, not Play Store — this avoids the default-SMS-app policy restriction on `READ_SMS`/`RECEIVE_SMS` that would otherwise apply. Revisit before any future Play Store submission.

## Platform Targets
- **minSdk**: 26 (Android 8.0)
- **targetSdk**: latest stable at build time

## Build Tooling
Gradle with Kotlin DSL (`build.gradle.kts`)

## Package Manager
Gradle (dependency management via `libs.versions.toml` version catalog recommended)

## Decision Relationships
- Compose + MVVM + Clean Architecture together dictate the module layering used by Construction's DDD bolts (domain/data) and simple bolts (UI)
- Room (data-stack) is consumed by the domain layer's repository interfaces, not directly by UI
- No-backend/personal-distribution decision directly shaped intent `001-sms-transaction-capture`'s permission-model assumptions
