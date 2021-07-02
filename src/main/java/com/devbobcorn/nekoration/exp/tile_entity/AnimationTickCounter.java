package com.devbobcorn.nekoration.exp.tile_entity;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Simple class to count the number of ticks on the client; used for animation purposes
 * Created by TGG on 10/03/2020.
 */
public class AnimationTickCounter {

  public static long getTotalElapsedTicksInGame() {
    return totalElapsedTicksInGame;
  }

  @SubscribeEvent
  public static void clientTickEnd(TickEvent.ClientTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      if (!Minecraft.getInstance().isPaused()) {
        totalElapsedTicksInGame++;
      }
    }
  }

  private static long totalElapsedTicksInGame = 0;
}
