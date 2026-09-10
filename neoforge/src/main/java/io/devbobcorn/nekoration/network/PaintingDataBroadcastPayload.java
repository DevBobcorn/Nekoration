package io.devbobcorn.nekoration.network;

import com.mojang.logging.LogUtils;

import io.devbobcorn.nekoration.NekoConfig;
import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.entities.PaintingEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;

/**
 * Server -> client sync packet for a part of a painting's pixel data.
 * The client who edited the painting has already got the up-to-date data,
 * so it's skipped by checking the composite hash...
 */
public record PaintingDataBroadcastPayload(int paintingId, byte partX, byte partY, byte partW, byte partH,
        int[] pixels, int compositeHash) implements CustomPacketPayload {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Type<PaintingDataBroadcastPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "painting_data_broadcast"));
    public static final StreamCodec<FriendlyByteBuf, PaintingDataBroadcastPayload> STREAM_CODEC =
            StreamCodec.of((buffer, payload) -> payload.write(buffer), PaintingDataBroadcastPayload::read);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PaintingDataBroadcastPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            // Handle this on CLIENT SIDE...
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) {
                return;
            }
            Entity entity = level.getEntity(payload.paintingId());
            if (entity instanceof PaintingEntity painting && painting.data != null) {
                if (painting.data.getPaintingHash() != payload.compositeHash()) { // Not the client who edited the painting...
                    // Cached image already obsoleted, clear...
                    painting.data.clearCache(painting.data.getPaintingHash());
                    // Update pixels, meanwhile updating the hash value...
                    painting.data.setAreaPixels(payload.partX(), payload.partY(), payload.partW(), payload.partH(), payload.pixels());
                    boolean synced = painting.data.getPaintingHash() == payload.compositeHash();
                    LOGGER.info(String.format("Painting %s Synced: %s", payload.compositeHash(), synced));
                    if (synced && NekoConfig.CLIENT.useImageRendering.get()) // The whole picture synced, then re-cache the updated painting...
                        painting.data.cache();
                }
            }
        });
    }

    private static PaintingDataBroadcastPayload read(FriendlyByteBuf buffer) {
        int id = buffer.readInt();
        byte x = buffer.readByte();
        byte y = buffer.readByte();
        byte w = buffer.readByte();
        byte h = buffer.readByte();
        int[] p = buffer.readVarIntArray();
        int hash = buffer.readInt();
        return new PaintingDataBroadcastPayload(id, x, y, w, h, p, hash);
    }

    private void write(FriendlyByteBuf buffer) {
        buffer.writeInt(paintingId);
        buffer.writeByte(partX);
        buffer.writeByte(partY);
        buffer.writeByte(partW);
        buffer.writeByte(partH);
        buffer.writeVarIntArray(pixels);
        buffer.writeInt(compositeHash);
    }
}
