package com.devbobcorn.nekoration.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WindowBlock extends DyeableVerticalConnectBlock {
    public WindowBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 2));
    }
    
    public WindowBlock(Properties settings, ConnectionType tp, boolean co) {
		super(settings, tp, co);
        this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 2));
	}

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> s) {
		s.add(CONNECTION, COLOR);
	}

    @OnlyIn(Dist.CLIENT)
	public static boolean shouldRenderFace(BlockState state, IBlockReader world, BlockPos from, Direction dir) {
		return !(world.getBlockState(from).getBlock() instanceof WindowBlock);
	}

	@Override
	public BlockRenderType getRenderShape(BlockState blockState) {
		return BlockRenderType.MODEL;
	}

    @OnlyIn(Dist.CLIENT)
    public float getShadeBrightness(BlockState p_220080_1_, IBlockReader p_220080_2_, BlockPos p_220080_3_) {
       return 0.5F;
    }
 
    public boolean propagatesSkylightDown(BlockState p_200123_1_, IBlockReader p_200123_2_, BlockPos p_200123_3_) {
       return true;
    }
}
