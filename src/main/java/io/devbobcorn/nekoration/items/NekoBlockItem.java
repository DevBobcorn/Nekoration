package io.devbobcorn.nekoration.items;

import java.util.List;

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
    private static final String CONNECT_TOOLTIP_KEY = "tooltip.nekoration.connect_block";
    private static final String CONNECT_TOOLTIP_TIP_KEY = "tooltip.nekoration.connect_block_tip";
    private static final String DIRECTION_HORIZONTAL_TOOLTIP_KEY = "tooltip.nekoration.direction_horizontal";
    private static final String DIRECTION_VERTICAL_TOOLTIP_KEY = "tooltip.nekoration.direction_vertical";
    private static final String SNEAKING_TOOLTIP_KEY = "tooltip.nekoration.sneaking";

    public NekoBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        Block block = getBlock();
        if (block instanceof HorizontalConnectedBlock) {
            Component direction = Component.translatable(DIRECTION_HORIZONTAL_TOOLTIP_KEY).withStyle(ChatFormatting.WHITE);
            tooltipComponents.add(Component
                    .translatable(CONNECT_TOOLTIP_KEY, direction)
                    .withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(Component
                    .translatable(CONNECT_TOOLTIP_TIP_KEY,
                            Component.translatable(SNEAKING_TOOLTIP_KEY).withStyle(ChatFormatting.AQUA))
                    .withStyle(ChatFormatting.GRAY));
        } else if (block instanceof VerticalConnectedBlock) {
            Component direction = Component.translatable(DIRECTION_VERTICAL_TOOLTIP_KEY).withStyle(ChatFormatting.WHITE);
            tooltipComponents.add(Component
                    .translatable(CONNECT_TOOLTIP_KEY, direction)
                    .withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(Component
                    .translatable(CONNECT_TOOLTIP_TIP_KEY,
                            Component.translatable(SNEAKING_TOOLTIP_KEY).withStyle(ChatFormatting.AQUA))
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
