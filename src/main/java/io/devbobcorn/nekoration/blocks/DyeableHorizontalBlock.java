package io.devbobcorn.nekoration.blocks;

import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class DyeableHorizontalBlock extends DyeableBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    protected DyeableHorizontalBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState placed = super.getStateForPlacement(ctx);
        if (placed == null) {
            return null;
        }
        return placed.setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    protected static Map<Direction, VoxelShape> getAABBs(double thickness) {
        return Map.of(
                Direction.NORTH, Block.box(0.0D, 0.0D, 16.0D - thickness, 16.0D, 16.0D, 16.0D),
                Direction.SOUTH, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, thickness),
                Direction.EAST, Block.box(0.0D, 0.0D, 0.0D, thickness, 16.0D, 16.0D),
                Direction.WEST, Block.box(16.0D - thickness, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D));
    }

    protected BlockPos getLeftBlock(BlockPos pos, Direction dir) {
        return switch (dir) {
            case NORTH -> pos.east();
            case EAST -> pos.south();
            case SOUTH -> pos.west();
            default -> pos.north();
        };
    }

    protected BlockPos getRightBlock(BlockPos pos, Direction dir) {
        return switch (dir) {
            case NORTH -> pos.west();
            case EAST -> pos.north();
            case SOUTH -> pos.east();
            default -> pos.south();
        };
    }

    protected Direction getLeftDir(Direction selfDir) {
        return switch (selfDir) {
            case NORTH -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            default -> Direction.NORTH;
        };
    }

    protected Direction getRightDir(Direction selfDir) {
        return switch (selfDir) {
            case NORTH -> Direction.WEST;
            case EAST -> Direction.NORTH;
            case SOUTH -> Direction.EAST;
            default -> Direction.SOUTH;
        };
    }
}
