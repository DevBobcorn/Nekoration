package io.devbobcorn.nekoration.items;

import java.util.List;

import io.devbobcorn.nekoration.common.ComponentCompat;
import io.devbobcorn.nekoration.entities.WallpaperEntity;
import io.devbobcorn.nekoration.entities.WallpaperEntity.Part;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class WallpaperItem extends Item {
    public WallpaperItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        Direction direction = context.getClickedFace();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (player != null && (!direction.getAxis().isHorizontal() || !player.mayUseItemAt(pos, direction, stack))) {
            return InteractionResult.FAIL;
        }

        Level level = context.getLevel();
        WallpaperEntity wallpaper = new WallpaperEntity(level, pos, direction, stack, Part.FULL);
        CustomData entityData = stack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
        if (!entityData.isEmpty()) {
            net.minecraft.world.entity.EntityType.updateCustomEntityTag(level, player, wallpaper, entityData);
        }

        if (!wallpaper.survives()) {
            wallpaper.setPart(Part.UPPER);
            if (!wallpaper.survives()) {
                return InteractionResult.CONSUME;
            }
        }

        if (!level.isClientSide()) {
            wallpaper.playPlacementSound();
            level.gameEvent(player, GameEvent.ENTITY_PLACE, wallpaper.position());
            level.addFreshEntity(wallpaper);
        }
        stack.shrink(1);
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public Component getName(ItemStack stack) {
        String color = hasPatternData(stack) ? getColor(stack).getSerializedName() : "blank";
        return Component.translatable(getDescriptionId(stack), ComponentCompat.interpolationArg("color.nekoration." + color));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        BannerItem.appendHoverTextFromBannerBlockEntityTag(stack, tooltip);
    }

    public static boolean hasPatternData(ItemStack stack) {
        return stack.has(DataComponents.BASE_COLOR) || stack.has(DataComponents.BANNER_PATTERNS);
    }

    public static DyeColor getColor(ItemStack stack) {
        return stack.getOrDefault(DataComponents.BASE_COLOR, DyeColor.WHITE);
    }
}
