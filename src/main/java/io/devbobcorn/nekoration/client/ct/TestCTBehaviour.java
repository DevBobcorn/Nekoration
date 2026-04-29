package io.devbobcorn.nekoration.client.ct;

import org.jetbrains.annotations.Nullable;

import io.devbobcorn.nekoration.Nekoration;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class TestCTBehaviour extends NekoConnectedTextureBehaviour {
    private final NekoCTSpriteShiftEntry shift = NekoCTSpriteShifter.getCT(
            NekoCTTypes.SINGLE,
            ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "block/original"),
            ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "block/connected"));

    @Override
    public @Nullable NekoCTSpriteShiftEntry getShift(BlockState state, Direction direction, TextureAtlasSprite sprite) {
        return shift;
    }

    @Override
    public @Nullable NekoCTType getDataType(BlockAndTintGetter world, BlockPos pos, BlockState state, Direction direction) {
        return NekoCTTypes.SINGLE;
    }
}
