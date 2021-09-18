package com.devbobcorn.nekoration.items;

import javax.annotation.Nonnull;

import com.devbobcorn.nekoration.client.event.PhotoEvents;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, @Nonnull InteractionHand hand) {
        
        ItemStack stack = player.getItemInHand(hand);
        if (world.isClientSide) {
            PhotoEvents.shouldTakePhoto = true;
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
	public Component getName(ItemStack stack) {
		return CaseTweak.getTweaked(new TranslatableComponent(this.getDescriptionId(stack)));
	}
}
