package com.wizlabs.ads.core;

import android.app.Activity;

import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;
import com.wizlabs.ads.AdsConfig;

/**
 * Java port of ConsentManager.kt (UMP 4.0.0). Constructing it touches nothing; every entry point
 * returns at once while ads are switched off, so no consent form and no UMP request ever happen.
 */
public final class ConsentManager {
    private volatile boolean canRequestAds;
    private volatile boolean privacyOptionsRequired;
    private boolean gathering;

    public boolean canRequestAds() { return canRequestAds; }
    public boolean privacyOptionsRequired() { return privacyOptionsRequired; }

    /** Call after the first frame. Never blocks UI. Shows the form only where it is required. */
    public void gather(final Activity activity, AdsConfig config, final Runnable onDone) {
        if (!config.enabled || gathering) return;
        gathering = true;
        try {
            final ConsentInformation info = UserMessagingPlatform.getConsentInformation(activity);
            if (info.canRequestAds()) { publish(info); onDone.run(); }   // a previous session already allows ads
            ConsentRequestParameters.Builder b = new ConsentRequestParameters.Builder();
            if (!config.admobAppId.isEmpty()) b.setAdMobAppId(config.admobAppId);
            if (config.debug && config.consentDebugGeography != 0) {
                ConsentDebugSettings.Builder d = new ConsentDebugSettings.Builder(activity)
                        .setDebugGeography(config.consentDebugGeography);
                for (String id : config.testDeviceHashedIds) d.addTestDeviceHashedId(id);
                b.setConsentDebugSettings(d.build());
            }
            info.requestConsentInfoUpdate(activity, b.build(),
                    () -> UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity, err -> finish(info, onDone)),
                    err -> finish(info, onDone));
        } catch (Throwable t) {
            gathering = false;
        }
    }

    public void showPrivacyOptions(final Activity activity, AdsConfig config, final Runnable onClosed) {
        if (!config.enabled) return;
        try {
            UserMessagingPlatform.showPrivacyOptionsForm(activity, err -> {
                publish(UserMessagingPlatform.getConsentInformation(activity));
                onClosed.run();
            });
        } catch (Throwable ignored) { }
    }

    private void finish(ConsentInformation info, Runnable onDone) {
        gathering = false;
        publish(info);
        onDone.run();
    }

    private void publish(ConsentInformation info) {
        canRequestAds = info.canRequestAds();
        privacyOptionsRequired = info.getPrivacyOptionsRequirementStatus()
                == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED;
    }
}
