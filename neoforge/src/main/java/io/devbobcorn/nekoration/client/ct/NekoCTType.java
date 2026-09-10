package io.devbobcorn.nekoration.client.ct;

import io.devbobcorn.nekoration.client.ct.NekoConnectedTextureBehaviour.CTContext;
import io.devbobcorn.nekoration.client.ct.NekoConnectedTextureBehaviour.ContextRequirement;
import net.minecraft.resources.ResourceLocation;

public interface NekoCTType {
    ResourceLocation getId();

    int getSheetSize();

    ContextRequirement getContextRequirement();

    int getTextureIndex(CTContext context);
}
