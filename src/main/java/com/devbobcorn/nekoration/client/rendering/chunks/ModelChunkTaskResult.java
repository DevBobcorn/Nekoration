package com.devbobcorn.nekoration.client.rendering.chunks;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum ModelChunkTaskResult {
   SUCCESSFUL,
   CANCELLED;
}