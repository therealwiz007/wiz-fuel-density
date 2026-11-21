# Wiz Fuel Density — STATUS

## 2026-11-21 — v1.6 (versionCode 9): SDK update
- Done: ads-mobile-sdk 1.6.0 -> 1.7.0 (Play Console "SDK outdated" warning). Version bumped, changelog 9.txt (en-US, hi-IN).
- Verified: `gradle :app:assembleDebug` OK on 1.7.0 (ads-off variant). Ads-on variant not built (no ads.properties).
- Remaining: push -> CI builds signed AAB; upload to Production in Play Console (needs user approval before submit).
- Next: Wiz-Dip-Charts (Sonnet), then Wiz-DNS-Changer.
