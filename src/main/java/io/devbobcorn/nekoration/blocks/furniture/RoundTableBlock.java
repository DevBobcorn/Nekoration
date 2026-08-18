package io.devbobcorn.nekoration.blocks.furniture;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Round table with an octagonal top and X-shaped crossed legs.
 * <p>
 * The collider mirrors {@code round_table.json}: the top is built from three
 * axis-aligned boxes, while the two 45&deg;-rotated leg boards are represented
 * by the single cuboid enclosing their combined bounding box.
 */
public class RoundTableBlock extends Block {
    private static final double TOP_MIN_Y = 14.0D;
    private static final double TOP_MAX_Y = 16.0D;
    /** Footprint bounds of the two 45&deg; leg boards. */
    private static final double LEG_MIN = 2.0D;
    private static final double LEG_MAX = 14.0D;

    private static final VoxelShape SHAPE = Shapes.or(
            // Octagonal tabletop (y 14-16)
            Block.box(0.0D, TOP_MIN_Y, 2.0D, 16.0D, TOP_MAX_Y, 14.0D),
            Block.box(2.0D, TOP_MIN_Y, 0.0D, 14.0D, TOP_MAX_Y, 2.0D),
            Block.box(2.0D, TOP_MIN_Y, 14.0D, 14.0D, TOP_MAX_Y, 16.0D),
            // X-shaped crossed legs -> single cuboid bounding box (y 0-14)
            Block.box(LEG_MIN, 0.0D, LEG_MIN, LEG_MAX, TOP_MIN_Y, LEG_MAX));

    public RoundTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
