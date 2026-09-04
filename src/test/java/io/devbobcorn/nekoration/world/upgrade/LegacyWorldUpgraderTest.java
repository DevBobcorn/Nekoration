package io.devbobcorn.nekoration.world.upgrade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

class LegacyWorldUpgraderTest {
    @Test
    void upgradesRepresentativeBlockStates() {
        CompoundTag legacyStone = state("stone_base", "level", "14", "vertical_connection", "d0");
        CompoundTag currentStone = state("stone_base");
        assertTrue(LegacyWorldUpgrader.upgradeBlockState(legacyStone));
        assertEquals("nekoration:cement", legacyStone.getString("Name"));
        assertEquals("white", legacyStone.getCompound("Properties").getString("color"));
        assertFalse(LegacyWorldUpgrader.upgradeBlockState(currentStone));

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
}
