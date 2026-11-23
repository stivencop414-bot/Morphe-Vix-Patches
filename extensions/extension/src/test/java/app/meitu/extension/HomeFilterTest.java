package app.meitu.extension;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the Clean Home filtering logic.
 *
 * They need no APK and no device: they cover the guarantees the patch promises,
 * which are the ones that can break when the extension is touched.
 */
class HomeFilterTest {

    /** Minimal icon with the same contract as the app's models. */
    public static final class FakeIcon {
        private final String name;

        FakeIcon(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /** Icon without `getName`, to check that the unknown is not hidden. */
    public static final class NamelessIcon {
    }

    private static List<Object> icons(String... names) {
        List<Object> result = new ArrayList<>();
        for (String name : names) {
            result.add(new FakeIcon(name));
        }
        return result;
    }

    private static List<String> namesOf(List<Object> list) {
        List<String> result = new ArrayList<>();
        for (Object item : list) {
            result.add(((FakeIcon) item).getName());
        }
        return result;
    }

    @Test
    void hidesConfiguredName() {
        List<Object> input = icons("Featured", "Camera");

        List<Object> result = HomeFilter.filter(input, "Featured", "Camera");

        assertEquals(List.of("Camera"), namesOf(result));
    }

    @Test
    void keepsProtectedIconEvenIfConfiguredForHiding() {
        List<Object> input = icons("Camera", "Featured");

        List<Object> result = HomeFilter.filter(input, "Camera,Featured", "Camera");

        assertEquals(List.of("Camera"), namesOf(result));
    }

    @Test
    void returnsInputWhenNothingIsConfigured() {
        List<Object> input = icons("Featured", "Camera");

        assertSame(input, HomeFilter.filter(input, "", "Camera"));
        assertFalse(HomeFilter.isEnabled());
    }

    @Test
    void returnsInputWhenNoNameMatches() {
        List<Object> input = icons("Camera", "Collage");

        List<Object> result = HomeFilter.filter(input, "Featured", "");

        assertSame(input, result);
    }

    @Test
    void neverEmptiesTheHome() {
        List<Object> input = icons("Featured", "Dust");

        List<Object> result = HomeFilter.filter(input, "Featured,Dust", "");

        // Hiding everything would leave the home with no entries, so the original
        // list is kept.
        assertSame(input, result);
    }

    @Test
    void keepsIconWhoseNameCannotBeRead() {
        List<Object> input = new ArrayList<>();
        input.add(new NamelessIcon());
        input.add(new FakeIcon("Featured"));

        List<Object> result = HomeFilter.filter(input, "Featured", "");

        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof NamelessIcon);
    }

    @Test
    void handlesNullList() {
        assertNull(HomeFilter.filter(null, "Featured", ""));
    }

    @Test
    void ignoresWhitespaceAndEmptyEntries() {
        List<Object> input = icons("Featured", "Dust");

        List<Object> result = HomeFilter.filter(input, " , Featured ,  ", "");

        assertEquals(List.of("Dust"), namesOf(result));
    }
}
