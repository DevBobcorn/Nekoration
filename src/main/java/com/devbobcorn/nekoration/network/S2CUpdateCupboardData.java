package com.devbobcorn.nekoration.network;

import java.util.function.Supplier;

import com.devbobcorn.nekoration.blocks.entities.ItemDisplayBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class S2CUpdateCupboardData {
    public ItemStack[] items = new ItemStack[4];
    public BlockPos pos = BlockPos.ZERO;

    public S2CUpdateCupboardData(BlockPos pos, ItemStack[] items) {
        this.pos = pos;
        this.items = items;
    }

    public static void encode(final S2CUpdateCupboardData msg, final FriendlyByteBuf packetBuffer) {
        packetBuffer.writeBlockPos(msg.pos);
        for (int i = 0;i < 4;i++){
            packetBuffer.writeItemStack(msg.items[i], false);
        }
    }

    public static S2CUpdateCupboardData decode(final FriendlyByteBuf packetBuffer) {
        BlockPos pos = packetBuffer.readBlockPos();
        ItemStack[] t = new ItemStack[4];
        for (int i = 0;i < 4;i++){
            t[i] = packetBuffer.readItem();
        }
        return new S2CUpdateCupboardData(pos, t);
    }

    @SuppressWarnings({ "resource", "null" })
    public static void handle(final S2CUpdateCupboardData msg, final Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            //Handle this on CLIENT SIDE...
            ClientLevel world = Minecraft.getInstance().level;
            if (world.isLoaded(msg.pos)) {
                BlockEntity tileEntity = world.getBlockEntity(msg.pos);
                if (tileEntity instanceof ItemDisplayBlockEntity) {
                    ItemDisplayBlockEntity te = (ItemDisplayBlockEntity) tileEntity;
                    for (int i = 0;i < 4;i++){
                        te.renderItems[i] = msg.items[i];
                    }
                    tileEntity.setChanged();
                }
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }
}