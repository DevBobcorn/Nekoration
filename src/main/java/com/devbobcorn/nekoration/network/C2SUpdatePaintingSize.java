package com.devbobcorn.nekoration.network;

import java.util.function.Supplier;

import com.devbobcorn.nekoration.items.ModItems;
import com.devbobcorn.nekoration.items.PaintingItem;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

public class C2SUpdatePaintingSize {
    public Hand hand;
	public short width;
    public short height;

	public C2SUpdatePaintingSize(Hand hand, short w, short h) {
        this.hand = hand;
		this.width = w;
        this.height = h;
	}

	public static void encode(final C2SUpdatePaintingSize msg, final PacketBuffer packetBuffer) {
        packetBuffer.writeEnum(msg.hand);
        packetBuffer.writeShort(msg.width);
        packetBuffer.writeShort(msg.height);
	}

	public static C2SUpdatePaintingSize decode(final PacketBuffer packetBuffer) {
        Hand hand = packetBuffer.readEnum(Hand.class);
        short w = packetBuffer.readShort();
        short h = packetBuffer.readShort();
		return new C2SUpdatePaintingSize(hand, w, h);
	}

	public static void handle(final C2SUpdatePaintingSize msg, final Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            //Handle this on SERVER SIDE...
            ServerPlayerEntity player = contextSupplier.get().getSender();
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