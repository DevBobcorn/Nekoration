package com.devbobcorn.nekoration.blocks;

import com.devbobcorn.nekoration.blockentities.EaselMenuBlockEnity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class EaselMenuBlock extends DyeableBlock {
    public EaselMenuBlock(Properties settings) {
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
		return new EaselMenuBlockEnity();
	}

    // Called just after the player places a block.
	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);

        LOGGER.debug("Easel Menu Placed!");

		TileEntity tileentity = worldIn.getBlockEntity(pos);
		if (tileentity instanceof EaselMenuBlockEnity) { // prevent a crash if not the right type, or is null
			LOGGER.info(tileentity);
            return;
		}

        LOGGER.error("Tile Entity NOT Found!");
	}
}
