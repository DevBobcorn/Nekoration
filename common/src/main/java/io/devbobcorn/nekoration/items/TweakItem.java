package io.devbobcorn.nekoration.items;

import io.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * Paw tool that transforms the active entry of a Custom Block. Using the item
 * while sneaking applies the opposite transformation.
 */
public class TweakItem extends Item {
    public enum Aspect {
        PosX,
        PosY,
        PosZ,
        Rotation
    }

    private final Aspect aspect;
    private final int amount;

    public TweakItem(Properties properties, Aspect aspect, int amount) {
        super(properties);
        this.aspect = aspect;
        this.amount = amount;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!(level.getBlockEntity(pos) instanceof CustomBlockEntity customBlock)
                || customBlock.activeEntry() == null) {
            return InteractionResult.PASS;
        }
        boolean invert = context.getPlayer() != null && context.getPlayer().isSecondaryUseActive();
        int delta = invert ? -amount : amount;
        if (!level.isClientSide) {
            switch (aspect) {
                case PosX -> customBlock.moveActive(0, delta);
                case PosY -> customBlock.moveActive(1, delta);
                case PosZ -> customBlock.moveActive(2, delta);
                case Rotation -> customBlock.rotateActive(delta);
            }
            customBlock.markUpdated();
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
