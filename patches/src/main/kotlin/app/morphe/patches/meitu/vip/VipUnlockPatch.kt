package app.morphe.patches.meitu.vip

import app.morphe.patches.meitu.meitu
import app.morphe.patcher.patch.patch
import app.morphe.util.returnEarly

/**
 * **Experimental.** Makes the app treat the current account as VIP.
 *
 * Single intervention point: `XXVipUtil.x0()Z`. It answers "is this account
 * VIP?" across the whole app (364 call sites in the 19 DEX files), and the other
 * answers derive from it:
 *
 * - `u0()Z` is literally `x0() ^ 1`. Returning `true` here makes `u0()` return
 *   `false` for its 10+ consumers (draft saving, adjust, enhance, object removal),
 *   which is the "paid user" branch.
 * - `v0()Z` additionally requires `m0() == 0x65` (a subscription type), so it
 *   still distinguishes subscription VIP from other routes. It is left alone.
 * - `ModuleVipApi.isVip()` delegates directly here.
 *
 * The body is replaced with `return true` rather than rewriting the internal
 * logic: the original condition reads `q.t2()` (subscription state reported by
 * the module) and `w0()` (the `sp_key_google_is_valid_user` flag), both fed by
 * server data a non-purchasing account never receives. Rewriting them separately
 * would leave the branches inconsistent with each other.
 *
 * Scope, stated plainly: VIP state becomes local. Whatever the Meitu server
 * validates on its own (paid material downloads, cloud-side rendering, the
 * subscription purchase itself) is still validated there. This patch does not
 * create server rights.
 */
@Suppress("unused")
val vipUnlockPatch = patch(
    name = "VIP Unlock",
    description = "EXPERIMENTAL. Makes the app treat the current account as VIP by replacing the " +
        "`XXVipUtil.x0()` gate with `true`. Affects local VIP checks (interface, material flags, " +
        "local export routes). It does not create rights the Meitu server validates itself.",
    default = false,
) {
    category("Premium")
    compatibleWith(meitu)

    execute {
        // `method` throws PatchException when the fingerprint does not match, so a
        // build with a moved gate fails here instead of patching blind.
        VipAccountGateFingerprint.method.returnEarly(true)
    }
}
