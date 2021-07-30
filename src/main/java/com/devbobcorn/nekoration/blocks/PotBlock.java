package com.devbobcorn.nekoration.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.PlantType;

public class PotBlock extends DyeableBlock {
    public final VoxelShape Shape;
    public final double Radius = 6.0D;

    public PotBlock(Properties settings, double radius) {
		super(settings);
        Shape = Block.box(8.0D - Radius, 0.0D, 8.0D - Radius, 8.0D + Radius, 16.0D, 8.0 + Radius);
		this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 14));
	}

    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        return Shape;
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
