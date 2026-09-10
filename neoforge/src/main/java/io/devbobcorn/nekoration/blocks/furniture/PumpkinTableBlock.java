package io.devbobcorn.nekoration.blocks.furniture;

import java.util.Collections;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Pumpkin table with a square top and X-shaped crossed legs.
 */
public class PumpkinTableBlock extends Block {
    private static final double TOP_MIN_Y = 14.0D;
    private static final double TOP_MAX_Y = 16.0D;
    private static final double LEG_MIN = 2.0D;
    private static final double LEG_MAX = 14.0D;

    private static final VoxelShape SHAPE = Shapes.or(
            // Square tabletop (y 14-16)
            Block.box(0.0D, TOP_MIN_Y, 0.0D, 16.0D, TOP_MAX_Y, 16.0D),
            // X-shaped crossed legs -> single cuboid bounding box (y 0-14)
            Block.box(LEG_MIN, 0.0D, LEG_MIN, LEG_MAX, TOP_MIN_Y, LEG_MAX));

    public PumpkinTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return Collections.singletonList(new ItemStack(asItem()));
    }
}
