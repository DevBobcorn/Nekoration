package io.devbobcorn.nekoration.blocks;

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

import io.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;
import io.devbobcorn.nekoration.items.PaletteItem;
import io.devbobcorn.nekoration.items.TweakItem;
import io.devbobcorn.nekoration.registry.ModItems;
import io.devbobcorn.nekoration.xplat.NekoPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * An invisible block whose block entity displays up to sixteen transformed and
 * tinted block states. Ported from the v1 Custom Block.
 */
public class CustomBlock extends Block implements EntityBlock {
    public static final IntegerProperty LIGHT = BlockStateProperties.LEVEL;
    public static final BooleanProperty AMBIENT_OCCLUSION = BooleanProperty.create("ambient_occlusion");

    public CustomBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(AMBIENT_OCCLUSION, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIGHT, AMBIENT_OCCLUSION);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof CustomBlockEntity customBlock)
                || !player.getMainHandItem().isEmpty()) {
            return InteractionResult.PASS;
        }
        if (player.isSecondaryUseActive()) {
            if (!level.isClientSide) {
                customBlock.toggleShowHint();
                customBlock.markUpdated();
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            NekoPlatform.openCustomBlockMenu(serverPlayer, customBlock);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof CustomBlockEntity customBlock)
                || stack.getItem() instanceof TweakItem) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (stack.is(ModItems.PALETTE.get())) {
            if (!level.isClientSide) {
                customBlock.tintActive(PaletteItem.getActiveColor(stack));
                customBlock.markUpdated();
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (stack.is(ModItems.PAW.get())) {
            if (!level.isClientSide) {
                customBlock.clearActiveTint();
                customBlock.markUpdated();
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (stack.getItem() instanceof AxeItem) {
            level.setBlock(pos, state.cycle(LIGHT), Block.UPDATE_ALL);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (stack.getItem() instanceof PickaxeItem) {
            level.setBlock(pos, state.cycle(AMBIENT_OCCLUSION), Block.UPDATE_ALL);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (stack.getItem() instanceof BlockItem blockItem && !(blockItem.getBlock() instanceof CustomBlock)) {
            if (!level.isClientSide) {
                BlockState displayState = entryState(blockItem,
                        new BlockPlaceContext(player, hand, stack, hitResult));
                if (customBlock.canAddEntry(displayState, level, pos, CollisionContext.of(player))) {
                    customBlock.addEntry(displayState);
                    customBlock.markUpdated();
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    /** Handles block items used on a neighbor surface whose placement position is a Custom Block. */
    public static InteractionResult addEntryFromPlacement(Player player, Level level, InteractionHand hand,
            BlockHitResult hitResult) {
        if (player.isSpectator() || level.getBlockState(hitResult.getBlockPos()).getBlock() instanceof CustomBlock) {
            return InteractionResult.PASS;
        }
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof BlockItem blockItem) || blockItem.getBlock() instanceof CustomBlock) {
            return InteractionResult.PASS;
        }
        BlockPlaceContext context = new BlockPlaceContext(player, hand, stack, hitResult);
        BlockPos pos = context.getClickedPos();
        if (!(level.getBlockEntity(pos) instanceof CustomBlockEntity customBlock)) {
            return InteractionResult.PASS;
        }
        BlockState displayState = entryState(blockItem, context);
        if (!level.isClientSide && customBlock.canAddEntry(displayState, level, pos, CollisionContext.of(player))) {
            customBlock.addEntry(displayState);
            customBlock.markUpdated();
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static BlockState entryState(BlockItem blockItem, BlockPlaceContext context) {
        BlockState displayState = blockItem.getBlock().getStateForPlacement(context);
        return displayState == null ? blockItem.getBlock().defaultBlockState() : displayState;
    }

    /**
     * Peels the pointed entry off the Custom Block. Returns {@code false} to cancel
     * the break while entries remain, and {@code true} to let the last one break
     * the block itself.
     */
    public static boolean beforeBlockBreak(Level level, Player player, BlockPos pos, BlockState state,
            @Nullable BlockEntity blockEntity) {
        if (!(blockEntity instanceof CustomBlockEntity customBlock)) {
            return true;
        }
        int pointed = customBlock.pointedEntry(level, pos, player);
        CustomBlockEntity.CustomEntry entry = customBlock.entry(pointed);
        if (entry == null) {
            return true;
        }
        if (!level.isClientSide && !player.isCreative()) {
            ItemStack drop = new ItemStack(entry.displayState().getBlock());
            if (!drop.isEmpty()) {
                Block.popResource(level, pos, drop);
            }
        }
        if (customBlock.entryCount() <= 1) {
            return true;
        }
        if (!level.isClientSide) {
            customBlock.removeEntry(pointed);
            customBlock.markUpdated();
            level.scheduleTick(pos, state.getBlock(), 2);
        }
        return false;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof CustomBlockEntity customBlock) {
            customBlock.markUpdated();
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return mergedEntryShapes(level, pos, displayState -> displayState.getShape(level, pos, context));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
            CollisionContext context) {
        return Shapes.block();
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return mergedEntryShapes(level, pos, displayState -> displayState.getInteractionShape(level, pos));
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(AMBIENT_OCCLUSION) ? 0.2F : 1.0F;
    }

    private static VoxelShape mergedEntryShapes(BlockGetter level, BlockPos pos, Function<BlockState, VoxelShape> shape) {
        if (!(level.getBlockEntity(pos) instanceof CustomBlockEntity customBlock)) {
            return Shapes.block();
        }
        VoxelShape merged = Shapes.empty();
        boolean empty = true;
        for (int index = 0; index < CustomBlockEntity.MAX_ENTRIES; index++) {
            CustomBlockEntity.CustomEntry entry = customBlock.entry(index);
            if (entry == null) {
                continue;
            }
            empty = false;
            merged = Shapes.or(merged, shape.apply(entry.displayState()));
        }
        return empty ? Shapes.block() : merged;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return List.of(new ItemStack(asItem()));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CustomBlockEntity(pos, state);
    }
}
