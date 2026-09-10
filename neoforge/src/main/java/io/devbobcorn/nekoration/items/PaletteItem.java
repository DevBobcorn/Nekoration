package io.devbobcorn.nekoration.items;

import java.awt.Color;

import io.devbobcorn.nekoration.NekoColors;
import io.devbobcorn.nekoration.client.ClientHelper;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public class PaletteItem extends Item {
    public final static Color[] DEFAULT_COLOR_SET = { Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA };

    public static final String ACTIVE = "active";
    public static final String COLORS = "colors";

    public PaletteItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            // First get the existing data in this palette...
            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

            if (tag.contains(ACTIVE)) {
                byte active = tag.getByte(ACTIVE);
                int[] colors = tag.getIntArray(COLORS);

                Color[] col = new Color[6];
                for (int i = 0; i < 6; i++) {
                    col[i] = new Color(NekoColors.getRed(colors[i]), NekoColors.getGreen(colors[i]), NekoColors.getBlue(colors[i]));
                }
                ClientHelper.showPaletteScreen(hand, active, col);
            } else {
                ClientHelper.showPaletteScreen(hand, (byte) 0, DEFAULT_COLOR_SET);
            }
        }
        return InteractionResultHolder.success(stack);
    }

    public static void setColorData(ItemStack stack, byte active, int[] colors) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putByte(ACTIVE, active);
            tag.putIntArray(COLORS, colors);
        });
    }
}
