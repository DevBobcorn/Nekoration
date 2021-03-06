package com.devbobcorn.nekoration.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.StateDefinition;

public abstract class DyeableHorizontalBlock extends DyeableBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    protected DyeableHorizontalBlock(Properties settings) {
        super(settings);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> s) {
        s.add(COLOR, FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    protected static Map<Direction, VoxelShape> getAABBs(double thickness){
        return Maps.newEnumMap(ImmutableMap.of(
                Direction.NORTH, Block.box(0.0D, 0.0D, 16.0D - thickness, 16.0D, 16.0D, 16.0D),
                Direction.SOUTH, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, thickness), 
                Direction.EAST, Block.box(0.0D, 0.0D, 0.0D, thickness, 16.0D, 16.0D),
                Direction.WEST, Block.box(16.0D - thickness, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)));
    }

    public BlockPos getLeftBlock(BlockPos pos, Direction dir) {
        switch (dir) {
        case NORTH:
            return pos.east();
        case EAST:
            return pos.south();
        case SOUTH:
            return pos.west();
        default:
            return pos.north();
        }
    }
    
    public BlockPos getRightBlock(BlockPos pos, Direction dir) {
        switch (dir) {
        case NORTH:
            return pos.west();
        case EAST:
            return pos.north();
        case SOUTH:
            return pos.east();
        default:
            return pos.south();
        }
    }

    public Direction getLeftDir(Direction selfDir) {
        switch (selfDir) {
        case NORTH:
            return Direction.EAST;
        case EAST:
            return Direction.SOUTH;
        case SOUTH:
            return Direction.WEST;
        default:
            return Direction.NORTH;
        }
    }
    
    public Direction getRightDir(Direction selfDir) {
        switch (selfDir) {
        case NORTH:
            return Direction.WEST;
        case EAST:
            return Direction.NORTH;
        case SOUTH:
            return Direction.EAST;
        default:
            return Direction.SOUTH;
        }
    }
}