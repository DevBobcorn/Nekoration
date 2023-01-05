package com.devbobcorn.nekoration.items;

import com.devbobcorn.nekoration.client.event.PhotoEvents;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CameraItem extends Item {
    

    public CameraItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        
        ItemStack stack = player.getItemInHand(hand);
        if (world.isClientSide) {
            PhotoEvents.shouldTakePhoto = true;
        }
        return InteractionResultHolder.success(stack);
    }
}
