# Copilot Coding Agent Onboarding — android-kSync

## Overview
kSync is an Infomaniak-branded fork of [DAVx⁵](https://github.com/bitfireAT/davx5-ose) providing CalDAV/CardDAV sync for Infomaniak accounts. Modifications are minimal: Infomaniak branding, automatic Infomaniak account login. Active branch: **`kSync-v5`**. **Java 21 required.** `compileSdk 36`, `minSdk 24`.

## Build & Test (CI: `.github/workflows/test-dev.yml`)
```bash
# compile
./gradlew app-ose:assembleDebug

# lint + unit tests
./gradlew core:lintDebug app:lintOseDebug
./gradlew core:testDebugUnitTest
```

## Project Layout
```
app-ose/                    # Infomaniak OSE application module (branding, entry points)
core/                       # Shared DAVx⁵ logic (sync engine, account management)
gradle/libs.versions.toml
```

## PR Review Instructions

- Ensure strings are localized via `strings.xml` resources.
- Keep Infomaniak-specific changes **isolated** in `app-ose/` — minimize divergence from upstream DAVx⁵ to ease future merges.
- Always target branch `kSync-v5`, not `main`.
- Lint must pass for both modules: `./gradlew core:lintDebug app:lintOseDebug`.
- When adding/removing a runtime dependency, update `LICENSES.md` at the repo root.
