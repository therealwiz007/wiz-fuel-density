# Wiz Fuel Density (Android) — Wiz Labs

Offline Android app. Enter observed temperature and density, get density at 15 °C from ASTM Table 53B.
Package: com.wizlabs.fueldensity · minSdk 26 · compileSdk 36 · targetSdk 36 · no internet permission.

## Build
1. Open this folder in Android Studio (Ladybug or newer). Let it sync; it creates the Gradle wrapper.
2. Run on a device, or Build > Generate Signed Bundle / APK for the Play Store (.aab).

## Edit the app
All screens and the Table 53B data are in app/src/main/assets/index.html.
Change package name in app/build.gradle.kts and the Java package folder if you want a different ID.


## Build on GitHub Actions
This repository includes `.github/workflows/build.yml`. Upload the contents of this
`FuelDensity` folder to the root of a GitHub repository (not the outer ZIP folder),
then open **Actions** and run **Build Wiz Fuel Density**. A successful run uploads
the generated Android App Bundle (`.aab`) as an artifact.

Important: this workflow builds an **unsigned release AAB**. Google Play requires an
AAB signed with your upload key. Do not put keystores or passwords in the repository.
Configure signing secrets and a signing step before using the bundle for Play Console.
The workflow has not been executed in GitHub from this package, so the first run must
be treated as a build test.


## Branding and scope
Wiz Fuel Density is an independent, offline density-correction utility for fuel retail teams. It is not affiliated with or endorsed by IOCL, BPCL, HPCL, Nayara Energy, or Jio-bp. The app currently focuses on density correction to 15 °C; tank-volume calculations are not included.


## Density history
Saved readings stay on the device and include the local date/time, observed temperature and density, fuel-type classification, and corrected density at 15 °C. Users can delete individual entries, clear history, or export CSV. Fuel classification follows the requested rule: observed density 800 kg/m³ and above = Diesel; 700–799 kg/m³ = Petrol.


## App icon
The launcher icon now uses the clean fuel-nozzle and 15°C artwork. The density history and calculation logic are unchanged.
