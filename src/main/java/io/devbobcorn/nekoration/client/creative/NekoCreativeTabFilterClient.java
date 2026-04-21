package io.devbobcorn.nekoration.client.creative;

import java.util.Arrays;

import io.devbobcorn.nekoration.HalfTimberCreativeTabOrdering;
import io.devbobcorn.nekoration.HalfTimberItemPaths;
import io.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.NekoWood;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import io.devbobcorn.nekoration.registry.HalfTimberRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

/**
 * Wood-type filter UI for the Nekoration creative tab (ported from 1.16.5 creative screen hooks).
 * <p>
 * Registered on {@link net.neoforged.neoforge.common.NeoForge#EVENT_BUS} from client setup.
 */
public final class NekoCreativeTabFilterClient {
    private static final boolean[] WOOD_ENABLED = new boolean[NekoWood.values().length];

    private static int woodStartIndex;
    private static CreativeTabIconButton btnScrollUp;
    private static CreativeTabIconButton btnScrollDown;
    private static WoodTypeFilterButton[] woodSlots = new WoodTypeFilterButton[4];
    private static CreativeModeTab lastSeenTab;
    private static boolean filterChromeShown;

    static {
        selectOnly(NekoWood.values()[0]);
    }

    private NekoCreativeTabFilterClient() {
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        selectOnly(NekoWood.values()[0]);
        woodStartIndex = 0;
        lastSeenTab = null;
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof CreativeModeInventoryScreen creative)) {
            return;
        }

        int left = creative.getGuiLeft();
        int top = creative.getGuiTop();

        // Match legacy Y (slightly above panel); top - 72 is often clipped above the window.
        btnScrollUp = new CreativeTabIconButton(left - 22, top - 12, Component.translatable("gui.nekoration.button.scroll_up"), () -> {
            if (woodStartIndex > 0) {
                woodStartIndex--;
                updateWoodSlotButtons(creative);
            }
        }, 64, 0);
        btnScrollDown = new CreativeTabIconButton(left - 22, top + 127, Component.translatable("gui.nekoration.button.scroll_down"), () -> {
            NekoWood[] v = NekoWood.values();
            if (woodStartIndex <= v.length - 4 - 1) {
                woodStartIndex++;
                updateWoodSlotButtons(creative);
            }
        }, 80, 0);

        event.addListener(btnScrollUp);
        event.addListener(btnScrollDown);

        for (int i = 0; i < 4; i++) {
            WoodTypeFilterButton b = new WoodTypeFilterButton(0, 0, (wood, on) -> {
                selectOnly(wood);
                updateWoodSlotButtons(creative);
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

        updateWoodSlotButtons(creative);
        setFilterChromeVisible(false);

        if (isOurTab(CreativeInventoryReflection.getSelectedTab())) {
            setFilterChromeVisible(true);
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
            if (isOurTab(cur)) {
                repositionChrome(creative);
                setFilterChromeVisible(true);
                applyFilteredItems(creative);
            } else {
                setFilterChromeVisible(false);
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
    }

    private static void setFilterChromeVisible(boolean visible) {
        filterChromeShown = visible;
        if (btnScrollUp == null) {
            return;
        }
        btnScrollUp.setFilterUiActive(visible);
        btnScrollDown.setFilterUiActive(visible);
        for (WoodTypeFilterButton b : woodSlots) {
            if (b != null) {
                b.setFilterUiActive(visible && b.isBound());
            }
        }
        if (visible) {
            refreshScrollButtonStates();
        }
    }

    private static void refreshScrollButtonStates() {
        NekoWood[] v = NekoWood.values();
        btnScrollUp.active = woodStartIndex > 0;
        btnScrollDown.active = woodStartIndex <= v.length - 4 - 1;
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
                woodSlots[i].bind(w, WOOD_ENABLED[w.ordinal()], left - 28, top + 29 * i + 10);
            } else {
                woodSlots[i].bind(null, true, 0, 0);
            }
            woodSlots[i].setFilterUiActive(filterChromeShown && woodSlots[i].isBound());
        }
        refreshScrollButtonStates();
    }

    private static void selectOnly(NekoWood selected) {
        Arrays.fill(WOOD_ENABLED, false);
        WOOD_ENABLED[selected.ordinal()] = true;
    }

    private static boolean isOurTab(CreativeModeTab tab) {
        return tab != null && tab == Nekoration.NEKORATION_HALF_TIMBER_TAB.get();
    }

    private static void applyFilteredItems(CreativeModeInventoryScreen screen) {
        if (!isOurTab(CreativeInventoryReflection.getSelectedTab())) {
            return;
        }
        if (!(screen.getMenu() instanceof CreativeModeInventoryScreen.ItemPickerMenu picker)) {
            return;
        }
        NonNullList<ItemStack> out = NonNullList.create();
        for (var holder : HalfTimberRegistration.blockItemsView()) {
            Item item = holder.get();
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            if (id == null || !Nekoration.MODID.equals(id.getNamespace())) {
                continue;
            }
            NekoWood w = HalfTimberItemPaths.parseWood(id.getPath());
            if (w == null) {
                continue;
            }
            if (WOOD_ENABLED[w.ordinal()]) {
                out.add(DyeableBlockItem.createCreativeTabStack(item, EnumNekoColor.WHITE));
            }
        }
        out.sort(HalfTimberCreativeTabOrdering.stackComparator());
        picker.items.clear();
        picker.items.addAll(out);
        picker.scrollTo(0f);
    }
}
