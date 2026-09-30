package io.devbobcorn.nekoration.network;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.containers.CustomBlockMenu;
import io.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;
import io.devbobcorn.nekoration.xplat.PayloadContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

/**
 * Client -> server sync packet for the Custom Block's active entry.
 */
public record CustomBlockUpdatePayload(BlockPos pos, int active) implements CustomPacketPayload {

    public static final Type<CustomBlockUpdatePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "custom_block_update"));
    public static final StreamCodec<FriendlyByteBuf, CustomBlockUpdatePayload> STREAM_CODEC =
            StreamCodec.of((buffer, payload) -> payload.write(buffer), CustomBlockUpdatePayload::read);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CustomBlockUpdatePayload payload, PayloadContext context) {
        context.enqueue(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            if (!(player.containerMenu instanceof CustomBlockMenu menu)
                    || !menu.getCustomBlock().getBlockPos().equals(payload.pos())) {
                return;
            }
            CustomBlockEntity customBlock = menu.getCustomBlock();
            customBlock.setActive(Mth.clamp(payload.active(), 0, CustomBlockEntity.MAX_ENTRIES - 1));
            customBlock.markUpdated();
        });
    }

    private static CustomBlockUpdatePayload read(FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        int active = buffer.readByte();
        return new CustomBlockUpdatePayload(pos, active);
    }

    private void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeByte(active);
    }
}
