package io.devbobcorn.nekoration.items;

import java.util.UUID;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

/**
 * The painting content carried by a painted painting item,
 * used to render a preview in the item's tooltip.
 * Width and height are in blocks(1 block = 16 pixels).
 */
public record PaintingTooltipComponent(short width, short height, UUID dataId, int[] pixels) implements TooltipComponent {
}
