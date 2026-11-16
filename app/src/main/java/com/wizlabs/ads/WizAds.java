package com.wizlabs.ads;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.PowerManager;

import com.wizlabs.ads.core.AdFormat;
import com.wizlabs.ads.core.AdGate;
import com.wizlabs.ads.core.AdState;
import com.wizlabs.ads.core.AdsInitializer;
import com.wizlabs.ads.core.ConsentManager;

/**
 * Java port of WizAds.kt for the Java app (Fuel Density). Same method names as the Kotlin master;
 * StateFlows become plain getters plus one change listener. `install` is cheap and does no SDK
 * work. With ads switched off, nothing here touches the SDK, UMP, prefs or the network.
 * Never hand-edit the copy inside an app: fix it in wiz-ads/java/ and sync again.
 */
public final class WizAds {
    /** Version of the shared package; matches WIZ_ADS_VERSION in WizAds.kt. */
    public static final String WIZ_ADS_VERSION = "0.2";
    /** True in builds that link the ad SDK (Next-Gen GMA + UMP). */
    public static final boolean WIZ_ADS_SDK_LINKED = true;

    private static final long SESSION_GAP_MS = 30 * 60_000L;
    private static final long CONSENT_DELAY_MS = 1_500L;

    private static AdsConfig config;
    private static AdsPrefs prefs;
    private static AdGate gate;
    private static Context appCtx;
    private static ConsentManager consent;
    private static String screen = "";
    private static boolean adsVisible;
    private static boolean privacyOptionsRequired;
    private static Runnable listener;

    private WizAds() {}

    /** True only in a build with ads switched on. */
    public static boolean isEnabled() { return config != null && config.enabled; }
    public static AdsConfig configOrNull() { return config; }
    public static String currentScreen() { return screen; }
    /** False while ads are off, in grace, Pro, no consent, etc. Slots render nothing then. */
    public static boolean adsVisible() { return adsVisible; }
    /** Show the "Privacy options" entry only while this is true (always false with ads off). */
    public static boolean privacyOptionsRequired() { return privacyOptionsRequired; }
    public static boolean canRequestAds() { return consent != null && consent.canRequestAds(); }

    /** Called on the main thread whenever adsVisible or privacyOptionsRequired may have changed. */
    public static void setListener(Runnable l) { listener = l; }

    public static void install(Context app, AdsConfig c) {
        config = c;
        appCtx = app.getApplicationContext();
        prefs = new AdsPrefs(appCtx);
        gate = new AdGate(c);
        consent = new ConsentManager();
    }

    /**
     * Counts sessions and, after the first frame, gathers consent. Returns at once, before any
     * prefs or consent work, while ads are off.
     */
    public static void onActivityStart(final Activity activity) {
        final AdsConfig c = config;
        if (c == null || !c.enabled || prefs == null) return;
        long now = System.currentTimeMillis();
        if (prefs.getAdsEraStart() == 0L) prefs.setAdsEraStart(now);
        long lastFg = prefs.getLastForegroundAt();
        if (lastFg == 0L || now - lastFg > SESSION_GAP_MS) {
            prefs.setSessions(prefs.getSessions() + 1);
            prefs.setSessionStartedAt(now);
        }
        prefs.setLastForegroundAt(now);
        refresh();
        final ConsentManager cm = consent;
        if (cm == null) return;
        activity.getWindow().getDecorView().post(() -> activity.getWindow().getDecorView().postDelayed(() -> {
            if (activity.isFinishing() || activity.isDestroyed()) return;
            cm.gather(activity, c, () -> { privacyOptionsRequired = cm.privacyOptionsRequired(); refresh(); });
        }, CONSENT_DELAY_MS));
    }

    public static void onActivityStop() {
        if (!isEnabled() || prefs == null) return;
        prefs.setLastForegroundAt(System.currentTimeMillis());
    }

    /** The app calls this from its navigation, with a stable screen name. */
    public static void onScreen(String name) {
        screen = name;
        refresh();
    }

    public static boolean mayShow(AdFormat format, String screenName) {
        AdState s = state();
        return gate != null && s != null && gate.mayShow(format, screenName, s);
    }

    /** Why nothing shows right now (for debug). */
    public static String whyNot(AdFormat format, String screenName) {
        AdState s = state();
        if (gate == null || s == null) return "not installed";
        String r = gate.reason(s, format, screenName);
        return r == null ? "allowed" : r;
    }

    /** Opens the UMP privacy options form. No-op while ads are off. */
    public static void showPrivacyOptions(Activity activity) {
        final AdsConfig c = config;
        final ConsentManager cm = consent;
        if (c == null || !c.enabled || cm == null) return;
        cm.showPrivacyOptions(activity, c, () -> { privacyOptionsRequired = cm.privacyOptionsRequired(); refresh(); });
    }

    /** Called by a slot about to appear: initialises the SDK once, off the main thread. */
    public static void ensureInitialized(Context context, Runnable onReady) {
        AdsConfig c = config;
        if (c == null) return;
        AdsInitializer.ensure(context, c, canRequestAds(), onReady);
    }

    private static void refresh() {
        AdState s = state();
        adsVisible = gate != null && s != null && gate.adsAllowedAtAll(s);
        Runnable l = listener;
        if (l != null) { try { l.run(); } catch (Throwable ignored) { } }
    }

    private static AdState state() {
        if (prefs == null || appCtx == null) return null;
        long now = System.currentTimeMillis();
        long era = prefs.getAdsEraStart();
        AdState s = new AdState(now, Math.max(installTime(appCtx), era > 0 ? era : now),
                prefs.getSessions(), prefs.getSessionStartedAt());
        s.proOwned = prefs.getProOwned();
        s.adFreeUntil = prefs.getAdFreeUntil();
        s.consentOk = canRequestAds();
        s.sdkLinked = WIZ_ADS_SDK_LINKED;
        try { s.lowRam = ((ActivityManager) appCtx.getSystemService(Context.ACTIVITY_SERVICE)).isLowRamDevice(); }
        catch (Throwable t) { s.lowRam = true; }
        try { s.batterySaver = ((PowerManager) appCtx.getSystemService(Context.POWER_SERVICE)).isPowerSaveMode(); }
        catch (Throwable t) { s.batterySaver = false; }
        return s;
    }

    private static long installTime(Context ctx) {
        try { return ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).firstInstallTime; }
        catch (Throwable t) { return 0L; }
    }
}
