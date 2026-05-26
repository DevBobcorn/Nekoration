package io.devbobcorn.nekoration.blocks;

import java.util.List;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Vanilla stone-derived properties for stone blocks (one block id per stone type).
 */
public enum NekoStone {
    // Vanilla Stone Types
    GRANITE(true, false, true),
    DIORITE(true, false, true),
    ANDESITE(true, false, true),
    CALCITE(true, true, true),
    DRIPSTONE(true, true, true),
    TUFF(true, false, false),
    SANDSTONE(false, true, true),
    RED_SANDSTONE(false, true, true),
    STONE(false, true, false);

    private final boolean needsSmoothVariant;
    private final boolean needsPolishedVariant;
    private final boolean needsBricksVariant;

    NekoStone(boolean needsSmoothVariant, boolean needsPolishedVariant, boolean needsBricksVariant) {
        this.needsSmoothVariant = needsSmoothVariant;
        this.needsPolishedVariant = needsPolishedVariant;
        this.needsBricksVariant = needsBricksVariant;
    }

    public String id() {
        return name().toLowerCase();
    }

    public boolean needsSmoothVariant() {
        return needsSmoothVariant;
    }

    public boolean needsPolishedVariant() {
        return needsPolishedVariant;
    }

    public boolean needsBricksVariant() {
        return needsBricksVariant;
    }

    public BlockBehaviour.Properties stoneProperties() {
        return BlockBehaviour.Properties.ofFullCopy(vanillaStoneBlock());
    }

    /** Vanilla stone block for this type (icons, sounds, creative filter). */
    public Block vanillaStoneBlock() {
        return switch (this) {
            case STONE -> Blocks.STONE;
            case GRANITE -> Blocks.GRANITE;
            case DIORITE -> Blocks.DIORITE;
            case ANDESITE -> Blocks.ANDESITE;
            case CALCITE -> Blocks.CALCITE;
            case DRIPSTONE -> Blocks.DRIPSTONE_BLOCK;
            case TUFF -> Blocks.TUFF;
            case SANDSTONE -> Blocks.SANDSTONE;
            case RED_SANDSTONE -> Blocks.RED_SANDSTONE;
        };
    }

    /** Vanilla smooth block set for this stone type (block, stairs, slab). */
    public List<Block> vanillaSmoothStoneBlockSet() {
        return switch (this) {
            case STONE -> List.of(Blocks.SMOOTH_STONE);
            case SANDSTONE -> List.of(Blocks.SMOOTH_SANDSTONE, Blocks.SMOOTH_SANDSTONE_STAIRS, Blocks.SMOOTH_SANDSTONE_SLAB);
            case RED_SANDSTONE -> List.of(Blocks.SMOOTH_RED_SANDSTONE, Blocks.SMOOTH_RED_SANDSTONE_STAIRS, Blocks.SMOOTH_RED_SANDSTONE_SLAB);
            default -> List.of();
        };
    }

    /** Vanilla polished block set for this stone type (block, stairs, slab). */
    public List<Block> vanillaPolishedStoneBlockSet() {
        return switch (this) {
            case GRANITE -> List.of(Blocks.POLISHED_GRANITE, Blocks.POLISHED_GRANITE_STAIRS, Blocks.POLISHED_GRANITE_SLAB);
            case DIORITE -> List.of(Blocks.POLISHED_DIORITE, Blocks.POLISHED_DIORITE_STAIRS, Blocks.POLISHED_DIORITE_SLAB);
            case ANDESITE -> List.of(Blocks.POLISHED_ANDESITE, Blocks.POLISHED_ANDESITE_STAIRS, Blocks.POLISHED_ANDESITE_SLAB);
            case TUFF -> List.of(Blocks.POLISHED_TUFF, Blocks.POLISHED_TUFF_STAIRS, Blocks.POLISHED_TUFF_SLAB);

            default -> List.of(Blocks.POLISHED_TUFF, Blocks.POLISHED_TUFF_STAIRS, Blocks.POLISHED_TUFF_SLAB);
        };
    }

    /** Vanilla bricks block set for this stone type (block, stairs, slab). */
    public List<Block> vanillaBricksStoneBlockSet() {
        return switch (this) {
            case TUFF -> List.of(Blocks.TUFF_BRICKS, Blocks.TUFF_BRICK_STAIRS, Blocks.TUFF_BRICK_SLAB);
            case STONE -> List.of(Blocks.STONE_BRICKS, Blocks.STONE_BRICK_STAIRS, Blocks.STONE_BRICK_SLAB);
            default -> List.of();
        };
    }

    /** {@code block.minecraft.<id>} (e.g. {@code block.minecraft.granite}). */
    public String vanillaStoneDescriptionId() {
        if (this == DRIPSTONE) {
            return "block.minecraft.dripstone_block";
        }
        return "block.minecraft." + id();
    }
}
