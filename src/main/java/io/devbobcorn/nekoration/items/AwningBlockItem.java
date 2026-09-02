package io.devbobcorn.nekoration.items;

import javax.annotation.Nullable;

import io.devbobcorn.nekoration.blocks.AwningBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

/**
 * Awning item that, when used on the front side of the bottom step of an awning, places the new
 * awning right below the position in front of it, visually connecting the two.
 */
public class AwningBlockItem extends DyeableBlockItem {

    public AwningBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Nullable
    @Override
    public BlockPlaceContext updatePlacementContext(BlockPlaceContext context) {
        if (getBlock() instanceof AwningBlock) {
            Direction face = context.getClickedFace();
            BlockPos targetPos = getTargetPos(context);
            BlockPos pos = targetPos == null ? null
                    : AwningBlock.getConnectedPlacementPos(context.getLevel(), targetPos, face,
                            context.getClickLocation());
            if (pos != null && context.getLevel().getBlockState(pos).canBeReplaced(context)) {
                return BlockPlaceContext.at(context, pos, face);
            }
        }
        return super.updatePlacementContext(context);
    }

    /** The position of the block being looked at, or {@code null} if a replaceable block was clicked. */
    @Nullable
    private static BlockPos getTargetPos(BlockPlaceContext context) {
        if (context.replacingClickedOnBlock()) {
            return null;
        }
        return context.getClickedPos().relative(context.getClickedFace().getOpposite());
    }
}
