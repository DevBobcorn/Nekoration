package com.devbobcorn.nekoration.items;

import com.devbobcorn.nekoration.blocks.ModBlocks;
import com.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TweakItem extends Item {
    public enum Aspect {
        PosX,
        PosY,
        PosZ,
        Rotation
    }

    public Aspect aspect;
    public int amount;

    public TweakItem(Properties settings, Aspect aspect, int amount) {
        super(settings);
        this.aspect = aspect;
        this.amount = amount;
    }

    public InteractionResult useOn(UseOnContext ctx) {
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() != ModBlocks.CUSTOM.get())
            return InteractionResult.PASS;
        
        // Get block entity...
        CustomBlockEntity te = (CustomBlockEntity)world.getBlockEntity(pos);
        
        boolean invert = ctx.getPlayer().isSecondaryUseActive();
        
        // Do the tweak...
        switch (aspect) {
            case PosX:
                te.offset[0] += invert ? -amount : amount;
                break;
            case PosY:
                te.offset[1] += invert ? -amount : amount;
                break;
            case PosZ:
                te.offset[2] += invert ? -amount : amount;
                break;
            case Rotation:
                if (invert) te.dir = ((byte)((te.dir + 24 - amount) % 24));
                else te.dir = ((byte)((te.dir + amount) % 24));
                break;
        }

        te.setChanged();
        return InteractionResult.sidedSuccess(world.isClientSide);
    }
    
}
