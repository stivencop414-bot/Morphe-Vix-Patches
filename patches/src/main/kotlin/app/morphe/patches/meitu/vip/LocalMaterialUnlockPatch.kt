package app.morphe.patches.meitu.vip

import app.morphe.patches.meitu.meitu
import app.morphe.patcher.patch.patch
import app.morphe.util.returnEarly

/**
 * **Experimental.** Enables the VIP facial-retouch materials.
 *
 * The problem it solves: `VIP Unlock` changes the *account* VIP state, but the
 * lock on a beauty material does not depend on it. `Skin.isVip()Z` decides on
 * its own whether a particular `Skin` is paid, by comparing its `type` and
 * `material_id`. That is why an account with VIP can still see locks on some
 * materials.
 *
 * Single intervention point: `Skin.isVip()Z` (13 call sites, all in the retouch
 * modules: `SkinViewModel`, `SkinIconViewModel`, `SkinIconGlFragment`,
 * `SkinGlFragment`, `BoySkinFragment`).
 *
 * Why `Skin$q.m(I)Z` is not touched: that method resolves the material from
 * `type` and delegates to `Skin$q.p(Leax;)Z`, which besides the VIP flag may
 * carry availability information. Replacing it with `true` would claim any
 * `type` is VIP, including ones with no associated material (`m` can return
 * `null` and `p` then receives `null`). The patch acts in `isVip()`, where the
 * boolean already has exactly the meaning needed.
 *
 * Scope, stated plainly: affects only the local classification of the material
 * in the facial-retouch modules. Downloading the material still goes through
 * `MaterialKitApiImpl`, which reads the account VIP state and, for remote
 * material, the server. Combined with `VIP Unlock` both local gates are covered;
 * material the server withholds is still withheld.
 */
@Suppress("unused")
val localMaterialUnlockPatch = patch(
    name = "Unlock Local Materials",
    description = "EXPERIMENTAL. Makes `Skin.isVip()` return `true` so facial-retouch materials stop " +
        "being classified locally as paid. Independent of account state; combines with VIP Unlock. " +
        "It does not download material the server withholds.",
    default = false,
) {
    category("Premium")
    compatibleWith(meitu)

    execute {
        SkinVipMaterialFingerprint.method.returnEarly(true)
    }
}
