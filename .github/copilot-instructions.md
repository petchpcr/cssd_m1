# CSSD M1 - Copilot Instructions

**Project**: Android hospital supply chain management application (CSSD - Central Sterile Supply Department)  
**Min SDK**: 21 | **Target SDK**: 30 | **Compile SDK**: 33  
**Key Stack**: Kotlin + Java, SQLite (local) + MS SQL Server (backend), HTTP REST + JTDS for DB connections

## Architecture Overview

### Data Flow
1. **Local SQLite** (`CSSD_M1.db`): Offline-first data caching via [FeedReaderDbHelper](app/src/main/java/com/poseintelligence/cssdm1/core/SQLiteDB/FeedReaderDbHelper.java)
2. **MS SQL Server Backend**: Direct JTDS connection (via [DBConnect](app/src/main/java/com/poseintelligence/cssdm1/core/connect/DBConnect.java)) or HTTP REST API (via [HTTPConnect](app/src/main/java/com/poseintelligence/cssdm1/core/connect/HTTPConnect.java))
3. **Hardware Integration**: Sunmi printer (thermal receipts), Bluetooth/USB peripherals, barcode scanning (ZXing)

### Key Components
- **Global State**: [CssdProject](app/src/main/java/com/poseintelligence/cssdm1/CssdProject.java) extends `Application` - holds user session (`Parameter`), debug flags, connection configs
- **UI Structure**: Activity-based navigation (no fragments except nav components) → Menu_* folders (e.g., [Menu_Receive](app/src/main/java/com/poseintelligence/cssdm1/Menu_Receive/), Menu_Sterile, Menu_Dispensing)
- **Data Models**: Plain POJOs in [model/](app/src/main/java/com/poseintelligence/cssdm1/model/) - no annotations, no ORMs
- **Adapters**: ListBox* and ListItem* custom adapters for RecyclerView/ListView in [adapter/](app/src/main/java/com/poseintelligence/cssdm1/adapter/)

## Critical Patterns

### Database Connections
- **Direct SQL Server**: `DBConnect.ConnectionString` hardcoded with `host: 10.11.9.6`, `user: sa` - uses `CallableStatement` for stored procedures
- **HTTP Fallback**: `HTTPConnect.sendPostRequest()` wraps HTTPS + custom SSL certificate handling
- **Local Cache**: `PreferenceUtil` for SharedPreferences, SQLite for larger datasets
- **CRITICAL**: Both DBConnect and HTTPConnect check `CssdProject.isNonActiveTime` for session timeout (increment on network calls, reset to 0)

### Activity Navigation Pattern
```java
// Standard pattern across all activities:
public void callActivity(Class c, String param1, String param2) {
    Intent it = new Intent(CurrentActivity.this, c);
    if (param1 != null) it.putExtra("key", param1);
    startActivity(it);
    finish(); // Usually followed by finish()
}
```
Entry point: [MainActivity](app/src/main/java/com/poseintelligence/cssdm1/MainActivity.java) → [SplashScreen](app/src/main/java/com/poseintelligence/cssdm1/SplashScreen.java) → [Login](app/src/main/java/com/poseintelligence/cssdm1/Login.java) → [MainMenu](app/src/main/java/com/poseintelligence/cssdm1/MainMenu.java)

### Stored Procedure Pattern
Query config on app start via `EXEC getConfigAddSterileBasket` - returns boolean flags that control business logic:
- `SR_IsCheckItemInMachine`: Validate item location before adding to sterile basket
- `SS_IsMatchBasketAndType`: Ensure basket type matches item requirements

### Error Handling
- Global exception handler: [CustomExceptionHandler](app/src/main/java/com/poseintelligence/cssdm1/core/CustomExceptionHandler.java) writes stacktraces to `/Download/` directory
- No explicit try-catch patterns visible - relies on global handler and logging via `Log.d()`

### Printing Integration
[SunmiPrintHelper](app/src/main/java/com/poseintelligence/cssdm1/utils/SunmiPrintHelper.java) manages thermal printer (receipt generation) - device-specific API via `com.sunmi:printerlibrary:1.0.21`

## Build & Runtime

### Build Command
```bash
./gradlew build  # Increments versionCode, generates APK as "Cssd-M1-FULL-MM-dd-HHmm.apk"
```

### APK Naming
Auto-generated per variant (debug/release) with timestamp - see [build.gradle applicationVariants](app/build.gradle#L27-L33)

### Permissions
App requires: GPS, Camera, Bluetooth, Storage R/W, Internet, Audio, Phone state read - check [AndroidManifest.xml](app/src/main/AndroidManifest.xml) for full list

### Dependencies
- **UI**: Material Design, ConstraintLayout, AppCompat
- **Database**: JTDS (MS SQL), MS JDBC driver v8.4.1
- **Utilities**: Picasso (image loading), ZXing (barcode scanning), ImageSlideshow, CardSlider
- **Device**: Sunmi printer library, SearchableSpinner

## Conventions & Gotchas

1. **Static Config Hardcoding**: DB credentials live in [DBConnect.java](app/src/main/java/com/poseintelligence/cssdm1/core/connect/DBConnect.java#L15-L19) - update for different environments
2. **No DI Framework**: Manual dependency passing; Application-level singletons via `CssdProject` static methods
3. **Mixed Java/Kotlin**: Kotlin plugin enabled but Java primary - use Java for new code unless modernizing
4. **View Binding**: Enabled in build config - use `binding.viewId` instead of `findViewById()`
5. **ProGuard**: Enabled only for release builds - configuration in [proguard-rules.pro](app/proguard-rules.pro)
6. **AndroidX**: Migration complete (`android.useAndroidX=true`, `android.enableJetifier=true`)
7. **Stored Procedures Over SQL**: Most queries are named procedures in DB - check DB schema before writing raw SQL

## Task: When Making Changes

- **New Features**: Add new Activity in appropriate Menu_* folder → register in AndroidManifest.xml → add navigation in MainMenu
- **New Data Models**: Create POJO in `model/` → add adapter if needed in `adapter/`
- **New DB Tables**: Extend [FeedReaderDbHelper.onCreate()](app/src/main/java/com/poseintelligence/cssdm1/core/SQLiteDB/FeedReaderDbHelper.java#L24) with CREATE TABLE statement
- **Hardware Integration**: Extend `core/` utilities (e.g., Bluetooth → BluetoothUtil, printing → SunmiPrintHelper)
- **Testing**: Unit tests in `test/`, instrumentation tests in `androidTest/` with `AndroidJUnitRunner`
