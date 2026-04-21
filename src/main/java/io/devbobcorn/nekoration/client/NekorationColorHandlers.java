package io.devbobcorn.nekoration.client;

import org.jetbrains.annotations.Nullable;

import io.devbobcorn.nekoration.NekoColors.NekoColorPalette;
import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.DyeableBlock;
import io.devbobcorn.nekoration.blocks.DyeableVerticalConnectBlock;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import io.devbobcorn.nekoration.registry.OrnamentsRegistration;
import io.devbobcorn.nekoration.registry.WoodenBlocksRegistration;
import io.devbobcorn.nekoration.registry.StoneBlocksRegistration;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

/**
 * Tints {@code tintindex: 0} faces on dyeable block models (plaster / grayscale layer).
 */
@EventBusSubscriber(modid = Nekoration.MODID, value = Dist.CLIENT)
public final class NekorationColorHandlers {
    private NekorationColorHandlers() {
    }

    private static BlockColor dyeableBlockColor(NekoColorPalette palette) {
        return (BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) -> {
            if (tintIndex != 0 || !(state.getBlock() instanceof DyeableBlock || state.getBlock() instanceof DyeableVerticalConnectBlock)) {
                return 0xFFFFFFFF;
            }
            return 0xFF000000 | state.getValue(DyeableBlock.COLOR).getColor(palette);
        };
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

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        BlockColor halfTimber = dyeableBlockColor(NekoColorPalette.HALF_TIMBER);
        BlockColor stone = dyeableBlockColor(NekoColorPalette.STONE_COLUMNS);
        WoodenBlocksRegistration.halfTimberBlockItemsView().forEach(holder -> event.register(halfTimber, holder.get().getBlock()));
        StoneBlocksRegistration.blockItemsView().forEach(holder -> event.register(stone, holder.get().getBlock()));
        event.register(windowPlantBlockColor(), OrnamentsRegistration.windowPlantBlock().get());
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        ItemColor halfTimber = dyeableBlockItemColor(NekoColorPalette.HALF_TIMBER);
        ItemColor stone = dyeableBlockItemColor(NekoColorPalette.STONE_COLUMNS);
        WoodenBlocksRegistration.halfTimberBlockItemsView().forEach(holder -> event.register(halfTimber, holder.get()));
        StoneBlocksRegistration.blockItemsView().forEach(holder -> event.register(stone, holder.get()));
        event.register(windowPlantItemColor(), OrnamentsRegistration.windowPlantBlockItem().get());
    }
}
