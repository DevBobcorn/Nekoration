package com.devbobcorn.nekoration.blocks;

import javax.annotation.Nonnull;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.items.DyeableWoodenBlockItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WindowBlock extends DyeableVerticalConnectBlock {
    public WindowBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 2));
    }
    
    public WindowBlock(Properties settings, ConnectionType tp, boolean co) {
		super(settings, tp, co);
        this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 2));
	}

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> s) {
		s.add(CONNECTION, COLOR);
	}

    @OnlyIn(Dist.CLIENT)
	public boolean skipRendering(BlockState state, BlockState from, Direction dir) {
		return (from.getBlock() instanceof WindowBlock);
	}

    @OnlyIn(Dist.CLIENT)
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
       return 0.5F;
    }
 
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
       return true;
    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull BlockState state, HitResult target, @Nonnull BlockGetter world, @Nonnull BlockPos pos, Player player) {
		ItemStack stack = new ItemStack(this.asItem());
		DyeableWoodenBlockItem.setColor(stack, NekoColors.EnumWoodenColor.getColorEnumFromID(state.getValue(COLOR).byteValue()));
        return stack;
    }
}
