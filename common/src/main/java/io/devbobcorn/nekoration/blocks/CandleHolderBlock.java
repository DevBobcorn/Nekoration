package io.devbobcorn.nekoration.blocks;

import java.util.Collections;
import java.util.List;

import io.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import io.devbobcorn.nekoration.blocks.states.CandleColorType;
import io.devbobcorn.nekoration.blocks.states.CandleFlameType;
import io.devbobcorn.nekoration.common.VanillaCompat;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Candle holder with plain or dyed candles (color in block state) and a
 * configurable flame, lit with flame items.
 */
public class CandleHolderBlock extends Block {
    public static final EnumProperty<CandleColorType> COLOR = EnumProperty.create("color", CandleColorType.class);
    public static final EnumProperty<CandleFlameType> FLAME = EnumProperty.create("flame", CandleFlameType.class);
    private static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public CandleHolderBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, CandleColorType.UNCOLORED)
                .setValue(FLAME, CandleFlameType.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(COLOR, FLAME);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        ItemStack stack = context.getItemInHand();
        if (stack.getItem() instanceof DyeableBlockItem && DyeableBlockItem.hasColor(stack)) {
            return this.defaultBlockState().setValue(COLOR, CandleColorType.of(DyeableBlockItem.getColor(stack)));
        }
        return this.defaultBlockState();
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return VanillaCompat.FLAME_ITEMS.containsKey(stack.getItem())
                    || VanillaCompat.COLOR_ITEMS.containsKey(stack.getItem()) ? ItemInteractionResult.SUCCESS
                            : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        CandleFlameType flame = VanillaCompat.FLAME_ITEMS.get(stack.getItem());
        if (flame != null) {
            level.setBlock(pos, state.setValue(FLAME, flame), Block.UPDATE_ALL);
            return ItemInteractionResult.CONSUME;
        }
        Integer colorIndex = VanillaCompat.COLOR_ITEMS.get(stack.getItem());
        if (colorIndex != null) {
            level.setBlock(pos, state.setValue(COLOR, CandleColorType
                    .of(EnumNekoColor.getColorEnumFromId(colorIndex.byteValue()))), Block.UPDATE_ALL);
            return ItemInteractionResult.CONSUME;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return itemWithColor(new ItemStack(this.asItem()), state);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return Collections.singletonList(itemWithColor(new ItemStack(this.asItem()), state));
    }

    private static ItemStack itemWithColor(ItemStack stack, BlockState state) {
        CandleColorType color = state.getValue(COLOR);
        if (!color.isUncolored()) {
            DyeableBlockItem.setColor(stack, color.getDyeColor());
        }
        return stack;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        CandleFlameType flame = state.getValue(FLAME);
        if (!flame.isLit()) {
            return;
        }
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 1.2D;
        double z = pos.getZ() + 0.5D;
        double h = pos.getY() + 1.0D;
        double r = 0.38D;

        SimpleParticleType type = switch (flame) {
            case FLAME -> ParticleTypes.FLAME;
            case SOUL_FLAME -> ParticleTypes.SOUL_FIRE_FLAME;
            default -> ParticleTypes.FIREWORK;
        };

        level.addParticle(type, x, y, z, 0.0D, 0.0D, 0.0D);
        level.addParticle(type, x + r, h, z, 0.0D, 0.0D, 0.0D);
        level.addParticle(type, x - r, h, z, 0.0D, 0.0D, 0.0D);
        level.addParticle(type, x, h, z + r, 0.0D, 0.0D, 0.0D);
        level.addParticle(type, x, h, z - r, 0.0D, 0.0D, 0.0D);
    }
}
