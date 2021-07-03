package com.devbobcorn.nekoration.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.PlantType;

public class PotBlock extends DyeableBlock {
    public PotBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 14));
	}

	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> s) {
		s.add(COLOR);
	}

    @Override
    public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable)
    {
        PlantType type = plantable.getPlantType(world, pos.relative(facing));
        return type != PlantType.WATER;
    }
}
