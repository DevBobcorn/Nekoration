package io.devbobcorn.nekoration.blocks.containers;

import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Non-dyeable wooden item display base block.
 *
 * <p>
 * Legacy used a dyeable wooden base block. In the port wooden blocks are not
 * dyeable, so this block only keeps facing + open states.
 * </p>
 */
public class ItemDisplayBlock extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    private static final Map<Direction, VoxelShape> AABBS = getAABBs(9.0D, 16.0D);

    public ItemDisplayBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AABBS.get(state.getValue(FACING));
    }

    protected static Map<Direction, VoxelShape> getAABBs(double thickness, double height) {
        return Map.of(
                Direction.NORTH, Block.box(0.0D, 0.0D, 16.0D - thickness, 16.0D, height, 16.0D),
                Direction.SOUTH, Block.box(0.0D, 0.0D, 0.0D, 16.0D, height, thickness),
                Direction.EAST, Block.box(0.0D, 0.0D, 0.0D, thickness, height, 16.0D),
                Direction.WEST, Block.box(16.0D - thickness, 0.0D, 0.0D, 16.0D, height, 16.0D));
    }
}
