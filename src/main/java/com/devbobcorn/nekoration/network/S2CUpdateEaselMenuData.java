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

public class S2CUpdateEaselMenuData {
    public ItemStack[] items = new ItemStack[8];
    public BlockPos pos = BlockPos.ZERO;
    public ITextComponent[] texts = new ITextComponent[8];
    public DyeColor[] colors = new DyeColor[8];
    public boolean glow;

	public S2CUpdateEaselMenuData(BlockPos pos, ItemStack[] items, ITextComponent[] texts, DyeColor[] colors, boolean glowing) {
        this.pos = pos;
		this.items = items;
        this.texts = texts;
        this.colors = colors;
        this.glow = glowing;
	}

	public static void encode(final S2CUpdateEaselMenuData msg, final PacketBuffer packetBuffer) {
        packetBuffer.writeBlockPos(msg.pos);
        for (int i = 0;i < 8;i++){
		    packetBuffer.writeItemStack(msg.items[i], false);
		    packetBuffer.writeComponent(msg.texts[i]);
		    packetBuffer.writeEnum(msg.colors[i]);
        }
        packetBuffer.writeBoolean(msg.glow);
	}

	public static S2CUpdateEaselMenuData decode(final PacketBuffer packetBuffer) {
        BlockPos pos = packetBuffer.readBlockPos();
        ItemStack[] t = new ItemStack[8];
        ITextComponent[] x = new ITextComponent[8];
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