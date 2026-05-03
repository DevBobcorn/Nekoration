package io.devbobcorn.nekoration.items;

import java.util.List;
import java.util.Locale;

import io.devbobcorn.nekoration.NekoConfig;
import io.devbobcorn.nekoration.blocks.HorizontalConnectedBlock;
import io.devbobcorn.nekoration.blocks.VerticalConnectedBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

public class NekoBlockItem extends BlockItem {
    private static final String HORIZONTAL_CONNECT_TOOLTIP_KEY = "tooltip.nekoration.horizontal_connect_block";
    private static final String VERTICAL_CONNECT_TOOLTIP_KEY = "tooltip.nekoration.vertical_connect_block";

    public NekoBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        Block block = getBlock();
        if (block instanceof HorizontalConnectedBlock) {
            tooltipComponents.add(Component
                    .translatable(HORIZONTAL_CONNECT_TOOLTIP_KEY, currentHorizontalConnectionComponent())
                    .withStyle(ChatFormatting.GRAY));
        } else if (block instanceof VerticalConnectedBlock) {
            tooltipComponents.add(Component
                    .translatable(VERTICAL_CONNECT_TOOLTIP_KEY, currentVerticalConnectionComponent())
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    private static Component currentHorizontalConnectionComponent() {
        String key = "tooltip.nekoration.connection_" + NekoConfig.HOR_CONNECTION_DIR.get().name().toLowerCase(Locale.ROOT);
        return Component.translatable(key).withStyle(ChatFormatting.AQUA);
    }

    private static Component currentVerticalConnectionComponent() {
        String key = "tooltip.nekoration.connection_" + NekoConfig.VER_CONNECTION_DIR.get().name().toLowerCase(Locale.ROOT);
        return Component.translatable(key).withStyle(ChatFormatting.AQUA);
    }
}
