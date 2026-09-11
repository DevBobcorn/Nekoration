package io.devbobcorn.nekoration.client.creative;

import io.devbobcorn.nekoration.HalfTimberCreativeTabOrdering;
import io.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import io.devbobcorn.nekoration.blocks.NekoStone;
import io.devbobcorn.nekoration.blocks.NekoWood;
import io.devbobcorn.nekoration.blocks.OrnamentCategory;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import io.devbobcorn.nekoration.registry.ModCreativeTabs;
import io.devbobcorn.nekoration.registry.ModItems;
import io.devbobcorn.nekoration.registry.OrnamentRegistration;
import io.devbobcorn.nekoration.registry.StoneBlockRegistration;
import io.devbobcorn.nekoration.registry.WoodenBlockRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;
import net.minecraft.client.gui.components.AbstractWidget;

/**
 * Wood-type, stone-type and ornament-category filter UI for Nekoration creative tabs (ported from 1.16.5 creative screen hooks).
 * <p>
 * Platform client code forwards the corresponding screen events into
 * {@link #onScreenInit}, {@link #onRender} and {@link #reset}.
 */
public final class NekoCreativeTabFilterClient {
    /** Which filter button column is currently shown. */
    private enum FilterUi {
        WOOD,
        STONE,
        ORNAMENT
    }

    private static NekoWood selectedWood = NekoWood.values()[0];
    private static NekoStone selectedStone = NekoStone.values()[0];
    private static OrnamentCategory selectedOrnamentCategory = OrnamentCategory.values()[0];

    private static int woodStartIndex;
    private static int stoneStartIndex;
    private static int ornamentStartIndex;
    private static CreativeTabIconButton btnScrollUp;
    private static CreativeTabIconButton btnScrollDown;
    private static WoodTypeFilterButton[] woodSlots = new WoodTypeFilterButton[4];
    private static StoneTypeFilterButton[] stoneSlots = new StoneTypeFilterButton[4];
    private static OrnamentTypeFilterButton[] ornamentSlots = new OrnamentTypeFilterButton[4];
    private static CreativeModeTab lastSeenTab;
    /** Kind of filter chrome currently visible (scroll/slots); {@code null} when hidden. */
    @Nullable
    private static FilterUi filterUi;

    private NekoCreativeTabFilterClient() {
    }

    /** Clears filter state; called when the player logs out. */
    public static void reset() {
        selectWoodType(NekoWood.values()[0]);
        selectStoneType(NekoStone.values()[0]);
        selectOrnamentCategory(OrnamentCategory.values()[0]);
        woodStartIndex = 0;
        stoneStartIndex = 0;
        ornamentStartIndex = 0;
        lastSeenTab = null;
    }

    /**
     * Adds the filter chrome to a newly opened creative screen.
     * Platform client code calls this from its screen-init event for
     * {@link CreativeModeInventoryScreen} instances.
     */
    public static void onScreenInit(CreativeModeInventoryScreen creative, Consumer<AbstractWidget> addListener) {
        int left = CreativeInventoryReflection.getGuiLeft(creative);
        int top = CreativeInventoryReflection.getGuiTop(creative);

        btnScrollUp = new CreativeTabIconButton(left - 22, top - 12, Component.translatable("gui.nekoration.button.scroll_up"), () -> {
            if (filterUi == FilterUi.STONE) {
                if (stoneStartIndex > 0) {
                    stoneStartIndex--;
                    updateStoneSlotButtons(creative);
                    refreshScrollButtonStates();
                }
            } else if (filterUi == FilterUi.ORNAMENT) {
                if (ornamentStartIndex > 0) {
                    ornamentStartIndex--;
                    updateOrnamentSlotButtons(creative);
                    refreshScrollButtonStates();
                }
            } else {
                if (woodStartIndex > 0) {
                    woodStartIndex--;
                    updateWoodSlotButtons(creative);
                    refreshScrollButtonStates();
                }
            }
        }, 64, 0);
        btnScrollDown = new CreativeTabIconButton(left - 22, top + 127, Component.translatable("gui.nekoration.button.scroll_down"), () -> {
            if (filterUi == FilterUi.STONE) {
                NekoStone[] sv = NekoStone.values();
                if (stoneStartIndex <= sv.length - 4 - 1) {
                    stoneStartIndex++;
                    updateStoneSlotButtons(creative);
                    refreshScrollButtonStates();
                }
            } else if (filterUi == FilterUi.ORNAMENT) {
                OrnamentCategory[] cv = OrnamentCategory.values();
                if (ornamentStartIndex <= cv.length - 4 - 1) {
                    ornamentStartIndex++;
                    updateOrnamentSlotButtons(creative);
                    refreshScrollButtonStates();
                }
            } else {
                NekoWood[] v = NekoWood.values();
                if (woodStartIndex <= v.length - 4 - 1) {
                    woodStartIndex++;
                    updateWoodSlotButtons(creative);
                    refreshScrollButtonStates();
                }
            }
        }, 80, 0);

        addListener.accept(btnScrollUp);
        addListener.accept(btnScrollDown);

        for (int i = 0; i < 4; i++) {
            WoodTypeFilterButton b = new WoodTypeFilterButton(0, 0, (wood, on) -> {
                selectWoodType(wood);
                updateWoodSlotButtons(creative);
                refreshScrollButtonStates();
                Minecraft mc = Minecraft.getInstance();
                if (mc.screen instanceof CreativeModeInventoryScreen open) {
                    applyFilteredItems(open);
                } else {
                    applyFilteredItems(creative);
                }
            });
            woodSlots[i] = b;
            addListener.accept(b);
        }

        for (int i = 0; i < 4; i++) {
            StoneTypeFilterButton b = new StoneTypeFilterButton(0, 0, (stone, on) -> {
                selectStoneType(stone);
                updateStoneSlotButtons(creative);
                refreshScrollButtonStates();
                Minecraft mc = Minecraft.getInstance();
                if (mc.screen instanceof CreativeModeInventoryScreen open) {
                    applyFilteredItems(open);
                } else {
                    applyFilteredItems(creative);
                }
            });
            stoneSlots[i] = b;
            addListener.accept(b);
        }

        for (int i = 0; i < 4; i++) {
            OrnamentTypeFilterButton b = new OrnamentTypeFilterButton(0, 0, (category, on) -> {
                selectOrnamentCategory(category);
                updateOrnamentSlotButtons(creative);
                refreshScrollButtonStates();
                Minecraft mc = Minecraft.getInstance();
                if (mc.screen instanceof CreativeModeInventoryScreen open) {
                    applyFilteredItems(open);
                } else {
                    applyFilteredItems(creative);
                }
            });
            ornamentSlots[i] = b;
            addListener.accept(b);
        }

        CreativeModeTab selected = CreativeInventoryReflection.getSelectedTab();
        updateWoodSlotButtons(creative);
        updateStoneSlotButtons(creative);
        updateOrnamentSlotButtons(creative);
        setFilterChromeForTab(selected);
        if (isWoodenTab(selected) || isStoneTab(selected) || isOrnamentTab(selected)) {
            applyFilteredItems(creative);
        }
    }

    /**
     * Reacts to tab switches on the creative screen.
     * Platform client code calls this from its pre-render event for
     * {@link CreativeModeInventoryScreen} instances.
     */
    public static void onRender(CreativeModeInventoryScreen creative) {
        CreativeModeTab cur = CreativeInventoryReflection.getSelectedTab();
        if (cur != lastSeenTab) {
            lastSeenTab = cur;
            if (isWoodenTab(cur) || isStoneTab(cur) || isOrnamentTab(cur)) {
                repositionChrome(creative);
                setFilterChromeForTab(cur);
                applyFilteredItems(creative);
            } else {
                setFilterChromeForTab(null);
            }
        }
    }

    private static void repositionChrome(CreativeModeInventoryScreen creative) {
        if (btnScrollUp == null) {
            return;
        }
        int left = CreativeInventoryReflection.getGuiLeft(creative);
        int top = CreativeInventoryReflection.getGuiTop(creative);
        btnScrollUp.setPosition(left - 22, top - 12);
        btnScrollDown.setPosition(left - 22, top + 127);
        updateWoodSlotButtons(creative);
        updateStoneSlotButtons(creative);
        updateOrnamentSlotButtons(creative);
    }

    private static void setFilterChromeForTab(@Nullable CreativeModeTab tab) {
        boolean wooden = isWoodenTab(tab);
        boolean stone = isStoneTab(tab);
        boolean ornament = isOrnamentTab(tab);
        boolean showChrome = wooden || stone || ornament;
        filterUi = wooden ? FilterUi.WOOD : stone ? FilterUi.STONE : ornament ? FilterUi.ORNAMENT : null;
        if (btnScrollUp == null) {
            return;
        }
        btnScrollUp.setFilterUiActive(showChrome);
        btnScrollDown.setFilterUiActive(showChrome);
        for (WoodTypeFilterButton b : woodSlots) {
            if (b != null) {
                b.setFilterUiActive(wooden && b.isBound());
            }
        }
        for (StoneTypeFilterButton b : stoneSlots) {
            if (b != null) {
                b.setFilterUiActive(stone && b.isBound());
            }
        }
        for (OrnamentTypeFilterButton b : ornamentSlots) {
            if (b != null) {
                b.setFilterUiActive(ornament && b.isBound());
            }
        }
        if (showChrome) {
            refreshScrollButtonStates();
        }
    }

    private static void refreshScrollButtonStates() {
        if (btnScrollUp == null || !btnScrollUp.visible) {
            return;
        }
        if (filterUi == FilterUi.STONE) {
            NekoStone[] sv = NekoStone.values();
            btnScrollUp.active = stoneStartIndex > 0;
            btnScrollDown.active = stoneStartIndex <= sv.length - 4 - 1;
        } else if (filterUi == FilterUi.ORNAMENT) {
            OrnamentCategory[] cv = OrnamentCategory.values();
            btnScrollUp.active = ornamentStartIndex > 0;
            btnScrollDown.active = ornamentStartIndex <= cv.length - 4 - 1;
        } else {
            NekoWood[] v = NekoWood.values();
            btnScrollUp.active = woodStartIndex > 0;
            btnScrollDown.active = woodStartIndex <= v.length - 4 - 1;
        }
    }

    private static void updateWoodSlotButtons(CreativeModeInventoryScreen creative) {
        if (woodSlots[0] == null) {
            return;
        }
        int left = CreativeInventoryReflection.getGuiLeft(creative);
        int top = CreativeInventoryReflection.getGuiTop(creative);
        NekoWood[] v = NekoWood.values();
        for (int i = 0; i < 4; i++) {
            int idx = woodStartIndex + i;
            if (idx < v.length) {
                NekoWood w = v[idx];
                woodSlots[i].bind(w, selectedWood == w, left - 28, top + 29 * i + 10);
            } else {
                woodSlots[i].bind(null, true, 0, 0);
            }
        }
    }

    private static void updateStoneSlotButtons(CreativeModeInventoryScreen creative) {
        if (stoneSlots[0] == null) {
            return;
        }
        int left = CreativeInventoryReflection.getGuiLeft(creative);
        int top = CreativeInventoryReflection.getGuiTop(creative);
        NekoStone[] v = NekoStone.values();
        for (int i = 0; i < 4; i++) {
            int idx = stoneStartIndex + i;
            if (idx < v.length) {
                NekoStone s = v[idx];
                stoneSlots[i].bind(s, selectedStone == s, left - 28, top + 29 * i + 10);
            } else {
                stoneSlots[i].bind(null, true, 0, 0);
            }
        }
    }

    private static void updateOrnamentSlotButtons(CreativeModeInventoryScreen creative) {
        if (ornamentSlots[0] == null) {
            return;
        }
        int left = CreativeInventoryReflection.getGuiLeft(creative);
        int top = CreativeInventoryReflection.getGuiTop(creative);
        OrnamentCategory[] v = OrnamentCategory.values();
        for (int i = 0; i < 4; i++) {
            int idx = ornamentStartIndex + i;
            if (idx < v.length) {
                OrnamentCategory c = v[idx];
                ornamentSlots[i].bind(c, selectedOrnamentCategory == c, left - 28, top + 29 * i + 10);
            } else {
                ornamentSlots[i].bind(null, true, 0, 0);
            }
        }
    }

    private static void selectWoodType(NekoWood selected) {
        selectedWood = selected;
    }

    private static void selectStoneType(NekoStone selected) {
        selectedStone = selected;
    }

    private static void selectOrnamentCategory(OrnamentCategory selected) {
        selectedOrnamentCategory = selected;
    }

    private static boolean isWoodenTab(CreativeModeTab tab) {
        return tab != null && tab == ModCreativeTabs.NEKORATION_WOODEN_BLOCKS_TAB.get();
    }

    private static boolean isStoneTab(CreativeModeTab tab) {
        return tab != null && tab == ModCreativeTabs.NEKORATION_STONE_BLOCKS_TAB.get();
    }

    private static boolean isOrnamentTab(CreativeModeTab tab) {
        return tab != null && tab == ModCreativeTabs.NEKORATION_ORNAMENTS_TAB.get();
    }

    private static void prependFilterIconIfMissing(ItemStack icon, NonNullList<ItemStack> out) {
        if (icon.isEmpty()) {
            return;
        }
        for (ItemStack stack : out) {
            if (ItemStack.isSameItemSameComponents(stack, icon)) {
                return;
            }
        }
        out.add(0, icon);
    }

    private static void applyFilteredItems(CreativeModeInventoryScreen screen) {
        CreativeModeTab tab = CreativeInventoryReflection.getSelectedTab();
        if (!(screen.getMenu() instanceof CreativeModeInventoryScreen.ItemPickerMenu picker)) {
            return;
        }
        if (isWoodenTab(tab)) {
            NonNullList<ItemStack> out = NonNullList.create();
            for (var holder : WoodenBlockRegistration.dyedItemsForWood(selectedWood)) {
                Item item = holder.get();
                out.add(DyeableBlockItem.createCreativeTabStack(item, EnumNekoColor.WHITE));
            }
            for (var holder : WoodenBlockRegistration.windowItemsForWood(selectedWood)) {
                out.add(new ItemStack(holder.get()));
            }
            WoodenBlockRegistration.addFurnitureStacksForWood(selectedWood, out::add);
            WoodenBlockRegistration.addContainerStacksForWood(selectedWood, out::add);
            out.sort(HalfTimberCreativeTabOrdering.stackComparator());
            prependFilterIconIfMissing(new ItemStack(selectedWood.planks().asItem()), out);
            picker.items.clear();
            picker.items.addAll(out);
            picker.scrollTo(0f);
            return;
        }
        if (isStoneTab(tab)) {
            NonNullList<ItemStack> out = NonNullList.create();
            for (var supplier : StoneBlockRegistration.itemSuppliersForStone(selectedStone)) {
                out.add(new ItemStack(supplier.get()));
            }
            prependFilterIconIfMissing(new ItemStack(selectedStone.vanillaStoneBlock().asItem()), out);
            picker.items.clear();
            picker.items.addAll(out);
            picker.scrollTo(0f);
            return;
        }
        if (isOrnamentTab(tab)) {
            NonNullList<ItemStack> out = NonNullList.create();
            switch (selectedOrnamentCategory) {
                case POTS_AND_PLANTERS -> OrnamentRegistration.addPotsAndPlantersCategoryStacks(out::add);
                case WINDOW_ATTACHMENT -> OrnamentRegistration.addAwningCategoryStacks(out::add);
                case FURNITURE -> {
                    WoodenBlockRegistration.addFurnitureCategoryStacks(out::add);
                    OrnamentRegistration.addFurnitureCategoryStacks(out::add);
                }
                case CONTAINER -> WoodenBlockRegistration.addContainerCategoryStacks(out::add);
                case MISC -> {
                    OrnamentRegistration.addMiscCategoryStacks(out::add);
                    out.add(new ItemStack(ModItems.PAINTING.get()));
                    out.add(new ItemStack(ModItems.PALETTE.get()));
                }
            }
            picker.items.clear();
            picker.items.addAll(out);
            picker.scrollTo(0f);
        }
    }
}
