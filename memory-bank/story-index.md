# Global Story Index

## Overview
- **Total stories**: 13
- **Generated**: 13
- **Last updated**: 2026-07-28

---

## Stories by Intent

### 001-sms-transaction-capture

#### Unit: 001-sms-ingestion

- [x] **001-request-sms-permission** (sms-ingestion): Request SMS permission with rationale - Must - ✅ GENERATED
- [x] **002-handle-permission-denial-retry** (sms-ingestion): Handle permission denial and retry - Must - ✅ GENERATED
- [x] **003-import-historical-transaction-sms** (sms-ingestion): Import last 6 months of transaction SMS - Must - ✅ GENERATED
- [x] **004-filter-non-transactional-sms** (sms-ingestion): Filter out OTP and promotional SMS - Must - ✅ GENERATED
- [x] **005-detect-live-incoming-sms** (sms-ingestion): Detect and forward new SMS live - Must - ✅ GENERATED

#### Unit: 002-transaction-parser

- [x] **001-define-bank-parser-configuration** (transaction-parser): Data-driven parser config for banks/MFS - Must - ✅ GENERATED
- [x] **002-parse-transaction-amount-and-metadata** (transaction-parser): Extract amount/currency/date/time/bank/account/merchant - Must - ✅ GENERATED
- [x] **003-identify-bank-from-sender-with-fallback** (transaction-parser): Identify bank via sender ID with body-keyword fallback - Should - ✅ GENERATED
- [x] **004-classify-transaction-type** (transaction-parser): Classify into Expense/Income/Transfer/ATM/Card/Refund - Must - ✅ GENERATED
- [x] **005-flag-unrecognized-sms-for-review** (transaction-parser): Flag unparseable-but-known-sender SMS for review - Should - ✅ GENERATED

#### Unit: 003-transaction-persistence

- [x] **001-define-room-schema-transaction-smslog** (transaction-persistence): Room entities + repository for Transaction/SMSLog - Must - ✅ GENERATED
- [x] **002-deduplicate-transactions-on-insert** (transaction-persistence): Dedup key logic on insert - Must - ✅ GENERATED
- [x] **003-persist-import-and-live-results** (transaction-persistence): Unified insert path for import + live pipelines - Must - ✅ GENERATED

---

## Stories by Status

- **Planned**: 0
- **Generated**: 13
- **In Progress**: 0
- **Completed**: 0
