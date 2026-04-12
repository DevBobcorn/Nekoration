package io.devbobcorn.nekoration.client;

import org.jetbrains.annotations.Nullable;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.DyeableBlock;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import io.devbobcorn.nekoration.registry.HalfTimberRegistration;
import io.devbobcorn.nekoration.registry.StoneColumnsRegistration;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

/**
 * Tints {@code tintindex: 0} faces on dyeable half-timber models (plaster / grayscale layer).
 */
@EventBusSubscriber(modid = Nekoration.MODID, value = Dist.CLIENT)
public final class NekorationColorHandlers {
    private NekorationColorHandlers() {
    }

    private static final BlockColor DYEABLE_BLOCK_COLOR = (BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos,
            int tintIndex) -> {
        if (tintIndex != 0 || !(state.getBlock() instanceof DyeableBlock)) {
            return 0xFFFFFFFF;
        }
        return 0xFF000000 | state.getValue(DyeableBlock.COLOR).getColor();
    };

    private static final ItemColor DYEABLE_BLOCK_ITEM_COLOR = (ItemStack stack, int tintIndex) -> {
        if (tintIndex != 0) {
            return 0xFFFFFFFF;
        }
        return 0xFF000000 | DyeableBlockItem.getColor(stack).getColor();
    };

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        HalfTimberRegistration.blockItemsView().forEach(holder -> event.register(DYEABLE_BLOCK_COLOR, holder.get().getBlock()));
        StoneColumnsRegistration.blockItemsView().forEach(holder -> event.register(DYEABLE_BLOCK_COLOR, holder.get().getBlock()));
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        HalfTimberRegistration.blockItemsView().forEach(holder -> event.register(DYEABLE_BLOCK_ITEM_COLOR, holder.get()));
        StoneColumnsRegistration.blockItemsView().forEach(holder -> event.register(DYEABLE_BLOCK_ITEM_COLOR, holder.get()));
    }
}
