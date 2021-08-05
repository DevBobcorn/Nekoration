package com.devbobcorn.nekoration.network;

import java.util.function.Supplier;

import com.devbobcorn.nekoration.blocks.entities.EaselMenuBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

public class S2CUpdateEaselMenuItems {
    public ItemStack[] items = new ItemStack[8];
    public BlockPos pos = BlockPos.ZERO;
    public ITextComponent[] texts = new ITextComponent[8];
    public DyeColor[] colors = new DyeColor[8];

	public S2CUpdateEaselMenuItems(BlockPos pos, ItemStack[] items, ITextComponent[] texts, DyeColor[] colors) {
        this.pos = pos;
		this.items = items;
        this.texts = texts;
        this.colors = colors;
	}

	public static void encode(final S2CUpdateEaselMenuItems msg, final PacketBuffer packetBuffer) {
        packetBuffer.writeBlockPos(msg.pos);
        for (int i = 0;i < 8;i++){
		    packetBuffer.writeItemStack(msg.items[i], false);
		    packetBuffer.writeComponent(msg.texts[i]);
		    packetBuffer.writeEnum(msg.colors[i]);
        }
	}

	public static S2CUpdateEaselMenuItems decode(final PacketBuffer packetBuffer) {
        BlockPos pos = packetBuffer.readBlockPos();
        ItemStack[] t = new ItemStack[8];
        ITextComponent[] x = new ITextComponent[8];
        DyeColor[] c = new DyeColor[8];
        for (int i = 0;i < 8;i++){
            t[i] = packetBuffer.readItem();
            x[i] = packetBuffer.readComponent();
            c[i] = packetBuffer.readEnum(DyeColor.class);
        }
		return new S2CUpdateEaselMenuItems(pos, t, x, c);
	}

    @SuppressWarnings("resource")
	public static void handle(final S2CUpdateEaselMenuItems msg, final Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            //Handle this on CLIENT SIDE...
            ClientWorld world = Minecraft.getInstance().level;
            if (world.isLoaded(msg.pos)) {
                TileEntity tileEntity = world.getBlockEntity(msg.pos);
                if (tileEntity instanceof EaselMenuBlockEntity) {
                        EaselMenuBlockEntity te = (EaselMenuBlockEntity) tileEntity;
                    for (int i = 0;i < 8;i++){
                        te.renderItems[i] = msg.items[i];
                        te.setMessage(i, msg.texts[i]);
                    }
                    te.setColor(msg.colors);
                    //world.getChunkSource().blockChanged(msg.pos);
                    tileEntity.setChanged();
                    //System.out.println("ITEM UPDATE Packet Received From Server.");
                }
            }
        });
        contextSupplier.get().setPacketHandled(true);
	}
}