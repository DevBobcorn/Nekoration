package io.devbobcorn.nekoration.blocks;

import java.util.List;

import io.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;
import io.devbobcorn.nekoration.items.PaletteItem;
import io.devbobcorn.nekoration.items.TweakItem;
import io.devbobcorn.nekoration.registry.ModItems;
import io.devbobcorn.nekoration.xplat.NekoPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;

/**
 * An invisible block whose block entity displays up to sixteen transformed and
 * tinted block states. Ported from the v1 Custom Block.
 */
public class CustomBlock extends Block implements EntityBlock {
    public static final IntegerProperty LIGHT = BlockStateProperties.LEVEL;

    public CustomBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIGHT);
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
        if (stack.getItem() instanceof BlockItem blockItem && !(blockItem.getBlock() instanceof CustomBlock)) {
            if (!level.isClientSide) {
                BlockState displayState = blockItem.getBlock()
                        .getStateForPlacement(new BlockPlaceContext(player, hand, stack, hitResult));
                if (displayState == null) {
                    displayState = blockItem.getBlock().defaultBlockState();
                }
                customBlock.addEntry(displayState);
                customBlock.markUpdated();
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
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
