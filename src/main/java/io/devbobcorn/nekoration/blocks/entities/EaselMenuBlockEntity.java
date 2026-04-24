package io.devbobcorn.nekoration.blocks.entities;

import javax.annotation.Nullable;

import io.devbobcorn.nekoration.blocks.DyeableBlock;
import io.devbobcorn.nekoration.blocks.containers.EaselMenuMenu;
import io.devbobcorn.nekoration.common.ComponentCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import io.devbobcorn.nekoration.registry.ModBlockEntities;

/**
 * 8-slot easel container storing line text, line colors, and glow state.
 */
public class EaselMenuBlockEntity extends RandomizableContainerBlockEntity {
    public static final int NUMBER_OF_SLOTS = 8;
    private static final int LINE_COUNT = 8;

    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            level.playSound(null, pos, SoundEvents.BARREL_OPEN, SoundSource.BLOCKS, 0.5F,
                    level.getRandom().nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            level.playSound(null, pos, SoundEvents.BARREL_CLOSE, SoundSource.BLOCKS, 0.5F,
                    level.getRandom().nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected boolean isOwnContainer(Player player) {
            return player.containerMenu instanceof EaselMenuMenu easelMenu && easelMenu.getEasel() == EaselMenuBlockEntity.this;
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int count, int oldCount) {
        }
    };

    private NonNullList<ItemStack> items = NonNullList.withSize(NUMBER_OF_SLOTS, ItemStack.EMPTY);
    private final Component[] messages = new Component[] {
            CommonComponents.EMPTY,
            CommonComponents.EMPTY,
            CommonComponents.EMPTY,
            CommonComponents.EMPTY,
            CommonComponents.EMPTY,
            CommonComponents.EMPTY,
            CommonComponents.EMPTY,
            CommonComponents.EMPTY
    };
    private final DyeColor[] textColors = new DyeColor[] {
            DyeColor.WHITE,
            DyeColor.WHITE,
            DyeColor.WHITE,
            DyeColor.WHITE,
            DyeColor.WHITE,
            DyeColor.WHITE,
            DyeColor.WHITE,
            DyeColor.WHITE
    };
    private boolean glowing;

    public EaselMenuBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EASEL_MENU.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, EaselMenuBlockEntity blockEntity) {
        if (!level.isClientSide()) {
            blockEntity.openersCounter.recheckOpeners(level, pos, state);
        }
    }

    @Override
    public int getContainerSize() {
        return NUMBER_OF_SLOTS;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> newItems) {
        items = newItems;
    }

    @Override
    protected Component getDefaultName() {
        BlockState state = getBlockState();
        if (state.hasProperty(DyeableBlock.COLOR)) {
            String colorKey = "color.nekoration." + state.getValue(DyeableBlock.COLOR).getSerializedName();
            return Component.translatable(state.getBlock().getDescriptionId(), ComponentCompat.interpolationArg(colorKey));
        }
        return Component.translatable(state.getBlock().getDescriptionId());
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory) {
        return new EaselMenuMenu(containerId, playerInventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, items, registries);
        }
        for (int i = 0; i < LINE_COUNT; i++) {
            tag.putString("Text" + (i + 1), Component.Serializer.toJson(messages[i], registries));
            tag.putString("Color" + i, textColors[i].getName());
        }
        tag.putBoolean("Glowing", glowing);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        if (!tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, items, registries);
        }
        for (int i = 0; i < LINE_COUNT; i++) {
            String json = tag.getString("Text" + (i + 1));
            messages[i] = Component.Serializer.fromJson(json.isEmpty() ? "\"\"" : json, registries);
            textColors[i] = DyeColor.byName(tag.getString("Color" + i), DyeColor.WHITE);
        }
        glowing = tag.getBoolean("Glowing");
    }

    public Component getMessage(int line) {
        return messages[line];
    }

    public Component[] getMessages() {
        Component[] copy = new Component[LINE_COUNT];
        System.arraycopy(messages, 0, copy, 0, LINE_COUNT);
        return copy;
    }

    public void setMessage(int line, Component text) {
        messages[line] = text;
        setChanged();
    }

    public DyeColor getColor(int line) {
        return textColors[line];
    }

    public DyeColor[] getColors() {
        DyeColor[] copy = new DyeColor[LINE_COUNT];
        System.arraycopy(textColors, 0, copy, 0, LINE_COUNT);
        return copy;
    }

    public void setColor(int line, DyeColor color) {
        textColors[line] = color;
        setChanged();
    }

    public void setColors(DyeColor[] colors) {
        for (int i = 0; i < LINE_COUNT && i < colors.length; i++) {
            textColors[i] = colors[i];
        }
        setChanged();
    }

    public boolean isGlowing() {
        return glowing;
    }

    public void setGlowing(boolean glow) {
        glowing = glow;
        setChanged();
    }

    public boolean toggleGlowing() {
        glowing = !glowing;
        setChanged();
        return glowing;
    }

    @Override
    public void startOpen(Player player) {
        if (!player.isSpectator()) {
            Level level = getLevel();
            if (level != null) {
                openersCounter.incrementOpeners(player, level, getBlockPos(), getBlockState());
            }
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!player.isSpectator()) {
            Level level = getLevel();
            if (level != null) {
                openersCounter.decrementOpeners(player, level, getBlockPos(), getBlockState());
            }
        }
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
