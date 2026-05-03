package io.devbobcorn.nekoration.client.creative;

import java.util.Comparator;

import io.devbobcorn.nekoration.HalfTimberCreativeTabOrdering;
import io.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.NekoStone;
import io.devbobcorn.nekoration.blocks.NekoWood;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import io.devbobcorn.nekoration.registry.StoneBlockRegistration;
import io.devbobcorn.nekoration.registry.WoodenBlockRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Wood-type and stone-type filter UI for Nekoration creative tabs (ported from 1.16.5 creative screen hooks).
 * <p>
 * Registered on {@link net.neoforged.neoforge.common.NeoForge#EVENT_BUS} from client setup.
 */
public final class NekoCreativeTabFilterClient {
    private static NekoWood selectedWood = NekoWood.values()[0];
    private static NekoStone selectedStone = NekoStone.values()[0];

    private static int woodStartIndex;
    private static int stoneStartIndex;
    private static CreativeTabIconButton btnScrollUp;
    private static CreativeTabIconButton btnScrollDown;
    private static WoodTypeFilterButton[] woodSlots = new WoodTypeFilterButton[4];
    private static StoneTypeFilterButton[] stoneSlots = new StoneTypeFilterButton[4];
    private static CreativeModeTab lastSeenTab;
    /** When the filter chrome is visible, whether scroll/slots refer to stone ({@code true}) or wood ({@code false}). */
    private static boolean stoneFilterUi;

    private NekoCreativeTabFilterClient() {
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        selectWoodType(NekoWood.values()[0]);
        selectStoneType(NekoStone.values()[0]);
        woodStartIndex = 0;
        stoneStartIndex = 0;
        lastSeenTab = null;
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof CreativeModeInventoryScreen creative)) {
            return;
        }

        int left = creative.getGuiLeft();
        int top = creative.getGuiTop();

        btnScrollUp = new CreativeTabIconButton(left - 22, top - 12, Component.translatable("gui.nekoration.button.scroll_up"), () -> {
            if (stoneFilterUi) {
                if (stoneStartIndex > 0) {
                    stoneStartIndex--;
                    updateStoneSlotButtons(creative);
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
            if (stoneFilterUi) {
                NekoStone[] sv = NekoStone.values();
                if (stoneStartIndex <= sv.length - 4 - 1) {
                    stoneStartIndex++;
                    updateStoneSlotButtons(creative);
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

        event.addListener(btnScrollUp);
        event.addListener(btnScrollDown);

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
            event.addListener(b);
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
            event.addListener(b);
        }

        CreativeModeTab selected = CreativeInventoryReflection.getSelectedTab();
        updateWoodSlotButtons(creative);
        updateStoneSlotButtons(creative);
        setFilterChromeForTab(selected);
        if (isWoodenTab(selected) || isStoneTab(selected)) {
            applyFilteredItems(creative);
        }
    }

    @SubscribeEvent
    public static void onRenderPre(ScreenEvent.Render.Pre event) {
        if (!(event.getScreen() instanceof CreativeModeInventoryScreen creative)) {
            return;
        }
        CreativeModeTab cur = CreativeInventoryReflection.getSelectedTab();
        if (cur != lastSeenTab) {
            lastSeenTab = cur;
            if (isWoodenTab(cur) || isStoneTab(cur)) {
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
        int left = creative.getGuiLeft();
        int top = creative.getGuiTop();
        btnScrollUp.setPosition(left - 22, top - 12);
        btnScrollDown.setPosition(left - 22, top + 127);
        updateWoodSlotButtons(creative);
        updateStoneSlotButtons(creative);
    }

    private static void setFilterChromeForTab(@Nullable CreativeModeTab tab) {
        boolean wooden = isWoodenTab(tab);
        boolean stone = isStoneTab(tab);
        boolean showChrome = wooden || stone;
        stoneFilterUi = stone;
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
        if (showChrome) {
            refreshScrollButtonStates();
        }
    }

    private static void refreshScrollButtonStates() {
        if (btnScrollUp == null || !btnScrollUp.visible) {
            return;
        }
        if (stoneFilterUi) {
            NekoStone[] sv = NekoStone.values();
            btnScrollUp.active = stoneStartIndex > 0;
            btnScrollDown.active = stoneStartIndex <= sv.length - 4 - 1;
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
        int left = creative.getGuiLeft();
        int top = creative.getGuiTop();
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
        int left = creative.getGuiLeft();
        int top = creative.getGuiTop();
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

    private static void selectWoodType(NekoWood selected) {
        selectedWood = selected;
    }

    private static void selectStoneType(NekoStone selected) {
        selectedStone = selected;
    }

    private static boolean isWoodenTab(CreativeModeTab tab) {
        return tab != null && tab == Nekoration.NEKORATION_WOODEN_BLOCKS_TAB.get();
    }

    private static boolean isStoneTab(CreativeModeTab tab) {
        return tab != null && tab == Nekoration.NEKORATION_STONE_BLOCKS_TAB.get();
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
            for (var holder : WoodenBlockRegistration.plainItemsForWood(selectedWood)) {
                Item item = holder.get();
                if (item instanceof DyeableBlockItem) {
                    out.add(DyeableBlockItem.createCreativeTabStack(item, EnumNekoColor.WHITE));
                    out.add(DyeableBlockItem.createCreativeTabStack(item, EnumNekoColor.BLACK));
                } else {
                    out.add(new ItemStack(item));
                }
            }
            out.sort(HalfTimberCreativeTabOrdering.stackComparator());
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
            out.sort(Comparator.comparingInt(s -> BuiltInRegistries.ITEM.getId(s.getItem())));
            picker.items.clear();
            picker.items.addAll(out);
            picker.scrollTo(0f);
        }
    }
}
