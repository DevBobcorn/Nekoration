package com.devbobcorn.nekoration.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.PlantType;

public class PotBlock extends DyeableBlock {
    public final VoxelShape Shape;
    public final double Radius;

    public PotBlock(Properties settings, double radius) {
        super(settings);
        Radius = radius;
        Shape = Block.box(8.0D - Radius, 0.0D, 8.0D - Radius, 8.0D + Radius, 16.0D, 8.0 + Radius);
        this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 14));
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return Shape;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> s) {
        s.add(COLOR);
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, net.minecraftforge.common.IPlantable plantable)
    {
        PlantType type = plantable.getPlantType(world, pos.relative(facing));
        return type != PlantType.WATER;
    }
}
