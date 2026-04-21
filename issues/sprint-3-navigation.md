---
repo: giodm97/confessio-app
title: "Sprint 3 — NavGraph, MainActivity, ConfessioApplication"
labels: enhancement
status: pending
base_branch: develop
---

## Overview

Wire up the navigation graph, MainActivity, and Application class. On launch,
check DataStore consent: if granted go to HomeScreen, otherwise go to OnboardingScreen.

## Changes required

### 1. `app/src/main/java/com/confessio/app/ConfessioApplication.kt`

```kotlin
class ConfessioApplication : Application()
```

Register in `AndroidManifest.xml`:
```xml
android:name=".ConfessioApplication"
```

### 2. `app/src/main/java/com/confessio/app/ui/navigation/NavGraph.kt`

Routes as a sealed class or object:
```kotlin
object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME       = "home"
    const val CONFESSION = "confession"
    const val ABSOLUTION = "absolution"
    const val PROFILE    = "profile"
}
```

`NavGraph` composable:
```kotlin
@Composable
fun NavGraph(navController: NavHostController, startDestination: String)
```

Route wiring:
- `ONBOARDING` → `OnboardingScreen(onConsentGranted = { navController.navigate(Routes.HOME) { popUpTo(Routes.ONBOARDING) { inclusive = true } } })`
- `HOME` → `HomeScreen(onConfessClick = { navController.navigate(Routes.CONFESSION) }, onProfileClick = { navController.navigate(Routes.PROFILE) })`
- `CONFESSION` → `ConfessionScreen(onAbsolutionReady = { absolution, prayers, score -> navController.navigate(...) }, onBack = { navController.popBackStack() })` — pass absolution data as nav arguments (encode as URL-safe string or use a shared ViewModel)
- `ABSOLUTION` → `AbsolutionScreen(onAmen = { navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } })`
- `PROFILE` → `ProfileScreen(onBack = { navController.popBackStack() })`

Use `AnimatedNavHost` with `fadeIn`/`fadeOut` transitions.

### 3. `app/src/main/java/com/confessio/app/MainActivity.kt`

```kotlin
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConfessioTheme {
                val context = LocalContext.current
                var startDestination by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    startDestination = if (ConfessioDataStore.hasConsent(context))
                        Routes.HOME else Routes.ONBOARDING
                }

                startDestination?.let {
                    val navController = rememberNavController()
                    NavGraph(navController = navController, startDestination = it)
                }
            }
        }
    }
}
```

Use `androidx.compose.ui.platform.LocalContext` and
`androidx.compose.ui.platform.LocalLifecycleOwner` (NOT `androidx.lifecycle.compose`).

## Acceptance criteria

- [ ] `AndroidManifest.xml` registers `ConfessioApplication`
- [ ] First launch (no consent) starts on `OnboardingScreen`
- [ ] After consent, subsequent launches start on `HomeScreen`
- [ ] All 5 routes are defined in `NavGraph`
- [ ] Back navigation works from `ConfessionScreen` and `ProfileScreen`
