package io.devbobcorn.nekoration.blocks;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Vanilla plank-derived properties for wooden block variants (one block id per wood type).
 */
public enum NekoWood {
    // Vanilla Wood Types
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
    WARPED,
    // Biome O' Plenty Wood Types
    FIR,
    PINE,
    MAPLE,
    REDWOOD,
    MAHOGANY,
    JACARANDA,
    PALM,
    WILLOW,
    DEAD,
    MAGIC,
    UMBRAN,
    HELLBARK,
    EMPYREAL;

    public String id() {
        return name().toLowerCase();
    }

    /** Nether woods (crimson/warped) are not flammable, matching their vanilla planks. */
    public boolean isFlammable() {
        return this != CRIMSON && this != WARPED;
    }

    public BlockBehaviour.Properties plankProperties() {
        return BlockBehaviour.Properties.ofFullCopy(planks());
    }

    /** Plank block used for properties and creative icons, with oak as the optional-mod fallback. */
    public Block planks() {
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
            default -> BuiltInRegistries.BLOCK.getOptional(
                    ResourceLocation.fromNamespaceAndPath("biomesoplenty", id() + "_planks"))
                    .orElse(Blocks.OAK_PLANKS);
        };
    }

    public boolean isBiomesOPlenty() {
        return ordinal() >= FIR.ordinal();
    }

    /** Texture used by furniture models; BOP textures are bundled so the variants work without BOP. */
    public String plankTexture() {
        return (isBiomesOPlenty() ? "nekoration:" : "") + "block/" + id() + "_planks";
    }

    /** {@code wood.nekoration.<id>} (e.g. {@code wood.nekoration.oak}). */
    public String descriptionId() {
        return "wood.nekoration." + id();
    }
}
