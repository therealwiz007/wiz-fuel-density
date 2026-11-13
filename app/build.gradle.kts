
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
        versionCode = 7
        versionName = "1.4"
    }

    signingConfigs {
        create("release") {
            // CI supplies the keystore; local release builds without it stay unsigned.
            System.getenv("RELEASE_STORE_FILE")?.let { storeFile = file(it) }
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            if (System.getenv("RELEASE_STORE_FILE") != null) signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
