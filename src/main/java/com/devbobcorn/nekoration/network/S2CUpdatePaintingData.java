package com.devbobcorn.nekoration.network;

import java.util.function.Supplier;

import com.devbobcorn.nekoration.NekoConfig;
import com.devbobcorn.nekoration.entities.PaintingEntity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class S2CUpdatePaintingData {
    private static final Logger LOGGER = LogManager.getLogger("Painting Data Packet");

    public int paintingId;
    public byte partX;
    public byte partY;
    public byte partW;
    public byte partH;
    public int[] pixels;
    public int compositeHash;

	public S2CUpdatePaintingData(int id, byte x, byte y, byte w, byte h, int[] p, int hash) {
        this.paintingId = id;
        this.partX = x;
        this.partY = y;
        this.partW = w;
        this.partH = h;
        this.pixels = p;
        this.compositeHash = hash;
	}

	public static void encode(final S2CUpdatePaintingData msg, final FriendlyByteBuf packetBuffer) {
        packetBuffer.writeInt(msg.paintingId);
        packetBuffer.writeByte(msg.partX);
        packetBuffer.writeByte(msg.partY);
        packetBuffer.writeByte(msg.partW);
        packetBuffer.writeByte(msg.partH);
        packetBuffer.writeVarIntArray(msg.pixels);
        packetBuffer.writeInt(msg.compositeHash);
	}

	public static S2CUpdatePaintingData decode(final FriendlyByteBuf packetBuffer) {
        int id = packetBuffer.readInt();
        byte x = packetBuffer.readByte();
        byte y = packetBuffer.readByte();
        byte w = packetBuffer.readByte();
        byte h = packetBuffer.readByte();
        int[] p = packetBuffer.readVarIntArray();
        int hash = packetBuffer.readInt();
		return new S2CUpdatePaintingData(id, x, y, w, h, p, hash);
	}

    @SuppressWarnings("resource")
	public static void handle(final S2CUpdatePaintingData msg, final Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            //Handle this on CLIENT SIDE...
            ClientLevel world = Minecraft.getInstance().level;
                Entity entity = world.getEntity(msg.paintingId);
                if (entity instanceof PaintingEntity) {
                    PaintingEntity pe = (PaintingEntity) entity;
                    if (pe.data.getPaintingHash() != msg.compositeHash) { // Not the client who edited the painting...
                        // Cached image already obsoleted, clear...
                        pe.data.clearCache(pe.data.getPaintingHash());
                        // Update pixels, meanwhile updating the hash value...
                        //pe.data.setPixels(msg.pixels);
                        pe.data.setAreaPixels(msg.partX, msg.partY, msg.partW, msg.partH, msg.pixels);
                        boolean synced = pe.data.getPaintingHash() == msg.compositeHash;
                        LOGGER.info(String.format("Painting %s Synced: %s", msg.compositeHash, synced));
                        if (synced && NekoConfig.CLIENT.useImageRendering.get()) // The whole picture synced, then re-cache the updated painting...
                            pe.data.cache();
                    }
                }
        });
        contextSupplier.get().setPacketHandled(true);
	}
}