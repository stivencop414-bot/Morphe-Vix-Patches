package app.morphe.patches.meitu

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility
import app.morphe.patcher.patch.SupportedAbi

/**
 * Meitu — AI Photo & Video Editor (`com.mt.mtxx.mtxx`).
 *
 * Only version `12.23.3` was examined, and only at the bytecode level: the APK
 * ships `arm64-v8a`/`armeabi-v7a` only and no ARM device was available to run
 * the patched build. The target is therefore flagged [AppTarget.isExperimental]
 * so Morphe shows it as experimental rather than as a confirmed-working patch.
 *
 * The release certificate is pinned, so a differently signed build, a modified
 * build or a different version is rejected instead of being patched blind.
 */
internal val meitu = Compatibility(
    name = "Meitu",
    packageName = "com.mt.mtxx.mtxx",
    apkFileType = ApkFileType.APKM,
    appIconColor = 0xF01C48,
    signatures = setOf("fbf80b9d1e44cc257b3ff3e51abece20d911fe76c0210b21ff58602c734c0153"),
    targets = listOf(
        AppTarget(
            version = "12.23.3",
            versionCodes = mapOf(
                SupportedAbi.ARM64_V8A to 122303,
                SupportedAbi.ARMEABI_V7A to 122303,
            ),
            isExperimental = true,
            description = "Bytecode verified against the original APK. Not yet run on a device, " +
                "so the on-screen result is unconfirmed.",
        ),
    ),
)
