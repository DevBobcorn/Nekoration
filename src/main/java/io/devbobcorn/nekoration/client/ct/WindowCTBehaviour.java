package io.devbobcorn.nekoration.client.ct;

import java.util.Set;

import org.jetbrains.annotations.Nullable;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.WindowBlock;
import io.devbobcorn.nekoration.blocks.WindowPaneBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class WindowCTBehaviour extends NekoConnectedTextureBehaviour {
    private static final Set<String> CT_TEXTURES = Set.of(
            "window_simple",
            "window_arch",
            "window_cross",
            "window_shade",
            "window_lancet");

    @Override
    @Nullable
    public NekoCTSpriteShiftEntry getShift(BlockState state, Direction direction, TextureAtlasSprite sprite) {
        if (!isWindowCtBlock(state) || !direction.getAxis().isHorizontal()) {
            return null;
        }

        ResourceLocation original = sprite.contents().name();
        if (!Nekoration.MODID.equals(original.getNamespace())) {
            return null;
        }

        String path = original.getPath();
        if (!path.startsWith("block/window/")) {
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
        return NekoCTSpriteShifter.getCT(NekoCTTypes.RECTANGLE, original, connected);
    }

    @Override
    @Nullable
    public NekoCTType getDataType(BlockAndTintGetter world, BlockPos pos, BlockState state, Direction direction) {
        if (!isWindowCtBlock(state) || !direction.getAxis().isHorizontal()) {
            return null;
        }
        return NekoCTTypes.RECTANGLE;
    }

    @Override
    public boolean buildContextForOccludedDirections() {
        // Pane multipart geometry can render quads on directions that vanilla face-culling
        // considers hidden, especially at corners. Build CT context for all directions so
        // those quads still receive connected tile indices.
        return true;
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos,
            Direction face) {
        if (isBeingBlocked(state, reader, pos, otherPos, face)) {
            return false;
        }
        if (!isWindowCtBlock(state) || !isWindowCtBlock(other)) {
            return false;
        }

        ResourceLocation selfId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        ResourceLocation otherId = BuiltInRegistries.BLOCK.getKey(other.getBlock());
        if (!Nekoration.MODID.equals(selfId.getNamespace()) || !Nekoration.MODID.equals(otherId.getNamespace())) {
            return false;
        }

        String selfWood = windowWoodId(selfId.getPath());
        String otherWood = windowWoodId(otherId.getPath());
        return selfWood != null && selfWood.equals(otherWood);
    }

    @Override
    protected boolean reverseUVsHorizontally(BlockState state, Direction face) {
        // Pane side quads on negative horizontal axes have mirrored UV orientation
        // compared to the positive faces; flip CT U mapping to keep atlas order consistent.
        if (!(state.getBlock() instanceof WindowPaneBlock)) {
            return false;
        }
        return face == Direction.NORTH || face == Direction.WEST;
    }

    @Override
    protected BlockState getCTBlockState(BlockAndTintGetter reader, BlockState reference, Direction face, BlockPos fromPos,
            BlockPos toPos) {
        // Window panes rely on their real world state (including connection properties).
        // Using appearance states can collapse expected pane-to-pane CT adjacency.
        return reader.getBlockState(toPos);
    }

    private static boolean isWindowCtBlock(BlockState state) {
        return state.getBlock() instanceof WindowBlock || state.getBlock() instanceof WindowPaneBlock;
    }

    @Nullable
    private static String windowWoodId(String blockPath) {
        int marker = blockPath.indexOf("_window_");
        if (marker <= 0) {
            return null;
        }
        return blockPath.substring(0, marker);
    }
}
