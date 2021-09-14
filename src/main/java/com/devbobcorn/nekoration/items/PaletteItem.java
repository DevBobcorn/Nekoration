package com.devbobcorn.nekoration.items;

import java.awt.Color;

import javax.annotation.Nonnull;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.client.ClientHelper;
import com.devbobcorn.nekoration.utils.TagTypes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class PaletteItem extends Item {
    public final static Color[] DEFAULT_COLOR_SET = { Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA };

    public static final String ACTIVE = "active";
    public static final String COLORS = "colors";

    public PaletteItem(Properties settings) {
        super(settings);
    }

    @Nonnull
	@Override
    @SuppressWarnings("deprecation")
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @Nonnull InteractionHand hand) {
        //System.out.println("Used Palette Item!");
        ItemStack stack = player.getItemInHand(hand);
        if (world.isClientSide) {
            // First get the existing data in this palette...
            CompoundTag nbt = stack.getTag();

            if (nbt != null && nbt.contains(ACTIVE, TagTypes.BYTE_NBT_ID)){
                byte a = nbt.getByte(ACTIVE);
                int[] c = nbt.getIntArray(COLORS);

                Color[] col = new Color[6];
                for (int i = 0;i < 6;i++){
                    col[i] = new Color(NekoColors.getRed(c[i]), NekoColors.getGreen(c[i]), NekoColors.getBlue(c[i]));
                }
                DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> { 
                    //Minecraft.getInstance().setScreen(new PaletteScreen(hand, a, col));
                    ClientHelper.showPaletteScreen(hand, a, col);
                });
            } else DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> { 
                //Minecraft.getInstance().setScreen(new PaletteScreen(hand, a, DEFAULT_COLOR_SET));
                ClientHelper.showPaletteScreen(hand, (byte)0, DEFAULT_COLOR_SET);
            });
		}
        return InteractionResultHolder.success(stack);
	}

    @Override
	public Component getName(ItemStack stack) {
		return CaseTweak.getTweaked(new TranslatableComponent(this.getDescriptionId(stack)));
	}
}
