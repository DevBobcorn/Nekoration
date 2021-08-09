package com.devbobcorn.nekoration.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.devbobcorn.nekoration.blocks.entities.EaselMenuBlockEntity;
import com.devbobcorn.nekoration.items.DyeableWoodenBlockItem;
import com.devbobcorn.nekoration.NekoColors;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class EaselMenuBlock extends DyeableHorizontalBlock {
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
	
	/*
    @Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	// Called when the block is placed or loaded client side to get the tile entity
	// for the block
	// Should return a new instance of the tile entity for the block
	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
		EaselMenuBlockEntity te = new EaselMenuBlockEntity(white);
		return te;
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
				te.setColor(colors);
			} else {
				final DyeColor[] colors = { DyeColor.WHITE, DyeColor.WHITE, DyeColor.WHITE, DyeColor.WHITE, DyeColor.WHITE, DyeColor.WHITE, DyeColor.WHITE, DyeColor.WHITE };
				te.setColor(colors);
			}
            return;
		}
		System.err.println("Tile Entity NOT Found!");
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult rayTraceResult) {
		if (world.isClientSide())
			return InteractionResult.SUCCESS; // on client side, don't do anything
		INamedContainerProvider namedContainerProvider = this.getMenuProvider(state, world, pos);
		if (namedContainerProvider != null) {
			if (!(player instanceof ServerPlayer))
				return InteractionResult.FAIL; // should always be true, but just in case...
			ServerPlayer serverPlayerEntity = (ServerPlayer) player;
			NetworkHooks.openGui(serverPlayerEntity, namedContainerProvider, (packetBuffer) -> {
				// Prepare Data for EaselMenuContainer: createContainerClientSide(), which will then be used to initialize the screen with old data(old texts and colors)
				EaselMenuBlockEntity te = (EaselMenuBlockEntity) world.getBlockEntity(pos);
				packetBuffer.writeBlockPos(pos);
				for (int i = 0;i < 8;i++)
					packetBuffer.writeComponent(te.getMessage(i));
				for (int i = 0;i < 8;i++)
					packetBuffer.writeEnum(te.getColor()[i]);
				packetBuffer.writeBoolean(white);
				packetBuffer.writeBoolean(te.getGlowing());
			});
		}
		return InteractionResult.SUCCESS;
	}

	// This is where you can do something when the block is broken. In this case
	// drop the inventory's contents
	// Code is copied directly from vanilla eg ChestBlock, CampfireBlock
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity tileentity = world.getBlockEntity(pos);
			if (tileentity instanceof EaselMenuBlockEntity) {
				EaselMenuBlockEntity tileEntityInventoryBasic = (EaselMenuBlockEntity) tileentity;
				tileEntityInventoryBasic.dropAllContents(world, pos);
			}
			world.removeBlockEntity(pos);
		}
	}

	@Nullable
	public INamedContainerProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
	   BlockEntity tileentity = world.getBlockEntity(pos);
	   return tileentity instanceof INamedContainerProvider ? (INamedContainerProvider)tileentity : null;
	}

	*/
	
	@Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull BlockState state, HitResult target, @Nonnull BlockGetter world, @Nonnull BlockPos pos, Player player) {
		ItemStack stack = new ItemStack(this.asItem());
		DyeableWoodenBlockItem.setColor(stack, NekoColors.EnumWoodenColor.getColorEnumFromID(state.getValue(COLOR).byteValue()));
        return stack;
    }
}
