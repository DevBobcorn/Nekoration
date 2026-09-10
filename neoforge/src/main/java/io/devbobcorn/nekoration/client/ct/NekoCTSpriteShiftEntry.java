package io.devbobcorn.nekoration.client.ct;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class NekoCTSpriteShiftEntry {
    private final NekoCTType type;
    private final ResourceLocation original;
    private final ResourceLocation target;

    public NekoCTSpriteShiftEntry(NekoCTType type, ResourceLocation original, ResourceLocation target) {
        this.type = type;
        this.original = original;
        this.target = target;
    }

    public NekoCTType getType() {
        return type;
    }

    public TextureAtlasSprite getOriginal() {
        return Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(original);
    }

    public TextureAtlasSprite getTarget() {
        return Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(target);
    }

    public float getTargetU(float localU, int index) {
        float uOffset = index % type.getSheetSize();
        return getTarget().getU((getUnInterpolatedU(getOriginal(), localU) + uOffset) / (float) type.getSheetSize());
    }

    public float getTargetV(float localV, int index) {
        float vOffset = index / type.getSheetSize();
        return getTarget().getV((getUnInterpolatedV(getOriginal(), localV) + vOffset) / (float) type.getSheetSize());
    }

    private static float getUnInterpolatedU(TextureAtlasSprite sprite, float u) {
        return (u - sprite.getU0()) / (sprite.getU1() - sprite.getU0());
    }

    private static float getUnInterpolatedV(TextureAtlasSprite sprite, float v) {
        return (v - sprite.getV0()) / (sprite.getV1() - sprite.getV0());
    }
}
