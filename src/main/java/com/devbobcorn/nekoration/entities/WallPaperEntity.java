package com.devbobcorn.nekoration.entities;

import java.util.List;

import javax.annotation.Nullable;

import com.devbobcorn.nekoration.items.ModItems;
import com.devbobcorn.nekoration.utils.TagTypes;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;

public class WallPaperEntity extends HangingEntity implements IEntityAdditionalSpawnData {
    @Nullable
    private DyeColor baseColor = DyeColor.WHITE;
    @Nullable
    private ListTag itemPatterns;
    private boolean receivedData = false;
    @Nullable
    private List<Pair<BannerPattern, DyeColor>> patterns = null;

    public enum Part {
        UPPER((byte) 0, "upper"),
        LOWER((byte) 1, "lower"),
        FULL((byte) 2, "full");

        Part(byte id, String name){
            this.id = id;
            this.name = name;
        }

        public static Part fromId(byte id){
            for (Part p : Part.values()) {
                if (p.id == id)
                    return p;
            }
            return Part.FULL;
        }

        public final byte id;
        public final String name;
    }

    private Part part = Part.FULL;

    protected WallPaperEntity(EntityType<WallPaperEntity> type, Level world) {
        // Constructor 1: the default one, but not used to create instances in worlds
        super(ModEntityType.WALLPAPER_TYPE, world);
    }

    public WallPaperEntity(Level world, BlockPos pos, Direction dir, ItemStack stack, Part part) {
        // Constructor 2: the one for server-side to create WallPaperEntity Objects
        super(ModEntityType.WALLPAPER_TYPE, world, pos);
        this.part = part;
        this.setDirection(dir);
        fromItem(stack);
    }

    public WallPaperEntity(PlayMessages.SpawnEntity packet, Level world) {
        // Constructor 3: the one for client-side, creating instances with data packets from the Server
        // Enable by adding 'setCustomClientFactory' when building the entity type
        super(ModEntityType.WALLPAPER_TYPE, world);
    }

    public void setPart(Part part){
        this.part = part;
    }

    public Part getPart(){
        return this.part;
    }

    @Nullable
    public static ListTag getPatterns(CompoundTag tag) {
        ListTag listnbt = null;
        if (tag.contains("Patterns", TagTypes.LIST_NBT_ID)) {
            listnbt = tag.getList("Patterns", TagTypes.COMPOUND_NBT_ID).copy();
        }
        return listnbt;
    }

    @Nullable
    public static DyeColor getBaseColor(CompoundTag tag) {
        if (tag.contains("Base", TagTypes.INT_NBT_ID)) {
            return DyeColor.byId(tag.getInt("Base"));
        }
        return DyeColor.WHITE;
    }

    public DyeColor getBaseColor(){
        return this.baseColor;
    }

    public ItemStack getItem(){
        ItemStack itemstack = new ItemStack(ModItems.WALLPAPER.get());
        CompoundTag tag = itemstack.getOrCreateTagElement("BlockEntityTag");

        if (this.itemPatterns != null && !this.itemPatterns.isEmpty()) {
            tag.put("Patterns", this.itemPatterns.copy());
            tag.putInt("Base", this.baseColor.getId());
        } else if (this.baseColor != DyeColor.WHITE)
            tag.putInt("Base", this.baseColor.getId());
        return itemstack;
    }

    public void fromItem(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("BlockEntityTag");
        if (tag != null) {
            this.itemPatterns = getPatterns(tag);
            this.baseColor = getBaseColor(tag);
            this.patterns = null;
            this.receivedData = true;
        }
    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        if (this.part == Part.LOWER)
            this.part = Part.UPPER;
        else if (this.part == Part.UPPER)
            this.part = Part.LOWER;

        return InteractionResult.SUCCESS;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putByte("Facing", (byte) direction.get2DDataValue());
        tag.putByte("Part", part.id);
        tag.putInt("Base", this.baseColor.getId());
        if (this.itemPatterns != null) {
            tag.put("Patterns", this.itemPatterns);
        }
        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        this.direction = Direction.from2DDataValue((int) (tag.getByte("Facing")));
        this.part = Part.fromId(tag.getByte("Part"));
        if (tag.contains("Base", TagTypes.INT_NBT_ID))
            this.baseColor = DyeColor.byId(tag.getInt("Base"));
        else this.baseColor = DyeColor.WHITE;
        if (tag.contains("Patterns", TagTypes.LIST_NBT_ID)) {
            this.itemPatterns = tag.getList("Patterns", TagTypes.COMPOUND_NBT_ID);
            this.patterns = null;
            this.receivedData = true;
        }
        super.readAdditionalSaveData(tag);
        this.setDirection(this.direction);
    }

    @OnlyIn(Dist.CLIENT)
    public List<Pair<BannerPattern, DyeColor>> getPatterns() {
        if (this.patterns == null && this.receivedData) {
            this.patterns = createPatterns(this.baseColor, this.itemPatterns);
        }
        return this.patterns;
    }

    @OnlyIn(Dist.CLIENT)
    public static List<Pair<BannerPattern, DyeColor>> createPatterns(DyeColor baseColor, @Nullable ListTag tag) {
        List<Pair<BannerPattern, DyeColor>> list = Lists.newArrayList();
        list.add(Pair.of(BannerPattern.BASE, baseColor));
        if (tag != null) {
            for (int i = 0; i < tag.size(); ++i) {
                CompoundTag compoundnbt = tag.getCompound(i);
                BannerPattern bannerpattern = BannerPattern.byHash(compoundnbt.getString("Pattern"));
                if (bannerpattern != null) {
                    int j = compoundnbt.getInt("Color");
                    list.add(Pair.of(bannerpattern, DyeColor.byId(j)));
                }
            }
        }
        return list;
    }

    @Override
    public int getWidth() {
        return 14;
    }

    @Override
    public int getHeight() {
        return (part == Part.FULL) ? 32 : 16;
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return getItem();
    }

    @Override
    public void dropItem(Entity entity) {
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.playSound(SoundEvents.BOOK_PAGE_TURN, 1.0F, 1.0F);
            if (entity instanceof Player) {
                Player playerentity = (Player) entity;
                if (playerentity.getAbilities().instabuild) {
                    return;
                }
            }
            this.spawnAtLocation(getItem());
        }
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.BOOK_PUT, 1.0F, 1.0F);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        Packet<?> packet = NetworkHooks.getEntitySpawningPacket(this);
        return packet;
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        // We cannot get the same bounding box output with different position inputs,
        // because server makes adjustments to that. As a result, we need to pass the
        // right position and hitbox straight over to the clients...
        buffer.writeByte(this.part.id);
        buffer.writeDouble(this.position().x);
        buffer.writeDouble(this.position().y);
        buffer.writeDouble(this.position().z);
        buffer.writeBlockPos(this.blockPosition());
        buffer.writeByte(direction.get2DDataValue());
        buffer.writeDouble(this.getBoundingBox().minX);
        buffer.writeDouble(this.getBoundingBox().minY);
        buffer.writeDouble(this.getBoundingBox().minZ);
        buffer.writeDouble(this.getBoundingBox().maxX);
        buffer.writeDouble(this.getBoundingBox().maxY);
        buffer.writeDouble(this.getBoundingBox().maxZ);
        CompoundTag tag = new CompoundTag();
        tag.putInt("Base",this.baseColor.getId());
        if (this.itemPatterns != null)
            tag.put("Patterns", this.itemPatterns);
        buffer.writeNbt(tag);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        // Client receives...
        this.part = Part.fromId(additionalData.readByte());
        this.setPosRaw(additionalData.readDouble(), additionalData.readDouble(), additionalData.readDouble());
        this.pos = additionalData.readBlockPos();
        byte dir = additionalData.readByte();
        //this.setDirection(Direction.from2DDataValue(dir)); This will call recalcuateBoundingBox, which isn't what we want
        this.direction = Direction.from2DDataValue(dir);
        this.setYRot((float) (this.direction.get2DDataValue() * 90));
        this.yRotO = this.getYRot();
        //this.recalculateBoundingBox(); To use this we'll need the original position data, which we don't have on the client-side
        this.setBoundingBox(new AABB(additionalData.readDouble(), additionalData.readDouble(), additionalData.readDouble(), additionalData.readDouble(), additionalData.readDouble(), additionalData.readDouble()));
        CompoundTag tag = additionalData.readNbt();
        if (tag.contains("Base", TagTypes.INT_NBT_ID))
            this.baseColor = DyeColor.byId(tag.getInt("Base"));
        if (tag.contains("Patterns", TagTypes.LIST_NBT_ID)) {
            this.itemPatterns = tag.getList("Patterns", TagTypes.COMPOUND_NBT_ID);
            this.patterns = null;
            this.receivedData = true;
        }
    }
}
