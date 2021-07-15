package com.devbobcorn.nekoration.blocks;

import com.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class CustomBlock extends DirectionalBlock {
	public static final IntegerProperty MODEL = BlockStateProperties.LEVEL;

	public CustomBlock(Properties settings) {
		super(settings);
	}
	
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> s) {
		s.add(FACING, MODEL);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new CustomBlockEntity();
	}
}
