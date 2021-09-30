package com.devbobcorn.nekoration.blocks;

import com.devbobcorn.nekoration.common.VanillaCompat;
import com.devbobcorn.nekoration.entities.SeatEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DyeableChairBlock extends DyeableHorizontalWoodenBlock {
    public final double SideSpace = 1.0D;
    public final double SeatHeight;
    public final double BackRestHeight;
    public static final double BackRestThickness = 3.0D; // SideSpace included...

    public final VoxelShape SeatShape;
    public final VoxelShape[] BackRestShapes = new VoxelShape[4];
    public final VoxelShape[] ChairShapes = new VoxelShape[4];

    protected DyeableChairBlock(Properties settings) {
        this(settings, 8, 20);
    }

    protected DyeableChairBlock(Properties settings, int sh, int bsh) {
        super(settings);
        SeatHeight = sh;
        BackRestHeight = bsh;
        SeatShape = Block.box(SideSpace, 0.0D, SideSpace, 16.0D - SideSpace, SeatHeight, 16.0D - SideSpace);
        BackRestShapes[1] = Block.box(16.0D - BackRestThickness, 0.0D, SideSpace, 16.0D - SideSpace, BackRestHeight, 16.0D - SideSpace); // West  -X
        BackRestShapes[3] = Block.box(SideSpace, 0.0D, SideSpace, BackRestThickness, BackRestHeight, 16.0D - SideSpace);                 // East  +X
        BackRestShapes[0] = Block.box(SideSpace, 0.0D, SideSpace, 16.0D - SideSpace, BackRestHeight, BackRestThickness);                 // South +Z
        BackRestShapes[2] = Block.box(SideSpace, 0.0D, 16.0D - BackRestThickness, 16.0D - SideSpace, BackRestHeight, 16.0D - SideSpace); // North -Z
        for (int i = 0;i < 4;i++)
            ChairShapes[i] = Shapes.or(BackRestShapes[i], SeatShape);
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return ChairShapes[state.getValue(FACING).get2DDataValue()];
    }
    
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> s) {
        s.add(COLOR, FACING);
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (VanillaCompat.COLOR_ITEMS.containsKey(itemStack.getItem())) { // Then tint
            world.setBlock(pos, state.setValue(COLOR, VanillaCompat.COLOR_ITEMS.get(itemStack.getItem())), 3);
            return InteractionResult.SUCCESS;
        } else { // Then sit
            return SeatEntity.create(world, pos, (SeatHeight - 4.0D) / 16.0D, player);
        }
    }
}
