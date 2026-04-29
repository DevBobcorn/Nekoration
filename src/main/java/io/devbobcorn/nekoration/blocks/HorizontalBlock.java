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

public class HorizontalBlock extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public HorizontalBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    protected static Map<Direction, VoxelShape> getAABBs(double thickness, double height) {
        return Map.of(
                Direction.NORTH, Block.box(0.0D, 0.0D, 16.0D - thickness, 16.0D, height, 16.0D),
                Direction.SOUTH, Block.box(0.0D, 0.0D, 0.0D, 16.0D, height, thickness),
                Direction.EAST, Block.box(0.0D, 0.0D, 0.0D, thickness, height, 16.0D),
                Direction.WEST, Block.box(16.0D - thickness, 0.0D, 0.0D, 16.0D, height, 16.0D));
    }

    protected static BlockPos getLeftBlock(BlockPos pos, Direction dir) {
        return switch (dir) {
            case NORTH -> pos.east();
            case EAST -> pos.south();
            case SOUTH -> pos.west();
            default -> pos.north();
        };
    }

    protected static BlockPos getRightBlock(BlockPos pos, Direction dir) {
        return switch (dir) {
            case NORTH -> pos.west();
            case EAST -> pos.north();
            case SOUTH -> pos.east();
            default -> pos.south();
        };
    }

    protected static Direction getLeftDir(Direction selfDir) {
        return switch (selfDir) {
            case NORTH -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            default -> Direction.NORTH;
        };
    }

    protected static Direction getRightDir(Direction selfDir) {
        return switch (selfDir) {
            case NORTH -> Direction.WEST;
            case EAST -> Direction.NORTH;
            case SOUTH -> Direction.EAST;
            default -> Direction.SOUTH;
        };
    }
}
