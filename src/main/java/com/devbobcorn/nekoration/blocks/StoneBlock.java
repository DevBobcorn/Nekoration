package com.devbobcorn.nekoration.blocks;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.items.DyeableStoneBlockItem;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;

public class StoneBlock extends DyeableBlock{
    public StoneBlock(Properties settings) {
        super(settings);
    }
    
    @Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull BlockState state, RayTraceResult target, @Nonnull IBlockReader world, @Nonnull BlockPos pos, PlayerEntity player) {
		ItemStack stack = new ItemStack(this.asItem());
		DyeableStoneBlockItem.setColor(stack, NekoColors.EnumStoneColor.getColorEnumFromID(state.getValue(COLOR).byteValue()));
        return stack;
    }

	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		ItemStack stack = new ItemStack(this.asItem());
		DyeableStoneBlockItem.setColor(stack, NekoColors.EnumStoneColor.getColorEnumFromID(state.getValue(COLOR).byteValue()));
		return Collections.singletonList(stack);
	}
}
