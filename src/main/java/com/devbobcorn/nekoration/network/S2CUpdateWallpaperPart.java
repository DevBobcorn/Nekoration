package com.devbobcorn.nekoration.network;

import java.util.function.Supplier;

import com.devbobcorn.nekoration.entities.WallPaperEntity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

public class S2CUpdateWallpaperPart {
    private static final Logger LOGGER = LogManager.getLogger("Wallpaper Part Packet");

    public int wallpaperId;
    public byte part;

	public S2CUpdateWallpaperPart(int id, byte p) {
        this.wallpaperId = id;
        this.part = p;
	}

	public static void encode(final S2CUpdateWallpaperPart msg, final FriendlyByteBuf packetBuffer) {
        packetBuffer.writeInt(msg.wallpaperId);
        packetBuffer.writeByte(msg.part);
	}

	public static S2CUpdateWallpaperPart decode(final FriendlyByteBuf packetBuffer) {
        int id = packetBuffer.readInt();
        byte p = packetBuffer.readByte();
		return new S2CUpdateWallpaperPart(id, p);
	}

    @SuppressWarnings({ "resource", "null" })
	public static void handle(final S2CUpdateWallpaperPart msg, final Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            //Handle this on CLIENT SIDE...
            ClientLevel world = Minecraft.getInstance().level;
                Entity entity = world.getEntity(msg.wallpaperId);
                if (entity instanceof WallPaperEntity) {
                    WallPaperEntity we = (WallPaperEntity) entity;
                    we.setPart(WallPaperEntity.Part.fromId(msg.part));
                    LOGGER.info("Wallpaper part " + msg.wallpaperId + " updated to " + msg.part);
                }
        });
        contextSupplier.get().setPacketHandled(true);
	}
}