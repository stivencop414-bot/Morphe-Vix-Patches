package app.morphe.patches.meitu.home

import app.morphe.patches.meitu.meitu
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.patch
import app.morphe.patcher.patch.stringOption
import app.morphe.patcher.util.getFreeRegisterProvider

private const val HOME_FILTER_CLASS = "Lapp/meitu/extension/HomeFilter;"

/**
 * Clean Home — hides promotional home icons with separate options.
 *
 * Scope, as verified: Meitu's home is composed from a remote response
 * (`HomeIconJsonResp.DataResp`). There is no "Discover" tab with a stable
 * identifier in the APK; sections are data. The only verifiable lever is the
 * `BaseHomePageTopToolsAdapter.o0(List)` funnel every icon passes through.
 *
 * The patch filters that funnel by exact name. If a name does not exist in your
 * region nothing is hidden and the home stays as it was.
 *
 * Safeguards: camera, photo, beautify, face, body and collage are protected by
 * default. The extension never leaves the home with no icons: if the filter
 * would remove all of them, it returns the original list.
 */
@Suppress("unused")
val cleanHomePatch = patch(
    name = "Clean Home",
    description = "Hides promotional icons from Meitu's home by filtering its icon list before it is drawn. " +
        "Camera, photo, beautify, face, body and collage are protected and never hidden. " +
        "If a name does not exist in your region, the home is left untouched.",
    default = false,
) {
    category("Layout")
    compatibleWith(meitu)

    extendWith("extensions/extension.mpe")

    val hiddenIcons by stringOption(
        key = "hiddenIcons",
        title = "Icons to hide",
        description = "Exact names separated by commas, as they appear on the home. " +
            "Example: Featured,Dust",
        default = "Featured",
        required = false,
    )

    val protectedIcons by stringOption(
        key = "protectedIcons",
        title = "Protected icons",
        description = "Names this patch never hides, to keep access to the editor.",
        default = "Camera,Photo,Beautify,Face,Body,Collage",
        required = false,
    )

    execute {
        val method = HomeIconsFeedFingerprint.method

        // The injection goes at the start of the method, before the list is
        // copied into the adapter's internal list. `p1` is the icon list.
        val registers = method.getFreeRegisterProvider(0, 2, emptyList())
        val hiddenRegister = registers.getFreeRegister4Bit()
        val protectedRegister = registers.getFreeRegister4Bit()

        method.addInstructions(
            0,
            """
                const-string v$hiddenRegister, ${hiddenIcons.orEmpty().escapeSmaliString()}
                const-string v$protectedRegister, ${protectedIcons.orEmpty().escapeSmaliString()}
                invoke-static { p1, v$hiddenRegister, v$protectedRegister }, $HOME_FILTER_CLASS->filter(Ljava/util/List;Ljava/lang/String;Ljava/lang/String;)Ljava/util/List;
                move-result-object p1
            """,
        )
    }
}

/** Escapes an option value so it can be embedded as a smali string literal. */
private fun String.escapeSmaliString(): String =
    "\"" + replace("\\", "\\\\").replace("\"", "\\\"") + "\""
