package app.morphe.patches.meitu.premium

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.patch
import app.morphe.patches.shared.compat.AppCompatibilities

/**
 * Forces the client-side VIP state.
 *
 * The VIP decision lives in `com.meitu.vip.util.XXVipUtil`: `isVip()` (x0) consults the
 * VIP module's type and then `isValidUser()` (w0), while `getVipType()` (m0) backs the
 * VIP badge and tier checks. Returning a subscriber value from all of them keeps the
 * app from showing the purchase dialogs and from locking VIP-only materials.
 *
 * Server-driven rights (per-material entitlements and AIGC quotas) still come from the
 * network and are not covered here.
 */
@Suppress("unused")
val unlockVipPatch = patch(
    name = "Unlock VIP",
    description = "Reports an active VIP subscriber: isVip() returns true, isValidUser() returns 1, " +
        "getVipType() returns 1 and VIP trial materials count as unlocked. " +
        "Server-side per-material rights and AIGC quotas are not affected.",
    default = true,
) {
    category("Premium")
    compatibleWith(*AppCompatibilities.meitu())

    execute {
        IsVipFingerprint.method.apply {
            addInstructions(
                0,
                """
                    const/4 v0, 0x1
                    return v0
                """,
            )
        }

        ValidUserFingerprint.method.apply {
            addInstructions(
                0,
                """
                    const/4 v0, 0x1
                    return v0
                """,
            )
        }

        VipTypeFingerprint.method.apply {
            addInstructions(
                0,
                """
                    const/4 v0, 0x1
                    return v0
                """,
            )
        }

        TrialMaterialFingerprint.method.apply {
            addInstructions(
                0,
                """
                    const/4 v0, 0x1
                    return v0
                """,
            )
        }
    }
}
