package com.devbobcorn.nekoration.network;

import java.util.function.Supplier;

import com.devbobcorn.nekoration.items.ModItems;
import com.devbobcorn.nekoration.items.PaintingItem;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class C2SUpdatePaintingSize {
    public InteractionHand hand;
	public short width;
    public short height;

	public C2SUpdatePaintingSize(InteractionHand hand, short w, short h) {
        this.hand = hand;
		this.width = w;
        this.height = h;
	}

	public static void encode(final C2SUpdatePaintingSize msg, final FriendlyByteBuf packetBuffer) {
        packetBuffer.writeEnum(msg.hand);
        packetBuffer.writeShort(msg.width);
        packetBuffer.writeShort(msg.height);
	}

	public static C2SUpdatePaintingSize decode(final FriendlyByteBuf packetBuffer) {
        InteractionHand hand = packetBuffer.readEnum(InteractionHand.class);
        short w = packetBuffer.readShort();
        short h = packetBuffer.readShort();
		return new C2SUpdatePaintingSize(hand, w, h);
	}

	public static void handle(final C2SUpdatePaintingSize msg, final Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            //Handle this on SERVER SIDE...
            ServerPlayer player = contextSupplier.get().getSender();
            if (player != null) {
                // ...
                ItemStack updated = new ItemStack(ModItems.PAINTING.get());
                PaintingItem.setWidth(updated, msg.width);
                PaintingItem.setHeight(updated, msg.height);
                player.setItemInHand(msg.hand, updated);
            }
        });
        contextSupplier.get().setPacketHandled(true);
	}
}