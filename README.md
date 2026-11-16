# Wiz Fuel Density — Android project

Android utility for converting observed fuel density to reference density at 15 °C using the bundled reference table. Package ID: `com.wizlabs.fueldensity`.

Version: 1.5 (8)

## Features
- Animated hydrometer hero (v1.3): a float bobs in a fuel column tinted by density, its scale slides to the reading, and a mercury line shows the correction to 15 °C. Motion is compositor-only CSS, settles after 9 s idle, and stops under Battery Saver or with animations off
- Minimal black-and-yellow design that follows the phone's light/dark setting, with a manual switch (status bar matches)
- Observed density first, then temperature. A 3-digit density jumps straight to temperature, and Enter calculates
- Explicit Calculate action; no result until Calculate is tapped
- Result shows the change from observed density and flags values marked as extrapolated in the source table
- Copy, Share (full reading as text) and **Next**, which clears density and keeps the temperature for the next sample
- Log of the last 50 calculations on the device: tap to load one again, delete single entries, or clear all
- Works offline. The calculator page is blocked from the network. Internet permission is used for Google Play updates; the ad SDK is linked but switched off (`wizAdsEnabled=false`)
- Google Play in-app updates (FLEXIBLE, always dismissible) and the in-app review card, from the shared `wiz-update` package; Rate app in the menu
- Readings and theme (WebView localStorage) are in Android Auto Backup through include-only rules (`res/xml/backup_rules.xml`, `data_extraction_rules.xml`)
- Store listing text (en-US, hi-IN) in `fastlane/metadata/android/`
- GitHub Actions builds a signed APK and AAB as separate artifacts named `Wiz Fuel Density (<version>)`

## Build
Shared code is synced, never edited here: `wiz-update/sync.sh <repo>` and `wiz-ads/sync.sh <repo> --java` (Java ports). Debug with test ads: `gradle :app:assembleDebug -PwizAdsEnabled=true`.

Open this folder in Android Studio with JDK 17, or run `gradle bundleRelease` with Android SDK 36 installed. GitHub Actions can build on pushes to `main` or manually from Actions.

## Signing
This source package does not contain private signing keys. Configure signing securely in GitHub Secrets if you need a Play Store upload key. Do not commit a keystore or passwords.

## Important
The bundled correction table is the supplied data and is not independently certified here. Confirm operational results against the approved table and your organisation's procedures.
