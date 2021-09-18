package com.devbobcorn.nekoration.blocks;

import java.util.Collections;
import java.util.List;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;
import com.devbobcorn.nekoration.items.ModItems;
import com.devbobcorn.nekoration.items.PaletteItem;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;

public class CustomBlock extends Block implements EntityBlock {
	public static final IntegerProperty MODEL = BlockStateProperties.LEVEL;

	public CustomBlock(Properties settings) {
		super(settings);
	}
	
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> s) {
		s.add(MODEL);
	}
	
	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
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

	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
	BlockHitResult hit) {
		ItemStack itemStack = player.getItemInHand(hand);
		Item item = itemStack.getItem();
		CustomBlockEntity te = (CustomBlockEntity)world.getBlockEntity(pos);

		// Both Sides Changes...
		if (item == ModItems.PAW_15.get()){
			te.dir = ((byte)((te.dir + 1) % 24));
			return InteractionResult.CONSUME;
		} else if (item == ModItems.PAW_90.get()){
			te.dir = ((byte)((te.dir + 6) % 24));
			return InteractionResult.CONSUME;
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
			CompoundTag nbt = itemStack.getTag();
			byte a = nbt.getByte(PaletteItem.ACTIVE);
			int[] c = nbt.getIntArray(PaletteItem.COLORS);
			// So c[a] is the color we need...
			te.color[0] = NekoColors.getRed(c[a]);
			te.color[1] = NekoColors.getGreen(c[a]);
			te.color[2] = NekoColors.getBlue(c[a]);
		} else if (item instanceof BlockItem){
			if (((BlockItem)item).getBlock() instanceof CustomBlock)
				return InteractionResult.PASS;
			te.model = 16;
			BlockState newState = ((BlockItem)item).getBlock().getStateForPlacement(new BlockPlaceContext(player, hand, itemStack, hit));
			if (te.displayBlock == newState)
				return InteractionResult.PASS;
			else {
				te.displayBlock = newState;
				ItemStack newStack = itemStack.copy();
				newStack.setCount(1);
				te.containItem = newStack;
			}
		} else return InteractionResult.PASS;
		return InteractionResult.sidedSuccess(world.isClientSide);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		ItemStack stack = new ItemStack(this.asItem());
		return Collections.singletonList(stack);
	}

	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CustomBlockEntity(pos, state);
	}
}
