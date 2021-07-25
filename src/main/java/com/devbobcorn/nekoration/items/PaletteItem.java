package com.devbobcorn.nekoration.items;

import java.awt.Color;

import javax.annotation.Nonnull;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.client.ClientHelper;
import com.devbobcorn.nekoration.exp.ExpNBTTypes;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class PaletteItem extends Item {
    public final Color[] DEFAULT_COLOR_SET = { Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA };

    public static final String ACTIVE = "Active";
    public static final String COLORS = "Colors";

    public PaletteItem(Properties settings) {
        super(settings);
    }

    @Nonnull
	@Override
    @SuppressWarnings("deprecation")
	public ActionResult<ItemStack> use(World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            // First get the existing data in this palette...
            CompoundNBT nbt = stack.getTag();

            if (nbt != null && nbt.contains(ACTIVE, ExpNBTTypes.BYTE_NBT_ID)){
                byte a = nbt.getByte(ACTIVE);
                int[] c = nbt.getIntArray(COLORS);

                Color[] col = new Color[6];
                for (int i = 0;i < 6;i++){
                    col[i] = new Color(NekoColors.getRed(c[i]), NekoColors.getGreen(c[i]), NekoColors.getBlue(c[i]));
                }
                DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> { 
                    //Minecraft.getInstance().setScreen(new PaletteScreen(hand, a, col));
                    ClientHelper.showGuiDraw(hand, a, col);
                });
            } else DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> { 
                //Minecraft.getInstance().setScreen(new PaletteScreen(hand, a, DEFAULT_COLOR_SET));
                ClientHelper.showGuiDraw(hand, (byte)0, DEFAULT_COLOR_SET);
            });
		}
        return ActionResult.success(stack);
	}
}
