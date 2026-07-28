# Data Stack

## Overview
All persistence is local: a single SQLite database managed through Room, with no server-side or cloud database. This matches the app's no-backend, on-device-only architecture.

## Database
SQLite (via Room)

Local-only by design — no data leaves the device (per intent `001-sms-transaction-capture`'s security constraints). Room manages schema, migrations, and compile-time-verified queries over SQLite. Encryption at rest (e.g., SQLCipher) is deferred as a future hardening item, not part of the current standard.

## ORM / Database Client
Room

Annotation-based entities (`@Entity`) and DAOs (`@Dao`), compile-time SQL verification, and native support for `Flow`/`suspend` return types — integrates directly with the Coroutines/Flow choice in `tech-stack.md` and feeds reactive state up through ViewModels to Compose UI.

## Known Entities (cross-intent)
- `Transaction`, `SMSLog` — defined in `001-sms-transaction-capture`
- `Category`, `Merchant`, `Budget`, `Settings` — planned for later intents (002-transaction-management, 004-budget-tracking, 006-app-settings-appearance)

## Decision Relationships
- Room was chosen because it's the Android-standard, Compose/Coroutines-friendly persistence library, and requires no additional infrastructure given the app has no backend
- The `Transaction`/`SMSLog` schema from intent 001 is treated as a stable contract other intents build on (see that intent's `003-transaction-persistence` unit brief)
