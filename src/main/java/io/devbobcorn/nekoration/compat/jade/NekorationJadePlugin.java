package io.devbobcorn.nekoration.compat.jade;

import io.devbobcorn.nekoration.blocks.DyeableBlock;
import io.devbobcorn.nekoration.blocks.DyeableHorizontalConnectedBlock;
import io.devbobcorn.nekoration.blocks.DyeableVerticalConnectedBlock;
import io.devbobcorn.nekoration.blocks.cement.DyeableFrameSideBlock;
import io.devbobcorn.nekoration.blocks.cement.DyeablePotBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

/**
 * Jade integration for resolving dyeable block names from picked stacks.
 */
@WailaPlugin("jade")
public final class NekorationJadePlugin implements IWailaPlugin {
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(NekorationJadeNameProvider.INSTANCE, DyeableBlock.class);
        registration.registerBlockComponent(NekorationJadeNameProvider.INSTANCE, DyeableVerticalConnectedBlock.class);
        registration.registerBlockComponent(NekorationJadeNameProvider.INSTANCE, DyeableHorizontalConnectedBlock.class);
        registration.registerBlockComponent(NekorationJadeNameProvider.INSTANCE, DyeableFrameSideBlock.class);
        registration.registerBlockComponent(NekorationJadeNameProvider.INSTANCE, DyeablePotBlock.class);
    }
}
