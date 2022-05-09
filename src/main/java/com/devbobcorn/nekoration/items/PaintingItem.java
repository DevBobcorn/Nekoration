package com.devbobcorn.nekoration.items;

import java.util.UUID;

import javax.annotation.Nullable;

import com.devbobcorn.nekoration.client.ClientHelper;
import com.devbobcorn.nekoration.entities.PaintingEntity;
import com.devbobcorn.nekoration.utils.TagTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class PaintingItem extends Item {
    public static final String TYPE = "type";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String DATAID = "dataid";
    public static final String PIXELS = "pixels";
    public static final String ENTITYID = "entityid";

    public enum Type {
        BLANK((byte) 0, "blank"),
        PAINTED((byte) 1, "painted"),
        MAGIC((byte) 2, "magic");

        Type(byte id, String name){
            this.id = id;
            this.name = name;
        }

        public static Type fromId(byte id){
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
    
    public static float getTypePropertyOverride(ItemStack itemStack, @Nullable Level world, @Nullable LivingEntity livingEntity, int what){
        return (float) getType(itemStack);
    }

    public InteractionResult useOn(UseOnContext ctx) {
        BlockPos blockpos = ctx.getClickedPos();
        Direction direction = ctx.getClickedFace();
        BlockPos blockpos1 = blockpos.relative(direction);
        Player player = ctx.getPlayer();
        ItemStack stack = ctx.getItemInHand();
        if (player != null && !this.mayPlace(player, direction, stack, blockpos1)) {
            return InteractionResult.FAIL;
        } else {
            Level world = ctx.getLevel();
            PaintingEntity painting;
            CompoundTag tag = stack.getOrCreateTag();
            if (tag.contains(TYPE, TagTypes.BYTE_NBT_ID)) {
                if (tag.getByte(TYPE) == Type.PAINTED.id) {
                    // Then make the painting with existing data...
                    painting = new PaintingEntity(world, blockpos1, direction, (short)(PaintingItem.getWidth(stack) * 16), (short)(PaintingItem.getHeight(stack) * 16), tag.getUUID(DATAID));
                    if (!world.isClientSide) painting.data.setPixels(tag.getIntArray(PIXELS)); // Meanless to operate on client-side as it'll not be actually added into the world...
                } else if (tag.getByte(TYPE) == Type.MAGIC.id) {
                    painting = new PaintingEntity(world, blockpos1, direction, (short)(PaintingItem.getWidth(stack) * 16), (short)(PaintingItem.getHeight(stack) * 16), tag.getUUID(DATAID));
                    Entity entity;
                    if ((entity = world.getEntity(tag.getInt(ENTITYID))) instanceof PaintingEntity)
                        painting.data.setPixels(((PaintingEntity)entity).data.getPixels().clone());
                    else {
                        if (world.isClientSide)
                            player.displayClientMessage(new TranslatableComponent("gui.nekoration.message.link_expired"), true);
                        return InteractionResult.FAIL;
                    }
                } else painting = new PaintingEntity(world, blockpos1, direction, (short)(PaintingItem.getWidth(stack) * 16), (short)(PaintingItem.getHeight(stack) * 16));
            } else painting = new PaintingEntity(world, blockpos1, direction, (short)(PaintingItem.getWidth(stack) * 16), (short)(PaintingItem.getHeight(stack) * 16));
            painting.setPos(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
            if (painting.survives()) {
                if (!world.isClientSide) {
                    painting.playPlacementSound();
                    //hangingentity.recalculateBoundingBox();
                    world.addFreshEntity(painting);
                }
                stack.shrink(1);
                return InteractionResult.sidedSuccess(world.isClientSide);
            } else {
                return InteractionResult.CONSUME;
            }
        }
    }

    @SuppressWarnings("deprecation")
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        CompoundTag tag = stack.getOrCreateTag();
        boolean hasType = tag.contains(TYPE, TagTypes.BYTE_NBT_ID);
        if (!hasType || (hasType && tag.getByte(TYPE) == Type.BLANK.id)) { // If a blank one...
            if (world.isClientSide) {
                DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> { 
                    //Minecraft.getInstance().setScreen(new PaletteScreen(hand, a, col));
                    ClientHelper.showPaintingSizeScreen(hand);
                });
            }
        } else if (hasType && tag.getByte(TYPE) == Type.MAGIC.id) { // If a magic link...
            if (!world.isClientSide) {
                tag.remove("DataId");
                tag.remove("EntityId");
                tag.putByte(TYPE, Type.BLANK.id);
            }
        }
        return InteractionResultHolder.<ItemStack>success(stack);
     }

    protected boolean mayPlace(Player player, Direction dir, ItemStack stack, BlockPos pos) {
        return !dir.getAxis().isVertical() && player.mayUseItemAt(pos, dir, stack);
    }

    public static int getWidth(ItemStack stack) {
        short w = stack.getOrCreateTag().getShort(WIDTH);
        return w <= 0 ? 1 : w;
    }

    public static void setWidth(ItemStack stack, short w) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putShort(WIDTH, w);
    }

    public static int getHeight(ItemStack stack) {
        short h = stack.getOrCreateTag().getShort(HEIGHT);
        return h <= 0 ? 1 : h;
    }

    public static void setHeight(ItemStack stack, short h) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putShort(HEIGHT, h);
    }

    public static byte getType(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(TYPE, TagTypes.BYTE_NBT_ID))
            return tag.getByte(TYPE);
        return Type.BLANK.id;
    }

    public static void setContent(ItemStack stack, short w, short h, UUID seed, int[] pixels) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putByte(TYPE, Type.PAINTED.id);
        tag.putShort(WIDTH, w);
        tag.putShort(HEIGHT, h);
        tag.putUUID(DATAID, seed);
        tag.putIntArray(PIXELS, pixels);
    }

    public static void setSize(ItemStack stack, short w, short h) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putByte(TYPE, Type.BLANK.id);
        tag.putShort(WIDTH, w);
        tag.putShort(HEIGHT, h);
    }

    public static void setLink(ItemStack stack, short w, short h, UUID DataId, int EntityId) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putByte(TYPE, Type.MAGIC.id);
        tag.putShort(WIDTH, w);
        tag.putShort(HEIGHT, h);
        tag.putUUID(DATAID, DataId);
        tag.putInt(ENTITYID, EntityId);
    }

    @Override
    public Component getName(ItemStack stack) {
        return new TranslatableComponent(this.getDescriptionId(stack) + '.' + Type.fromId(getType(stack)).name, getWidth(stack), getHeight(stack));
    }
}
