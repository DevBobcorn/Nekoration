package io.devbobcorn.nekoration.world.upgrade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.devbobcorn.nekoration.Nekoration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.SimpleBitStorage;

/** Rewrites v1 data before Minecraft attempts to resolve its registry ids. */
public final class LegacyWorldUpgrader {
    private static final String PREFIX = Nekoration.MODID + ":";
    private static final String[] V1_COLOR_NAMES = {
            "black", "blue", "brown", "cyan", "gray", "green", "light_blue", "light_gray",
            "lime", "magenta", "orange", "pink", "purple", "red", "white", "yellow"
    };
    private static final String[] V1_WOODS = {
            "dark_oak", "magic", "spruce", "warped", "jungle", "pine", "magic", "oak",
            "willow", "crimson", "acacia", "cherry", "mangrove", "redwood", "birch", "palm"
    };
    private static final byte[] V2_COLOR_IDS = {
            3, 12, 4, 10, 2, 9, 11, 1, 8, 14, 6, 15, 13, 5, 0, 7
    };

    private static final Map<String, String> SIMPLE_RENAMES = Map.ofEntries(
            Map.entry("window_top", "cement_frame_peak"),
            Map.entry("window_sill", "cement_frame_sill"),
            Map.entry("stone_base", "cement"),
            Map.entry("stone_base_bottom", "trimmed_cement"),
            Map.entry("stone_frame", "paneled_cement"),
            Map.entry("stone_frame_bottom", "paneled_cement"),
            Map.entry("stone_pillar", "paneled_cement"),
            Map.entry("stone_doric", "paneled_cement"),
            Map.entry("stone_ionic", "paneled_cement"),
            Map.entry("stone_corinthian", "paneled_cement"),
            Map.entry("stone_pillar_bottom", "paneled_cement"),
            Map.entry("stone_layered", "layered_cement"),
            Map.entry("stone_pot", "cement_pot"),
            Map.entry("stone_planter", "cement_planter"),
            Map.entry("lamp_post_iron", "iron_lamp_post"),
            Map.entry("lamp_post_gold", "gold_lamp_post"),
            Map.entry("lamp_post_quartz", "quartz_lamp_post"),
            Map.entry("candle_holder_iron", "iron_candle_holder"),
            Map.entry("candle_holder_gold", "gold_candle_holder"),
            Map.entry("candle_holder_quartz", "quartz_candle_holder"),
            Map.entry("flower_basket_iron", "iron_flower_basket"),
            Map.entry("flower_basket_gold", "gold_flower_basket"),
            Map.entry("flower_basket_quartz", "quartz_flower_basket"),
            Map.entry("awning_pure_short", "short_awning_pure"),
            Map.entry("awning_stripe_short", "short_awning_stripe"),
            Map.entry("door_1", "quartz_door"),
            Map.entry("door_2", "chiseled_quartz_door"),
            Map.entry("door_3", "quartz_bricks_door"),
            Map.entry("door_tall_1", "tall_quartz_door"),
            Map.entry("door_tall_2", "tall_chiseled_quartz_door"),
            Map.entry("door_tall_3", "tall_quartz_bricks_door"));

    private static final Set<String> COLOR_ITEMS = Set.of(
            "window_top", "window_sill", "window_frame", "window_plant",
            "stone_base", "stone_base_bottom", "stone_frame", "stone_frame_bottom", "stone_pillar",
            "stone_doric", "stone_ionic", "stone_corinthian", "stone_pillar_bottom", "stone_layered",
            "stone_pot", "stone_planter", "candle_holder_iron", "candle_holder_gold",
            "candle_holder_quartz", "awning_pure", "awning_stripe", "awning_pure_short",
            "awning_stripe_short");

    private static final Set<String> COLOR_BLOCKS = Set.of(
            "window_top", "window_sill", "window_frame", "window_plant",
            "stone_base", "stone_base_bottom", "stone_frame", "stone_frame_bottom", "stone_pillar",
            "stone_doric", "stone_ionic", "stone_corinthian", "stone_pillar_bottom", "stone_layered",
            "stone_pot", "stone_planter", "candle_holder_iron", "candle_holder_gold",
            "candle_holder_quartz", "awning_pure", "awning_stripe", "awning_pure_short",
            "awning_stripe_short", "door_1", "door_2", "door_3", "door_tall_1", "door_tall_2",
            "door_tall_3");

    private static final Set<String> WOODEN_BLOCKS = Set.of(
            "window_simple", "window_arch", "window_cross", "window_shade", "window_lancet",
            "glass_table", "glass_round_table", "arm_chair", "bench", "drawer", "cabinet",
            "drawer_chest", "cupboard", "shelf", "wall_shelf", "easel_menu", "easel_menu_white");
    private static final Set<String> COLLIDING_STONE_IDS = Set.of("stone_base", "stone_pot", "stone_planter");

    private LegacyWorldUpgrader() {
    }

    public static void upgradeChunk(CompoundTag chunk) {
        upgradeItemStacks(chunk);
        ListTag sections = chunk.getList("sections", Tag.TAG_COMPOUND);
        for (int sectionIndex = 0; sectionIndex < sections.size(); sectionIndex++) {
            CompoundTag section = sections.getCompound(sectionIndex);
            CompoundTag blockStates = section.getCompound("block_states");
            ListTag palette = blockStates.getList("palette", Tag.TAG_COMPOUND);
            for (int paletteIndex = 0; paletteIndex < palette.size(); paletteIndex++) {
                upgradeBlockState(palette.getCompound(paletteIndex));
            }
            deduplicatePalette(blockStates, palette);
        }
    }

    private static void deduplicatePalette(CompoundTag blockStates, ListTag palette) {
        List<CompoundTag> uniqueStates = new ArrayList<>();
        int[] remappedPalette = new int[palette.size()];
        for (int oldIndex = 0; oldIndex < palette.size(); oldIndex++) {
            CompoundTag state = palette.getCompound(oldIndex);
            int newIndex = uniqueStates.indexOf(state);
            if (newIndex < 0) {
                newIndex = uniqueStates.size();
                uniqueStates.add(state);
            }
            remappedPalette[oldIndex] = newIndex;
        }
        if (uniqueStates.size() == palette.size()) {
            return;
        }

        int[] values = new int[4096];
        SimpleBitStorage oldStorage = new SimpleBitStorage(
                serializedBlockStateBits(palette.size()), values.length, blockStates.getLongArray("data"));
        for (int index = 0; index < values.length; index++) {
            values[index] = remappedPalette[oldStorage.get(index)];
        }

        ListTag newPalette = new ListTag();
        newPalette.addAll(uniqueStates);
        blockStates.put("palette", newPalette);
        if (uniqueStates.size() == 1) {
            blockStates.remove("data");
        } else {
            SimpleBitStorage newStorage = new SimpleBitStorage(
                    serializedBlockStateBits(uniqueStates.size()), values.length, values);
            blockStates.putLongArray("data", newStorage.getRaw());
        }
    }

    private static int serializedBlockStateBits(int paletteSize) {
        return Math.max(4, 32 - Integer.numberOfLeadingZeros(paletteSize - 1));
    }

    /** Rewrites every legacy-format item stack nested in the supplied saved data. */
    public static void upgradeItemStacks(CompoundTag data) {
        upgradeNestedTag(data);
    }

    /** Moves custom item data left outside components by data fixes for modded containers. */
    public static void finalizeItemStacks(CompoundTag data) {
        finalizeNestedTag(data);
    }

    private static void upgradeNestedTag(Tag tag) {
        if (tag instanceof CompoundTag compound) {
            upgradeItemStack(compound);
            for (String key : Set.copyOf(compound.getAllKeys())) {
                upgradeNestedTag(compound.get(key));
            }
        } else if (tag instanceof ListTag list) {
            for (int index = 0; index < list.size(); index++) {
                upgradeNestedTag(list.get(index));
            }
        }
    }

    private static void finalizeNestedTag(Tag tag) {
        if (tag instanceof CompoundTag compound) {
            finalizeItemStack(compound);
            for (String key : Set.copyOf(compound.getAllKeys())) {
                finalizeNestedTag(compound.get(key));
            }
        } else if (tag instanceof ListTag list) {
            for (int index = 0; index < list.size(); index++) {
                finalizeNestedTag(list.get(index));
            }
        }
    }

    static boolean finalizeItemStack(CompoundTag stack) {
        boolean legacyCount = stack.contains("Count", Tag.TAG_ANY_NUMERIC);
        if ((!legacyCount && !stack.contains("count", Tag.TAG_ANY_NUMERIC))
                || !stack.getString("id").startsWith(PREFIX)) {
            return false;
        }

        if (legacyCount) {
            stack.putInt("count", stack.getInt("Count"));
            stack.remove("Count");
        }

        boolean movedLegacyTag = false;
        if (stack.contains("tag", Tag.TAG_COMPOUND)) {
            CompoundTag legacyTag = stack.getCompound("tag");
            if (!legacyTag.isEmpty()) {
                CompoundTag components = stack.contains("components", Tag.TAG_COMPOUND)
                        ? stack.getCompound("components")
                        : new CompoundTag();
                CompoundTag customData = components.contains("minecraft:custom_data", Tag.TAG_COMPOUND)
                        ? components.getCompound("minecraft:custom_data")
                        : new CompoundTag();
                customData.merge(legacyTag);
                components.put("minecraft:custom_data", customData);
                stack.put("components", components);
            }
            stack.remove("tag");
            movedLegacyTag = true;
        }
        return legacyCount || movedLegacyTag;
    }

    static boolean upgradeItemStack(CompoundTag stack) {
        // V1 stacks use Count; requiring it also distinguishes colliding v2 item ids after migration.
        if (!stack.contains("Count", Tag.TAG_ANY_NUMERIC)) {
            return false;
        }
        String id = stack.getString("id");
        if (!id.startsWith(PREFIX)) {
            return false;
        }

        String oldPath = id.substring(PREFIX.length());
        String newPath = SIMPLE_RENAMES.getOrDefault(oldPath, oldPath);
        CompoundTag itemTag = stack.contains("tag", Tag.TAG_COMPOUND)
                ? stack.getCompound("tag")
                : new CompoundTag();
        boolean recognized = SIMPLE_RENAMES.containsKey(oldPath) || COLOR_ITEMS.contains(oldPath);

        if (oldPath.startsWith("half_timber_p")) {
            String wood = V1_WOODS[getItemOrdinal(itemTag, "color_0", 0)];
            String pattern = oldPath.replace("half_timber_pillar_", "half_timber_");
            newPath = wood + "_" + pattern;
            itemTag.putByte("color", V2_COLOR_IDS[getItemOrdinal(itemTag, "color_1", 0)]);
            itemTag.remove("color_0");
            itemTag.remove("color_1");
            recognized = true;
        } else if (WOODEN_BLOCKS.contains(oldPath)) {
            String wood = V1_WOODS[getItemOrdinal(itemTag, "color", 0)];
            itemTag.remove("color");
            String suffix = switch (oldPath) {
                case "glass_round_table" -> "round_glass_table";
                case "arm_chair" -> "armchair";
                case "shelf" -> "cupboard";
                case "easel_menu_white" -> "easel_menu";
                default -> oldPath;
            };
            newPath = wood + "_" + suffix;
            if (oldPath.equals("easel_menu") || oldPath.equals("easel_menu_white")) {
                itemTag.putByte("color", oldPath.equals("easel_menu_white") ? (byte) 0 : (byte) 3);
            }
            recognized = true;
        } else if (COLOR_ITEMS.contains(oldPath)) {
            itemTag.putByte("color", V2_COLOR_IDS[getItemOrdinal(itemTag, "color", 0)]);
            if (oldPath.equals("window_frame")) {
                newPath = "cement_frame_side";
            }
        }

        if (!recognized) {
            return false;
        }
        stack.putString("id", PREFIX + newPath);
        if (!itemTag.isEmpty()) {
            stack.put("tag", itemTag);
        }
        return true;
    }

    private static int getItemOrdinal(CompoundTag itemTag, String key, int defaultValue) {
        if (!itemTag.contains(key, Tag.TAG_ANY_NUMERIC)) {
            return defaultValue;
        }
        int ordinal = itemTag.getInt(key);
        return ordinal >= 0 && ordinal < 16 ? ordinal : defaultValue;
    }

    static boolean upgradeBlockState(CompoundTag state) {
        String id = state.getString("Name");
        if (!id.startsWith(PREFIX)) {
            return false;
        }

        String oldPath = id.substring(PREFIX.length());
        CompoundTag original = state.copy();
        CompoundTag properties = state.contains("Properties", Tag.TAG_COMPOUND)
                ? state.getCompound("Properties")
                : new CompoundTag();
        if (COLLIDING_STONE_IDS.contains(oldPath) && !properties.contains("level", Tag.TAG_STRING)) {
            return false;
        }
        String newPath = SIMPLE_RENAMES.getOrDefault(oldPath, oldPath);

        if (COLOR_BLOCKS.contains(oldPath)) {
            renameColorProperty(properties, "level", "color");
        }

        if (oldPath.equals("window_frame")) {
            newPath = upgradeWindowFrame(properties);
        } else if (oldPath.startsWith("half_timber_p")) {
            newPath = upgradeHalfTimber(oldPath, properties);
        } else if (WOODEN_BLOCKS.contains(oldPath)) {
            newPath = upgradeWoodenBlock(oldPath, properties);
        }

        if (oldPath.startsWith("window_") && !oldPath.equals("window_plant")
                && !oldPath.equals("window_top") && !oldPath.equals("window_sill")
                && !oldPath.equals("window_frame")) {
            properties.remove("vertical_connection");
        }
        if (oldPath.endsWith("_bottom") && oldPath.startsWith("stone_")
                && !oldPath.equals("stone_base_bottom")) {
            properties.putString("vertical_connection", "s0");
        }
        if (oldPath.equals("stone_base_bottom")) {
            properties.remove("vertical_connection");
        }
        if (oldPath.startsWith("candle_holder_")) {
            properties.putString("flame", switch (properties.getString("age")) {
                case "1" -> "flame";
                case "2" -> "soul_flame";
                case "3" -> "firework";
                default -> "none";
            });
            properties.remove("age");
        }
        if (oldPath.startsWith("door_tall_")) {
            String segment = properties.getString("half").equals("upper") ? "middle" : "lower";
            properties.putString("segment", segment);
            properties.putString("half", segment.equals("lower") ? "lower" : "upper");
        }

        state.putString("Name", newPath.equals("minecraft:air") ? newPath : PREFIX + newPath);
        if (properties.isEmpty()) {
            state.remove("Properties");
        } else {
            state.put("Properties", properties);
        }
        return !state.equals(original);
    }

    private static String upgradeWindowFrame(CompoundTag properties) {
        String framePart = properties.getString("frame_part");
        boolean left = properties.getString("left").equals("true");
        boolean right = properties.getString("right").equals("true");
        if (!framePart.equals("middle") && (!left || !right)) {
            for (String key : Set.copyOf(properties.getAllKeys())) {
                properties.remove(key);
            }
            return "minecraft:air";
        }
        String newPath = switch (framePart) {
            case "top" -> "cement_frame_head";
            case "middle" -> "cement_frame_side";
            default -> "cement_frame_sill";
        };
        if (framePart.equals("middle")) {
            properties.putString("frame_connection", left == right ? "both" : left ? "right" : "left");
        } else {
            properties.putString("horizontal_connection", "s0");
        }
        properties.remove("frame_part");
        properties.remove("left");
        properties.remove("right");
        return newPath;
    }

    private static String upgradeHalfTimber(String oldPath, CompoundTag properties) {
        String wood = removeWood(properties);
        renameColorProperty(properties, "age", "color");
        String pattern = oldPath.replace("half_timber_pillar_", "half_timber_");
        if (!oldPath.startsWith("half_timber_pillar_")) {
            properties.putString("vertical_connection", "s0");
        }
        return wood + "_" + pattern;
    }

    private static String upgradeWoodenBlock(String oldPath, CompoundTag properties) {
        String wood = removeWood(properties);
        if (oldPath.equals("easel_menu") || oldPath.equals("easel_menu_white")) {
            properties.putString("color", oldPath.equals("easel_menu_white") ? "white" : "black");
        }
        String suffix = switch (oldPath) {
            case "glass_round_table" -> "round_glass_table";
            case "arm_chair" -> "armchair";
            case "shelf" -> "cupboard";
            case "easel_menu_white" -> "easel_menu";
            default -> oldPath;
        };
        return wood + "_" + suffix;
    }

    private static String removeWood(CompoundTag properties) {
        int oldColor = parseOrdinal(properties.getString("level"));
        properties.remove("level");
        return V1_WOODS[oldColor];
    }

    private static void renameColorProperty(CompoundTag properties, String oldName, String newName) {
        if (!properties.contains(oldName, Tag.TAG_STRING)) {
            return;
        }
        properties.putString(newName, V1_COLOR_NAMES[parseOrdinal(properties.getString(oldName))]);
        properties.remove(oldName);
    }

    private static int parseOrdinal(String value) {
        try {
            int ordinal = Integer.parseInt(value);
            return ordinal >= 0 && ordinal < 16 ? ordinal : 0;
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
}
