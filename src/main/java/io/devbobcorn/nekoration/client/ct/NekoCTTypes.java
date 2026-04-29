package io.devbobcorn.nekoration.client.ct;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.client.ct.NekoConnectedTextureBehaviour.CTContext;
import io.devbobcorn.nekoration.client.ct.NekoConnectedTextureBehaviour.ContextRequirement;
import net.minecraft.resources.ResourceLocation;

public enum NekoCTTypes implements NekoCTType {
    SINGLE(1, ContextRequirement.builder().build()) {
        @Override
        public int getTextureIndex(CTContext context) {
            return 0;
        }
    },
    RECTANGLE(4, ContextRequirement.builder().axisAligned().build()) {
        @Override
        public int getTextureIndex(CTContext context) {
            int x = context.left && context.right ? 2 : context.left ? 3 : context.right ? 1 : 0;
            int y = context.up && context.down ? 1 : context.up ? 2 : context.down ? 0 : 3;
            return x + y * 4;
        }
    };

    private final ResourceLocation id;
    private final int sheetSize;
    private final ContextRequirement contextRequirement;

    NekoCTTypes(int sheetSize, ContextRequirement contextRequirement) {
        this.id = ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, name().toLowerCase());
        this.sheetSize = sheetSize;
        this.contextRequirement = contextRequirement;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public int getSheetSize() {
        return sheetSize;
    }

    @Override
    public ContextRequirement getContextRequirement() {
        return contextRequirement;
    }
}
