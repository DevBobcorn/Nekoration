package com.devbobcorn.nekoration.blocks;

import com.devbobcorn.nekoration.blocks.entities.PhonographBlockEnity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class PhonographBlock extends DyeableBlock {
	public PhonographBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 2));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	// Called when the block is placed or loaded client side to get the tile entity
	// for the block
	// Should return a new instance of the tile entity for the block
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new PhonographBlockEnity();
	}

	// Called just after the player places a block.
	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);

		TileEntity tileentity = worldIn.getBlockEntity(pos);
		if (tileentity instanceof PhonographBlockEnity) { // prevent a crash if not the right type, or is null
			// LOGGER.info(tileentity);
			return;
		}

		LOGGER.error("Tile Entity NOT Found!");
	}

	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockRayTraceResult hit) {
		PhonographBlockEnity te = (PhonographBlockEnity) world.getBlockEntity(pos);
		if (world.isClientSide()) {
			LOGGER.info("Client " + te.getMessage(0).getString() + te.getMessage(1).getString() + te.getMessage(2).getString() + te.getMessage(3).getString());
			return ActionResultType.SUCCESS;
		} else {
			LOGGER.info("Server " + te.getMessage(0).getString() + te.getMessage(1).getString() + te.getMessage(2).getString() + te.getMessage(3).getString());
			return ActionResultType.SUCCESS;
		}
	}
}
