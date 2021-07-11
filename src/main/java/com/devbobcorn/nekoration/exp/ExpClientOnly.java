package com.devbobcorn.nekoration.exp;

import net.minecraftforge.eventbus.api.IEventBus;

/*
    This class is required to make sure that we don't accidentally try to load any client-side-only classes
      on a dedicated server.
    It is a rather convoluted way of doing it, but I haven't found a simpler way to do it which is robust
 */

public class ExpClientOnly {
    private final IEventBus eventBus;

    /**
     * @param eventBus an instance of the mod event bus
     */
    public ExpClientOnly(IEventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * Register client only events. This method must only be called when it is certain that the mod is
     * is executing code on the client side and not the dedicated server.
     */
    public void registerClientOnlyEvents() {
        eventBus.register(com.devbobcorn.nekoration.exp.dynamic_block.StartupClientOnly.class);
        eventBus.register(com.devbobcorn.nekoration.exp.tile_entity.StartupClientOnly.class);
        eventBus.register(com.devbobcorn.nekoration.exp.monster_drink.StartupClientOnly.class);
        eventBus.register(com.devbobcorn.nekoration.exp.foot_locker.StartupClientOnly.class);

        //----------------
        //eventBus.register(minecraftbyexample.usefultools.debugging.StartupClientOnly.class);
    }
}
