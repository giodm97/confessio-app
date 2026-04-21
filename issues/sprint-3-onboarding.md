---
repo: giodm97/confessio-app
title: "Sprint 3 — OnboardingScreen with GDPR Art. 9 consent"
labels: enhancement
status: pending
base_branch: develop
---

## Overview

First-launch screen with mandatory GDPR Art. 9 consent and EU AI Act Art. 52
disclaimer. The "Begin" button is disabled until the mandatory checkbox is checked.
Consent is persisted in DataStore. On subsequent launches this screen is skipped.

## Changes required

### 1. `app/src/main/java/com/confessio/app/ui/onboarding/OnboardingScreen.kt`

The composable receives a `onConsentGranted: () -> Unit` callback and navigates
to `HomeScreen` when the user taps "Begin".

Layout (top to bottom):
- App name "Confessio" in `displayLarge` serif, color `ConfessioGold`, centered
- Subtitle "Redeem Your Sins" in `bodyLarge`, color `ConfessioMuted`, centered
- Divider with `ConfessioGoldMuted`
- Disclaimer card (surface background) with the following text:
  ```
  ⚠ Questa app utilizza un'intelligenza artificiale.
  Non stai parlando con un sacerdote reale.
  Questa app non sostituisce la confessione sacramentale.
  ```
- Privacy section with two `Row` items each containing a `Checkbox` + label:
  1. **Mandatory**: "Acconsento al trattamento dei dati di categoria speciale
     ai sensi dell'Art. 9 GDPR. Il testo delle confessioni non viene mai salvato.
     Vengono salvate solo categorie astratte, identificate da un codice anonimo."
  2. **Optional**: "Acconsento all'utilizzo anonimo per il miglioramento del servizio."
- Privacy policy note: "Puoi cancellare tutti i tuoi dati in qualsiasi momento
  dal menu Profilo."
- `Button("Inizia")` — enabled only when checkbox 1 is checked.
  On click: call `ConfessioDataStore.setConsent(context, true)`, then `onConsentGranted()`

Use `ConfessioTheme` colors throughout. Background: `ConfessioBg`.
Button: `ConfessioGold` background, `ConfessioBg` text.
Mandatory checkbox tint: `ConfessioGold`.

## Acceptance criteria

- [ ] "Inizia" button is disabled when mandatory checkbox is unchecked
- [ ] "Inizia" button becomes enabled when mandatory checkbox is checked
- [ ] `ConfessioDataStore.setConsent(context, true)` is called on button press
- [ ] AI disclaimer is visible with the exact warning text
- [ ] Both checkboxes are visually distinct (mandatory vs optional)
