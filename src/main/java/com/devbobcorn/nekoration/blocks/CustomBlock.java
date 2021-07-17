package com.devbobcorn.nekoration.blocks;

import com.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class CustomBlock extends Block {
	public static final IntegerProperty MODEL = BlockStateProperties.LEVEL;

	public CustomBlock(Properties settings) {
		super(settings);
	}
	
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> s) {
		s.add(MODEL);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new CustomBlockEntity();
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);

		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof CustomBlockEntity) { // prevent a crash if not the right type, or is null
			// LOGGER.info(tileentity);
			CustomBlockEntity te = (CustomBlockEntity)tileEntity;
			switch (placer.getDirection().getOpposite()){
				case SOUTH:  // +Z
				default:
					break;
				case EAST:   // +X
					te.dir = 6;
					break;
				case NORTH:  // -Z
					te.dir = 12;
					break;
				case WEST:   // -X
					te.dir = 18;
					break;
			}
			return;
		}
		LOGGER.error("Tile Entity NOT Found!");
	}

	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
	BlockRayTraceResult hit) {
			ItemStack itemStack = player.getItemInHand(hand);
			CustomBlockEntity te = (CustomBlockEntity)world.getBlockEntity(pos);

			// Both Sides Changes...
			if (itemStack.getItem() == Items.NETHER_STAR){
				te.dir = ((byte)((te.dir + 1) % 24));
				return ActionResultType.CONSUME;
			}
			if (itemStack.getItem() == Items.DIAMOND){
				te.dir = ((byte)((te.dir + 6) % 24));
				return ActionResultType.CONSUME;
			}
			if (itemStack.getItem() == Items.RED_DYE){
				te.offset[0]--;
			}
			if (itemStack.getItem() == Items.GREEN_DYE){
				te.offset[0]++;
			}
			if (itemStack.getItem() == Items.ORANGE_DYE){
				te.offset[1]++;
			}
			if (itemStack.getItem() == Items.LIME_DYE){
				te.offset[1]--;
			}
			if (itemStack.getItem() == Items.PINK_DYE){
				te.offset[2]++;
			}
			if (itemStack.getItem() == Items.LIGHT_BLUE_DYE){
				te.offset[2]--;
			}
			return ActionResultType.PASS;
		}
}
