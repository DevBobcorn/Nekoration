package com.devbobcorn.nekoration.network;

import java.util.function.Supplier;

import com.devbobcorn.nekoration.items.ModItems;
import com.devbobcorn.nekoration.items.PaintingItem;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class C2SUpdatePaintingSize {
    public InteractionHand hand;
    public short width;
    public short height;
    public int count;

    public C2SUpdatePaintingSize(InteractionHand hand, short w, short h, int c) {
        this.hand = hand;
        this.width = w;
        this.height = h;
        this.count = c;
    }

    public static void encode(final C2SUpdatePaintingSize msg, final FriendlyByteBuf packetBuffer) {
        packetBuffer.writeEnum(msg.hand);
        packetBuffer.writeShort(msg.width);
        packetBuffer.writeShort(msg.height);
        packetBuffer.writeInt(msg.count);
    }

    public static C2SUpdatePaintingSize decode(final FriendlyByteBuf packetBuffer) {
        InteractionHand hand = packetBuffer.readEnum(InteractionHand.class);
        short w = packetBuffer.readShort();
        short h = packetBuffer.readShort();
        int c = packetBuffer.readInt();
        return new C2SUpdatePaintingSize(hand, w, h, c);
    }

    public static void handle(final C2SUpdatePaintingSize msg, final Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            //Handle this on SERVER SIDE...
            ServerPlayer player = contextSupplier.get().getSender();
            if (player != null) {
                // ...
                ItemStack updated = new ItemStack(ModItems.PAINTING.get(), msg.count);
                PaintingItem.setWidth(updated, msg.width);
                PaintingItem.setHeight(updated, msg.height);
                player.setItemInHand(msg.hand, updated);
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }
}