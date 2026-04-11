package io.devbobcorn.nekoration.blocks;

import java.util.Collections;
import java.util.List;

import io.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import io.devbobcorn.nekoration.common.VanillaCompat;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Block with plaster / fill color in block state as {@link EnumNekoColor} under the {@code color} property.
 */
public class DyeableBlock extends Block {
    public static final EnumProperty<EnumNekoColor> COLOR = EnumProperty.create("color", EnumNekoColor.class);

    public DyeableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, EnumNekoColor.WHITE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(COLOR);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hitResult) {
        boolean canColor = VanillaCompat.RAW_COLOR_ITEMS.containsKey(stack.getItem())
                || VanillaCompat.COLOR_ITEMS.containsKey(stack.getItem());
        if (!canColor) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }
        Integer raw = VanillaCompat.RAW_COLOR_ITEMS.get(stack.getItem());
        int colorIndex = raw != null ? raw : VanillaCompat.COLOR_ITEMS.get(stack.getItem());
        EnumNekoColor next = EnumNekoColor.getColorEnumFromId((byte) colorIndex);
        level.setBlock(pos, state.setValue(COLOR, next), Block.UPDATE_ALL);
        return ItemInteractionResult.CONSUME;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        ItemStack stack = context.getItemInHand();
        if (stack.getItem() instanceof DyeableBlockItem) {
            return this.defaultBlockState().setValue(COLOR, DyeableBlockItem.getColor(stack));
        }
        return this.defaultBlockState();
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        ItemStack stack = new ItemStack(this.asItem());
        DyeableBlockItem.setColor(stack, state.getValue(COLOR));
        return stack;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        ItemStack stack = new ItemStack(this.asItem());
        DyeableBlockItem.setColor(stack, state.getValue(COLOR));
        return Collections.singletonList(stack);
    }
}
