package com.devbobcorn.nekoration.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.devbobcorn.nekoration.blocks.entities.EaselMenuBlockEntity;
import com.devbobcorn.nekoration.items.DyeableWoodenBlockItem;
import com.devbobcorn.nekoration.NekoColors;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class EaselMenuBlock extends DyeableHorizontalBlock {
	private static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public EaselMenuBlock(Properties settings) {
        super(settings);
		this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, 2));
    }
    
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        return SHAPE;
    }
	
    @Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	// Called when the block is placed or loaded client side to get the tile entity
	// for the block
	// Should return a new instance of the tile entity for the block
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new EaselMenuBlockEntity();
	}

    // Called just after the player places a block.
	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);

		TileEntity tileentity = worldIn.getBlockEntity(pos);
		if (tileentity instanceof EaselMenuBlockEntity) { // prevent a crash if not the right type, or is null
            return;
		}
        //LOGGER.error("Tile Entity NOT Found!");
		System.out.println("Tile Entity NOT Found!");
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand hand, BlockRayTraceResult rayTraceResult) {
		if (worldIn.isClientSide())
			return ActionResultType.SUCCESS; // on client side, don't do anything
		INamedContainerProvider namedContainerProvider = this.getMenuProvider(state, worldIn, pos);
		if (namedContainerProvider != null) {
			if (!(player instanceof ServerPlayerEntity))
				return ActionResultType.FAIL; // should always be true, but just in case...
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
			NetworkHooks.openGui(serverPlayerEntity, namedContainerProvider, (packetBuffer) -> {
				// Prepare Data for EaselMenuContainer: createContainerClientSide(), which will then be used to initialize the screen with old data(old texts and colors)
				EaselMenuBlockEntity te = (EaselMenuBlockEntity) worldIn.getBlockEntity(pos);
				packetBuffer.writeBlockPos(pos);
				for (int i = 0;i < 8;i++)
					packetBuffer.writeComponent(te.getMessage(i));
				for (int i = 0;i < 8;i++)
					packetBuffer.writeEnum(te.getColor()[i]);
			});
		}
		return ActionResultType.SUCCESS;
	}

	// This is where you can do something when the block is broken. In this case
	// drop the inventory's contents
	// Code is copied directly from vanilla eg ChestBlock, CampfireBlock
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity tileentity = world.getBlockEntity(pos);
			if (tileentity instanceof EaselMenuBlockEntity) {
				EaselMenuBlockEntity tileEntityInventoryBasic = (EaselMenuBlockEntity) tileentity;
				tileEntityInventoryBasic.dropAllContents(world, pos);
			}
			world.removeBlockEntity(pos);
		}
	}

	@Nullable
	public INamedContainerProvider getMenuProvider(BlockState state, World world, BlockPos pos) {
	   TileEntity tileentity = world.getBlockEntity(pos);
	   return tileentity instanceof INamedContainerProvider ? (INamedContainerProvider)tileentity : null;
	}

	@Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull BlockState state, RayTraceResult target, @Nonnull IBlockReader world, @Nonnull BlockPos pos, PlayerEntity player) {
		ItemStack stack = new ItemStack(this.asItem());
		DyeableWoodenBlockItem.setColor(stack, NekoColors.EnumWoodenColor.getColorEnumFromID(state.getValue(COLOR).byteValue()));
        return stack;
    }
}
