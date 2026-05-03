package io.devbobcorn.nekoration.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Vanilla stone-derived properties for stone blocks (one block id per stone type).
 */
public enum NekoStone {
    // Vanilla Stone Types
    // STONE(false),
    GRANITE(true),
    DIORITE(true),
    ANDESITE(true);

    private final boolean needsSmoothVariant;

    NekoStone(boolean needsSmoothVariant) {
        this.needsSmoothVariant = needsSmoothVariant;
    }

    public String id() {
        return name().toLowerCase();
    }

    public boolean needsSmoothVariant() {
        return needsSmoothVariant;
    }

    public BlockBehaviour.Properties stoneProperties() {
        return BlockBehaviour.Properties.ofFullCopy(vanillaStoneBlock());
    }

    /** Vanilla stone block for this type (icons, sounds, creative filter). */
    public Block vanillaStoneBlock() {
        return switch (this) {
            case GRANITE -> Blocks.GRANITE;
            case DIORITE -> Blocks.DIORITE;
            case ANDESITE -> Blocks.ANDESITE;
        };
    }

    /** Vanilla smooth/polished block for this stone type. */
    public Block vanillaSmoothStoneBlock() {
        return switch (this) {
            default -> Blocks.SMOOTH_STONE;
        };
    }

    /** {@code block.minecraft.<id>} (e.g. {@code block.minecraft.granite}). */
    public String vanillaStoneDescriptionId() {
        return "block.minecraft." + id();
    }
}
