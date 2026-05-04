# JsonQuizzz - Advanced Android Quiz App

A professional, feature-rich Android quiz application built with **Kotlin**, **Jetpack Compose**, and **Material Design 3**. Supports multiple question types, custom `.jqz` file format for quiz import/export, offline-first architecture, and comprehensive analytics.

## Features

### Core Quiz Engine
- **11 Question Types**: Multiple Choice, Image-based MC, True/False, Numeric Input, Short Answer, Matching, Fill-in-the-Blank, Graph-based, Code-based, Math-rendered, Chemical Equation
- **Section-based quiz structure** with per-question scoring
- **Hint system** with tap-to-reveal
- **Instant feedback** with explanations
- **Real-time progress tracking**

### Quiz Modes
- **Practice Mode**: Instant feedback, hints enabled, explanations visible
- **Exam Mode**: No hints, no feedback until the end
- **Review Mode**: All answers and explanations visible

### `.jqz` Custom File Format
- JSON-based with metadata wrapper
- Schema versioning for forward compatibility
- Full validation before loading
- Import from file picker / Export to share
- Supports all question types including math, code, chemistry

### Result & Analytics
- Score percentage, correct/incorrect counts
- Per-question review with explanations
- Section-based breakdown
- Weakness detection by tags/topics
- Retry incorrect questions

### UI & Design
- **Material Design 3** with dynamic theming
- **Dark/Light mode** toggle
- **8 selectable theme colors**
- Card-based layout with smooth animations
- Bottom navigation with 5 main sections
- Question navigation panel with jump-to-any

### Offline & Storage
- **Room database** for all quiz data
- **DataStore** for user preferences
- Full offline functionality
- Resume unfinished quizzes
- Bookmark system for review later

### Quiz Builder
- Visual quiz builder (no JSON needed)
- Add questions, select type, set options/answers/hints/explanations
- Section management
- Import/export `.jqz` files
- Category and difficulty selection

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose |
| Design | Material Design 3 |
| Architecture | MVVM |
| Database | Room |
| Preferences | DataStore |
| Navigation | Navigation Compose |
| JSON | Gson |
| Image Loading | Coil |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 34 (Android 14) |

## Project Structure

```
app/src/main/java/com/jsonquizzz/
├── data/
│   ├── local/          # Room database, DAOs, entities
│   └── repository/     # Data repositories
├── domain/
│   └── model/          # Domain models (Quiz, Question, etc.)
├── engine/             # Quiz engine (scoring, validation)
├── fileformat/         # .jqz file format handler
├── ui/
│   ├── components/     # Reusable UI components
│   ├── navigation/     # Navigation graph & routes
│   ├── screens/        # Screen composables & ViewModels
│   └── theme/          # Material 3 theming
└── util/               # Sample quizzes, utilities
```

## .jqz File Format

The `.jqz` (JsonQuizzz) file format is a JSON-based format with metadata:

```json
{
  "format": "jqz",
  "schemaVersion": "1.0",
  "createdWith": "JsonQuizzz Android",
  "exportedAt": 1700000000000,
  "quiz": {
    "title": "My Quiz",
    "sections": [
      {
        "title": "Section 1",
        "questions": [...]
      }
    ]
  }
}
```

See `app/src/main/assets/sample_quiz.jqz` for a complete example.

## Building

```bash
# Clone the repo
git clone https://github.com/null0git/JsonQuizzz.git
cd JsonQuizzz

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

## License

MIT License
