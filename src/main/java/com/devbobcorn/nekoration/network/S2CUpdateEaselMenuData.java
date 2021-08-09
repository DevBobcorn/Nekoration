package com.devbobcorn.nekoration.network;

import java.util.function.Supplier;

import com.devbobcorn.nekoration.blocks.entities.EaselMenuBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class S2CUpdateEaselMenuData {
    public ItemStack[] items = new ItemStack[8];
    public BlockPos pos = BlockPos.ZERO;
    public Component[] texts = new Component[8];
    public DyeColor[] colors = new DyeColor[8];
    public boolean glow;

	public S2CUpdateEaselMenuData(BlockPos pos, ItemStack[] items, Component[] texts, DyeColor[] colors, boolean glowing) {
        this.pos = pos;
		this.items = items;
        this.texts = texts;
        this.colors = colors;
        this.glow = glowing;
	}

	public static void encode(final S2CUpdateEaselMenuData msg, final FriendlyByteBuf packetBuffer) {
        packetBuffer.writeBlockPos(msg.pos);
        for (int i = 0;i < 8;i++){
		    packetBuffer.writeItemStack(msg.items[i], false);
		    packetBuffer.writeComponent(msg.texts[i]);
		    packetBuffer.writeEnum(msg.colors[i]);
        }
        packetBuffer.writeBoolean(msg.glow);
	}

	public static S2CUpdateEaselMenuData decode(final FriendlyByteBuf packetBuffer) {
        BlockPos pos = packetBuffer.readBlockPos();
        ItemStack[] t = new ItemStack[8];
        Component[] x = new Component[8];
        DyeColor[] c = new DyeColor[8];
        for (int i = 0;i < 8;i++){
            t[i] = packetBuffer.readItem();
            x[i] = packetBuffer.readComponent();
            c[i] = packetBuffer.readEnum(DyeColor.class);
        }
		return new S2CUpdateEaselMenuData(pos, t, x, c, packetBuffer.readBoolean());
	}

    @SuppressWarnings("resource")
	public static void handle(final S2CUpdateEaselMenuData msg, final Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            //Handle this on CLIENT SIDE...
            ClientLevel world = Minecraft.getInstance().level;
            if (world.isLoaded(msg.pos)) {
                BlockEntity tileEntity = world.getBlockEntity(msg.pos);
                if (tileEntity instanceof EaselMenuBlockEntity) {
                        EaselMenuBlockEntity te = (EaselMenuBlockEntity) tileEntity;
                    for (int i = 0;i < 8;i++){
                        te.renderItems[i] = msg.items[i];
                        te.setMessage(i, msg.texts[i]);
                    }
                    te.setColor(msg.colors);
                    te.setGlowing(msg.glow);
                    //world.getChunkSource().blockChanged(msg.pos);
                    tileEntity.setChanged();
                    //System.out.println("Item Update Packet Received From Server.");
                }
            }
        });
        contextSupplier.get().setPacketHandled(true);
	}
}