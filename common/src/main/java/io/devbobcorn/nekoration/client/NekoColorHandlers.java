package io.devbobcorn.nekoration.client;

import java.util.List;
import java.util.function.BiConsumer;

import org.jetbrains.annotations.Nullable;

import io.devbobcorn.nekoration.NekoColors.NekoColorPalette;
import io.devbobcorn.nekoration.blocks.DyeableBlock;
import io.devbobcorn.nekoration.blocks.DyeableHorizontalConnectedBlock;
import io.devbobcorn.nekoration.blocks.DyeableVerticalConnectedBlock;
import io.devbobcorn.nekoration.blocks.cement.DyeableFrameSideBlock;
import io.devbobcorn.nekoration.blocks.cement.DyeablePotBlock;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import io.devbobcorn.nekoration.registry.CementBlockRegistration;
import io.devbobcorn.nekoration.xplat.RegistrySupplier;
import io.devbobcorn.nekoration.registry.OrnamentRegistration;
import io.devbobcorn.nekoration.registry.WoodenBlockRegistration;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;

/**
 * Tints {@code tintindex: 0} faces on dyeable block models (plaster / grayscale layer).
 * Platform modules wire these providers into their own registration points.
 */
public final class NekoColorHandlers {
    private NekoColorHandlers() {
    }

    private static BlockColor dyeableBlockColor(NekoColorPalette palette) {
        return (BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) -> {
            if (tintIndex != 0 || !(isDyeableBlock(state))) {
                return 0xFFFFFFFF;
            }
            return 0xFF000000 | state.getValue(DyeableBlock.COLOR).getColor(palette);
        };
    }

    private static boolean isDyeableBlock(BlockState state) {
        return state.getBlock() instanceof DyeableBlock
                || state.getBlock() instanceof DyeableVerticalConnectedBlock
                || state.getBlock() instanceof DyeableHorizontalConnectedBlock
                || state.getBlock() instanceof DyeableFrameSideBlock
                || state.getBlock() instanceof DyeablePotBlock;
    }

    private static ItemColor dyeableBlockItemColor(NekoColorPalette palette) {
        return (ItemStack stack, int tintIndex) -> {
            if (tintIndex != 0) {
                return 0xFFFFFFFF;
            }
            return 0xFF000000 | DyeableBlockItem.getColor(stack).getColor(palette);
        };
    }

    private static BlockColor windowPlantBlockColor() {
        return (BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) -> {
            if (tintIndex == 0) {
                if (level == null || pos == null) {
                    return 0xFF000000 | FoliageColor.getDefaultColor();
                }
                return 0xFF000000 | BiomeColors.getAverageFoliageColor(level, pos);
            }
            if (tintIndex == 1 && state.getBlock() instanceof DyeableBlock) {
                return 0xFF000000 | state.getValue(DyeableBlock.COLOR).getColor();
            }
            return 0xFFFFFFFF;
        };
    }

    private static ItemColor windowPlantItemColor() {
        return (ItemStack stack, int tintIndex) -> {
            if (tintIndex == 0) {
                return 0xFF000000 | FoliageColor.getDefaultColor();
            }
            if (tintIndex == 1) {
                return 0xFF000000 | DyeableBlockItem.getColor(stack).getColor();
            }
            return 0xFFFFFFFF;
        };
    }

    /** Registers every block color provider through the given registrar. */
    public static void registerBlockColors(BiConsumer<BlockColor, List<Block>> registrar) {
        BlockColor halfTimber = dyeableBlockColor(NekoColorPalette.HALF_TIMBER);
        registrar.accept(halfTimber, WoodenBlockRegistration.halfTimberBlockItemsView().stream()
                .map(holder -> holder.get().getBlock()).toList());
        registrar.accept(halfTimber, WoodenBlockRegistration.easelMenuBlockItemsView().stream()
                .map(holder -> holder.get().getBlock()).toList());
        BlockColor cement = dyeableBlockColor(NekoColorPalette.CEMENT);
        registrar.accept(cement, CementBlockRegistration.blockItemsView().stream()
                .map(holder -> (Block) ((DyeableBlockItem) holder.get()).getBlock()).toList());
        registrar.accept(windowPlantBlockColor(), List.of(OrnamentRegistration.windowPlantBlock().get()));
        BlockColor awning = dyeableBlockColor(NekoColorPalette.CEMENT);
        registrar.accept(awning, OrnamentRegistration.awningBlockItemsView().stream()
                .map(holder -> holder.get().getBlock()).toList());
        BlockColor candleHolder = dyeableBlockColor(NekoColorPalette.HALF_TIMBER);
        registrar.accept(candleHolder, OrnamentRegistration.candleHolderBlockItemsView().stream()
                .map(holder -> holder.get().getBlock()).toList());
    }

    /** Registers every item color provider through the given registrar. */
    public static void registerItemColors(BiConsumer<ItemColor, List<Item>> registrar) {
        ItemColor halfTimber = dyeableBlockItemColor(NekoColorPalette.HALF_TIMBER);
        registrar.accept(halfTimber, WoodenBlockRegistration.halfTimberBlockItemsView().stream()
                .map(holder -> (Item) holder.get()).toList());
        registrar.accept(halfTimber, WoodenBlockRegistration.easelMenuBlockItemsView().stream()
                .map(holder -> (Item) holder.get()).toList());
        ItemColor cement = dyeableBlockItemColor(NekoColorPalette.CEMENT);
        registrar.accept(cement, CementBlockRegistration.blockItemsView().stream()
                .map(holder -> (Item) holder.get()).toList());
        registrar.accept(windowPlantItemColor(), List.of(OrnamentRegistration.windowPlantBlockItem().get()));
        registrar.accept(dyeableBlockItemColor(NekoColorPalette.CEMENT), OrnamentRegistration.awningBlockItemsView().stream()
                .map(holder -> (Item) holder.get()).toList());
        ItemColor candleHolder = dyeableBlockItemColor(NekoColorPalette.HALF_TIMBER);
        registrar.accept(candleHolder, OrnamentRegistration.candleHolderBlockItemsView().stream()
                .map(holder -> (Item) holder.get()).toList());
    }
}
