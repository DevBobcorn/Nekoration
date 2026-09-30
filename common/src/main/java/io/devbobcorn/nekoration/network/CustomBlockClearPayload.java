package io.devbobcorn.nekoration.network;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.CustomBlock;
import io.devbobcorn.nekoration.blocks.containers.CustomBlockMenu;
import io.devbobcorn.nekoration.blocks.entities.CustomBlockEntity;
import io.devbobcorn.nekoration.xplat.PayloadContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Client -> server packet that clears one entry of a Custom Block.
 */
public record CustomBlockClearPayload(BlockPos pos, int index) implements CustomPacketPayload {

    public static final Type<CustomBlockClearPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "custom_block_clear"));
    public static final StreamCodec<FriendlyByteBuf, CustomBlockClearPayload> STREAM_CODEC =
            StreamCodec.of((buffer, payload) -> payload.write(buffer), CustomBlockClearPayload::read);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CustomBlockClearPayload payload, PayloadContext context) {
        context.enqueue(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            if (!(player.containerMenu instanceof CustomBlockMenu menu)
                    || !menu.getCustomBlock().getBlockPos().equals(payload.pos())) {
                return;
            }
            CustomBlockEntity customBlock = menu.getCustomBlock();
            CustomBlock.collectEntry(player.level(), customBlock.getBlockPos(), player, customBlock, payload.index());
        });
    }

    private static CustomBlockClearPayload read(FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        int index = buffer.readByte();
        return new CustomBlockClearPayload(pos, index);
    }

    private void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeByte(index);
    }
}
