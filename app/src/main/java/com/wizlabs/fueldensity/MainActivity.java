package com.wizlabs.fueldensity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.PowerManager;
import android.view.HapticFeedbackConstants;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.wizlabs.ads.AdsConfig;
import com.wizlabs.ads.WizAds;
import com.wizlabs.ads.core.AdFormat;
import com.wizlabs.ads.core.BannerHost;
import com.wizlabs.update.UpdatePalette;
import com.wizlabs.update.WizReview;
import com.wizlabs.update.WizUpdate;

import java.util.Collections;

public class MainActivity extends Activity {
    private static final String SCREEN = "main";
    // In-app review thresholds (plan 2.6): 15 calculations on 4 or more active days.
    private static final int REVIEW_MIN_EVENTS = 15;
    private static final int REVIEW_MIN_DAYS = 4;

    private WebView web;
    private FrameLayout adContainer;
    private BannerHost banner;
    private WizUpdate update;
    private boolean dark;
    private int imeBottom;
    private boolean bannerLoaded;
    private boolean pageReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WizAds.install(getApplicationContext(), new AdsConfig(BuildConfig.WIZ_ADS_ENABLED,
                new AdsConfig.AdUnits(BuildConfig.AD_BANNER, null), new AdsConfig.AdCaps(),
                Collections.<String>emptySet(), Collections.singleton(SCREEN),
                BuildConfig.ADMOB_APP_ID, BuildConfig.DEBUG, 0, null));

        // WebView on top, then the banner slot. The slot is GONE (zero height) unless an ad has
        // loaded, so with ads off the screen is the same as before.
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        web = new WebView(this);
        root.addView(web, new LinearLayout.LayoutParams(-1, 0, 1f));
        adContainer = new FrameLayout(this);
        int strip = Math.round(12 * getResources().getDisplayMetrics().density);
        adContainer.setPadding(0, strip, 0, strip);   // the ad never touches page content
        adContainer.setVisibility(View.GONE);
        root.addView(adContainer, new LinearLayout.LayoutParams(-1, -2));
        setContentView(root);

        // Keep content clear of the status and navigation bars (edge-to-edge on Android 15+).
        root.setOnApplyWindowInsetsListener((v, insets) -> {
            if (android.os.Build.VERSION.SDK_INT >= 30) {
                android.graphics.Insets i = insets.getInsets(WindowInsets.Type.systemBars() | WindowInsets.Type.ime());
                imeBottom = insets.getInsets(WindowInsets.Type.ime()).bottom;
                v.setPadding(i.left, i.top, i.right, i.bottom);
                syncAdVisibility();
                return WindowInsets.CONSUMED;
            }
            v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            // Before API 30 the IME is part of the bottom inset; anything well above a nav bar is the keyboard.
            imeBottom = insets.getSystemWindowInsetBottom() > Math.round(100 * getResources().getDisplayMetrics().density)
                    ? insets.getSystemWindowInsetBottom() : 0;
            syncAdVisibility();
            return insets.consumeSystemWindowInsets();
        });

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(false);          // assets load via file:///android_asset only
        s.setAllowContentAccess(false);
        s.setBlockNetworkLoads(true);         // the page stays offline even though the app has INTERNET
        s.setTextZoom(100);
        applyTheme(getResources().getConfiguration());
        // Lets the page match the system bars to its own light/dark choice.
        web.addJavascriptInterface(new Object() {
            @JavascriptInterface public void setDark(boolean dark) { runOnUiThread(() -> applyColors(dark)); }
            // Light tick for primary actions; needs no VIBRATE permission.
            @JavascriptInterface public void haptic() { runOnUiThread(() -> web.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)); }
            // Use events for the review prompt. Only these three fixed names are accepted.
            @JavascriptInterface public void event(String name) {
                if ("calc".equals(name)) WizReview.event(getApplicationContext(), "calc");
                else if ("copy".equals(name) || "next".equals(name)) runOnUiThread(MainActivity.this::maybeAskReview);
            }
            // Opens this app's own Play listing. Takes nothing from the page.
            @JavascriptInterface public void openListing() { runOnUiThread(MainActivity.this::openListingNow); }
            // UMP privacy options; does nothing while ads are off.
            @JavascriptInterface public void privacyOptions() { runOnUiThread(() -> WizAds.showPrivacyOptions(MainActivity.this)); }
        }, "WizAndroid");
        web.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) { return true; }
            @Override public void onPageFinished(WebView v, String url) {
                pageReady = true;
                syncMotion();
                pushAdsState();
                web.post(MainActivity.this::maybeLoadBanner);   // after the first frame
            }
        });
        web.loadUrl("file:///android_asset/index.html");

        WizAds.setListener(() -> runOnUiThread(() -> { pushAdsState(); maybeLoadBanner(); }));
        update = new WizUpdate(this, new WizUpdate.Host() {
            @Override public UpdatePalette palette() {
                return dark
                        ? new UpdatePalette(0xFF151517, 0xFF242428, 0xFFF4F4F2, 0xFFB4B4BA, 0xFFFFC400, 0xFF111113, 22)
                        : new UpdatePalette(0xFFFFFFFF, 0xFFE7E6E0, 0xFF111113, 0xFF55555C, 0xFFFFC400, 0xFF111113, 22);
            }
            @Override public boolean mayPromptNow() { return imeBottom == 0; }   // not while typing
            @Override public String appName() { return getString(R.string.app_name); }
            @Override public String restartNote() { return null; }
            @Override public int barBottomOffsetPx() {
                return adContainer != null && adContainer.getVisibility() == View.VISIBLE ? adContainer.getHeight() : 0;
            }
        }, BuildConfig.DEBUG);
    }

    private void maybeAskReview() {
        WizReview.maybeAsk(this, REVIEW_MIN_EVENTS, REVIEW_MIN_DAYS, WizUpdate.wasShownThisProcess());
    }

    private void openListingNow() {
        String pkg = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkg)));
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + pkg)));
            } catch (ActivityNotFoundException ignored) { }
        }
    }

    // ---- Ads (switched off in this build: every step below returns at once) ----

    private void pushAdsState() {
        if (web == null || !pageReady) return;
        web.evaluateJavascript("window.__wizAds && window.__wizAds({enabled:" + WizAds.isEnabled()
                + ",privacyRequired:" + WizAds.privacyOptionsRequired() + "})", null);
    }

    private void maybeLoadBanner() {
        if (!WizAds.isEnabled() || !pageReady || banner != null || isFinishing() || isDestroyed()) return;
        if (!WizAds.mayShow(AdFormat.BANNER, SCREEN)) return;
        WizAds.ensureInitialized(this, () -> {
            if (banner != null || isFinishing() || isDestroyed()) return;
            banner = new BannerHost(this);
            adContainer.addView(banner.view, new FrameLayout.LayoutParams(-1, -2));
            float density = getResources().getDisplayMetrics().density;
            int widthDp = Math.round(getResources().getDisplayMetrics().widthPixels / density);
            banner.load(BuildConfig.AD_BANNER, widthDp, new BannerHost.Listener() {
                @Override public void onLoaded(int heightPx) { bannerLoaded = true; syncAdVisibility(); }
                @Override public void onFailed() { bannerLoaded = false; syncAdVisibility(); }
            });
        });
    }

    private void syncAdVisibility() {
        if (adContainer == null) return;
        boolean show = bannerLoaded && imeBottom == 0 && WizAds.adsVisible();
        int v = show ? View.VISIBLE : View.GONE;
        if (adContainer.getVisibility() != v) adContainer.setVisibility(v);
    }

    // Battery Saver or system animations off: the page shows a still frame instead of looping motion.
    private final BroadcastReceiver powerReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context c, Intent i) { syncMotion(); }
    };

    private void syncMotion() {
        if (web == null) return;
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean still = (pm != null && pm.isPowerSaveMode()) || !ValueAnimator.areAnimatorsEnabled();
        web.evaluateJavascript("window.__wizMotion && window.__wizMotion(" + still + ")", null);
    }

    @Override protected void onStart() {
        super.onStart();
        WizAds.onActivityStart(this);
    }

    @Override protected void onResume() {
        super.onResume();
        if (web != null) { web.onResume(); web.resumeTimers(); }
        if (update != null) update.onResume();
        IntentFilter f = new IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED);
        if (android.os.Build.VERSION.SDK_INT >= 33) registerReceiver(powerReceiver, f, Context.RECEIVER_NOT_EXPORTED);
        else registerReceiver(powerReceiver, f);
        syncMotion();
    }

    // Nothing runs while the app is in the background. pauseTimers() also pauses the ad's WebView.
    @Override protected void onPause() {
        try { unregisterReceiver(powerReceiver); } catch (IllegalArgumentException ignored) {}
        if (update != null) update.onPause();
        if (web != null) { web.onPause(); web.pauseTimers(); }
        super.onPause();
    }

    @Override protected void onStop() {
        WizAds.onActivityStop();
        super.onStop();
    }

    private void applyTheme(Configuration c) {
        applyColors((c.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES);
    }

    private void applyColors(boolean dark) {
        if (web == null) return;
        this.dark = dark;
        int bg = dark ? Color.parseColor("#0B0B0C") : Color.parseColor("#F6F6F3");
        web.setBackgroundColor(bg);
        if (adContainer != null) adContainer.setBackgroundColor(bg);
        getWindow().getDecorView().setBackgroundColor(bg);
        View d = getWindow().getDecorView();
        int flags = d.getSystemUiVisibility();
        if (dark) flags &= ~(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        else flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        d.setSystemUiVisibility(flags);
    }

    @Override public void onConfigurationChanged(Configuration c) {
        super.onConfigurationChanged(c);
        applyTheme(c);
        if (web != null) web.evaluateJavascript("window.__wizSyncBars && window.__wizSyncBars()", null);
    }

    @Override protected void onDestroy() {
        WizAds.setListener(null);
        if (update != null) update.onDestroy();
        if (banner != null) { banner.destroy(); banner = null; }
        if (web != null) web.destroy();
        super.onDestroy();
    }
}
