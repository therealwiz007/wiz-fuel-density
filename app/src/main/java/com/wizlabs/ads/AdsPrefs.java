package com.wizlabs.ads;

import android.content.Context;
import android.content.SharedPreferences;

/** Java port of AdsPrefs.kt: "wiz_ads" preferences, kept apart from each app's own prefs (plan 2.2). */
public final class AdsPrefs {
    private final SharedPreferences p;

    public AdsPrefs(Context ctx) { p = ctx.getSharedPreferences("wiz_ads", Context.MODE_PRIVATE); }

    public int getSessions() { return p.getInt("sessions", 0); }
    public void setSessions(int v) { p.edit().putInt("sessions", v).apply(); }

    public long getLastForegroundAt() { return p.getLong("last_fg", 0); }
    public void setLastForegroundAt(long v) { p.edit().putLong("last_fg", v).apply(); }

    public long getSessionStartedAt() { return p.getLong("session_start", 0); }
    public void setSessionStartedAt(long v) { p.edit().putLong("session_start", v).apply(); }

    /** First launch of a build that can show ads. Stays 0 until then (plan 7.5). */
    public long getAdsEraStart() { return p.getLong("ads_era_start", 0); }
    public void setAdsEraStart(long v) { p.edit().putLong("ads_era_start", v).apply(); }

    public boolean getProOwned() { return p.getBoolean("pro_owned", false); }
    public void setProOwned(boolean v) { p.edit().putBoolean("pro_owned", v).apply(); }

    public long getAdFreeUntil() { return p.getLong("ad_free_until", 0); }
    public void setAdFreeUntil(long v) { p.edit().putLong("ad_free_until", v).apply(); }
}
