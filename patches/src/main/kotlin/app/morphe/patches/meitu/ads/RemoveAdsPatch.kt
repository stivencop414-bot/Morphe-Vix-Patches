package app.morphe.patches.meitu.ads

import app.morphe.patches.meitu.meitu
import app.morphe.patcher.patch.patch
import app.morphe.util.returnEarly

/**
 * Disables Meitu's own ad placements.
 *
 * Strategy: close the gates that decide whether a placement is active. With no
 * active placement the SDK does not request creative, does not record
 * impressions, and no layout receives data to draw.
 *
 * 1. `SettingsManager.s(String)` returns `false` — central gate, read by the
 *    startup engine, the load session and the scheduler.
 * 2. `SettingsBean.isAdOpen(String)` returns `false` — covers callers that read
 *    the remote `ad_settings` list without going through `SettingsManager`.
 *
 * **The cold-start splash is not patched separately.** Closing the gates leaves
 * the startup engine (`hzq`) uninitialised (`hzq.j == true`), and the original
 * `MainActivity.sk()` already has a failure branch for that case: it calls
 * `MainActivity$u.onFailed(10, "未初始化！")` and delivers `SplashState.FINISHED`
 * to the home. That native behaviour is preserved without touching `sk()`.
 *
 * An earlier revision injected a `return-void` into `sk()`, which replaced the
 * whole body and skipped startup initialisation. On device that produced an
 * empty home and an editor that refused to open. Replacing the method was wrong:
 * the app's own failure branch already does exactly what the patch needs.
 *
 * No editor, camera or gallery screen is touched.
 *
 * Why `MtbBaseLayout.display(...)` is not patched: its own no-agent branch
 * invokes the `splashDisplayCallback`, and a return placed there could leave a
 * caller waiting. The gates already stop ad data from arriving.
 */
@Suppress("unused")
val removeAdsPatch = patch(
    name = "Remove Ads",
    description = "Disables Meitu's banners, interstitials, cold-start splash and native ads by closing " +
        "its placement gates. The editor, gallery and normal content loading are unaffected.",
    default = true,
) {
    category("Ads")
    compatibleWith(meitu)

    execute {
        IsAdOpenGateFingerprint.method.returnEarly(false)
        SettingsBeanIsAdOpenFingerprint.method.returnEarly(false)
    }
}
