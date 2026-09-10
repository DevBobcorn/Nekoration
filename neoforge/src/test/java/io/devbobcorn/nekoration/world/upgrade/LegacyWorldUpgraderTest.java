package io.devbobcorn.nekoration.world.upgrade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;

class LegacyWorldUpgraderTest {
    private static final String[] V1_WOODS = {
            "dark_oak", "magic", "spruce", "warped", "jungle", "pine", "magic", "oak",
            "willow", "crimson", "acacia", "cherry", "umbran", "redwood", "birch", "palm"
    };

    @Test
    void upgradesRepresentativeBlockStates() {
        CompoundTag legacyStone = state("stone_base", "level", "14", "vertical_connection", "d0");
        CompoundTag legacyStoneWithoutProperties = state("stone_base");
        assertTrue(LegacyWorldUpgrader.upgradeBlockState(legacyStone));
        assertEquals("nekoration:cement", legacyStone.getString("Name"));
        assertEquals("white", legacyStone.getCompound("Properties").getString("color"));
        assertTrue(LegacyWorldUpgrader.upgradeBlockState(legacyStoneWithoutProperties));
        assertEquals("nekoration:cement", legacyStoneWithoutProperties.getString("Name"));

        CompoundTag frame = state("window_frame", "level", "0", "frame_part", "middle",
                "left", "true", "right", "false", "facing", "north");
        LegacyWorldUpgrader.upgradeBlockState(frame);
        assertEquals("nekoration:cement_frame_side", frame.getString("Name"));
        assertEquals("right", frame.getCompound("Properties").getString("frame_connection"));

        CompoundTag timber = state("half_timber_pillar_p2", "level", "11", "age", "13",
                "vertical_connection", "t1");
        LegacyWorldUpgrader.upgradeBlockState(timber);
        assertEquals("nekoration:cherry_half_timber_p2", timber.getString("Name"));
        assertEquals("red", timber.getCompound("Properties").getString("color"));

        CompoundTag upperDoor = state("door_tall_2", "level", "2", "half", "upper");
        LegacyWorldUpgrader.upgradeBlockState(upperDoor);
        assertEquals("middle", upperDoor.getCompound("Properties").getString("segment"));
    }

    @Test
    void removesIncompleteWindowFrameHeadsAndSills() {
        CompoundTag head = state("window_frame", "level", "0", "frame_part", "top",
                "left", "true", "right", "false", "facing", "north");
        CompoundTag sill = state("window_frame", "level", "0", "frame_part", "bottom",
                "left", "false", "right", "false", "facing", "north");
        CompoundTag completeHead = state("window_frame", "level", "0", "frame_part", "top",
                "left", "true", "right", "true", "facing", "north");

        LegacyWorldUpgrader.upgradeBlockState(head);
        LegacyWorldUpgrader.upgradeBlockState(sill);
        LegacyWorldUpgrader.upgradeBlockState(completeHead);

        assertEquals("minecraft:air", head.getString("Name"));
        assertFalse(head.contains("Properties"));
        assertEquals("minecraft:air", sill.getString("Name"));
        assertFalse(sill.contains("Properties"));
        assertEquals("nekoration:cement_frame_head", completeHead.getString("Name"));
        assertEquals("s0", completeHead.getCompound("Properties").getString("horizontal_connection"));
    }

    @Test
    void upgradesChunkPalettesIdempotently() {
        CompoundTag chunk = new CompoundTag();
        CompoundTag section = new CompoundTag();
        CompoundTag blockStates = new CompoundTag();
        ListTag palette = new ListTag();
        palette.add(state("lamp_post_gold", "facing", "east"));
        palette.add(state("window_plant", "level", "15", "facing", "south"));
        blockStates.put("palette", palette);
        section.put("block_states", blockStates);
        ListTag sections = new ListTag();
        sections.add(section);
        chunk.put("sections", sections);

        LegacyWorldUpgrader.upgradeChunk(chunk);
        CompoundTag firstResult = chunk.copy();
        LegacyWorldUpgrader.upgradeChunk(chunk);

        assertEquals("nekoration:gold_lamp_post", palette.getCompound(0).getString("Name"));
        assertEquals("yellow", palette.getCompound(1).getCompound("Properties").getString("color"));
        assertEquals(firstResult, chunk);
    }

    @Test
    void deduplicatesUpgradedChunkPaletteAndRemapsStorage() {
        CompoundTag chunk = new CompoundTag();
        CompoundTag section = new CompoundTag();
        CompoundTag blockStates = new CompoundTag();
        ListTag palette = new ListTag();
        palette.add(minecraftState("air"));
        palette.add(state("window_frame", "level", "0", "frame_part", "top",
                "left", "true", "right", "false", "facing", "north"));
        palette.add(state("stone_frame", "level", "0"));
        palette.add(state("stone_pillar", "level", "0"));
        blockStates.put("palette", palette);
        int[] values = new int[4096];
        values[0] = 0;
        values[1] = 1;
        values[2] = 2;
        values[3] = 3;
        blockStates.putLongArray("data", new SimpleBitStorage(4, values.length, values).getRaw());
        section.put("block_states", blockStates);
        ListTag sections = new ListTag();
        sections.add(section);
        chunk.put("sections", sections);

        LegacyWorldUpgrader.upgradeChunk(chunk);

        ListTag upgradedPalette = blockStates.getList("palette", 10);
        assertEquals(2, upgradedPalette.size());
        assertEquals("minecraft:air", upgradedPalette.getCompound(0).getString("Name"));
        assertEquals("nekoration:paneled_cement", upgradedPalette.getCompound(1).getString("Name"));
        SimpleBitStorage storage = new SimpleBitStorage(4, values.length, blockStates.getLongArray("data"));
        assertEquals(0, storage.get(0));
        assertEquals(0, storage.get(1));
        assertEquals(1, storage.get(2));
        assertEquals(1, storage.get(3));
    }

    @Test
    void repacksChunkStorageWhenDeduplicationReducesBitWidth() {
        CompoundTag chunk = new CompoundTag();
        CompoundTag section = new CompoundTag();
        CompoundTag blockStates = new CompoundTag();
        ListTag palette = new ListTag();
        for (int index = 0; index < 15; index++) {
            palette.add(minecraftState("test_" + index));
        }
        palette.add(state("stone_frame", "level", "0"));
        palette.add(state("stone_pillar", "level", "0"));
        blockStates.put("palette", palette);
        int[] values = new int[4096];
        values[0] = 15;
        values[1] = 16;
        blockStates.putLongArray("data", new SimpleBitStorage(5, values.length, values).getRaw());
        section.put("block_states", blockStates);
        ListTag sections = new ListTag();
        sections.add(section);
        chunk.put("sections", sections);

        LegacyWorldUpgrader.upgradeChunk(chunk);

        assertEquals(16, blockStates.getList("palette", 10).size());
        SimpleBitStorage storage = new SimpleBitStorage(4, values.length, blockStates.getLongArray("data"));
        assertEquals(15, storage.get(0));
        assertEquals(15, storage.get(1));
    }

    @Test
    void insertsTallDoorUpperSegmentInSameSection() {
        CompoundTag upperDoor = state("door_tall_2", "level", "2", "half", "upper",
                "facing", "north", "hinge", "left", "open", "false", "powered", "false");
        CompoundTag section = section(0, minecraftState("air"), upperDoor);
        setSectionValue(section, 4, 1);
        CompoundTag chunk = chunk(section);

        LegacyWorldUpgrader.upgradeChunk(chunk);

        assertEquals("middle", sectionState(section, 4).getCompound("Properties").getString("segment"));
        CompoundTag top = sectionState(section, 4 + 256);
        assertEquals("nekoration:tall_chiseled_quartz_door", top.getString("Name"));
        assertEquals("upper", top.getCompound("Properties").getString("segment"));
        assertEquals("upper", top.getCompound("Properties").getString("half"));
        assertEquals("brown", top.getCompound("Properties").getString("color"));

        CompoundTag upgraded = chunk.copy();
        LegacyWorldUpgrader.upgradeChunk(chunk);
        assertEquals(upgraded, chunk);
    }

    @Test
    void insertsTallDoorUpperSegmentAcrossSectionBoundary() {
        CompoundTag upperDoor = state("door_tall_1", "level", "14", "half", "upper",
                "facing", "east", "hinge", "right", "open", "true", "powered", "false");
        CompoundTag lowerSection = section(-1, minecraftState("air"), upperDoor);
        CompoundTag upperSection = section(0, minecraftState("air"));
        setSectionValue(lowerSection, (15 << 8) | 7, 1);
        CompoundTag chunk = chunk(lowerSection, upperSection);

        LegacyWorldUpgrader.upgradeChunk(chunk);

        CompoundTag top = sectionState(upperSection, 7);
        assertEquals("nekoration:tall_quartz_door", top.getString("Name"));
        assertEquals("upper", top.getCompound("Properties").getString("segment"));
        assertEquals("white", top.getCompound("Properties").getString("color"));
        assertFalse(chunk.contains("Heightmaps"));
        assertFalse(chunk.contains("isLightOn"));
    }

    @Test
    void doesNotOverwriteBlockAboveTallDoor() {
        CompoundTag upperDoor = state("door_tall_3", "level", "0", "half", "upper");
        CompoundTag section = section(0, minecraftState("air"), upperDoor, minecraftState("stone"));
        setSectionValue(section, 0, 1);
        setSectionValue(section, 256, 2);
        CompoundTag chunk = chunk(section);

        LegacyWorldUpgrader.upgradeChunk(chunk);

        assertEquals("minecraft:stone", sectionState(section, 256).getString("Name"));
    }

    @Test
    void upgradesLegacyItemIdsAndColors() {
        CompoundTag cement = stack("stone_pot", "color", 14);
        assertTrue(LegacyWorldUpgrader.upgradeItemStack(cement));
        assertEquals("nekoration:cement_pot", cement.getString("id"));
        assertEquals(0, cement.getCompound("tag").getByte("color"));

        CompoundTag frame = stack("window_frame", "color", 1);
        LegacyWorldUpgrader.upgradeItemStack(frame);
        assertEquals("nekoration:cement_frame_side", frame.getString("id"));
        assertEquals(12, frame.getCompound("tag").getByte("color"));

        CompoundTag timber = stack("half_timber_pillar_p2", "color_0", 11, "color_1", 13);
        assertTrue(LegacyWorldUpgrader.upgradeItemStack(timber));
        assertEquals("nekoration:cherry_half_timber_p2", timber.getString("id"));
        assertEquals(5, timber.getCompound("tag").getByte("color"));
        assertFalse(timber.getCompound("tag").contains("color_0"));
        assertFalse(timber.getCompound("tag").contains("color_1"));

        CompoundTag easel = stack("easel_menu_white", "color", 9);
        LegacyWorldUpgrader.upgradeItemStack(easel);
        assertEquals("nekoration:crimson_easel_menu", easel.getString("id"));
        assertEquals(0, easel.getCompound("tag").getByte("color"));

        CompoundTag door = stack("door_tall_3");
        LegacyWorldUpgrader.upgradeItemStack(door);
        assertEquals("nekoration:tall_quartz_bricks_door", door.getString("id"));
    }

    @Test
    void upgradesWallpaperPatternComponentsOnItems() {
        CompoundTag wallpaper = legacyWallpaperStack(3,
                pattern("ss", 11), pattern("cbo", 15));

        assertTrue(LegacyWorldUpgrader.upgradeItemStack(wallpaper));

        CompoundTag components = wallpaper.getCompound("components");
        assertEquals("light_blue", components.getString("minecraft:base_color"));
        ListTag patterns = components.getList("minecraft:banner_patterns", 10);
        assertEquals(2, patterns.size());
        assertEquals("minecraft:small_stripes", patterns.getCompound(0).getString("pattern"));
        assertEquals("blue", patterns.getCompound(0).getString("color"));
        assertEquals("minecraft:curly_border", patterns.getCompound(1).getString("pattern"));
        assertEquals("black", patterns.getCompound(1).getString("color"));
        assertFalse(wallpaper.getCompound("tag").contains("BlockEntityTag"));
    }

    @Test
    void upgradesWallpaperPatternDataOnEntities() {
        CompoundTag entity = new CompoundTag();
        entity.putString("id", "nekoration:wallpaper");
        entity.putInt("Base", 14);
        ListTag patterns = new ListTag();
        patterns.add(pattern("moj", 1));
        entity.put("Patterns", patterns);

        assertTrue(LegacyWorldUpgrader.upgradeWallpaperEntity(entity));

        assertFalse(entity.contains("Base"));
        assertFalse(entity.contains("Patterns"));
        CompoundTag item = entity.getCompound("Item");
        assertEquals("nekoration:wallpaper", item.getString("id"));
        assertEquals(1, item.getInt("count"));
        CompoundTag components = item.getCompound("components");
        assertEquals("red", components.getString("minecraft:base_color"));
        CompoundTag pattern = components.getList("minecraft:banner_patterns", 10).getCompound(0);
        assertEquals("minecraft:mojang", pattern.getString("pattern"));
        assertEquals("orange", pattern.getString("color"));
    }

    @Test
    void finalizesWallpaperPatternDataLeftInCustomData() {
        CompoundTag wallpaper = new CompoundTag();
        wallpaper.putString("id", "nekoration:wallpaper");
        wallpaper.putInt("count", 1);
        CompoundTag blockEntityTag = new CompoundTag();
        blockEntityTag.putInt("Base", 7);
        ListTag oldPatterns = new ListTag();
        oldPatterns.add(pattern("bri", 8));
        blockEntityTag.put("Patterns", oldPatterns);
        CompoundTag customData = new CompoundTag();
        customData.put("BlockEntityTag", blockEntityTag);
        customData.putString("note", "preserved");
        CompoundTag components = new CompoundTag();
        components.put("minecraft:custom_data", customData);
        wallpaper.put("components", components);

        assertTrue(LegacyWorldUpgrader.finalizeItemStack(wallpaper));

        assertEquals("gray", components.getString("minecraft:base_color"));
        CompoundTag converted = components.getList("minecraft:banner_patterns", 10).getCompound(0);
        assertEquals("minecraft:bricks", converted.getString("pattern"));
        assertEquals("light_gray", converted.getString("color"));
        assertEquals("preserved", components.getCompound("minecraft:custom_data").getString("note"));
        assertFalse(components.getCompound("minecraft:custom_data").contains("BlockEntityTag"));
    }

    @Test
    void preservesWallpaperPatternsInPlayerInventoryThroughVanillaDataFixes() {
        SharedConstants.tryDetectVersion();
        CompoundTag player = new CompoundTag();
        NbtUtils.addDataVersion(player, 3120);
        ListTag items = new ListTag();
        items.add(legacyWallpaperStack(12, pattern("cre", 13)));
        player.put("Inventory", items);

        LegacyWorldUpgrader.upgradeItemStacks(player);
        CompoundTag fixed = DataFixTypes.PLAYER.update(
                DataFixers.getDataFixer(), player, 3120,
                SharedConstants.getCurrentVersion().getDataVersion().getVersion());

        CompoundTag fixedStack = fixed.getList("Inventory", 10).getCompound(0);
        CompoundTag components = fixedStack.getCompound("components");
        assertEquals(1, fixedStack.getInt("count"));
        assertFalse(fixedStack.contains("Count"));
        assertEquals("brown", components.getString("minecraft:base_color"));
        CompoundTag pattern = components.getList("minecraft:banner_patterns", 10).getCompound(0);
        assertEquals("minecraft:creeper", pattern.getString("pattern"));
        assertEquals("green", pattern.getString("color"));
    }

    @Test
    void preservesWallpaperEntityPatternsThroughVanillaDataFixes() {
        SharedConstants.tryDetectVersion();
        CompoundTag entityChunk = new CompoundTag();
        NbtUtils.addDataVersion(entityChunk, 3120);
        CompoundTag wallpaper = new CompoundTag();
        wallpaper.putString("id", "nekoration:wallpaper");
        wallpaper.putInt("Base", 4);
        ListTag oldPatterns = new ListTag();
        oldPatterns.add(pattern("flo", 6));
        wallpaper.put("Patterns", oldPatterns);
        ListTag entities = new ListTag();
        entities.add(wallpaper);
        entityChunk.put("Entities", entities);

        LegacyWorldUpgrader.upgradeItemStacks(entityChunk);
        CompoundTag fixed = DataFixTypes.ENTITY_CHUNK.update(
                DataFixers.getDataFixer(), entityChunk, 3120,
                SharedConstants.getCurrentVersion().getDataVersion().getVersion());
        LegacyWorldUpgrader.finalizeItemStacks(fixed);

        CompoundTag fixedEntity = fixed.getList("Entities", 10).getCompound(0);
        assertFalse(fixedEntity.contains("Base"));
        assertFalse(fixedEntity.contains("Patterns"));
        CompoundTag fixedStack = fixedEntity.getCompound("Item");
        assertEquals("nekoration:wallpaper", fixedStack.getString("id"));
        assertEquals(1, fixedStack.getInt("count"));
        CompoundTag components = fixedStack.getCompound("components");
        assertEquals("yellow", components.getString("minecraft:base_color"));
        CompoundTag pattern = components.getList("minecraft:banner_patterns", 10).getCompound(0);
        assertEquals("minecraft:flower", pattern.getString("pattern"));
        assertEquals("pink", pattern.getString("color"));
    }

    @Test
    void mapsEveryLegacyWoodOrdinalForBlocksAndItems() {
        for (int ordinal = 0; ordinal < V1_WOODS.length; ordinal++) {
            CompoundTag block = state("bench", "level", Integer.toString(ordinal));
            LegacyWorldUpgrader.upgradeBlockState(block);
            assertEquals("nekoration:" + V1_WOODS[ordinal] + "_bench", block.getString("Name"));

            CompoundTag item = stack("bench", "color", ordinal);
            LegacyWorldUpgrader.upgradeItemStack(item);
            assertEquals("nekoration:" + V1_WOODS[ordinal] + "_bench", item.getString("id"));
        }
    }

    @Test
    void upgradesNestedInventoriesWithoutTouchingCurrentStacks() {
        CompoundTag root = new CompoundTag();
        CompoundTag blockEntity = new CompoundTag();
        ListTag items = new ListTag();
        items.add(stack("bench"));
        blockEntity.put("Items", items);
        ListTag blockEntities = new ListTag();
        blockEntities.add(blockEntity);
        root.put("block_entities", blockEntities);

        CompoundTag currentCollision = new CompoundTag();
        currentCollision.putString("id", "nekoration:stone_pot");
        currentCollision.putInt("count", 1);
        root.put("current", currentCollision);

        LegacyWorldUpgrader.upgradeItemStacks(root);

        assertEquals("nekoration:dark_oak_bench", items.getCompound(0).getString("id"));
        assertEquals("nekoration:stone_pot", currentCollision.getString("id"));
    }

    @Test
    void movesResidualContainerColorIntoCustomData() {
        CompoundTag root = new CompoundTag();
        CompoundTag blockEntity = new CompoundTag();
        ListTag items = new ListTag();
        CompoundTag awning = modernStackWithLegacyTag("awning_stripe", 15);
        CompoundTag plant = modernStackWithLegacyTag("window_plant", 9);
        CompoundTag components = new CompoundTag();
        CompoundTag existingCustomData = new CompoundTag();
        existingCustomData.putString("note", "preserved");
        components.put("minecraft:custom_data", existingCustomData);
        awning.put("components", components);
        items.add(awning);
        items.add(plant);
        blockEntity.put("Items", items);
        ListTag blockEntities = new ListTag();
        blockEntities.add(blockEntity);
        root.put("block_entities", blockEntities);

        LegacyWorldUpgrader.finalizeItemStacks(root);

        assertEquals(15, customData(awning).getByte("color"));
        assertEquals("preserved", customData(awning).getString("note"));
        assertEquals(9, customData(plant).getByte("color"));
        assertFalse(awning.contains("tag"));
        assertFalse(plant.contains("tag"));

        CompoundTag finalized = root.copy();
        LegacyWorldUpgrader.finalizeItemStacks(root);
        assertEquals(finalized, root);
    }

    @Test
    void preservesContainerColorThroughVanillaChunkDataFixes() {
        SharedConstants.tryDetectVersion();
        CompoundTag chunk = new CompoundTag();
        NbtUtils.addDataVersion(chunk, 3120);
        CompoundTag blockEntity = new CompoundTag();
        blockEntity.putString("id", "nekoration:item_display");
        ListTag items = new ListTag();
        items.add(stack("awning_stripe", "color", 11));
        items.add(stack("bench", "color", 0));
        blockEntity.put("Items", items);
        ListTag blockEntities = new ListTag();
        blockEntities.add(blockEntity);
        chunk.put("block_entities", blockEntities);

        LegacyWorldUpgrader.upgradeChunk(chunk);
        CompoundTag fixed = DataFixTypes.CHUNK.update(
                DataFixers.getDataFixer(), chunk, 3120,
                SharedConstants.getCurrentVersion().getDataVersion().getVersion());
        LegacyWorldUpgrader.finalizeItemStacks(fixed);

        CompoundTag fixedStack = fixed.getList("block_entities", 10)
                .getCompound(0).getList("Items", 10).getCompound(0);
        assertEquals(15, customData(fixedStack).getByte("color"), fixedStack::toString);
        assertEquals(1, fixedStack.getInt("count"));
        assertFalse(fixedStack.contains("Count"));
        CompoundTag fixedBench = fixed.getList("block_entities", 10)
                .getCompound(0).getList("Items", 10).getCompound(1);
        assertEquals("nekoration:dark_oak_bench", fixedBench.getString("id"));
        assertEquals(1, fixedBench.getInt("count"));
        assertFalse(fixedBench.contains("Count"));
    }

    private static CompoundTag state(String path, String... properties) {
        CompoundTag state = new CompoundTag();
        state.putString("Name", "nekoration:" + path);
        if (properties.length > 0) {
            CompoundTag propertyTag = new CompoundTag();
            for (int i = 0; i < properties.length; i += 2) {
                propertyTag.putString(properties[i], properties[i + 1]);
            }
            state.put("Properties", propertyTag);
        }
        return state;
    }

    private static CompoundTag minecraftState(String path) {
        CompoundTag state = new CompoundTag();
        state.putString("Name", "minecraft:" + path);
        return state;
    }

    private static CompoundTag section(int sectionY, CompoundTag... states) {
        CompoundTag section = new CompoundTag();
        section.putByte("Y", (byte) sectionY);
        CompoundTag blockStates = new CompoundTag();
        ListTag palette = new ListTag();
        for (CompoundTag state : states) {
            palette.add(state);
        }
        blockStates.put("palette", palette);
        if (states.length > 1) {
            blockStates.putLongArray("data", new SimpleBitStorage(4, 4096).getRaw());
        }
        section.put("block_states", blockStates);
        return section;
    }

    private static CompoundTag chunk(CompoundTag... sections) {
        CompoundTag chunk = new CompoundTag();
        ListTag sectionList = new ListTag();
        for (CompoundTag section : sections) {
            sectionList.add(section);
        }
        chunk.put("sections", sectionList);
        chunk.put("Heightmaps", new CompoundTag());
        chunk.putBoolean("isLightOn", true);
        return chunk;
    }

    private static void setSectionValue(CompoundTag section, int index, int paletteIndex) {
        CompoundTag blockStates = section.getCompound("block_states");
        int paletteSize = blockStates.getList("palette", 10).size();
        SimpleBitStorage storage = new SimpleBitStorage(
                Math.max(4, 32 - Integer.numberOfLeadingZeros(paletteSize - 1)), 4096,
                blockStates.getLongArray("data"));
        storage.set(index, paletteIndex);
        blockStates.putLongArray("data", storage.getRaw());
    }

    private static CompoundTag sectionState(CompoundTag section, int index) {
        CompoundTag blockStates = section.getCompound("block_states");
        ListTag palette = blockStates.getList("palette", 10);
        if (palette.size() == 1) {
            return palette.getCompound(0);
        }
        SimpleBitStorage storage = new SimpleBitStorage(
                Math.max(4, 32 - Integer.numberOfLeadingZeros(palette.size() - 1)), 4096,
                blockStates.getLongArray("data"));
        return palette.getCompound(storage.get(index));
    }

    private static CompoundTag stack(String path, Object... tags) {
        CompoundTag stack = new CompoundTag();
        stack.putString("id", "nekoration:" + path);
        stack.putByte("Count", (byte) 1);
        if (tags.length > 0) {
            CompoundTag itemTag = new CompoundTag();
            for (int i = 0; i < tags.length; i += 2) {
                itemTag.putInt((String) tags[i], (Integer) tags[i + 1]);
            }
            stack.put("tag", itemTag);
        }
        return stack;
    }

    private static CompoundTag modernStackWithLegacyTag(String path, int color) {
        CompoundTag stack = new CompoundTag();
        stack.putString("id", "nekoration:" + path);
        stack.putInt("count", 1);
        CompoundTag legacyTag = new CompoundTag();
        legacyTag.putByte("color", (byte) color);
        stack.put("tag", legacyTag);
        return stack;
    }

    private static CompoundTag legacyWallpaperStack(int baseColor, CompoundTag... patterns) {
        CompoundTag stack = stack("wallpaper");
        CompoundTag blockEntityTag = new CompoundTag();
        blockEntityTag.putInt("Base", baseColor);
        ListTag patternList = new ListTag();
        for (CompoundTag pattern : patterns) {
            patternList.add(pattern);
        }
        blockEntityTag.put("Patterns", patternList);
        CompoundTag itemTag = new CompoundTag();
        itemTag.put("BlockEntityTag", blockEntityTag);
        stack.put("tag", itemTag);
        return stack;
    }

    private static CompoundTag pattern(String id, int color) {
        CompoundTag pattern = new CompoundTag();
        pattern.putString("Pattern", id);
        pattern.putInt("Color", color);
        return pattern;
    }

    private static CompoundTag customData(CompoundTag stack) {
        return stack.getCompound("components").getCompound("minecraft:custom_data");
    }
}
