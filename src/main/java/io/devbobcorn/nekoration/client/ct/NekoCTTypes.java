package io.devbobcorn.nekoration.client.ct;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.client.ct.NekoConnectedTextureBehaviour.CTContext;
import io.devbobcorn.nekoration.client.ct.NekoConnectedTextureBehaviour.ContextRequirement;
import net.minecraft.core.BlockPos;
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
    },
    POSITION(4, ContextRequirement.builder().build()) {
        @Override
        public int getTextureIndex(CTContext context) {
            return positionTileIndex(context.pos);
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

    /**
     * Picks one of the 16 tiles of a 4x4 sheet from the block's world position, so
     * neighbouring blocks deterministically get different variants without relying on
     * neighbour connectivity. Falls back to tile 0 when no position is available.
     */
    private static int positionTileIndex(BlockPos pos) {
        if (pos == null) {
            return 0;
        }
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        int hash = x * 374761393 + y * 668265263 + z * (int) 2246822519L;
        hash = (hash ^ (hash >>> 13)) * 1274126177;
        hash ^= hash >>> 16;
        return hash & 15;
    }
}
