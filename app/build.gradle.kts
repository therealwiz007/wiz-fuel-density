
plugins {
    id("com.android.application")
}

android {
    namespace = "com.wizlabs.fueldensity"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.wizlabs.fueldensity"
        minSdk = 26
        targetSdk = 36
        versionCode = 4
        versionName = "1.1"
    }

    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("RELEASE_STORE_FILE"))
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
