package io.devbobcorn.nekoration.blocks.furniture;

import io.devbobcorn.nekoration.blocks.HorizontalConnectedBlock;
import io.devbobcorn.nekoration.entities.SeatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Wooden bench which connects to adjacent benches and can be used as a seat. */
public class BenchBlock extends HorizontalConnectedBlock {
    private final VoxelShape[] benchShapes = new VoxelShape[4];

    public BenchBlock(Properties properties) {
        super(properties, ConnectionType.TRIPLE, false, 12, 1, 8);
        benchShapes[0] = Shapes.or(Block.box(0, 0, 0, 16, 9, 12), Block.box(0, 0, 0, 16, 21, 2));
        benchShapes[1] = Shapes.or(Block.box(4, 0, 0, 16, 9, 16), Block.box(14, 0, 0, 16, 21, 16));
        benchShapes[2] = Shapes.or(Block.box(0, 0, 4, 16, 9, 16), Block.box(0, 0, 14, 16, 21, 16));
        benchShapes[3] = Shapes.or(Block.box(0, 0, 0, 12, 9, 16), Block.box(0, 0, 0, 2, 21, 16));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return benchShapes[state.getValue(FACING).get2DDataValue()];
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        return SeatEntity.trySit(level, pos, 0.3125D, player);
    }
}
