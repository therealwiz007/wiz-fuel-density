package com.wizlabs.ads.core;

import com.wizlabs.ads.AdsConfig;

/**
 * Java port of AdGate.kt, banner and native rules only (the Java app has no interstitial or
 * rewarded slot). The first failing rule wins: compiled switch, SDK linked, Pro, Premium,
 * ad-free window, remote switch, grace, consent, screen, low RAM / Battery Saver.
 */
public final class AdGate {
    private final AdsConfig config;

    public AdGate(AdsConfig config) { this.config = config; }

    public boolean adsAllowedAtAll(AdState s) { return reason(s, AdFormat.BANNER, null) == null; }

    public boolean mayShow(AdFormat format, String screen, AdState s) { return reason(s, format, screen) == null; }

    /** Why nothing shows (null = allowed). */
    public String reason(AdState s, AdFormat format, String screen) {
        AdsConfig.AdCaps c = config.caps;
        if (!config.enabled) return "ads switched off in this build";
        if (!s.sdkLinked) return "no ad SDK in this build";
        if (s.proOwned) return "Pro";
        if (s.premium) return "Wiz Premium";
        if (s.adFreeUntil > s.now) return "ad-free window";
        if (!s.remoteEnabled) return "remote switch off";
        if (format == AdFormat.INTERSTITIAL || format == AdFormat.REWARDED) return "format not in the Java port";
        if (days(s) < c.graceDaysAfterInstall) return "grace (days)";
        if (s.sessions < c.graceSessions) return "grace (sessions)";
        if (!s.consentOk) return "no consent";
        if (screen != null && (config.noAdScreens.contains(screen) || !config.adScreens.contains(screen))) return "no-ad screen";
        if (s.lowRam && format != AdFormat.BANNER) return "low-RAM phone: banner only";
        if (s.batterySaver && format == AdFormat.NATIVE) return "Battery Saver";
        return null;
    }

    private static long days(AdState s) { return Math.max(0, s.now - s.graceStart) / 86_400_000L; }
}
