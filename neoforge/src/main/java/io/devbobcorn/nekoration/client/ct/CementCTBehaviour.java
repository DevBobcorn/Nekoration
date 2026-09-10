package io.devbobcorn.nekoration.client.ct;

import java.util.Set;

import org.jetbrains.annotations.Nullable;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.registry.CementBlockRegistration;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Connected texture behaviour for full-cube cement blocks. Top faces and the base
 * cement block's side tiles use position-based texture variation.
 */
public class CementCTBehaviour extends NekoConnectedTextureBehaviour {
    private static final Set<String> POSITION_TEXTURES = Set.of(
            "cement_t0",
            "cement_t1",
            "cement_t2");
    private static final String TOP_TEXTURE = "cement_top";

    @Override
    @Nullable
    public NekoCTSpriteShiftEntry getShift(BlockState state, Direction direction, TextureAtlasSprite sprite) {
        if (!supportsSourceBlock(state)) {
            return null;
        }

        ResourceLocation original = sprite.contents().name();
        if (!Nekoration.MODID.equals(original.getNamespace())) {
            return null;
        }

        String path = original.getPath();
        if (!path.startsWith("block/cement/")) {
            return null;
        }

        int slashIndex = path.lastIndexOf('/') + 1;
        String textureName = path.substring(slashIndex);
        NekoCTType type = getType(state, direction, textureName);
        if (type == null || textureName.endsWith("_connected")) {
            return null;
        }

        ResourceLocation connected = ResourceLocation.fromNamespaceAndPath(
                original.getNamespace(),
                path + "_connected");
        return NekoCTSpriteShifter.getCT(type, original, connected);
    }

    @Override
    @Nullable
    public NekoCTType getDataType(BlockAndTintGetter world, BlockPos pos, BlockState state, Direction direction) {
        if (direction == Direction.UP && supportsSourceBlock(state)) {
            return NekoCTTypes.POSITION;
        }
        return state.getBlock() == CementBlockRegistration.CEMENT.get() ? NekoCTTypes.POSITION : null;
    }

    protected boolean supportsSourceBlock(BlockState state) {
        return CementBlockRegistration.isFullCube(state.getBlock());
    }

    @Nullable
    private static NekoCTType getType(BlockState state, Direction direction, String textureName) {
        if (direction == Direction.UP && TOP_TEXTURE.equals(textureName)) {
            return NekoCTTypes.POSITION;
        }
        if (state.getBlock() == CementBlockRegistration.CEMENT.get() && POSITION_TEXTURES.contains(textureName)) {
            return NekoCTTypes.POSITION;
        }
        return null;
    }
}
