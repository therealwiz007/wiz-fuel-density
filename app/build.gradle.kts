plugins { id("com.android.application") }
android {
    namespace = "com.wizlabs.fueldensity"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.wizlabs.fueldensity"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }
    buildTypes {
        release { isMinifyEnabled = true; isShrinkResources = true }
    }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}
