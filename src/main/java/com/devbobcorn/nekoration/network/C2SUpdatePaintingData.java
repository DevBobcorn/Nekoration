package com.devbobcorn.nekoration.network;

import java.util.function.Supplier;

import com.devbobcorn.nekoration.entities.PaintingEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class C2SUpdatePaintingData {
    public int paintingId;
    public int[] pixels;

	public C2SUpdatePaintingData(int id, int[] pixels) {
        this.paintingId = id;
        this.pixels = pixels;
	}

	public static void encode(final C2SUpdatePaintingData msg, final PacketBuffer packetBuffer) {
        packetBuffer.writeInt(msg.paintingId);
        packetBuffer.writeVarIntArray(msg.pixels);
	}

	public static C2SUpdatePaintingData decode(final PacketBuffer packetBuffer) {
        int i = packetBuffer.readInt();
        int[] p = packetBuffer.readVarIntArray();
		return new C2SUpdatePaintingData(i, p);
	}

	public static void handle(final C2SUpdatePaintingData msg, final Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            //Handle this on SERVER SIDE...
            ServerPlayerEntity player = contextSupplier.get().getSender();
            if (player != null) {
                // ...
                Entity entity = player.level.getEntity(msg.paintingId);
                if (entity instanceof PaintingEntity){
                    ((PaintingEntity)entity).data.setPixels(msg.pixels);
                    System.out.println("Painting Updated!");
                }
            }
        });
        contextSupplier.get().setPacketHandled(true);
	}
}