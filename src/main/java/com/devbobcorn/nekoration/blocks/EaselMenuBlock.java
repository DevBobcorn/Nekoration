package com.devbobcorn.nekoration.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.blocks.entities.EaselMenuBlockEntity;
import com.devbobcorn.nekoration.items.DyeableWoodenBlockItem;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class EaselMenuBlock extends DyeableHorizontalBlock implements EntityBlock {
	private static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
	public final boolean white;

    public EaselMenuBlock(Properties settings, boolean w) {
        super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 2));
		white = w;
    }
    
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new EaselMenuBlockEntity(white, pos, state);
	}

    // Called just after the player places a block.
	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);
		// Set default text colors...
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof EaselMenuBlockEntity) { // prevent a crash if not the right type, or is null
			EaselMenuBlockEntity te = (EaselMenuBlockEntity) tileEntity;
			if (white){
				final DyeColor[] colors = { DyeColor.PURPLE, DyeColor.PINK, DyeColor.ORANGE, DyeColor.YELLOW, DyeColor.LIME, DyeColor.LIGHT_BLUE, DyeColor.CYAN, DyeColor.BLUE };
				te.setColors(colors);
			} else {
				final DyeColor[] colors = { DyeColor.WHITE, DyeColor.WHITE, DyeColor.WHITE, DyeColor.WHITE, DyeColor.WHITE, DyeColor.WHITE, DyeColor.WHITE, DyeColor.WHITE };
				te.setColors(colors);
			}
            return;
		}
		LOGGER.error("Tile Entity NOT Found!");
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult rayTraceResult) {
			if(!world.isClientSide()) {
				if(world.getBlockEntity(pos) instanceof EaselMenuBlockEntity blockEntity) {
					NetworkHooks.openGui((ServerPlayer) player, blockEntity, pos);
				}
			}
		return InteractionResult.SUCCESS;
	}

	// This is where you can do something when the block is broken. In this case
	// drop the inventory's contents
	// Code is copied directly from vanilla eg ChestBlock, CampfireBlock
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
            if(world.getBlockEntity(pos) instanceof Container container)
            {
                Containers.dropContents(world, pos, container);
                world.updateNeighbourForOutputSignal(pos, this);
            }
		}
	}

	@Nullable
	public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
	   BlockEntity blockEntity = world.getBlockEntity(pos);
	   return blockEntity instanceof MenuProvider ? (MenuProvider)blockEntity : null;
	}
	
	@Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull BlockState state, HitResult target, @Nonnull BlockGetter world, @Nonnull BlockPos pos, Player player) {
		ItemStack stack = new ItemStack(this.asItem());
		DyeableWoodenBlockItem.setColor(stack, NekoColors.EnumWoodenColor.getColorEnumFromID(state.getValue(COLOR).byteValue()));
        return stack;
    }
}
