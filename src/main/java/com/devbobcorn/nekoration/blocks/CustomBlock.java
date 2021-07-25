package com.devbobcorn.nekoration.blocks;

import com.devbobcorn.nekoration.items.DyeableBlockItem;
import com.devbobcorn.nekoration.items.ModItems;
import com.devbobcorn.nekoration.items.PaletteItem;

import java.util.Collections;
import java.util.List;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.nbt.CompoundNBT;
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
		Item item = itemStack.getItem();
		CustomBlockEntity te = (CustomBlockEntity)world.getBlockEntity(pos);

		// Both Sides Changes...
		if (item == ModItems.PAW_15.get()){
			te.dir = ((byte)((te.dir + 1) % 24));
			return ActionResultType.CONSUME;
		} else if (item == ModItems.PAW_90.get()){
			te.dir = ((byte)((te.dir + 6) % 24));
			return ActionResultType.CONSUME;
		} else if (item == ModItems.PAW_LEFT.get()){
			te.offset[0]--;
		} else if (item == ModItems.PAW_RIGHT.get()){
			te.offset[0]++;
		} else if (item == ModItems.PAW_UP.get()){
			te.offset[1]++;
		} else if (item == ModItems.PAW_DOWN.get()){
			te.offset[1]--;
		} else if (item == ModItems.PAW_NEAR.get()){
			te.offset[2]++;
		} else if (item == ModItems.PAW_FAR.get()){
			te.offset[2]--;
		} else if (item == ModItems.PALETTE.get()){
			// Dye me!
			CompoundNBT nbt = itemStack.getTag();
			byte a = nbt.getByte(PaletteItem.ACTIVE);
			int[] c = nbt.getIntArray(PaletteItem.COLORS);
			// So c[a] is the color we need...
			te.color[0] = NekoColors.getRed(c[a]);
			te.color[1] = NekoColors.getGreen(c[a]);
			te.color[2] = NekoColors.getBlue(c[a]);
			// ...
			System.out.println("Dye Custom Block!");
		} else if (item instanceof BlockItem){
			//System.out.println("Block Item.");
			if (((BlockItem)item).getBlock() instanceof CustomBlock)
				return ActionResultType.PASS;
			te.model = 16;
			BlockState newState = ((BlockItem)item).getBlock().getStateForPlacement(new BlockItemUseContext(player, hand, itemStack, hit));
			if (te.displayBlock == newState)
				return ActionResultType.PASS;
			else {
				te.displayBlock = newState;
				ItemStack newStack = itemStack.copy();
				newStack.setCount(1);
				te.containItem = newStack;
			}
		} else return ActionResultType.PASS;

		return ActionResultType.sidedSuccess(world.isClientSide);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		ItemStack stack = new ItemStack(this.asItem());
		return Collections.singletonList(stack);
	}
}
