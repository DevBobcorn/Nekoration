package io.devbobcorn.nekoration.blocks.cement;

import java.util.ArrayList;
import java.util.List;

import io.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import io.devbobcorn.nekoration.blocks.DyeableBlock;
import io.devbobcorn.nekoration.blocks.states.FrameConnection;
import io.devbobcorn.nekoration.blocks.stone.FrameSideBlock;
import io.devbobcorn.nekoration.common.VanillaCompat;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;

public class DyeableFrameSideBlock extends FrameSideBlock {

    public DyeableFrameSideBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(DyeableBlock.COLOR, EnumNekoColor.WHITE)
                .setValue(FACING, Direction.NORTH)
                .setValue(CONNECTION, FrameConnection.BOTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DyeableBlock.COLOR);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState placed = super.getStateForPlacement(ctx);
        if (placed == null) {
            return null;
        }
        return placed.setValue(DyeableBlock.COLOR, getStackColor(ctx.getItemInHand()));
    }

    @Override
    protected boolean isSameKind(BlockState state, ItemStack stack) {
        return super.isSameKind(state, stack) && state.getValue(DyeableBlock.COLOR) == getStackColor(stack);
    }

    private static EnumNekoColor getStackColor(ItemStack stack) {
        return stack.getItem() instanceof DyeableBlockItem ? DyeableBlockItem.getColor(stack) : EnumNekoColor.WHITE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hitResult) {
        boolean canColor = VanillaCompat.COLOR_ITEMS.containsKey(stack.getItem());
        if (!canColor) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }
        int colorIndex = VanillaCompat.COLOR_ITEMS.get(stack.getItem());
        EnumNekoColor next = EnumNekoColor.getColorEnumFromId((byte) colorIndex);
        level.setBlock(pos, state.setValue(DyeableBlock.COLOR, next), Block.UPDATE_ALL);
        return ItemInteractionResult.CONSUME;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        ItemStack stack = new ItemStack(this.asItem());
        DyeableBlockItem.setColor(stack, state.getValue(DyeableBlock.COLOR));
        return stack;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        // One item per side of the frame, each keeping the dyed color.
        List<ItemStack> drops = new ArrayList<>(getDropCount(state));
        for (int i = 0; i < getDropCount(state); i++) {
            ItemStack stack = new ItemStack(this.asItem());
            DyeableBlockItem.setColor(stack, state.getValue(DyeableBlock.COLOR));
            drops.add(stack);
        }
        return drops;
    }
}
