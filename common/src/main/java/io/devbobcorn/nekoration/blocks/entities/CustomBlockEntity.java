package io.devbobcorn.nekoration.blocks.entities;

import java.util.Arrays;

import javax.annotation.Nullable;

import io.devbobcorn.nekoration.blocks.containers.CustomBlockMenu;
import io.devbobcorn.nekoration.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Holds up to {@value #MAX_ENTRIES} display entries for a single Custom Block.
 * Each entry carries its own block state, tint and transform, and the active
 * entry is the one edited by palettes and paw tools.
 */
public class CustomBlockEntity extends BlockEntity implements MenuProvider {
    public static final int MAX_ENTRIES = 16;
    private static final int DIRECTION_STEPS = 24;

    private static final String ENTRIES = "Entries";
    private static final String ACTIVE = "Active";

    private final CustomEntry[] entries = new CustomEntry[MAX_ENTRIES];
    private int active;

    public CustomBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CUSTOM_BLOCK.get(), pos, state);
    }

    @Nullable
    public CustomEntry entry(int index) {
        return index >= 0 && index < MAX_ENTRIES ? entries[index] : null;
    }

    public int activeIndex() {
        return active;
    }

    @Nullable
    public CustomEntry activeEntry() {
        return entries[active];
    }

    public int entryCount() {
        int count = 0;
        for (CustomEntry entry : entries) {
            if (entry != null) {
                count++;
            }
        }
        return count;
    }

    /** Index of the entry the player is pointing at, or {@code -1} when none is hit. */
    public int pointedEntry(BlockGetter level, BlockPos pos, Player player) {
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getViewVector(1.0F).scale(player.blockInteractionRange() + 1.0));
        int pointed = -1;
        double closest = Double.MAX_VALUE;
        CollisionContext context = CollisionContext.of(player);
        for (int index = 0; index < MAX_ENTRIES; index++) {
            CustomEntry entry = entries[index];
            if (entry == null) {
                continue;
            }
            BlockHitResult clip = entry.displayState().getShape(level, pos, context).clip(start, end, pos);
            if (clip != null) {
                double distance = clip.getLocation().distanceToSqr(start);
                if (distance < closest) {
                    closest = distance;
                    pointed = index;
                }
            }
        }
        return pointed;
    }

    public void setActive(int index) {
        active = Mth.clamp(index, 0, MAX_ENTRIES - 1);
    }

    /** Adds a state to the first free slot, replacing the active entry when full. */
    public int addEntry(BlockState displayState) {
        int slot = firstEmptySlot();
        if (slot < 0) {
            slot = active;
        }
        entries[slot] = new CustomEntry(displayState);
        active = slot;
        return slot;
    }

    public void removeEntry(int index) {
        if (index >= 0 && index < MAX_ENTRIES) {
            entries[index] = null;
        }
    }

    public boolean canAddEntry(BlockState displayState, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape interaction = displayState.getShape(level, pos, context);
        VoxelShape collision = displayState.getCollisionShape(level, pos, context);
        for (CustomEntry entry : entries) {
            if (entry == null) {
                continue;
            }
            BlockState existing = entry.displayState();
            if (Shapes.joinIsNotEmpty(interaction, existing.getShape(level, pos, context), BooleanOp.AND)
                    || Shapes.joinIsNotEmpty(collision, existing.getCollisionShape(level, pos, context),
                            BooleanOp.AND)) {
                return false;
            }
        }
        return true;
    }

    public void tintActive(int rgb) {
        CustomEntry entry = activeEntry();
        if (entry != null) {
            entry.setTint(rgb);
        }
    }

    public void clearActiveTint() {
        CustomEntry entry = activeEntry();
        if (entry != null) {
            entry.clearTint();
        }
    }

    public void toggleActiveTintAllFaces() {
        CustomEntry entry = activeEntry();
        if (entry != null) {
            entry.toggleTintAllFaces();
        }
    }

    /** Moves the active entry along one axis, in 1/32 block units. */
    public void moveActive(int axis, int amount) {
        CustomEntry entry = activeEntry();
        if (entry != null) {
            entry.move(axis, amount);
        }
    }

    /** Rotates the active entry around the Y axis, in 15 degree steps. */
    public void rotateActive(int steps) {
        CustomEntry entry = activeEntry();
        if (entry != null) {
            entry.rotate(steps);
        }
    }

    private int firstEmptySlot() {
        for (int index = 0; index < MAX_ENTRIES; index++) {
            if (entries[index] == null) {
                return index;
            }
        }
        return -1;
    }

    /** Saves the block entity and tells clients to re-render it. */
    public void markUpdated() {
        setChanged();
        Level level = getLevel();
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    public boolean stillValid(Player player) {
        Level level = getLevel();
        if (level == null || level.getBlockEntity(worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr(Vec3.atCenterOf(worldPosition)) <= 64.0D;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ListTag list = new ListTag();
        for (CustomEntry entry : entries) {
            if (entry != null) {
                list.add(entry.save());
            }
        }
        tag.put(ENTRIES, list);
        tag.putByte(ACTIVE, (byte) active);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        Arrays.fill(entries, null);
        HolderGetter<Block> blocks = registries.lookupOrThrow(Registries.BLOCK);
        ListTag list = tag.getList(ENTRIES, Tag.TAG_COMPOUND);
        for (int index = 0; index < Math.min(MAX_ENTRIES, list.size()); index++) {
            entries[index] = CustomEntry.load(list.getCompound(index), blocks);
        }
        active = Mth.clamp(tag.getByte(ACTIVE), 0, MAX_ENTRIES - 1);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CustomBlockMenu(containerId, playerInventory, this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /** A single displayed block state with its own tint and transform. */
    public static final class CustomEntry {
        private static final String DISPLAY = "Display";
        private static final String TINTED = "Tinted";
        private static final String TINT_ALL_FACES = "TintAllFaces";
        private static final String COLOR = "Color";
        private static final String DIR = "Dir";
        private static final String OFFSET = "Offset";

        private final BlockState displayState;
        private boolean tinted;
        private boolean tintAllFaces = true;
        private int color = 0xFFFFFF;
        private byte dir;
        private int[] offset = new int[3];

        public CustomEntry(BlockState displayState) {
            this.displayState = displayState;
        }

        public BlockState displayState() {
            return displayState;
        }

        public boolean tinted() {
            return tinted;
        }

        public boolean tintAllFaces() {
            return tintAllFaces;
        }

        public int color() {
            return color;
        }

        public byte dir() {
            return dir;
        }

        public int offset(int axis) {
            return offset[axis];
        }

        public void setTint(int rgb) {
            tinted = true;
            color = rgb & 0xFFFFFF;
        }

        public void clearTint() {
            tinted = false;
        }

        public void toggleTintAllFaces() {
            tintAllFaces = !tintAllFaces;
        }

        public void move(int axis, int amount) {
            if (axis < 0 || axis > 2) {
                return;
            }
            offset[axis] += amount;
        }

        public void rotate(int steps) {
            dir = (byte) Mth.positiveModulo(dir + steps, DIRECTION_STEPS);
        }

        private CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.put(DISPLAY, NbtUtils.writeBlockState(displayState));
            tag.putBoolean(TINTED, tinted);
            tag.putBoolean(TINT_ALL_FACES, tintAllFaces);
            tag.putInt(COLOR, color);
            tag.putByte(DIR, dir);
            tag.putIntArray(OFFSET, offset);
            return tag;
        }

        @Nullable
        private static CustomEntry load(CompoundTag tag, HolderGetter<Block> blocks) {
            if (!tag.contains(DISPLAY, Tag.TAG_COMPOUND)) {
                return null;
            }
            BlockState displayState = NbtUtils.readBlockState(blocks, tag.getCompound(DISPLAY));
            if (displayState.isAir()) {
                return null;
            }
            CustomEntry entry = new CustomEntry(displayState);
            entry.tinted = tag.getBoolean(TINTED);
            entry.tintAllFaces = !tag.contains(TINT_ALL_FACES, Tag.TAG_BYTE) || tag.getBoolean(TINT_ALL_FACES);
            entry.color = tag.contains(COLOR, Tag.TAG_ANY_NUMERIC) ? tag.getInt(COLOR) & 0xFFFFFF : 0xFFFFFF;
            entry.dir = (byte) Mth.positiveModulo(tag.getByte(DIR), DIRECTION_STEPS);
            int[] offset = tag.getIntArray(OFFSET);
            if (offset.length == 3) {
                entry.offset = offset;
            }
            return entry;
        }
    }
}
