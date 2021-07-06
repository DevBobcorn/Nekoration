package com.devbobcorn.nekoration.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class AwningBlock extends DyeableHorizontalBlock {
    public static final VoxelShape SHAPE = Block.box(0.1D, 0.1D, 0.1D, 15.9D, 15.9D, 15.9D);

    public static final BooleanProperty IS_END = BlockStateProperties.BOTTOM;

	public AwningBlock(Properties settings) {
		super(settings);
	}

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> s) {
        s.add(COLOR, FACING, IS_END);
    }

	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockRayTraceResult hit) {
		ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getItem() == Items.WOODEN_AXE){
            if (world.isClientSide) {
                return ActionResultType.SUCCESS;
            }
            
            world.setBlock(pos, state.cycle(IS_END), 3);
            //Nekoration.LOGGER.info(VanillaCompat.COLOR_ITEMS.get(itemStack.getItem()));
            return ActionResultType.CONSUME;
        } else super.use(state, world, pos, player, hand, hit);
        return ActionResultType.PASS;
	}

    public VoxelShape getInteractionShape(BlockState state, IBlockReader world, BlockPos pos) {
		return SHAPE;
	}

	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos) {
		return SHAPE;
	}

	public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_,
			ISelectionContext p_220053_4_) {
		return SHAPE;
	}
}
