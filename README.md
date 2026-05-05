# telebirr SuperApp Clone

A mobile app clone of the telebirr SuperApp by Ethio Telecom, built with Kotlin and Jetpack Compose.

## Features

### Login Screen
- Phone number input with +251 country code prefix
- Auto-fills phone number after first login
- Biometric authentication (fingerprint) for returning users
- Clean, branded UI matching telebirr design

### Home Screen
- User greeting with avatar
- Balance display (ETB) with show/hide toggle
- Endekise and Reward balance display
- Service grid: Send Money, Buy Airtime, Buy Package, Cash In/Out, Financial Service, Pay for Merchant, Payment, Apps
- Promotional banner carousel
- Transaction Details link
- Quick actions: Pay Ethio telecom Bill, Schedule Payment, Transfer to Bank, Transfer to Wallet
- Scan QR button

### Payment Screen
- Expandable payment categories with sub-items:
  - Utility, Tax & Government Service, Transport Service
  - Entertainment Service, E-commerce, Event & Ticketing
  - Education Fee, Fundraising, Insurance
- Search functionality
- Financial services promotional banner

### Apps Screen
- Grid of third-party apps: My Ethiotel, Telegebeya, Ethiopian Airlines, DSTV, Public Transport, WebSprix, Zmall, Ahun, Digital Equb, Tikus Delivery, Hulu beje, Ashewa
- Additional apps section: Guzo Go, ACT American, AfroRead, Awra Store, MoveEt, Safe
- Promotional banner

### Account Screen
- User profile with avatar, name, and phone number
- Menu items: My Profile, My Cards, Transaction History, Biometric Settings, Change PIN, Notifications, Language
- Additional: Invite Friends, Rate Us, Help & Support, About
- **Hidden Settings**: Tap "Version 1.0.0" text 7 times to reveal editable fields for all account details (Display Name, Full Name, Phone, Email, Account Number, Balance, Endekise Balance, Reward Balance, Gender, Date of Birth, Region, City)
- Logout button

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Authentication**: AndroidX Biometric
- **Storage**: DataStore Preferences
- **Navigation**: Jetpack Navigation Compose
- **Architecture**: MVVM

## Requirements

- Android 8.0+ (API 26)

## Build

```bash
./gradlew assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## License

MIT
