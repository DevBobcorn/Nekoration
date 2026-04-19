package io.devbobcorn.nekoration;

import java.util.Locale;

import io.devbobcorn.nekoration.blocks.NekoWood;

/**
 * Parses {@code <wood>_half_timber_p*} / {@code <wood>_half_timber_pillar_p*} item ids.
 */
public final class HalfTimberItemPaths {
    private HalfTimberItemPaths() {
    }

    private static final String PILLAR_MARKER = "_half_timber_pillar_p";
    private static final String BASE_MARKER = "_half_timber_p";

    public static String woodIdSuffix(String path) {
        int pillarIdx = path.indexOf(PILLAR_MARKER);
        if (pillarIdx > 0) {
            return path.substring(0, pillarIdx);
        }
        int baseIdx = path.lastIndexOf(BASE_MARKER);
        if (baseIdx <= 0) {
            return null;
        }
        String tail = path.substring(baseIdx + BASE_MARKER.length());
        if (!tail.matches("\\d+")) {
            return null;
        }
        return path.substring(0, baseIdx);
    }

    public static NekoWood parseWood(String path) {
        String w = woodIdSuffix(path);
        if (w == null) {
            return null;
        }
        try {
            return NekoWood.valueOf(w.toUpperCase(Locale.ROOT).replace('-', '_'));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
