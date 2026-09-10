package io.devbobcorn.nekoration.blocks.stone;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class DirectionalColumnBlock extends ColumnBlock {
    public static final EnumProperty<Direction.Axis> AXIS;

    public DirectionalColumnBlock(Properties settings, int topPartHeight) {
        super(settings, topPartHeight);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockState = super.getStateForPlacement(context);
        return blockState.setValue(AXIS, context.getHorizontalDirection().getAxis());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AXIS);
    }

    static {
        AXIS = net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_AXIS;
    }   
}
