package app.morphe.patches.meitu.vip

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.methodCall
import com.android.tools.smali.dexlib2.AccessFlags

/**
 * Two independent local gates decide Premium access in Meitu. They are not the
 * same check, which is why there are two patches.
 */

/**
 * `XXVipUtil;->x0()Z` — the single account-level VIP gate.
 *
 * This answers "is this account VIP?" across the whole app: 364 call sites in
 * the 19 DEX files. `ModuleVipApi.isVip()` delegates here, and `u0()` (its
 * negation), `v0()` (a specific subscription VIP) all derive from it.
 *
 * Original body: returns `true` when `q.t2()` is 1 or 2, or when `w0()` (a
 * valid Google user flag read from `sp_key_google_is_valid_user`) is 1.
 *
 * Signals: the two calls to `com.meitu.vip.module.q.t2()` are exclusive to this
 * method inside the class.
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
 * `Skin;->isVip()Z` — the VIP state of one facial-retouch material.
 *
 * Decides whether a `Skin` is treated as paid. It is independent of the account
 * state: it never consults the subscription. A VIP account can still hold a
 * material that this method reports as VIP, and the UI draws the lock from this
 * answer. 13 call sites, all in the retouch modules.
 *
 * Original body: compares `type` with `Custom`; if it matches and `material_id`
 * is not the free material `0x153820f` it returns `false`; if `type` is `AIGC`
 * it delegates to `gg3.n0(material)`; otherwise it delegates to `Skin$q.m(type)`.
 *
 * Signals: `Skin$q.m(I)Z` is only invoked from this method in the class, and the
 * `getMaterial_id()` read narrows it further. Filters are listed in the order
 * the instructions appear: `getMaterial_id` comes first, `Skin$q.m` last.
 */
internal object SkinVipMaterialFingerprint : Fingerprint(
    definingClass = "Lcom/mt/formula/beauty/Skin;",
    name = "isVip",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Z",
    parameters = emptyList(),
    filters = listOf(
        methodCall(
            definingClass = "Lcom/mt/data/relation/MaterialResp_and_Local;",
            name = "getMaterial_id",
        ),
        methodCall(definingClass = "Lcom/mt/formula/beauty/Skin\$q;", name = "m"),
    ),
)
