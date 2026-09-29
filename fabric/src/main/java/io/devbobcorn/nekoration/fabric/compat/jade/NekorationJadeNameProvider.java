package io.devbobcorn.nekoration.fabric.compat.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;

/** Keeps the picked stack's translated color in Jade's object name. */
public enum NekorationJadeNameProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("nekoration", "jade_object_name");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        ItemStack picked = accessor.getPickedResult();
        if (!picked.isEmpty()) {
            tooltip.replace(JadeIds.CORE_OBJECT_NAME, picked.getHoverName().copy().withStyle(ChatFormatting.WHITE));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
