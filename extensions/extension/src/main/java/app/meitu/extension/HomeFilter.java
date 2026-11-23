package app.meitu.extension;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Home icon filter for Meitu.
 *
 * The extension does not know the app's obfuscated types, so it works on
 * {@code Object} and reads each icon's name reflectively through {@code getName()},
 * which exists on both {@code TopLevelToolResp} and {@code SubLevelToolResp}.
 *
 * Configuration arrives on every call from the injected code, so the filter does
 * not depend on any earlier initialisation having happened first.
 */
@SuppressWarnings("unused")
public final class HomeFilter {

    /** Names that are never hidden, so the editor stays reachable. */
    private static volatile Set<String> protectedNames = new LinkedHashSet<>();

    /** Names that are hidden. */
    private static volatile Set<String> hiddenNames = new LinkedHashSet<>();

    private static volatile boolean enabled = false;

    private HomeFilter() {
    }

    /**
     * Filters the home icon list.
     *
     * @param icons          original list; may be null
     * @param hidden         names to hide, separated by commas
     * @param protectedIcons names that are never hidden, separated by commas
     * @return the filtered list, or the original one if there is nothing to filter
     */
    public static List<Object> filter(List<Object> icons, String hidden, String protectedIcons) {
        configure(hidden, protectedIcons);

        if (icons == null || icons.isEmpty() || !enabled) {
            return icons;
        }

        List<Object> kept = new ArrayList<>(icons.size());
        for (Object icon : icons) {
            String name = nameOf(icon);
            if (name == null || !hiddenNames.contains(name)) {
                kept.add(icon);
            }
        }

        // If nothing was hidden, return the same list: the caller may compare by
        // identity and should not receive a useless copy.
        if (kept.size() == icons.size()) {
            return icons;
        }

        // Never leave the home with no access at all: if everything was filtered,
        // keep the original list.
        return kept.isEmpty() ? icons : kept;
    }

    /** @return whether anything is configured to be hidden. */
    public static boolean isEnabled() {
        return enabled;
    }

    private static void configure(String hidden, String protectedIcons) {
        Set<String> wanted = split(hidden);
        if (wanted.isEmpty()) {
            enabled = false;
            return;
        }

        // A protected entry always wins over a hidden one.
        wanted.removeAll(split(protectedIcons));

        hiddenNames = wanted;
        protectedNames = split(protectedIcons);
        enabled = !wanted.isEmpty();
    }

    /**
     * Reads the icon's display name without depending on its obfuscated type.
     *
     * @return the name, or {@code null} if it could not be read
     */
    private static String nameOf(Object icon) {
        if (icon == null) {
            return null;
        }
        try {
            Object value = icon.getClass().getMethod("getName").invoke(icon);
            return value instanceof String ? (String) value : null;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            // An icon whose name cannot be read stays visible: safer than hiding it.
            return null;
        }
    }

    private static Set<String> split(String value) {
        Set<String> result = new LinkedHashSet<>();
        if (value == null || value.isEmpty()) {
            return result;
        }
        for (String part : value.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }
}
