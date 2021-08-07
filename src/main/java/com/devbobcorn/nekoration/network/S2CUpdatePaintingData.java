package com.devbobcorn.nekoration.network;

import java.util.function.Supplier;

import com.devbobcorn.nekoration.entities.PaintingEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class S2CUpdatePaintingData {
    public int paintingId;
    public int[] pixels;
    public int compositeHash;

	public S2CUpdatePaintingData(int id, int[] p, int hash) {
        this.paintingId = id;
        this.pixels = p;
        this.compositeHash = hash;
	}

	public static void encode(final S2CUpdatePaintingData msg, final PacketBuffer packetBuffer) {
        packetBuffer.writeInt(msg.paintingId);
        packetBuffer.writeVarIntArray(msg.pixels);
        packetBuffer.writeInt(msg.compositeHash);
	}

	public static S2CUpdatePaintingData decode(final PacketBuffer packetBuffer) {
        int id = packetBuffer.readInt();
        int[] p = packetBuffer.readVarIntArray();
        int hash = packetBuffer.readInt();
		return new S2CUpdatePaintingData(id, p, hash);
	}

    @SuppressWarnings("resource")
	public static void handle(final S2CUpdatePaintingData msg, final Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            //Handle this on CLIENT SIDE...
            ClientWorld world = Minecraft.getInstance().level;
                Entity entity = world.getEntity(msg.paintingId);
                if (entity instanceof PaintingEntity) {
                    PaintingEntity pe = (PaintingEntity) entity;
                    if (pe.data.getPaintingHash() != msg.compositeHash) { // Not the client who edited the painting...
                        // Cached image already obsoleted, clear...
                        pe.data.clearCache(pe.data.getPaintingHash());
                        // Update pixels, meanwhile updating the hash value...
                        pe.data.setPixels(msg.pixels);
                    }
                    // Then re-cache the updated painting...
                    pe.data.cache();
                    System.out.println("Data Synced: " + String.valueOf(pe.data.getPaintingHash() == msg.compositeHash));
                }
        });
        contextSupplier.get().setPacketHandled(true);
	}
}