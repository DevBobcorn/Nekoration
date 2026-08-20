package io.devbobcorn.nekoration.blocks;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Dyeable short awning. */
public class ShortAwningBlock extends DyeableHorizontalBlock {
    public static final MapCodec<ShortAwningBlock> CODEC = simpleCodec(ShortAwningBlock::new);
    private static final VoxelShape SHAPE = Block.box(0.1D, 2.0D, 0.1D, 15.9D, 15.9D, 15.9D);

    public ShortAwningBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<ShortAwningBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
