package io.devbobcorn.nekoration.client;

import io.devbobcorn.nekoration.NekoColors.NekoColorPalette;
import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.DyeableVerticalConnectBlock;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import io.devbobcorn.nekoration.registry.WoodenBlocksRegistration;
import io.devbobcorn.nekoration.registry.StoneBlocksRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;

@EventBusSubscriber(modid = Nekoration.MODID, value = Dist.CLIENT)
public final class NekorationItemDecorators {
    private static final ResourceLocation VERTICAL_BADGE = ResourceLocation.fromNamespaceAndPath(
            Nekoration.MODID, "textures/item/vertical_badge.png");

    private NekorationItemDecorators() {
    }

    @SubscribeEvent
    public static void registerItemDecorations(RegisterItemDecorationsEvent event) {
        IItemDecorator halfTimberDecorator = verticalConnectDecorator(NekoColorPalette.HALF_TIMBER);
        IItemDecorator stoneDecorator = verticalConnectDecorator(NekoColorPalette.STONE_COLUMNS);
        WoodenBlocksRegistration.halfTimberBlockItemsView().forEach(holder -> registerIfVertical(event, holder.get(), halfTimberDecorator));
        StoneBlocksRegistration.blockItemsView().forEach(holder -> registerIfVertical(event, holder.get(), stoneDecorator));
    }

    private static IItemDecorator verticalConnectDecorator(NekoColorPalette palette) {
        return (graphics, font, stack, x, y) -> {
            int rgb = DyeableBlockItem.getColor(stack).getColor(palette);
            float red = ((rgb >> 16) & 0xFF) / 255.0F;
            float green = ((rgb >> 8) & 0xFF) / 255.0F;
            float blue = (rgb & 0xFF) / 255.0F;
            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, 0.0F, 250.0F);
            graphics.setColor(red, green, blue, 1.0F);
            graphics.blit(VERTICAL_BADGE, x, y, 0, 0, 16, 16, 16, 16);
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            graphics.pose().popPose();
            return false;
        };
    }

    private static void registerIfVertical(RegisterItemDecorationsEvent event, Item item, IItemDecorator decorator) {
        if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof DyeableVerticalConnectBlock) {
            event.register(item, decorator);
        }
    }
}
