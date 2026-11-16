package com.wizlabs.ads.core;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.android.libraries.ads.mobile.sdk.MobileAds;
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig;
import com.wizlabs.ads.AdsConfig;

/**
 * Java port of AdsInitializer.kt: lazy MobileAds.initialize on a background thread (Next-Gen SDK
 * 1.6.0 takes the app ID in code). Called by the first slot about to appear, never from onCreate.
 * No-op while ads are off, without consent, or without an app ID.
 */
public final class AdsInitializer {
    private AdsInitializer() {}

    private static volatile boolean started;
    private static volatile boolean ready;

    public static boolean isReady() { return ready; }

    /** onReady runs on the main thread once the SDK is initialised (at once if it already is). */
    public static void ensure(Context context, AdsConfig config, boolean canRequestAds, final Runnable onReady) {
        if (!config.enabled || !canRequestAds || config.admobAppId.isEmpty()) return;
        final Handler main = new Handler(Looper.getMainLooper());
        if (ready) { if (onReady != null) main.post(onReady); return; }
        if (started) return;
        started = true;
        final Context app = context.getApplicationContext();
        final String appId = config.admobAppId;
        new Thread(() -> {
            try {
                MobileAds.initialize(app, new InitializationConfig.Builder(appId).build(), status -> {
                    ready = true;
                    if (onReady != null) main.post(onReady);
                });
            } catch (Throwable t) {
                started = false;
            }
        }, "wiz-ads-init").start();
    }
}
