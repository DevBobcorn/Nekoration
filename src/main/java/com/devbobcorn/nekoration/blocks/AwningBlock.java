package com.devbobcorn.nekoration.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AwningBlock extends DyeableHorizontalBlock {
    public static final VoxelShape SHAPE = Block.box(0.1D, 0.1D, 0.1D, 15.9D, 15.9D, 15.9D);

    public static final BooleanProperty IS_END = BlockStateProperties.BOTTOM;

	public AwningBlock(Properties settings) {
		super(settings);
	}

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> s) {
        s.add(COLOR, FACING, IS_END);
    }

	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
			BlockHitResult hit) {
		ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getItem() instanceof AxeItem){
            if (world.isClientSide) {
                return InteractionResult.SUCCESS;
            }
            
            world.setBlock(pos, state.cycle(IS_END), 3);
            return InteractionResult.CONSUME;
        } else super.use(state, world, pos, player, hand, hit);
        return InteractionResult.PASS;
	}

	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
		return SHAPE;
	}
}
