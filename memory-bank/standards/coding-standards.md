# Coding Standards

## Overview
Kotlin/Android standards emphasizing Clean Architecture layering, sealed-class-based error handling for expected failure paths (over exceptions), and strict privacy rules around SMS content — no raw SMS body or unmasked account numbers may ever be logged.

## Code Formatting

**Tool**: ktlint (official Kotlin code style)
**Key Settings**:
- Indentation: 4 spaces
- No wildcard imports
- Trailing commas: allowed in multi-line declarations

**Enforcement**: Gradle `ktlint` plugin run at build time (fails build on violation)

## Linting

**Tool**: detekt (static analysis) + Android Lint (platform-specific checks)
**Base Config**: detekt default ruleset with project overrides
**Strictness**: Balanced — errors on unused code and excessive complexity; warnings on style nits

**Key Rules**:
- No unused imports/variables: error — keeps generated code clean
- Cyclomatic complexity threshold: warn above 15 — flags parser/classification logic that's grown too tangled
- Android Lint permission checks: error — catches accidental `SEND_SMS` usage or other permission misuse before it ships

## Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Variables/functions | camelCase | `parseTransaction`, `isDuplicate` |
| Classes | PascalCase | `TransactionParser`, `SmsIngestionReceiver` |
| Composable functions | PascalCase | `DashboardScreen`, `TransactionRow` |
| Constants | UPPER_SNAKE_CASE | `IMPORT_WINDOW_MONTHS`, `DEDUP_WINDOW_SECONDS` |
| Packages | all-lowercase, no underscores | `com.app.smsexpensetracker.parser` |

**File Naming**:
- Kotlin classes/composables: one public class/composable per file, filename matches the type name
- Tests: `{ClassUnderTest}Test.kt` (unit), `{ClassUnderTest}InstrumentedTest.kt` (Espresso)

## File Organization

**Pattern**: Clean Architecture, package-per-layer within each unit/feature

**Structure**:
```text
app/src/main/kotlin/.../<feature-or-unit>/
  domain/
    model/          # Entities, value objects (e.g., ParsedTransaction)
    repository/     # Repository interfaces
    usecase/        # Business logic operations
  data/
    local/          # Room entities, DAOs
    repository/     # Repository implementations
  presentation/
    (ViewModels, Compose screens — only for units with a UI)
```

**Conventions**:
- Tests: mirrored package structure under `src/test/` (unit) and `src/androidTest/` (instrumented)
- No barrel/index files (not a Kotlin convention) — import directly

## Testing Strategy

**Framework**: JUnit (unit), Espresso (instrumented/UI)
**Coverage Target**: 80% (already applied as the standard across all unit briefs in this project)

**Test Types**:

| Type | Tool | When to Use |
|------|------|-------------|
| Unit | JUnit | Domain logic (parser rules, classification, dedup key computation) |
| Integration | JUnit + in-memory Room | Repository/DAO behavior |
| Instrumented/UI | Espresso | Compose screens, permission flows, `BroadcastReceiver` behavior |

**Conventions**:
- Test naming: `` `given X, when Y, then Z`() `` (backtick function names, mirrors story Acceptance Criteria's Given/When/Then phrasing)
- Mock strategy: mock at architectural boundaries (e.g., mock the SMS content resolver in parser tests) — don't mock internal domain logic
- Test data: sample SMS fixtures per bank/MFS, stored as test resources (supports the "at least one passing sample-SMS test case per bank" requirement from intent 001)

## Error Handling

**Pattern**: Sealed-class result types for expected failure paths, not exceptions

```kotlin
sealed class ParseResult {
    data class Matched(val transaction: ParsedTransaction) : ParseResult()
    data class Unrecognized(val rawBody: String, val sender: String, val reason: String) : ParseResult()
}
```

**Custom Errors**: Yes — domain-specific sealed classes (e.g., `ParseResult`) instead of throwing for expected outcomes like "SMS didn't match any pattern." Exceptions are reserved for truly unexpected/programmer errors (e.g., malformed Room migration).

**API Errors**: N/A — no backend/API in this app

## Logging

**Tool**: Android `Log` (or Timber if adopted later)
**Format**: Plain text, tagged by class/component

**Levels**:

| Level | Usage |
|-------|-------|
| error | Parse pipeline crashes, unexpected exceptions |
| warn | Unrecognized SMS flagged, permission denied |
| info | Import job started/completed, live SMS processed |
| debug | Detailed parser matching steps (dev builds only) |

**Rules**:
- **Never log**: raw SMS body, unmasked account numbers, any PII beyond what's already masked at extraction time
- **Always log (at info)**: import job lifecycle events, counts of processed/flagged messages (no message content)
