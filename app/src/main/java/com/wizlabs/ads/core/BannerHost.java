package com.wizlabs.ads.core;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.libraries.ads.mobile.sdk.banner.AdSize;
import com.google.android.libraries.ads.mobile.sdk.banner.AdView;
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd;
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest;
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;

/**
 * Java port of BannerHost.kt: builds, loads and destroys one anchored adaptive banner. Next-Gen
 * SDK callbacks arrive on a background thread, so results are posted to main. The container has
 * zero height until an ad has loaded; a failed load stays collapsed.
 */
public final class BannerHost {
    public interface Listener { void onLoaded(int heightPx); void onFailed(); }

    public final FrameLayout view;
    private final Activity activity;
    private final Handler main = new Handler(Looper.getMainLooper());
    private AdView adView;

    public BannerHost(Activity activity) {
        this.activity = activity;
        this.view = new FrameLayout(activity);
    }

    public void load(String unitId, int widthDp, final Listener l) {
        if (unitId == null || unitId.isEmpty() || adView != null) return;
        try {
            AdSize size = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, widthDp);
            final AdView av = new AdView(activity);
            adView = av;
            view.addView(av, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            av.loadAd(new BannerAdRequest.Builder(unitId, size).build(), new AdLoadCallback<BannerAd>() {
                @Override public void onAdLoaded(BannerAd ad) {
                    final int h = ad.getAdSize().getHeightInPixels(activity);
                    main.post(() -> { if (adView == av) l.onLoaded(h); });
                }
                @Override public void onAdFailedToLoad(LoadAdError error) {
                    main.post(() -> { if (adView == av) l.onFailed(); });
                }
            });
        } catch (Throwable t) {
            l.onFailed();
        }
    }

    public void destroy() {
        main.removeCallbacksAndMessages(null);
        try { if (adView != null) adView.destroy(); } catch (Throwable ignored) { }
        adView = null;
        view.removeAllViews();
    }
}
