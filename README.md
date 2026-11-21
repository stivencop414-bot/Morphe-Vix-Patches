# ViX Patches (for Morphe)

Community patches for **ViX** (`com.univision.prendetv`) built for **Morphe** (`morphe-patcher 2.4.0`), covering ad blocking, auto-skip for VOD server-side ads, telemetry/tracking removal, consent/forced-update bypass, screenshot/screen-recording support, and Developer Tools unlocking.

> Note on server-enforced SVOD/DRM content: These patches modify client-side behavior only. Paid ViX Premium entitlements and hardware-backed Widevine L1 server licenses are enforced server-side and are not bypassed.

## Supported Target

| App | Package Name | Version | Version Code | Certificate SHA-256 |
| --- | --- | --- | --- | --- |
| ViX | `com.univision.prendetv` | `4.52.1_mobile` | `7902` | `eb6f3b76c6110bb945f88530ff5742634e48223c382931c319daceb431fe666d` |

## Patches Included (`8`)

### Ads

| Patch | Default | Description |
| --- | --- | --- |
| `Block player ads` | Enabled | Disables Lura client-side prerolls, pause ads (`F2` / `M2`), GAM `pod-serve` DAI configs, clip ad tags, and Google IMA ad tag resolution (`AdsWrapper`). |
| `Auto-skip server-side ads` | Enabled | Calls `LuraPlayer.skipAd()` when an ad starts on non-live VOD streams (`!player.isLive()`). |
| `Hide promotional popups` | Enabled | Suppresses Braze in-app promotional dialogs and upgrade banners (`BrazeInAppMessageManager.displayInAppMessage`). |

### Privacy

| Patch | Default | Description |
| --- | --- | --- |
| `Disable telemetry and tracking` | Enabled | Disables NewRelic startup, Permutive DMP SDK, AppsFlyer initialization, Conviva and Google PAL player telemetry plugins, Advertising ID collection, and strips `com.google.android.gms.permission.AD_ID` from `AndroidManifest.xml`. |
| `Bypass OneTrust consent delay` | Disabled | Skips `OTPublishersHeadlessSDK.startSDK` initialization at startup and invokes the continuation callback immediately. |

### Interaction

| Patch | Default | Description |
| --- | --- | --- |
| `Allow screenshots and screen recording` | Enabled | Strips `Window.setFlags(FLAG_SECURE)` and forces `LuraVideoView` / `LuraPlayerView` `setSecure` to `false`. Hardware-backed Widevine L1 DRM surfaces may still be restricted by Android codecs. |

### Misc

| Patch | Default | Description |
| --- | --- | --- |
| `Bypass forced app update` | Enabled | Prevents `SplashScreenFragment` from presenting `AppUpdateDialog` and clears the `REQUIRE_UPGRADE` gate. |
| `Unlock Developer Tools` | Enabled | Bypasses the SHA-1 passcode prompt for ViX's hidden Developer Tools (`DevToolsScreenFragment`). Tap the app version **6 times** in Settings to open it. |

## Building

Requires JDK 17+.

```bash
./gradlew :patches:jar :patches:generatePatchesList --no-daemon -Dorg.gradle.jvmargs="-Xmx1280m" -PallowMavenLocal=true
```

Outputs:

- Bundle: `patches/build/libs/patches-0.1.0.mpp`
- Catalog: `patches-list.json`

## License

Licensed under the [GNU General Public License v3.0](LICENSE) with Additional Terms under Section 7 described in [NOTICE](NOTICE).
