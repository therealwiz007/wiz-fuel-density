package com.wizlabs.ads.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.wizlabs.ads.AdsConfig;

import java.util.Collections;
import java.util.HashSet;
import java.util.Arrays;

import org.junit.Test;

/** JVM tests for the Java port of AdGate (plan 7A P4): ads off means off, for every format and screen. */
public class AdGateJavaTest {
    private static final long DAY = 86_400_000L;

    private static AdsConfig config(boolean enabled) {
        return new AdsConfig(enabled, new AdsConfig.AdUnits("b", null), new AdsConfig.AdCaps(),
                Collections.<String>emptySet(), new HashSet<>(Arrays.asList("main")), "app", false, 0, null);
    }

    private static AdState allowed() {
        AdState s = new AdState(10 * DAY, 0, 5, 0);
        s.consentOk = true;
        s.sdkLinked = true;
        return s;
    }

    @Test public void switchedOffWinsForEveryFormatAndScreen() {
        AdGate g = new AdGate(config(false));
        for (AdFormat f : AdFormat.values())
            for (String screen : new String[] { "main", "other", null })
                assertEquals("ads switched off in this build", g.reason(allowed(), f, screen));
    }

    @Test public void bannerAllowedOnAdScreenWhenEverythingHolds() {
        assertNull(new AdGate(config(true)).reason(allowed(), AdFormat.BANNER, "main"));
    }

    @Test public void otherRulesInOrder() {
        AdGate g = new AdGate(config(true));
        AdState s = allowed(); s.proOwned = true;
        assertEquals("Pro", g.reason(s, AdFormat.BANNER, "main"));
        s = allowed(); s.now = DAY / 2;
        assertEquals("grace (days)", g.reason(s, AdFormat.BANNER, "main"));
        s = allowed(); s.sessions = 1;
        assertEquals("grace (sessions)", g.reason(s, AdFormat.BANNER, "main"));
        s = allowed(); s.consentOk = false;
        assertEquals("no consent", g.reason(s, AdFormat.BANNER, "main"));
        assertEquals("no-ad screen", g.reason(allowed(), AdFormat.BANNER, "other"));
        assertEquals("format not in the Java port", g.reason(allowed(), AdFormat.INTERSTITIAL, "main"));
    }
}
