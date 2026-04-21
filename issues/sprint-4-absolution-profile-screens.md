---
repo: giodm97/confessio-app
title: "Sprint 4 — AbsolutionScreen + ProfileScreen + ProfileViewModel"
labels: enhancement
status: pending
base_branch: develop
---

## Overview

Implement the two remaining screens: AbsolutionScreen (shows the AI response with
prayers) and ProfileScreen (shows sin history + delete account option).

---

## AbsolutionScreen

### `app/src/main/java/com/confessio/app/ui/absolution/AbsolutionScreen.kt`

```kotlin
@Composable
fun AbsolutionScreen(
    absolution: AbsolutionResponse,
    onAmen: () -> Unit
)
```

Layout:
- Background `ConfessioBg`, centered column, vertical scroll
- Cross symbol `✝` in `ConfessioGold`, large (48sp)
- Title "Assoluzione" in `headlineMedium` serif, `ConfessioInk`
- Absolution text in `bodyLarge`, `ConfessioInk`, italic, center aligned,
  padded card with `ConfessioSurface` background
- Divider `ConfessioGoldMuted`
- Section title "La tua penitenza" in `titleMedium`, `ConfessioGold`
- Prayer rows — show only prayers with count > 0:
  - `🙏 Ave Maria × ${prayers.hailMary}`
  - `🙏 Padre Nostro × ${prayers.ourFather}`
  - `🙏 Gloria × ${prayers.gloryBe}`
  Each row: `bodyLarge`, `ConfessioInk`
- Button "Amen" full width, `ConfessioGold` bg, `ConfessioBg` text.
  On click: call `PUT /profile/{uuid}/session` (fire-and-forget in coroutine),
  then call `onAmen()`

---

## ProfileScreen

### `app/src/main/java/com/confessio/app/ui/profile/ProfileViewModel.kt`

```kotlin
class ProfileViewModel : ViewModel() {

    var uiState by mutableStateOf<ProfileUiState>(ProfileUiState.Loading)
        private set

    fun loadProfile(context: Context) {
        viewModelScope.launch {
            val uuid = ConfessioDataStore.getUuid(context) ?: run {
                uiState = ProfileUiState.NoProfile; return@launch
            }
            uiState = try {
                ProfileUiState.Loaded(ApiClient.confessioApi.getProfile(uuid))
            } catch (e: Exception) {
                ProfileUiState.Error(e.message ?: "Errore")
            }
        }
    }

    fun deleteProfile(context: Context, onDeleted: () -> Unit) {
        viewModelScope.launch {
            val uuid = ConfessioDataStore.getUuid(context) ?: return@launch
            try {
                ApiClient.confessioApi.deleteProfile(uuid)
                ConfessioDataStore.clearAll(context)
                onDeleted()
            } catch (e: Exception) {
                uiState = ProfileUiState.Error(e.message ?: "Errore")
            }
        }
    }
}

sealed class ProfileUiState {
    object Loading   : ProfileUiState()
    object NoProfile : ProfileUiState()
    data class Loaded(val profile: ProfileResponse) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}
```

### `app/src/main/java/com/confessio/app/ui/profile/ProfileScreen.kt`

```kotlin
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onAccountDeleted: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
)
```

Layout:
- Background `ConfessioBg`
- Top bar: back arrow in `ConfessioGold` + title "Il mio profilo"
- Session count: *"Sessioni di confessione: N"* in `bodyLarge`, `ConfessioInk`
- Section title "Le tue tendenze" in `titleMedium`, `ConfessioGold`
- `LazyColumn` of `sinSummary` items. Each row:
  - Category name capitalized, `bodyLarge`, `ConfessioInk`
  - `LinearProgressIndicator` scaled to `totalWeight / maxWeight`, color `ConfessioGold`
  - Occurrences label `${occurrences}×` in `ConfessioMuted`
- Empty state when `sinSummary` is empty: *"Nessun peccato registrato ancora."*
- Danger zone at bottom: outlined button "Cancella tutti i dati" in `ConfessioError`.
  On click: show `AlertDialog` confirming deletion.
  On confirm: call `viewModel.deleteProfile(context) { onAccountDeleted() }`
  Dialog text: *"Sei sicuro? Tutti i tuoi dati verranno eliminati definitivamente."*

Call `viewModel.loadProfile(context)` in `LaunchedEffect(Unit)`.

## Acceptance criteria

- [ ] `AbsolutionScreen` shows only prayers with count > 0
- [ ] "Amen" button fires session increment (fire-and-forget, no blocking)
- [ ] `ProfileScreen` sin bars scale proportionally to `totalWeight`
- [ ] Delete confirm dialog appears before calling the API
- [ ] After deletion: `ConfessioDataStore.clearAll` is called and `onAccountDeleted` navigates to onboarding
- [ ] Empty sin summary shows fallback text instead of an empty list
