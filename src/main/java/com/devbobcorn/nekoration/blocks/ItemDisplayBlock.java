package com.devbobcorn.nekoration.blocks;

import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import com.devbobcorn.nekoration.blocks.entities.ItemDisplayBlockEntity;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ItemDisplayBlock extends DyeableHorizontalWoodenBlock implements EntityBlock {
	protected static Double thickness = 9.0D;

	private static final Map<Direction, VoxelShape> AABBS = Maps
			.newEnumMap(ImmutableMap.of(
					Direction.NORTH, Block.box(0.0D, 0.0D, 16.0D - thickness, 16.0D, 16.0D, 16.0D),
					Direction.SOUTH, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, thickness), 
					Direction.EAST, Block.box(0.0D, 0.0D, 0.0D, thickness, 16.0D, 16.0D),
					Direction.WEST, Block.box(16.0D - thickness, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)));

	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

	public ItemDisplayBlock(BlockBehaviour.Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, Boolean.valueOf(false)));
	}

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> s) {
        s.add(COLOR, FACING, OPEN);
    }

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ItemDisplayBlockEntity(pos, state);
	}

	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
		return AABBS.get(state.getValue(FACING));
	}

	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		if (world.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			BlockEntity tileentity = world.getBlockEntity(pos);
			if (tileentity instanceof ItemDisplayBlockEntity) {
				player.openMenu((ItemDisplayBlockEntity) tileentity);
				PiglinAi.angerNearbyPiglins(player, true);
			}
			return InteractionResult.CONSUME;
		}
	}

	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockEntity tileentity = world.getBlockEntity(pos);
			if (tileentity instanceof Container) {
				Containers.dropContents(world, pos, (Container) tileentity);
				world.updateNeighbourForOutputSignal(pos, this);
			}
			world.removeBlockEntity(pos);
		}
	}

	public void tick(BlockState state, ServerLevel world, BlockPos pos, Random rand) {
		BlockEntity tileentity = world.getBlockEntity(pos);
		if (tileentity instanceof ItemDisplayBlockEntity) {
			((ItemDisplayBlockEntity) tileentity).recheckOpen();
		}
	}

	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		if (stack.hasCustomHoverName()) {
			BlockEntity tileentity = world.getBlockEntity(pos);
			if (tileentity instanceof ItemDisplayBlockEntity) {
				((ItemDisplayBlockEntity) tileentity).setCustomName(stack.getHoverName());
			}
		}
	}

	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
		return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos));
	}

	@Nullable
	public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
		BlockEntity tileentity = world.getBlockEntity(pos);
		return tileentity instanceof MenuProvider ? (MenuProvider) tileentity : null;
	}
}