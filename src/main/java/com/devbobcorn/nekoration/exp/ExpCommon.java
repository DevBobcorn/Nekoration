package com.devbobcorn.nekoration.exp;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ExpCommon {
    final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

  /**
   * Register common events for both dedicated servers and clients. This method is safe to call directly.
   */
  public void registerCommonEvents(IEventBus eventBus) {
    eventBus.register(com.devbobcorn.nekoration.exp.dynamic_block.StartupCommon.class);
    eventBus.register(com.devbobcorn.nekoration.exp.tile_entity.StartupCommon.class);

    //----------------
    //eventBus.register(minecraftbyexample.usefultools.debugging.StartupCommon.class);
  }
}
