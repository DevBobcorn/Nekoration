package com.devbobcorn.nekoration.blocks;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class ChairBlock extends HorizontalBlock {
    public final double SideSpace = 1.0D;
    public final double SeatHeight;
    public final double BackRestHeight;
    public static final double BackRestThickness = 3.0D; // SideSpace included...

    public final VoxelShape SeatShape;
    public final VoxelShape[] BackRestShapes = new VoxelShape[4];
    public final VoxelShape[] ChairShapes = new VoxelShape[4];

    protected ChairBlock(Properties settings) {
        this(settings, 8, 20);
    }

    protected ChairBlock(Properties settings, int sh, int bsh) {
        super(settings);
        SeatHeight = sh;
        BackRestHeight = bsh;
        SeatShape = Block.box(SideSpace, 0.0D, SideSpace, 16.0D - SideSpace, SeatHeight, 16.0D - SideSpace);
        BackRestShapes[1] = Block.box(16.0D - BackRestThickness, 0.0D, SideSpace, 16.0D - SideSpace, BackRestHeight, 16.0D - SideSpace); // West  -X
        BackRestShapes[3] = Block.box(SideSpace, 0.0D, SideSpace, BackRestThickness, BackRestHeight, 16.0D - SideSpace);                 // East  +X
        BackRestShapes[0] = Block.box(SideSpace, 0.0D, SideSpace, 16.0D - SideSpace, BackRestHeight, BackRestThickness);                 // South +Z
        BackRestShapes[2] = Block.box(SideSpace, 0.0D, 16.0D - BackRestThickness, 16.0D - SideSpace, BackRestHeight, 16.0D - SideSpace); // North -Z
        for (int i = 0;i < 4;i++)
            ChairShapes[i] = VoxelShapes.or(BackRestShapes[i], SeatShape);
    }

    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        return ChairShapes[state.getValue(FACING).get2DDataValue()];
    }
    
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> s) {
        s.add(FACING);
    }

    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		return Collections.singletonList(new ItemStack(this.asItem()));
	}
}
