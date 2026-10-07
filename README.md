# Learning Dashboard

Android · Kotlin · Jetpack Compose · MVVM + Repository · Room · Retrofit

**[Download APK](https://github.com/kevalpanwala27/learning-dashboard/releases/latest)** · **[Demo video (2 min)](demo/demo.mp4)**

Login → course list → course details → mark lessons complete → works offline after the first load.
Demo login: any valid email with a 6+ character password. `blocked@example.com` always fails, to show the error state.

<p>
  <img src="docs/screenshots/login.png" width="200" alt="Login" />
  <img src="docs/screenshots/dashboard.png" width="200" alt="Dashboard" />
  <img src="docs/screenshots/details.png" width="200" alt="Course details" />
</p>

**Run:** `./gradlew installDebug` fetches `mock-api/courses.json` from a local server (`cd mock-api && python3 -m http.server 8080`) so loading, failure and offline can be tested without GitHub. `assembleRelease` fetches the same file from this repo over HTTPS. `./gradlew test` runs the unit tests.

```
ui/        Compose screens + ViewModels, one sealed UiState per screen in a StateFlow
domain/    Course, Lesson, ProgressCalculator, LoginValidator, repository interfaces (no Android imports)
data/      CourseRepositoryImpl, MockAuthRepository → Retrofit (remote) + Room / EncryptedSharedPreferences (local)
di/        AppContainer: hand-wired dependencies
```

## 1. Architecture

MVVM with a repository, split into `ui`, `domain` and `data`. Each screen's ViewModel exposes a sealed `UiState`, so loading, empty, error and success are distinct types the compiler checks and cannot overlap. The repository is the only class that knows there are two data sources. The domain layer has no Android imports, which is why the progress calculator and the repository are tested with plain JUnit and fakes. Dependencies are wired by hand in one file rather than with Hilt: for three screens every wire stays visible, the build stays free of annotation processing, and moving to Hilt later is mechanical because every class already takes its dependencies through its constructor.

## 2. Offline support

Room is the single source of truth. The UI observes a `Flow` from Room and never reads the network directly. A refresh is a side job that writes into Room. On `IOException` the repository reports `Offline`, the list keeps showing cached rows and a banner says so. If Room is empty and the device is offline, the screen shows an error with a retry button. Progress is never stored: it is derived as completed ÷ total lessons. The server's `progress` field only seeds which lessons start completed on the first load. After that the server owns titles and the catalogue, the device owns completion, so a refresh can never undo the user's work. The sample data's 40 % course with 16 lessons therefore shows 38 % (6 of 16), because 40 % of 16 is not a whole lesson. That is deliberate.

## 3. Security

The session token is kept in `EncryptedSharedPreferences`, encrypted with an AES-256 key in the Android Keystore, so it is never plain text on disk and is not readable from backups or a rooted file browser. In production: a short-lived access token with a refresh token, attached by an OkHttp interceptor rather than by hand, cleared on logout and on a 401, never logged. No secrets live in the repo or in `BuildConfig`.

## 4. Scale: 1 million users, hundreds of courses

1. Cursor-based pagination with `Paging 3` backed by Room, so the list never loads the whole catalogue.
2. `ETag` / `If-None-Match` and `Cache-Control` so most refreshes are a 304 with no payload.
3. A `pending_updates` table synced by WorkManager with server-side versioning, instead of local-only completion.
4. Feature modules with Hilt so `login`, `catalogue` and `player` can be owned by separate teams and builds stay fast.
5. Crash reporting, structured logs with request IDs, and a few product metrics (refresh failure rate, time to first course) so regressions are seen before users report them.

## 5. The same app on iOS / macOS

The layering carries over one-to-one. SwiftUI views are driven by an `@Observable` view model holding an enum state (`.loading`, `.empty`, `.error`, `.success`), the Swift equivalent of a sealed interface. The repository stays a protocol with a `URLSession` implementation and SwiftData (or Core Data) as the local store observed with `@Query`, so the UI again reads only from the database. `async`/`await` replaces coroutines, `AsyncStream` replaces `Flow`, the token goes in the Keychain, and the progress and repository tests move to XCTest unchanged in intent. With Swift Package Manager modules the same code targets macOS using `NavigationSplitView` instead of `NavigationStack`.
