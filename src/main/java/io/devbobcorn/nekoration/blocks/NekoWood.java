package io.devbobcorn.nekoration.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Vanilla plank-derived properties for half-timber block variants (one block id per wood type).
 */
public enum NekoWood {
    OAK,
    SPRUCE,
    BIRCH,
    JUNGLE,
    ACACIA,
    DARK_OAK,
    MANGROVE,
    CHERRY,
    BAMBOO,
    CRIMSON,
    WARPED;

    /** Registry id wood segment: {@code <id>_half_timber_p0} */
    public String id() {
        return name().toLowerCase();
    }

    public BlockBehaviour.Properties plankProperties() {
        return BlockBehaviour.Properties.ofFullCopy(vanillaPlanks());
    }

    /** Vanilla plank block for this wood (icons, sounds, creative filter). */
    public Block vanillaPlanks() {
        return switch (this) {
            case OAK -> Blocks.OAK_PLANKS;
            case SPRUCE -> Blocks.SPRUCE_PLANKS;
            case BIRCH -> Blocks.BIRCH_PLANKS;
            case JUNGLE -> Blocks.JUNGLE_PLANKS;
            case ACACIA -> Blocks.ACACIA_PLANKS;
            case DARK_OAK -> Blocks.DARK_OAK_PLANKS;
            case MANGROVE -> Blocks.MANGROVE_PLANKS;
            case CHERRY -> Blocks.CHERRY_PLANKS;
            case BAMBOO -> Blocks.BAMBOO_PLANKS;
            case CRIMSON -> Blocks.CRIMSON_PLANKS;
            case WARPED -> Blocks.WARPED_PLANKS;
        };
    }

    /** {@code block.minecraft.<id>_planks} */
    public String vanillaPlanksDescriptionId() {
        return "block.minecraft." + id() + "_planks";
    }
}
