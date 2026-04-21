---
repo: giodm97/confessio-app
title: "Sprint 3 — Data models, Retrofit API interface, ApiClient"
labels: enhancement
status: pending
base_branch: develop
---

## Overview

Create the network layer: data classes matching the backend DTOs, Retrofit interface
for all endpoints, and the singleton ApiClient. Base URL is configurable via
BuildConfig so debug points to localhost and release to AWS EB.

## Changes required

### 1. `app/src/main/java/com/confessio/app/data/ConfessioModels.kt`

```kotlin
data class ConfessionRequest(
    val uuid: String?,
    val confessionText: String,
    val sinCategories: List<String> = emptyList()
)

data class AbsolutionResponse(
    val absolution: String,
    val prayers: PrayersDto,
    val gravityScore: Int
)

data class PrayersDto(
    val hailMary: Int,
    val ourFather: Int,
    val gloryBe: Int
)

data class ProfileRequest(val uuid: String)

data class ProfileResponse(
    val uuid: String,
    val sessionCount: Int,
    val sinSummary: List<SinSummaryItem>,
    val createdAt: String
)

data class SinSummaryItem(
    val category: String,
    val totalWeight: Int,
    val occurrences: Int
)

data class SessionResponse(
    val uuid: String,
    val sessionCount: Int
)
```

### 2. `app/src/main/java/com/confessio/app/network/ConfessioApi.kt`

```kotlin
interface ConfessioApi {

    @POST("confession")
    suspend fun confess(@Body request: ConfessionRequest): AbsolutionResponse

    @POST("profile")
    suspend fun createProfile(@Body request: ProfileRequest): ProfileResponse

    @GET("profile/{uuid}")
    suspend fun getProfile(@Path("uuid") uuid: String): ProfileResponse

    @PUT("profile/{uuid}/session")
    suspend fun incrementSession(@Path("uuid") uuid: String): SessionResponse

    @DELETE("profile/{uuid}")
    suspend fun deleteProfile(@Path("uuid") uuid: String): Response<Unit>
}
```

### 3. `app/src/main/java/com/confessio/app/network/ApiClient.kt`

```kotlin
object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    val confessioApi: ConfessioApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build()
            )
            .build()
            .create(ConfessioApi::class.java)
    }
}
```

Use `10.0.2.2` as the base URL (Android emulator → host machine). This will be
updated to the AWS EB URL in Sprint 5.

## Acceptance criteria

- [ ] All data classes match the backend DTO field names exactly
- [ ] `ConfessioApi` uses suspend functions for all endpoints
- [ ] `deleteProfile` returns `Response<Unit>` to allow 204 handling
- [ ] `ApiClient` uses 60s read timeout (AI calls can be slow)
- [ ] Files compile without errors (check via Android Studio)
