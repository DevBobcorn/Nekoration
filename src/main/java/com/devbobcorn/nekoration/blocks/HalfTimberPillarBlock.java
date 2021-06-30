package com.devbobcorn.nekoration.blocks;

import com.devbobcorn.nekoration.common.VanillaCompat;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class HalfTimberPillarBlock extends DyeableVerticalConnectBlock {
	public static final IntegerProperty COLOR_IN = BlockStateProperties.AGE_15;

	public HalfTimberPillarBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 2).setValue(COLOR_IN, 14));
	}

	public HalfTimberPillarBlock(Properties settings, ConnectionType tp, boolean co) {
		super(settings, tp, co);
		this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 2).setValue(COLOR_IN, 14));
	}

	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> s) {
		s.add(CONNECTION, COLOR, COLOR_IN);
	}

	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockRayTraceResult hit) {
		
		ItemStack itemStack = player.getItemInHand(hand);

		if (world.isClientSide) {
			return (VanillaCompat.COLOR_ITEMS.containsKey(itemStack.getItem()) || VanillaCompat.RAW_COLOR_ITEMS.containsKey(itemStack.getItem())) ?
			 ActionResultType.SUCCESS : ActionResultType.PASS;
		}
		
		if (VanillaCompat.COLOR_ITEMS.containsKey(itemStack.getItem())) {
			world.setBlock(pos, state.setValue(COLOR_IN, VanillaCompat.COLOR_ITEMS.get(itemStack.getItem())), 3);
			return ActionResultType.CONSUME;
		}
		if (VanillaCompat.RAW_COLOR_ITEMS.containsKey(itemStack.getItem())) {
			world.setBlock(pos, state.setValue(COLOR, VanillaCompat.RAW_COLOR_ITEMS.get(itemStack.getItem())), 3);
			return ActionResultType.CONSUME;
		}
		return ActionResultType.PASS;
	}
}