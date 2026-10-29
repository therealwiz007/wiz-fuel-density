# Wiz Fuel Density — Android project

Offline Android utility for converting observed fuel density to reference density at 15 °C using the bundled reference table. Package ID: `com.wizlabs.fueldensity`.

## Features
- Black-and-yellow mobile UI
- Explicit Calculate action; no result until Calculate is tapped
- Local saved reading history, per-item deletion, clear history
- Copy result, reset, quick examples, ASTM reference link in Settings
- Offline WebView app; no INTERNET permission
- GitHub Actions workflow builds a release Android App Bundle (AAB)

## Build
Open this folder in Android Studio with JDK 17, or run `gradle bundleRelease` with Android SDK 36 installed. GitHub Actions can build on pushes to `main` or manually from Actions.

## Signing
This source package does not contain private signing keys. Configure signing securely in GitHub Secrets if you need a Play Store upload key. Do not commit a keystore or passwords.

## Important
The bundled correction table is the supplied data and is not independently certified here. Confirm operational results against the approved table and your organisation's procedures.
