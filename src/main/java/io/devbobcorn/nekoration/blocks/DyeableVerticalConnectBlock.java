package io.devbobcorn.nekoration.blocks;

import java.util.Collections;
import java.util.List;

import io.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import io.devbobcorn.nekoration.blocks.states.VerticalConnection;
import io.devbobcorn.nekoration.common.VanillaCompat;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import net.minecraft.core.BlockPos;
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

public class DyeableVerticalConnectBlock extends VerticalConnectBlock {

    public DyeableVerticalConnectBlock(Properties settings) {
        super(settings);
        registerDefaultState(stateDefinition.any()
                .setValue(CONNECTION, VerticalConnection.S0)
                .setValue(DyeableBlock.COLOR, EnumNekoColor.WHITE));
    }

    public DyeableVerticalConnectBlock(Properties settings, ConnectionType type, boolean connectOtherVariant) {
        super(settings, type, connectOtherVariant);
        registerDefaultState(stateDefinition.any()
                .setValue(CONNECTION, VerticalConnection.S0)
                .setValue(DyeableBlock.COLOR, EnumNekoColor.WHITE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DyeableBlock.COLOR);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState placed = super.getStateForPlacement(ctx);
        ItemStack stack = ctx.getItemInHand();
        if (stack.getItem() instanceof DyeableBlockItem) {
            placed = placed.setValue(DyeableBlock.COLOR, DyeableBlockItem.getColor(stack));
        } else {
            placed = placed.setValue(DyeableBlock.COLOR, EnumNekoColor.WHITE);
        }
        return placed;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hitResult) {
        boolean canColor = VanillaCompat.RAW_COLOR_ITEMS.containsKey(stack.getItem())
                || VanillaCompat.COLOR_ITEMS.containsKey(stack.getItem());
        if (!canColor) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }
        Integer raw = VanillaCompat.RAW_COLOR_ITEMS.get(stack.getItem());
        int colorIndex = raw != null ? raw : VanillaCompat.COLOR_ITEMS.get(stack.getItem());
        EnumNekoColor next = EnumNekoColor.getColorEnumFromId((byte) colorIndex);
        level.setBlock(pos, state.setValue(DyeableBlock.COLOR, next), Block.UPDATE_ALL);
        return ItemInteractionResult.CONSUME;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        ItemStack stack = new ItemStack(asItem());
        DyeableBlockItem.setColor(stack, state.getValue(DyeableBlock.COLOR));
        return stack;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        ItemStack stack = new ItemStack(asItem());
        DyeableBlockItem.setColor(stack, state.getValue(DyeableBlock.COLOR));
        return Collections.singletonList(stack);
    }
}
