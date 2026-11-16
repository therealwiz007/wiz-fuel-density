package com.wizlabs.ads.core;

/** Java port of AdState (AdGate.kt): plain values, so the rules are unit-testable on the JVM. */
public final class AdState {
    public long now;
    /** max(first install, first launch of the ads release): grace counts from here (plan 7.5). */
    public long graceStart;
    public int sessions;
    public long sessionStartedAt;
    public boolean proOwned;
    public boolean premium;
    public long adFreeUntil;
    public boolean remoteEnabled = true;
    public boolean consentOk;
    public boolean sdkLinked;
    public boolean lowRam;
    public boolean batterySaver;

    public AdState(long now, long graceStart, int sessions, long sessionStartedAt) {
        this.now = now; this.graceStart = graceStart; this.sessions = sessions; this.sessionStartedAt = sessionStartedAt;
    }
}
