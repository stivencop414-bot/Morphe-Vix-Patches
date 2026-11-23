package app.morphe.patches.meitu.home

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.methodCall
import com.android.tools.smali.dexlib2.AccessFlags

/**
 * `BaseHomePageTopToolsAdapter;->o0(List)V` — the single funnel every top-of-home
 * icon passes through before being drawn.
 *
 * The nine calls from `HomeToolFragment` and `HomeToolFragment2` all converge
 * here, so filtering at this point covers every route without touching each one.
 */
internal object HomeIconsFeedFingerprint : Fingerprint(
    definingClass = "Lcom/mt/adapter/BaseHomePageTopToolsAdapter;",
    name = "o0",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf("Ljava/util/List;"),
    filters = listOf(
        // Empties its internal list before replacing it with the new one.
        methodCall(definingClass = "Ljava/util/ArrayList;", name = "clear"),
        methodCall(definingClass = "Ljava/util/ArrayList;", name = "addAll"),
    ),
)
