---
repo: giodm97/confessio-app
title: "Sprint 3 — ConfessioDataStore: UUID persistence + GDPR consent"
labels: enhancement
status: pending
---

## Overview

Implement DataStore Preferences wrapper for UUID generation/retrieval and GDPR
consent flag. The UUID is generated client-side on first launch and never
associated with any identity on the server.

## Changes required

### 1. `app/src/main/java/com/confessio/app/data/ConfessioDataStore.kt`

```kotlin
private val Context.dataStore by preferencesDataStore(name = "confessio_prefs")

object ConfessioDataStore {

    private val UUID_KEY    = stringPreferencesKey("user_uuid")
    private val CONSENT_KEY = booleanPreferencesKey("gdpr_consent")

    suspend fun getOrCreateUuid(context: Context): String {
        val prefs = context.dataStore.data.first()
        val existing = prefs[UUID_KEY]
        if (existing != null) return existing

        val newUuid = UUID.randomUUID().toString()
        context.dataStore.edit { it[UUID_KEY] = newUuid }
        return newUuid
    }

    suspend fun getUuid(context: Context): String? =
        context.dataStore.data.first()[UUID_KEY]

    suspend fun setConsent(context: Context, granted: Boolean) {
        context.dataStore.edit { it[CONSENT_KEY] = granted }
    }

    suspend fun hasConsent(context: Context): Boolean =
        context.dataStore.data.first()[CONSENT_KEY] ?: false

    suspend fun clearAll(context: Context) {
        context.dataStore.edit { it.clear() }
    }
}
```

### 2. `app/build.gradle.kts`

Ensure the DataStore dependency is present in the `dependencies` block:
```kotlin
implementation("androidx.datastore:datastore-preferences:1.1.1")
```

## Acceptance criteria

- [ ] `getOrCreateUuid` generates a new UUID on first call and returns the same
      UUID on subsequent calls
- [ ] `clearAll` removes both UUID and consent flag
- [ ] `hasConsent` returns `false` if the key was never set
- [ ] DataStore dependency present in `app/build.gradle.kts`
