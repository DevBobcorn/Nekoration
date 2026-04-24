package io.devbobcorn.nekoration.compat.jade;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;

/**
 * Replaces Jade object-name with picked stack hover name so interpolation args
 * from block state (e.g. dye color) are preserved.
 */
public enum NekorationJadeNameProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("nekoration", "jade_object_name");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        ItemStack picked = accessor.getPickedResult();
        if (picked.isEmpty()) {
            return;
        }
        tooltip.replace(JadeIds.CORE_OBJECT_NAME, picked.getHoverName());
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
