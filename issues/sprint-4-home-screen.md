---
repo: giodm97/confessio-app
title: "Sprint 4 — HomeScreen + HomeViewModel"
labels: enhancement
status: pending
---

## Overview

Main screen after onboarding. Shows a personalised greeting from Father Confessio
based on session count, fetched from the backend. Two action buttons: confess and
view profile.

## Changes required

### 1. `app/src/main/java/com/confessio/app/ui/home/HomeViewModel.kt`

```kotlin
class HomeViewModel : ViewModel() {

    var uiState by mutableStateOf<HomeUiState>(HomeUiState.Loading)
        private set

    fun loadProfile(context: Context) {
        viewModelScope.launch {
            val uuid = ConfessioDataStore.getUuid(context)
            if (uuid == null) {
                uiState = HomeUiState.NoProfile
                return@launch
            }
            uiState = try {
                val profile = ApiClient.confessioApi.getProfile(uuid)
                HomeUiState.Loaded(profile)
            } catch (e: Exception) {
                HomeUiState.NoProfile
            }
        }
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    object NoProfile : HomeUiState()
    data class Loaded(val profile: ProfileResponse) : HomeUiState()
}
```

### 2. `app/src/main/java/com/confessio/app/ui/home/HomeScreen.kt`

```kotlin
@Composable
fun HomeScreen(
    onConfessClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
)
```

Layout:
- Background `ConfessioBg`, full screen
- Centered column with vertical padding
- Candlestick icon or cross icon (use `Icons.Default` or a Unicode symbol: `✝`)
  in `ConfessioGold`, size 64dp
- Greeting text in `headlineMedium` serif, `ConfessioInk`, centered.
  Derive greeting from session count:
  - 0: *"Benvenuto, figlio mio. Sono qui per ascoltarti."*
  - 1–3: *"Bentornato. Il Signore ti accoglie sempre."*
  - 4+: *"Bentornato, figlio fedele. Il tuo cammino continua."*
  - Loading/error: *"Che il Signore ti guidi."*
- Primary button "Confessati" → `ConfessioGold` background, `ConfessioBg` text,
  full width, `onConfessClick`
- Secondary outlined button "Il mio profilo" → `ConfessioGold` border,
  `ConfessioInk` text, full width, `onProfileClick`

Call `viewModel.loadProfile(context)` in a `LaunchedEffect(Unit)`.
Use `androidx.compose.ui.platform.LocalContext`.

## Acceptance criteria

- [ ] Greeting changes based on `sessionCount` from backend
- [ ] When profile fetch fails, fallback greeting is shown (no crash)
- [ ] "Confessati" button calls `onConfessClick`
- [ ] "Il mio profilo" button calls `onProfileClick`
- [ ] Loading state shows a `CircularProgressIndicator` in `ConfessioGold`
