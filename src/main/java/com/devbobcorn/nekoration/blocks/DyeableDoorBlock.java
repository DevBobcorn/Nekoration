package com.devbobcorn.nekoration.blocks;

import net.minecraft.block.DoorBlock;

import java.util.Collections;
import java.util.List;

import com.devbobcorn.nekoration.common.VanillaCompat;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class DyeableDoorBlock extends DoorBlock {
	protected static final VoxelShape TALL_SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 32.0D, 3.0D);
	protected static final VoxelShape TALL_NORTH_AABB = Block.box(0.0D, 0.0D, 13.0D, 16.0D, 32.0D, 16.0D);
	protected static final VoxelShape TALL_WEST_AABB = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 32.0D, 16.0D);
	protected static final VoxelShape TALL_EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 32.0D, 16.0D);

	public static final IntegerProperty COLOR = BlockStateProperties.LEVEL;
	public final boolean isTall;

	public DyeableDoorBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 14));
		isTall = false;
	}

	public DyeableDoorBlock(Properties settings, boolean tall) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 14));
		isTall = tall;
	}

	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> s) {
		s.add(HALF, FACING, OPEN, HINGE, POWERED, COLOR);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
		Direction direction = state.getValue(FACING);
		boolean flag = !state.getValue(OPEN);
		boolean flag1 = state.getValue(HINGE) == DoorHingeSide.RIGHT;
		if (isTall && state.getValue(HALF) == DoubleBlockHalf.UPPER)
			switch(direction) {
				case EAST:
				default:
					return flag ? TALL_EAST_AABB : (flag1 ? TALL_NORTH_AABB : TALL_SOUTH_AABB);
				case SOUTH:
					return flag ? TALL_SOUTH_AABB : (flag1 ? TALL_EAST_AABB : TALL_WEST_AABB);
				case WEST:
					return flag ? TALL_WEST_AABB : (flag1 ? TALL_SOUTH_AABB : TALL_NORTH_AABB);
				case NORTH:
					return flag ? TALL_NORTH_AABB : (flag1 ? TALL_WEST_AABB : TALL_EAST_AABB);
			}
		else 
			switch(direction) {
			case EAST:
			default:
				return flag ? EAST_AABB : (flag1 ? NORTH_AABB : SOUTH_AABB);
			case SOUTH:
				return flag ? SOUTH_AABB : (flag1 ? EAST_AABB : WEST_AABB);
			case WEST:
				return flag ? WEST_AABB : (flag1 ? SOUTH_AABB : NORTH_AABB);
			case NORTH:
				return flag ? NORTH_AABB : (flag1 ? WEST_AABB : EAST_AABB);
		}
	 }

	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockRayTraceResult hit) {
		ItemStack itemStack = player.getItemInHand(hand);
		if (world.isClientSide) {
			return (VanillaCompat.COLOR_ITEMS.containsKey(itemStack.getItem())) ? ActionResultType.SUCCESS
					: super.use(state, world, pos, player, hand, hit);
		}
		
		if (VanillaCompat.COLOR_ITEMS.containsKey(itemStack.getItem())) {
			world.setBlock(pos, state.setValue(COLOR, VanillaCompat.COLOR_ITEMS.get(itemStack.getItem())), 3);
			return ActionResultType.CONSUME;
		}
		return super.use(state, world, pos, player, hand, hit);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		// Drop only when the lower part's broken, or we'll get 2 dropped items...
		return state.getValue(HALF) == DoubleBlockHalf.LOWER ? Collections.singletonList(new ItemStack(this.asItem())) : Collections.emptyList();
	}
}