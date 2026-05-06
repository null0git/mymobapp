# JsonQuizzz — Android

> Turn any JSON into an interactive quiz — now in your pocket.

A native Android port of the [JsonQuizzz](https://jsonquizzz.app) web platform. Same visual identity, same JSON schema, same 18+ question types, same Practice/Test modes — rebuilt natively with Jetpack Compose for performance, offline use, and Play Store distribution.

## Tech Stack

| Layer | Choice |
|---|---|
| Language | Kotlin 2.0 + Coroutines + Flow |
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation-Compose (type-safe routes) |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt |
| JSON | Kotlinx Serialization |
| Local DB | Room + DataStore |
| Networking | OkHttp |

## Project Structure

```
JsonQuizzz/
├── app/                    ← Main application module
├── core/
│   ├── design-system/      ← Theme, tokens, reusable components
│   ├── common/             ← Result, Dispatchers, extensions
│   └── testing/            ← Test fixtures
├── domain/                 ← Models, use cases, parser
├── data/                   ← Room, DataStore, repositories
└── feature/
    ├── quiz-player/        ← Quiz playing, setup, results
    ├── quiz-create/        ← JSON input, file import
    ├── library/            ← Saved quizzes
    ├── analytics/          ← Student dashboard
    ├── sharing/            ← Share & receive quizzes
    ├── leaderboard/        ← Shared quiz leaderboards
    ├── prompt-builder/     ← LLM prompt generator
    └── auth/               ← Profile & settings
```

## Requirements

- Android 7.0+ (API 24)
- JDK 17

## Build

```bash
./gradlew assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## Question Types Supported

1. Multiple Choice (Single)
2. Multiple Choice (Multiple)
3. Multiple Choice (Image)
4. True/False
5. Numeric (with tolerance)
6. Short Answer
7. Matching (drag-drop)
8. Fill in the Blank
9. Word Bank
10. Ordering
11. Error Identification
12. Reading Comprehension
13. Highlight Word
14. Audio Question
15. Picture Question
16. Video Question
17. Crossword
18. Multi-Part
19. Graph Question

Unknown question types fall back gracefully to `RawJson` — old/new schemas never crash the app.

## License

Proprietary — jsonquizzz@gmail.com
