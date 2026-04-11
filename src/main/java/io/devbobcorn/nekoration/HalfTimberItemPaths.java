package io.devbobcorn.nekoration;

import java.util.Locale;

import io.devbobcorn.nekoration.blocks.HalfTimberWood;

/**
 * Parses {@code half_timber_<wood>_p*} / {@code half_timber_<wood>_pillar_p*} item ids.
 */
public final class HalfTimberItemPaths {
    private HalfTimberItemPaths() {
    }

    public static String woodIdSuffix(String path) {
        if (!path.startsWith("half_timber_")) {
            return null;
        }
        String rest = path.substring("half_timber_".length());
        if (rest.contains("_pillar_p")) {
            return rest.substring(0, rest.indexOf("_pillar_p"));
        }
        int li = rest.lastIndexOf("_p");
        if (li <= 0) {
            return null;
        }
        String tail = rest.substring(li);
        if (!tail.matches("_p\\d+")) {
            return null;
        }
        return rest.substring(0, li);
    }

    public static HalfTimberWood parseWood(String path) {
        String w = woodIdSuffix(path);
        if (w == null) {
            return null;
        }
        try {
            return HalfTimberWood.valueOf(w.toUpperCase(Locale.ROOT).replace('-', '_'));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
