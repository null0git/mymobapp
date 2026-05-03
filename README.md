# PaySMS - Smart Payment Verification via SMS

A lightweight, professional payment verification system that automatically detects, parses, and processes bank/payment SMS messages on Android.

## Features

### Payment Detection System
- Automatic SMS detection via BroadcastReceiver
- Filters bank/payment messages from noise
- Supports multiple Ethiopian banks (CBE, Abay, Telebirr, Awash, Dashen, BOA, Wegagen, Oromia)

### Smart Data Extraction
- **Amount** (ETB/Birr)
- **Sender name**
- **Bank name**
- **Account number** (masked)
- **Date & time**
- **Transaction type** (credit/debit)

### API Integration
- Configurable endpoint URL
- POST / GET / PUT methods
- Custom headers and API key/token
- JSON payload

### Email Notifications
- SMTP-based email alerts on payment received
- Fully configurable sender/receiver/host/port

### Offline Mode
- Stores transactions locally when offline
- Auto-syncs via WorkManager when internet returns
- Retry mechanism with configurable max retries

### Duplicate Protection
- SHA-256 hash-based SMS deduplication
- Prevents sending the same transaction twice

### Notification System
- Real-time alerts when payments arrive
- Sync status notifications

### Multi-Bank Support
- 8 predefined Ethiopian bank patterns
- Custom bank rule creation via UI
- Configurable keywords and amount patterns

### AI Detection (Optional)
- Toggle on/off
- Configurable confidence threshold

## UI Screens

| Screen | Description |
|--------|-------------|
| **Dashboard** | Total received/sent today, transaction count, weekly bar chart, bank summary |
| **History** | Full transaction list with search, bank filter, and detail bottom sheet |
| **Analytics** | Time-period stats (week/month/all), daily income chart, bank distribution |
| **Settings** | API config, email SMTP, SMS filters, bank rules, AI toggle, notifications |

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Database**: Room (SQLite)
- **Background**: WorkManager
- **Networking**: OkHttp
- **Email**: JavaMail (SMTP)
- **Settings**: DataStore Preferences
- **Navigation**: Jetpack Navigation Compose
- **Architecture**: MVVM

## Requirements

- Android 8.0+ (API 26)
- SMS permissions

## Build

```bash
./gradlew assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## Permissions

The app requires:
- `RECEIVE_SMS` - Listen for incoming SMS
- `READ_SMS` - Parse SMS content
- `INTERNET` - Send API requests and emails
- `ACCESS_NETWORK_STATE` - Check connectivity for offline mode
- `POST_NOTIFICATIONS` - Show payment alerts
- `RECEIVE_BOOT_COMPLETED` - Restart SMS listener on device reboot

## License

MIT
