# Copilot Coding Agent Onboarding — android-kSync

## Overview
kSync is an Infomaniak-branded fork of [DAVx⁵](https://github.com/bitfireAT/davx5-ose) providing CalDAV/CardDAV sync for Infomaniak accounts. Modifications are minimal: Infomaniak branding, automatic Infomaniak account login flow. The active development branch is **`kSync-v5`**.

**Language**: Kotlin. **Architecture**: Hilt DI, Jetpack Compose (partial), KSP. **Java**: 21. **compileSdk**: 36. **minSdk**: 24.

## Build (Java 21 required)
```bash
./gradlew app-ose:assembleDebug    # the Infomaniak OSE app module
./gradlew build
```

## Tests & Lint (CI: `.github/workflows/test-dev.yml`)
CI runs on push to `main` and on every PR, in three parallel jobs:
```bash
# Job 1: compile
./gradlew app-ose:assembleDebug

# Job 2: lint + unit tests
./gradlew core:lintDebug app:lintOseDebug
./gradlew core:testDebugUnitTest      # no unit tests for app-ose currently

# Job 3: instrumented tests (emulator)
# Runs automatically in CI — not easily replicated locally without an emulator
```

## Project Layout
```
app-ose/                    # Infomaniak OSE application module (branding, entry points)
core/                       # Shared DAVx⁵ logic (sync engine, account management)
├── src/main/kotlin/        # CalDAV/CardDAV sync, account types, workers
gradle/libs.versions.toml   # All dependency versions (upstream DAVx⁵ deps + kSync additions)
settings.gradle.kts         # Gradle remote build cache config (optional; needs secrets)
```

## Key Rules
- Keep Infomaniak-specific changes **isolated** — minimize divergence from upstream DAVx⁵ to ease future merges.
- Always work on branch `kSync-v5`, not `main`.
- Lint must pass for both `core` and `app-ose`: `./gradlew core:lintDebug app:lintOseDebug`.
- No Compose BOM in this repo — Compose version comes from `libs.versions.toml` compose-bom entry used via `platform()`.
- When adding/removing a runtime dependency, update `LICENSES.md` at the repo root.
