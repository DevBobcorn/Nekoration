package io.devbobcorn.nekoration.network;

import java.util.UUID;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.client.ClientHelper;
import io.devbobcorn.nekoration.xplat.PayloadContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Server -> client payload carrying a painting's full data when a client
 * starts tracking the entity. Replaces NeoForge's entity spawn-data
 * ({@code IEntityWithComplexSpawn}) so the logic stays loader-agnostic.
 */
public record PaintingInitPayload(int paintingId, int width, int height, int[] pixels, UUID dataUuid)
        implements CustomPacketPayload {

    public static final Type<PaintingInitPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "painting_init"));
    public static final StreamCodec<FriendlyByteBuf, PaintingInitPayload> STREAM_CODEC =
            StreamCodec.of((buffer, payload) -> payload.write(buffer), PaintingInitPayload::read);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PaintingInitPayload payload, PayloadContext context) {
        // Client-only logic lives in ClientHelper so this class stays server-safe.
        context.enqueue(() -> ClientHelper.handlePaintingInit(payload));
    }

    public static PaintingInitPayload read(FriendlyByteBuf buffer) {
        int id = buffer.readVarInt();
        int width = buffer.readVarInt();
        int height = buffer.readVarInt();
        int[] pixels = buffer.readVarIntArray();
        UUID uuid = buffer.readUUID();
        return new PaintingInitPayload(id, width, height, pixels, uuid);
    }

    private void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(paintingId);
        buffer.writeVarInt(width);
        buffer.writeVarInt(height);
        buffer.writeVarIntArray(pixels);
        buffer.writeUUID(dataUuid);
    }
}
