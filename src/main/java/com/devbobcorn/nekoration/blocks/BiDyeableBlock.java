package com.devbobcorn.nekoration.blocks;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.common.VanillaCompat;
import com.devbobcorn.nekoration.items.BiDyeableBlockItem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BiDyeableBlock extends Block {
	public static final IntegerProperty COLOR0 = BlockStateProperties.LEVEL;
	public static final IntegerProperty COLOR1 = BlockStateProperties.AGE_15;

	public BiDyeableBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(COLOR0, 2).setValue(COLOR1, 14));
	}

	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> s) {
		s.add(COLOR0, COLOR1);
	}

	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockRayTraceResult hit) {
		ItemStack itemStack = player.getItemInHand(hand);

		if (world.isClientSide) {
			return (VanillaCompat.COLOR_ITEMS.containsKey(itemStack.getItem()) || VanillaCompat.RAW_COLOR_ITEMS.containsKey(itemStack.getItem())) ?
			 ActionResultType.SUCCESS : ActionResultType.PASS;
		}

		if (VanillaCompat.COLOR_ITEMS.containsKey(itemStack.getItem())) {
			world.setBlock(pos, state.setValue(COLOR1, VanillaCompat.COLOR_ITEMS.get(itemStack.getItem())), 3);
			return ActionResultType.SUCCESS;
		}
		if (VanillaCompat.RAW_COLOR_ITEMS.containsKey(itemStack.getItem())) {
			world.setBlock(pos, state.setValue(COLOR0, VanillaCompat.RAW_COLOR_ITEMS.get(itemStack.getItem())), 3);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		ItemStack stack = ctx.getItemInHand();
		if (stack.getItem() instanceof BiDyeableBlockItem)
			return this.defaultBlockState().setValue(COLOR0, BiDyeableBlockItem.getColor0(stack).getNBTId()).setValue(COLOR1, BiDyeableBlockItem.getColor1(stack).getNBTId());
		return this.defaultBlockState();
    }

	@Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull BlockState state, RayTraceResult target, @Nonnull IBlockReader world, @Nonnull BlockPos pos, PlayerEntity player) {
		ItemStack stack = new ItemStack(this.asItem());
		BiDyeableBlockItem.setColor0(stack, NekoColors.EnumWoodenColor.getColorEnumFromID(state.getValue(COLOR0).byteValue()));
		BiDyeableBlockItem.setColor1(stack, NekoColors.EnumNekoColor.getColorEnumFromID(state.getValue(COLOR1).byteValue()));
        return stack;
    }

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		ItemStack stack = new ItemStack(this.asItem());
		BiDyeableBlockItem.setColor0(stack, NekoColors.EnumWoodenColor.getColorEnumFromID(state.getValue(COLOR0).byteValue()));
		BiDyeableBlockItem.setColor1(stack, NekoColors.EnumNekoColor.getColorEnumFromID(state.getValue(COLOR1).byteValue()));
		return Collections.singletonList(stack);
	}
}