# Wiz Fuel Density — Android project

Offline Android utility for converting observed fuel density to reference density at 15 °C using the bundled reference table. Package ID: `com.wizlabs.fueldensity`.

## Features
- Minimal black-and-yellow design that follows the phone's light/dark setting, with a manual switch (status bar matches)
- Observed density first, then temperature. A 3-digit density jumps straight to temperature, and Enter calculates
- Explicit Calculate action; no result until Calculate is tapped
- Result shows the change from observed density and flags values marked as extrapolated in the source table
- Copy, Share (full reading as text) and **Next**, which clears density and keeps the temperature for the next sample
- Log of the last 50 calculations on the device: tap to load one again, delete single entries, or clear all
- Offline WebView app; no INTERNET permission
- GitHub Actions workflow builds a release Android App Bundle (AAB)

## Build
Open this folder in Android Studio with JDK 17, or run `gradle bundleRelease` with Android SDK 36 installed. GitHub Actions can build on pushes to `main` or manually from Actions.

## Signing
This source package does not contain private signing keys. Configure signing securely in GitHub Secrets if you need a Play Store upload key. Do not commit a keystore or passwords.

## Important
The bundled correction table is the supplied data and is not independently certified here. Confirm operational results against the approved table and your organisation's procedures.
