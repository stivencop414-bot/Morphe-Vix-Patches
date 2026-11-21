package app.morphe.patches.shared.compat

import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

internal object AppCompatibilities {
    const val VIX_NAME = "ViX"
    const val VIX_PACKAGE = "com.univision.prendetv"
    const val VIX_COLOR = 0xFF6B00
    const val VIX_SIGNER_SHA256 = "eb6f3b76c6110bb945f88530ff5742634e48223c382931c319daceb431fe666d"
    const val VIX_4521_VERSION_CODE = 7902

    fun vix(): Array<Compatibility> = arrayOf(
        Compatibility(
            name = VIX_NAME,
            packageName = VIX_PACKAGE,
            appIconColor = VIX_COLOR,
            signatures = setOf(VIX_SIGNER_SHA256),
            targets = listOf(
                AppTarget(version = "4.52.1_mobile", versionCode = VIX_4521_VERSION_CODE),
            ),
        ),
    )
}
