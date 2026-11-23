package app.morphe.patches.meitu.ads

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.fieldAccess
import app.morphe.patcher.methodCall
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

/**
 * Meitu's own ad SDK decides whether an ad placement is active in two places.
 * Closing both makes the SDK stop requesting creative, so no layout receives
 * data to draw.
 *
 * Each fingerprint combines independent signals because every class and method
 * name in this APK is obfuscated: a fingerprint that only matched a name would
 * silently bind to the wrong method after an obfuscation change.
 */

/** `SettingsManager;->s(String)Z` — central placement gate. */
internal object IsAdOpenGateFingerprint : Fingerprint(
    definingClass = "Lcom/meitu/business/ads/core/agent/setting/SettingsManager;",
    name = "s",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "Z",
    parameters = listOf("Ljava/lang/String;"),
    filters = listOf(
        methodCall(
            definingClass = "Lcom/meitu/business/ads/core/agent/setting/SettingsManager;",
            name = "n",
        ),
        methodCall(
            definingClass = "Lcom/meitu/business/ads/core/bean/SettingsBean;",
            name = "isAdOpen",
        ),
    ),
)

/** `SettingsBean;->isAdOpen(String)Z` — second gate, read directly by some callers. */
internal object SettingsBeanIsAdOpenFingerprint : Fingerprint(
    definingClass = "Lcom/meitu/business/ads/core/bean/SettingsBean;",
    name = "isAdOpen",
    accessFlags = listOf(AccessFlags.PUBLIC),
    returnType = "Z",
    parameters = listOf("Ljava/lang/String;"),
    filters = listOf(
        methodCall(definingClass = "Landroid/text/TextUtils;", name = "isEmpty"),
        string("SettingsBean isAdOpen ad_settings : "),
        string("SettingsBean isAdOpen ad_settings size: "),
    ),
)

/**
 * `MainActivity;->sk()V` — starts the cold-start ad flow.
 *
 * The patch replaces the body with the same failure path the app already takes
 * when its ad engine is not initialised, so the home still receives a finished
 * splash state from its own callback.
 */
internal object StartAdActivityFingerprint : Fingerprint(
    definingClass = "Lcom/mt/mtxx/mtxx/MainActivity;",
    name = "sk",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = emptyList(),
    filters = listOf(
        string("startAdActivity Initiator processScheme, campaignId="),
        string("xx_LaunchAdAll"),
    ),
)

/**
 * `ozq;->b()V` — startup engine that instantiates the splash screen.
 *
 * Used as an anchor only: if the splash flow moved, this fails first and points
 * at the part that changed.
 */
internal object StartupEngineShowSplashFingerprint : Fingerprint(
    definingClass = "Lozq;",
    name = "b",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = emptyList(),
    filters = listOf(
        string("handleSuccessSplahAdData() called dspName "),
        string("startTemplateSplashActivity() called with: e = ["),
        string("bundle_cold_start_up"),
    ),
)

/**
 * `TemplateSplashActivity;->onCreate(Bundle)V` — splash screen entry.
 *
 * Notes: the method is `public` with no `final`, and the filters are listed in
 * the order the instructions actually appear. `filters` is matched in source
 * order, so listing the log line before the super call never matches.
 */
internal object TemplateSplashOnCreateFingerprint : Fingerprint(
    definingClass = "Lcom/meitu/business/ads/core/activity/TemplateSplashActivity;",
    name = "onCreate",
    accessFlags = listOf(AccessFlags.PUBLIC),
    returnType = "V",
    parameters = listOf("Landroid/os/Bundle;"),
    filters = listOf(
        methodCall(
            definingClass = "Lcom/meitu/business/ads/core/activity/BaseActivity;",
            name = "onCreate",
        ),
        string("TemplateSplashActivity onCreate \u662f\u5426\u662f\u51b7\u542f\u52a8 : "),
    ),
)

/**
 * `MtbBaseLayout;->display(SyncLoadParams, AdDataBean, l4y)V` — where a banner
 * or native ad is painted.
 *
 * Notes: the real method takes three parameters (the callback `Ll4y;` was
 * missing), and the field read is `iget-object`, not `iget`. Anchor only; the
 * body is not replaced because its no-agent branch invokes the splash display
 * callback, and skipping it could leave a caller waiting.
 */
internal object AdLayoutDisplayFingerprint : Fingerprint(
    definingClass = "Lcom/meitu/business/ads/core/view/MtbBaseLayout;",
    name = "display",
    accessFlags = listOf(AccessFlags.PUBLIC),
    returnType = "V",
    parameters = listOf(
        "Lcom/meitu/business/ads/core/agent/syncload/SyncLoadParams;",
        "Lcom/meitu/business/ads/core/bean/AdDataBean;",
        "Ll4y;",
    ),
    filters = listOf(
        string("display() called with: loadParams = ["),
        fieldAccess(
            opcode = Opcode.IGET_OBJECT,
            definingClass = "this",
            name = "mAdAgent",
            type = "Lcom/meitu/business/ads/core/agent/q;",
        ),
    ),
)
