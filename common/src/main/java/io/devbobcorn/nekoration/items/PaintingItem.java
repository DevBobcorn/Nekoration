package io.devbobcorn.nekoration.items;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.devbobcorn.nekoration.client.ClientHelper;
import io.devbobcorn.nekoration.entities.PaintingEntity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class PaintingItem extends Item {
    public static final String TYPE = "type";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String DATAID = "dataid";
    public static final String PIXELS = "pixels";
    public static final String ENTITYID = "entityid";
    public static final String SIGNED = "signed";
    public static final String TITLE = "title";
    public static final String AUTHOR = "author";

    public enum Type {
        BLANK((byte) 0, "blank"),
        PAINTED((byte) 1, "painted"),
        MAGIC((byte) 2, "magic");

        Type(byte id, String name) {
            this.id = id;
            this.name = name;
        }

        public static Type fromId(byte id) {
            for (Type p : Type.values()) {
                if (p.id == id)
                    return p;
            }
            return Type.BLANK;
        }

        public final byte id;
        public final String name;
    }

    public PaintingItem(Properties settings) {
        super(settings);
    }

    /** Reads this item's custom data tag. */
    private static CompoundTag getTag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockPos attachedPos = pos.relative(direction);
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        Level level = context.getLevel();
        if (player != null && !mayPlace(player, direction, stack, attachedPos)) {
            return InteractionResult.FAIL;
        }
        PaintingEntity painting;
        CompoundTag tag = getTag(stack);
        if (tag.contains(TYPE)) {
            if (tag.getByte(TYPE) == Type.PAINTED.id) {
                // Then make the painting with existing data...
                painting = new PaintingEntity(level, attachedPos, direction, (short) (getWidth(stack) * 16), (short) (getHeight(stack) * 16), tag.getUUID(DATAID));
                if (!level.isClientSide()) {
                    painting.data.setPixels(tag.getIntArray(PIXELS)); // Meanless to operate on client-side as it'll not be actually added into the world...
                    if (isSigned(stack)) // Keep the signature when placing a signed painting...
                        painting.data.setSignature(getTitle(stack), getAuthor(stack));
                }
            } else if (tag.getByte(TYPE) == Type.MAGIC.id) {
                painting = new PaintingEntity(level, attachedPos, direction, (short) (getWidth(stack) * 16), (short) (getHeight(stack) * 16), tag.getUUID(DATAID));
                Entity entity = level.getEntity(tag.getInt(ENTITYID));
                if (entity instanceof PaintingEntity linkedPainting) {
                    painting.data.setPixels(linkedPainting.data.getPixels().clone());
                } else {
                    if (level.isClientSide())
                        player.displayClientMessage(Component.translatable("gui.nekoration.message.link_expired"), true);
                    return InteractionResult.FAIL;
                }
            } else {
                painting = new PaintingEntity(level, attachedPos, direction, (short) (getWidth(stack) * 16), (short) (getHeight(stack) * 16));
            }
        } else {
            painting = new PaintingEntity(level, attachedPos, direction, (short) (getWidth(stack) * 16), (short) (getHeight(stack) * 16));
        }
        painting.setPos(attachedPos.getX(), attachedPos.getY(), attachedPos.getZ());
        if (painting.survives()) {
            if (!level.isClientSide()) {
                painting.playPlacementSound();
                level.gameEvent(player, GameEvent.ENTITY_PLACE, painting.position());
                level.addFreshEntity(painting);
            }
            stack.shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide());
        } else {
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        CompoundTag tag = getTag(stack);
        boolean hasType = tag.contains(TYPE);
        if (!hasType || tag.getByte(TYPE) == Type.BLANK.id) { // If a blank one...
            if (level.isClientSide()) {
                ClientHelper.showPaintingSizeScreen(hand, stack.getCount());
            }
        } else if (hasType && hasData(stack) && !isSigned(stack) && stack.getCount() == 1) { // If a single painted one with data, not signed yet...
            if (level.isClientSide()) {
                ClientHelper.showPaintingSignScreen(hand);
            }
        } else if (hasType && tag.getByte(TYPE) == Type.MAGIC.id) { // If a magic link...
            if (!level.isClientSide()) {
                CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> {
                    t.remove(DATAID);
                    t.remove(ENTITYID);
                    t.remove(PIXELS);
                    t.putByte(TYPE, Type.BLANK.id);
                });
            }
        }
        return InteractionResultHolder.success(stack);
    }

    protected boolean mayPlace(Player player, Direction dir, ItemStack stack, BlockPos pos) {
        return !dir.getAxis().isVertical() && player.mayUseItemAt(pos, dir, stack);
    }

    public static int getWidth(ItemStack stack) {
        short w = getTag(stack).getShort(WIDTH);
        return w <= 0 ? 1 : w;
    }

    public static void setWidth(ItemStack stack, short w) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putShort(WIDTH, w));
    }

    public static int getHeight(ItemStack stack) {
        short h = getTag(stack).getShort(HEIGHT);
        return h <= 0 ? 1 : h;
    }

    public static void setHeight(ItemStack stack, short h) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putShort(HEIGHT, h));
    }

    public static byte getType(ItemStack stack) {
        CompoundTag tag = getTag(stack);
        if (tag.contains(TYPE))
            return tag.getByte(TYPE);
        return Type.BLANK.id;
    }

    /** Whether this stack stores actual painting data, i.e. it's a painted painting. */
    public static boolean hasData(ItemStack stack) {
        CompoundTag tag = getTag(stack);
        return tag.getByte(TYPE) == Type.PAINTED.id && tag.contains(PIXELS);
    }

    public static boolean isSigned(ItemStack stack) {
        return getTag(stack).getBoolean(SIGNED);
    }

    public static String getTitle(ItemStack stack) {
        return getTag(stack).getString(TITLE);
    }

    public static String getAuthor(ItemStack stack) {
        return getTag(stack).getString(AUTHOR);
    }

    public static void setSignature(ItemStack stack, String title, String author) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putBoolean(SIGNED, true);
            tag.putString(TITLE, title);
            tag.putString(AUTHOR, author);
        });
    }

    public static void setContent(ItemStack stack, short w, short h, UUID seed, int[] pixels) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putByte(TYPE, Type.PAINTED.id);
            tag.putShort(WIDTH, w);
            tag.putShort(HEIGHT, h);
            tag.putUUID(DATAID, seed);
            tag.putIntArray(PIXELS, pixels);
            tag.remove(SIGNED);
            tag.remove(TITLE);
            tag.remove(AUTHOR);
        });
    }

    public static void setSize(ItemStack stack, short w, short h) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putByte(TYPE, Type.BLANK.id);
            tag.putShort(WIDTH, w);
            tag.putShort(HEIGHT, h);
            tag.remove(SIGNED);
            tag.remove(TITLE);
            tag.remove(AUTHOR);
        });
    }

    public static void setLink(ItemStack stack, short w, short h, UUID dataId, int entityId) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putByte(TYPE, Type.MAGIC.id);
            tag.putShort(WIDTH, w);
            tag.putShort(HEIGHT, h);
            tag.putUUID(DATAID, dataId);
            tag.putInt(ENTITYID, entityId);
            tag.remove(SIGNED);
            tag.remove(TITLE);
            tag.remove(AUTHOR);
        });
    }

    @Override
    public Component getName(ItemStack stack) {
        if (isSigned(stack)) // Signed paintings are named after their titles...
            return Component.literal(getTitle(stack));
        return Component.translatable(this.getDescriptionId(stack) + '.' + Type.fromId(getType(stack)).name, getWidth(stack), getHeight(stack));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (isSigned(stack))
            tooltipComponents.add(Component.translatable("book.byAuthor", getAuthor(stack)).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (getType(stack) != Type.PAINTED.id)
            return Optional.empty();
        CompoundTag tag = getTag(stack);
        if (!tag.hasUUID(DATAID) || !tag.contains(PIXELS))
            return Optional.empty();
        short w = (short) getWidth(stack);
        short h = (short) getHeight(stack);
        int[] pixels = tag.getIntArray(PIXELS);
        if (pixels.length != w * 16 * h * 16) // The pixel data doesn't match the size; skip the preview...
            return Optional.empty();
        return Optional.of(new PaintingTooltipComponent(w, h, tag.getUUID(DATAID), pixels));
    }
}
