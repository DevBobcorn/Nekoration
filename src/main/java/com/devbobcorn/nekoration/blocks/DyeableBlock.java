package com.devbobcorn.nekoration.blocks;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.common.VanillaCompat;
import com.devbobcorn.nekoration.items.DyeableBlockItem;
import com.devbobcorn.nekoration.items.DyeableWoodenBlockItem;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

public class DyeableBlock extends Block {
	public static final IntegerProperty COLOR = BlockStateProperties.LEVEL;

	public DyeableBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 14));
	}
	
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> s) {
		s.add(COLOR);
	}

	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
			BlockHitResult hit) {
		ItemStack itemStack = player.getItemInHand(hand);

		if (world.isClientSide) {
			return (VanillaCompat.COLOR_ITEMS.containsKey(itemStack.getItem())) ? InteractionResult.SUCCESS : InteractionResult.PASS;
		}
		
		if (VanillaCompat.COLOR_ITEMS.containsKey(itemStack.getItem())) {
			world.setBlock(pos, state.setValue(COLOR, VanillaCompat.COLOR_ITEMS.get(itemStack.getItem())), 3);
			return InteractionResult.CONSUME;
		}
		return InteractionResult.PASS;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		ItemStack stack = ctx.getItemInHand();
		if (stack.getItem() instanceof DyeableBlockItem)
			return this.defaultBlockState().setValue(COLOR, DyeableBlockItem.getColor(stack).getNBTId());
		else if (stack.getItem() instanceof DyeableWoodenBlockItem)
			return this.defaultBlockState().setValue(COLOR, DyeableWoodenBlockItem.getColor(stack).getNBTId());
		return this.defaultBlockState();
    }

	@Nonnull
    @Override
    public ItemStack getCloneItemStack(@Nonnull BlockState state, HitResult target, @Nonnull BlockGetter world, @Nonnull BlockPos pos, Player player) {
		ItemStack stack = new ItemStack(this.asItem());
		DyeableBlockItem.setColor(stack, NekoColors.EnumNekoColor.getColorEnumFromID(state.getValue(COLOR).byteValue()));
        return stack;
    }

	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		ItemStack stack = new ItemStack(this.asItem());
		DyeableBlockItem.setColor(stack, NekoColors.EnumNekoColor.getColorEnumFromID(state.getValue(COLOR).byteValue()));
		return Collections.singletonList(stack);
	}
}