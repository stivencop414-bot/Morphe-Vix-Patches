package app.morphe.patches.meitu.vip

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.methodCall
import com.android.tools.smali.dexlib2.AccessFlags

/**
 * `XXVipUtil;->x0()Z` — the single account-level VIP gate.
 *
 * This answers "is this account VIP?" across the whole app: 364 call sites in
 * the 19 DEX files. `ModuleVipApi.isVip()` delegates here, and `u0()` (its
 * negation) and `v0()` (a specific subscription VIP) derive from it.
 *
 * Original body: returns `true` when `q.t2()` is 1 or 2, or when `w0()` (a
 * valid Google user flag read from `sp_key_google_is_valid_user`) is 1.
 *
 * Signals: the two calls to `com.meitu.vip.module.q.t2()` are exclusive to this
 * method inside the class.
 *
 * This is the right gate for account VIP state. `Skin.isVip()` is **not**
 * equivalent — see the note below — so it is deliberately not fingerprinted
 * here.
 */
internal object VipAccountGateFingerprint : Fingerprint(
    definingClass = "Lcom/meitu/vip/util/XXVipUtil;",
    name = "x0",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL),
    returnType = "Z",
    parameters = emptyList(),
    filters = listOf(
        methodCall(definingClass = "Lcom/meitu/vip/module/q;", name = "t2"),
        methodCall(definingClass = "Lcom/meitu/vip/util/XXVipUtil;", name = "w0"),
    ),
)

/**
 * Why there is no patch for `Skin.isVip()Z`.
 *
 * That method answers "does *this material* require VIP?", not "is the user
 * VIP?". Its result is written into `MTIKFilterDataModel.isVip`
 * (`SkinGlFragment.smali:5104`, `SkinIconGlFragment.smali:1904`,
 * `SkinWakeInfo`, `RemoldInfo`) and is combined with `and-int` before a decision
 * (`BoySkinFragment.smali:301`, `jq1.smali:855`).
 *
 * Forcing it to `true` marks every material as paid. On device that produced an
 * empty home, an editor that refused to open, and an error message from the paid
 * route. The effective lock is `materialRequiresVip && !userIsVip`, so patching
 * the account gate is both sufficient and safe; patching the material gate
 * inverts the meaning and breaks the engine.
 *
 * A correct material patch would have to act where the engine *consumes* that
 * flag, not where it produces it, and would need on-device verification. It is
 * not offered.
 */
