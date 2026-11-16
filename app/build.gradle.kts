import java.util.Properties

plugins {
    id("com.android.application")
}

// Ads switch (WIZ_3APPS_MASTER_PLAN.md 4.1). Off by default: the SDK is linked but never touched.
val wizAdsEnabled = (project.findProperty("wizAdsEnabled") as String?) == "true"
val adsProps = Properties().apply {
    rootProject.file("ads.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}

android {
    namespace = "com.wizlabs.fueldensity"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.wizlabs.fueldensity"
        minSdk = 26
        targetSdk = 36
        versionCode = 8
        versionName = "1.5"
        buildConfigField("boolean", "WIZ_ADS_ENABLED", "$wizAdsEnabled")
    }

    buildFeatures { buildConfig = true }

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
        debug {
            // Google's public test IDs. Only reachable with -PwizAdsEnabled=true.
            buildConfigField("String", "ADMOB_APP_ID", "\"ca-app-pub-3940256099942544~3347511713\"")
            buildConfigField("String", "AD_BANNER", "\"ca-app-pub-3940256099942544/9214589741\"")
        }
        release {
            if (System.getenv("RELEASE_STORE_FILE") != null) signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            fun id(k: String) = if (!wizAdsEnabled) "" else
                adsProps.getProperty(k) ?: error("wizAdsEnabled=true but ads.properties has no '$k'")
            buildConfigField("String", "ADMOB_APP_ID", "\"${id("appId")}\"")
            buildConfigField("String", "AD_BANNER", "\"${id("banner")}\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:review:2.0.2")
    implementation("com.google.android.libraries.ads.mobile.sdk:ads-mobile-sdk:1.6.0")
    implementation("com.google.android.ump:user-messaging-platform:4.0.0")
    testImplementation("junit:junit:4.13.2")
}
