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
 * Connected texture behaviour for {@code cement} blocks. Each base tile
 * ({@code cement_t0}, {@code cement_t1}, {@code cement_t2}, {@code cement_top})
 * is swapped for its 2x2 {@code _connected} sheet, and the variant tile is picked
 * from the block's world position instead of neighbour connectivity.
 */
public class CementCTBehaviour extends NekoConnectedTextureBehaviour {
    private static final Set<String> CT_TEXTURES = Set.of(
            "cement_t0",
            "cement_t1",
            "cement_t2",
            "cement_top");

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
        if (!CT_TEXTURES.contains(textureName) || textureName.endsWith("_connected")) {
            return null;
        }

        ResourceLocation connected = ResourceLocation.fromNamespaceAndPath(
                original.getNamespace(),
                path + "_connected");
        return NekoCTSpriteShifter.getCT(NekoCTTypes.POSITION, original, connected);
    }

    @Override
    @Nullable
    public NekoCTType getDataType(BlockAndTintGetter world, BlockPos pos, BlockState state, Direction direction) {
        if (!supportsSourceBlock(state)) {
            return null;
        }
        return NekoCTTypes.POSITION;
    }

    protected boolean supportsSourceBlock(BlockState state) {
        return state.getBlock() == CementBlockRegistration.CEMENT.get();
    }
}
