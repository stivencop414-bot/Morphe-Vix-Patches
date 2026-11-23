package app.morphe.patches.meitu.ads

import app.morphe.patches.meitu.meitu
import app.morphe.patcher.patch.patch
import app.morphe.util.addInstructions
import app.morphe.util.returnEarly

/**
 * Blocks the cold-start ad flow without delaying entry into the app.
 *
 * `MainActivity.sk()` builds the flow callback (`MainActivity$u`) and hands it to
 * the startup engine, which opens `TemplateSplashActivity`. When the engine is
 * not initialised, the app itself closes the splash state by calling
 * `MainActivity$u.onFailed(10, "未初始化！")` and returning.
 *
 * This patch substitutes the body with that same path, replicating behaviour the
 * app already has for "no ad engine" instead of inventing one. The home receives
 * `SplashState.FINISHED` through its own callback, so no screen is left waiting.
 *
 * It runs as a dependency of [removeAdsPatch] because the splash is one more ad
 * placement; it is not exposed as a separate option, so there is no control that
 * promises something the patch does not change.
 */
private val blockStartupAdFlow = patch {
    execute {
        // `method` throws PatchException when the fingerprint does not match, so
        // a version with a moved splash fails here instead of patching blind.
        // The startup engine is touched first so the error names the part that
        // changed.
        StartupEngineShowSplashFingerprint.method

        StartAdActivityFingerprint.method.addInstructions(
            0,
            """
                new-instance v0, Lcom/mt/mtxx/mtxx/MainActivity${'$'}u;
                invoke-direct {v0, p0}, Lcom/mt/mtxx/mtxx/MainActivity${'$'}u;-><init>(Lcom/mt/mtxx/mtxx/MainActivity;)V
                invoke-virtual {v0}, Lcom/mt/mtxx/mtxx/MainActivity${'$'}u;->onStart()V
                const/16 v1, 0xa
                const-string v2, "未初始化！"
                invoke-virtual {v0, v1, v2}, Lcom/mt/mtxx/mtxx/MainActivity${'$'}u;->onFailed(ILjava/lang/String;)V
                return-void
            """,
        )
    }
}

/**
 * Disables Meitu's own ad placements.
 *
 * Strategy: close the gates that decide whether a placement is active. With no
 * active placement the SDK does not request creative, does not record
 * impressions, and no layout receives data to draw.
 *
 * 1. Cold-start splash: the opening activity is skipped (see
 *    [blockStartupAdFlow]).
 * 2. `SettingsManager.s(String)` returns `false` — central gate, read by the
 *    startup engine, the load session and the scheduler.
 * 3. `SettingsBean.isAdOpen(String)` returns `false` — covers callers that read
 *    the remote `ad_settings` list without going through `SettingsManager`.
 *
 * No editor, camera or gallery screen is touched.
 *
 * Why `MtbBaseLayout.display(...)` is not patched: its own no-agent branch
 * invokes the `splashDisplayCallback`, and a return placed there could leave a
 * caller waiting. The gates already stop ad data from arriving, so the layout
 * does not need to be touched.
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

    dependsOn(blockStartupAdFlow)

    execute {
        IsAdOpenGateFingerprint.method.returnEarly(false)
        SettingsBeanIsAdOpenFingerprint.method.returnEarly(false)
    }
}
