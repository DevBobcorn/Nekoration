package com.devbobcorn.nekoration.items;

import com.devbobcorn.nekoration.blocks.ModBlocks;
import com.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;
import com.devbobcorn.nekoration.network.ModPacketHandler;
import com.devbobcorn.nekoration.network.S2CUpdateCustomBlockData;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;

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

    @SuppressWarnings("resource")
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

        if (!te.getLevel().isClientSide) { // Tell clients to update this block entity...
            te.setChanged(); // client setChanged() will be called when they receive the packet below
            
            final S2CUpdateCustomBlockData packet = new S2CUpdateCustomBlockData(te.getBlockPos(), te.dir, te.offset, te.retint, te.showHint, te.color, te.displayState);
            ModPacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
        }
        
        return InteractionResult.sidedSuccess(world.isClientSide);
    }
    
}
