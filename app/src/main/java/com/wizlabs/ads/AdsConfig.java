package com.wizlabs.ads;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Java port of AdsConfig.kt (WIZ_ADS_PLAN.md 2.3, 3.8 step 6) for the Java app (Fuel Density).
 * Same field names as the Kotlin master. Built in the app, never inside this package.
 */
public final class AdsConfig {

    public static final class AdUnits {
        public final String banner, nativeAd;
        public AdUnits(String banner, String nativeAd) { this.banner = banner; this.nativeAd = nativeAd; }
    }

    /** Banner grace only: Java ports carry no interstitial or rewarded slot. */
    public static final class AdCaps {
        public final int graceDaysAfterInstall, graceSessions;
        public AdCaps() { this(1, 2); }
        public AdCaps(int graceDaysAfterInstall, int graceSessions) {
            this.graceDaysAfterInstall = graceDaysAfterInstall; this.graceSessions = graceSessions;
        }
    }

    /**
     * Master switch compiled into the app (BuildConfig.WIZ_ADS_ENABLED). While false nothing
     * ad-related ever loads or shows, whatever the other settings say.
     */
    public final boolean enabled;
    public final AdUnits units;
    public final AdCaps caps;
    /** Screen names where nothing may load or show. */
    public final Set<String> noAdScreens;
    /** Screen names where a slot may appear at all. Anything else is treated as no-ad. */
    public final Set<String> adScreens;
    /** Passed to MobileAds.initialize and UMP in code (Next-Gen SDK). Empty = nothing can load. */
    public final String admobAppId;
    /** BuildConfig.DEBUG: enables the UMP debug geography. */
    public final boolean debug;
    /** UMP ConsentDebugSettings.DebugGeography value, debug builds only (0 = off). */
    public final int consentDebugGeography;
    public final List<String> testDeviceHashedIds;

    public AdsConfig(boolean enabled, AdUnits units, AdCaps caps, Set<String> noAdScreens, Set<String> adScreens,
                     String admobAppId, boolean debug, int consentDebugGeography, List<String> testDeviceHashedIds) {
        this.enabled = enabled;
        this.units = units != null ? units : new AdUnits(null, null);
        this.caps = caps != null ? caps : new AdCaps();
        this.noAdScreens = noAdScreens != null ? noAdScreens : Collections.<String>emptySet();
        this.adScreens = adScreens != null ? adScreens : Collections.<String>emptySet();
        this.admobAppId = admobAppId != null ? admobAppId : "";
        this.debug = debug;
        this.consentDebugGeography = consentDebugGeography;
        this.testDeviceHashedIds = testDeviceHashedIds != null ? testDeviceHashedIds : Collections.<String>emptyList();
    }
}
