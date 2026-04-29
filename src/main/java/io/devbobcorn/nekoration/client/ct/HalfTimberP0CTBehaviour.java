package io.devbobcorn.nekoration.client.ct;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.DyeableBlock;
import io.devbobcorn.nekoration.blocks.NekoWood;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class HalfTimberP0CTBehaviour extends NekoConnectedTextureBehaviour {
    private final Map<String, NekoCTSpriteShiftEntry> shiftsByWood = new HashMap<>();

    public HalfTimberP0CTBehaviour() {
        for (NekoWood wood : NekoWood.values()) {
            String id = wood.id();
            ResourceLocation original = ResourceLocation.fromNamespaceAndPath(Nekoration.MODID,
                    "block/half_timber/" + id + "/halftimber_frame_p0");
            ResourceLocation connected = ResourceLocation.fromNamespaceAndPath(Nekoration.MODID,
                    "block/half_timber/" + id + "/halftimber_frame_p0_connected");
            shiftsByWood.put(id, NekoCTSpriteShifter.getCT(NekoCTTypes.RECTANGLE, original, connected));
        }
    }

    @Override
    public @Nullable NekoCTSpriteShiftEntry getShift(BlockState state, Direction direction, TextureAtlasSprite sprite) {
        ResourceLocation key = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock());
        String path = key.getPath();
        if (!path.endsWith("_half_timber_p0")) {
            return null;
        }
        String wood = path.substring(0, path.length() - "_half_timber_p0".length());
        return shiftsByWood.get(wood);
    }

    @Override
    public @Nullable NekoCTType getDataType(BlockAndTintGetter world, BlockPos pos, BlockState state, Direction direction) {
        return NekoCTTypes.RECTANGLE;
    }

    @Override
    public boolean connectsTo(BlockState state, BlockState other, BlockAndTintGetter reader, BlockPos pos, BlockPos otherPos,
            Direction face) {
        if (!super.connectsTo(state, other, reader, pos, otherPos, face)) {
            return false;
        }
        if (!state.hasProperty(DyeableBlock.COLOR) || !other.hasProperty(DyeableBlock.COLOR)) {
            return true;
        }
        return state.getValue(DyeableBlock.COLOR) == other.getValue(DyeableBlock.COLOR);
    }
}
