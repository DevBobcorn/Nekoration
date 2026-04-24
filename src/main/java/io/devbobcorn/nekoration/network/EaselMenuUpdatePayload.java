package io.devbobcorn.nekoration.network;

import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.entities.EaselMenuBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client -> server sync packet for easel text, text color, and glow state.
 */
public record EaselMenuUpdatePayload(BlockPos pos, String[] texts, DyeColor[] colors, boolean glowing)
        implements CustomPacketPayload {

    public static final Type<EaselMenuUpdatePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, "easel_menu_update"));
    public static final StreamCodec<FriendlyByteBuf, EaselMenuUpdatePayload> STREAM_CODEC =
            StreamCodec.of((buffer, payload) -> payload.write(buffer), EaselMenuUpdatePayload::read);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(EaselMenuUpdatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            if (!player.level().isLoaded(payload.pos())) {
                return;
            }
            BlockEntity blockEntity = player.level().getBlockEntity(payload.pos());
            if (!(blockEntity instanceof EaselMenuBlockEntity easel)) {
                return;
            }

            String[] texts = payload.texts();
            DyeColor[] colors = payload.colors();
            for (int i = 0; i < EaselMenuBlockEntity.NUMBER_OF_SLOTS; i++) {
                easel.setMessage(i, net.minecraft.network.chat.Component.literal(i < texts.length ? texts[i] : ""));
                if (i < colors.length) {
                    easel.setColor(i, colors[i]);
                }
            }
            easel.setGlowing(payload.glowing());
            easel.setChanged();

            var state = easel.getBlockState();
            var level = easel.getLevel();
            if (level != null) {
                level.sendBlockUpdated(easel.getBlockPos(), state, state, 3);
            }
        });
    }

    private static EaselMenuUpdatePayload read(FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        String[] texts = new String[EaselMenuBlockEntity.NUMBER_OF_SLOTS];
        DyeColor[] colors = new DyeColor[EaselMenuBlockEntity.NUMBER_OF_SLOTS];
        for (int i = 0; i < EaselMenuBlockEntity.NUMBER_OF_SLOTS; i++) {
            texts[i] = buffer.readUtf(32767);
        }
        for (int i = 0; i < EaselMenuBlockEntity.NUMBER_OF_SLOTS; i++) {
            colors[i] = buffer.readEnum(DyeColor.class);
        }
        boolean glow = buffer.readBoolean();
        return new EaselMenuUpdatePayload(pos, texts, colors, glow);
    }

    private void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        for (int i = 0; i < EaselMenuBlockEntity.NUMBER_OF_SLOTS; i++) {
            buffer.writeUtf(i < texts.length ? texts[i] : "");
        }
        for (int i = 0; i < EaselMenuBlockEntity.NUMBER_OF_SLOTS; i++) {
            DyeColor color = i < colors.length ? colors[i] : DyeColor.GRAY;
            buffer.writeEnum(color);
        }
        buffer.writeBoolean(glowing);
    }
}
