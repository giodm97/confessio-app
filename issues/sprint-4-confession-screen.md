---
repo: giodm97/confessio-app
title: "Sprint 4 — ConfessionScreen + ConfessionViewModel"
labels: enhancement
status: pending
base_branch: develop
---

## Overview

The core screen where the user types their confession and submits it to Father
Confessio. Shows a loading state during the AI call, then navigates to
AbsolutionScreen with the response.

## Changes required

### 1. `app/src/main/java/com/confessio/app/ui/confession/ConfessionViewModel.kt`

```kotlin
class ConfessionViewModel : ViewModel() {

    var confessionText by mutableStateOf("")
        private set

    var uiState by mutableStateOf<ConfessionUiState>(ConfessionUiState.Idle)
        private set

    fun onTextChange(text: String) {
        if (text.length <= 1000) confessionText = text
    }

    fun confess(context: Context) {
        if (confessionText.isBlank()) return
        viewModelScope.launch {
            uiState = ConfessionUiState.Loading
            val uuid = ConfessioDataStore.getUuid(context)
            uiState = try {
                val response = ApiClient.confessioApi.confess(
                    ConfessionRequest(uuid, confessionText, emptyList())
                )
                ConfessionUiState.Success(response)
            } catch (e: Exception) {
                ConfessionUiState.Error(e.message ?: "Errore sconosciuto")
            }
        }
    }

    fun reset() { uiState = ConfessionUiState.Idle; confessionText = "" }
}

sealed class ConfessionUiState {
    object Idle    : ConfessionUiState()
    object Loading : ConfessionUiState()
    data class Success(val response: AbsolutionResponse) : ConfessionUiState()
    data class Error(val message: String) : ConfessionUiState()
}
```

### 2. `app/src/main/java/com/confessio/app/ui/confession/ConfessionScreen.kt`

```kotlin
@Composable
fun ConfessionScreen(
    onAbsolutionReady: (AbsolutionResponse) -> Unit,
    onBack: () -> Unit,
    viewModel: ConfessionViewModel = viewModel()
)
```

Layout:
- Background `ConfessioBg`
- Top bar: back arrow (`←`) in `ConfessioGold` + title "Confessati" in `ConfessioInk`
- Label text: *"Parlami dei tuoi peccati, figlio mio..."* in `ConfessioMuted`
- `OutlinedTextField` multiline, `confessionText`, `ConfessioInk` text,
  `ConfessioGoldMuted` border, fills available height
- Character counter below field: `${confessionText.length}/1000` in `ConfessioMuted`
- Disclaimer below counter: *"Il testo non viene mai salvato."* in `ConfessioMuted` small
- Button "Confessa" → `ConfessioGold` bg, `ConfessioBg` text, disabled when text blank
  or state is Loading
- When state is `Loading`: replace button with `CircularProgressIndicator` in `ConfessioGold`
- When state is `Success`: call `onAbsolutionReady(response)` in a `LaunchedEffect`
- When state is `Error`: show `Snackbar` with error message

## Acceptance criteria

- [ ] "Confessa" button is disabled when text field is empty
- [ ] Character counter stops at 1000 (input beyond 1000 chars is rejected)
- [ ] Loading spinner replaces the button during API call
- [ ] On success, `onAbsolutionReady` is called with the response
- [ ] Disclaimer "Il testo non viene mai salvato" is always visible
- [ ] Error state shows a Snackbar without crashing
