package com.devbobcorn.nekoration.client.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import com.devbobcorn.nekoration.NekoColors.EnumWoodenColor;
import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.blocks.ModBlocks;
import com.devbobcorn.nekoration.client.gui.widget.FilterButton;
import com.devbobcorn.nekoration.client.gui.widget.IconButton;
import com.devbobcorn.nekoration.items.DyeableBlockItem;
import com.devbobcorn.nekoration.items.HalfTimberBlockItem;
import com.devbobcorn.nekoration.items.ModItemTabs;
import com.devbobcorn.nekoration.utils.ItemIconHelper;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

// Creative Screen Things, adapted from MrCrayfish's Furniture Mod...
public class CreativeInventoryEvents
{
    private static final ResourceLocation ICONS = new ResourceLocation(Nekoration.MODID, "textures/gui/icons.png");
    private static int woodStartIndex;
    private static int decorStartIndex;

    private List<WoodFilter> woodFilters;
    private List<FilterButton> woodButtons;
    private List<DecorFilter> decorFilters;
    private List<FilterButton> decorButtons;
    private Button btnScrollUp;
    private Button btnScrollDown;
    private Button btnEnableAll;
    private Button btnDisableAll;
    private boolean viewingWoodTab;
    private boolean viewingDecorTab;
    private int guiCenterX = 0;
    private int guiCenterY = 0;

    @SubscribeEvent
    public void onPlayerLogout(ClientPlayerNetworkEvent.LoggedOutEvent event){
        this.woodFilters = null;
        this.decorFilters = null;
    }

    @SubscribeEvent
    @SuppressWarnings({"resource"})
    public void onScreenInit(GuiScreenEvent.InitGuiEvent.Post event){
        if(event.getGui() instanceof CreativeModeInventoryScreen){
            if(this.woodFilters == null)
                this.compileWoodItems();

            if(this.decorFilters == null)
                this.compileDecorItems();

            this.viewingWoodTab = false;
            this.viewingDecorTab = false;
            this.guiCenterX = ((CreativeModeInventoryScreen) event.getGui()).getGuiLeft();
            this.guiCenterY = ((CreativeModeInventoryScreen) event.getGui()).getGuiTop();
            this.woodButtons = new ArrayList<>();
            this.decorButtons = new ArrayList<>();

            event.addWidget(this.btnScrollUp = new IconButton(this.guiCenterX - 22, this.guiCenterY - 12, new TranslatableComponent("gui.nekoration.button.scroll_up"), button -> {
                if (viewingWoodTab) {
                    if (woodStartIndex > 0)
                        woodStartIndex--;
                    this.updateWoodTagButtons();
                }
                if (viewingDecorTab) {
                    if (decorStartIndex > 0)
                        decorStartIndex--;
                    this.updateDecorTagButtons();
                }
            }, ICONS, 64, 0));

            event.addWidget(this.btnScrollDown = new IconButton(this.guiCenterX - 22, this.guiCenterY + 127, new TranslatableComponent("gui.nekoration.button.scroll_down"), button -> {
                if (viewingWoodTab){
                    if (woodStartIndex <= woodFilters.size() - 4 - 1)
                        woodStartIndex++;
                    this.updateWoodTagButtons();
                }
                if (viewingDecorTab){
                    if (decorStartIndex <= decorFilters.size() - 4 - 1)
                        decorStartIndex++;
                    this.updateDecorTagButtons();
                }
            }, ICONS, 80, 0));

            event.addWidget(this.btnEnableAll = new IconButton(this.guiCenterX + 32, this.guiCenterY - 50, new TranslatableComponent("gui.nekoration.button.enable_all"), button -> {
                if (viewingWoodTab){
                    this.woodFilters.forEach(filters -> filters.setEnabled(true));
                    this.woodButtons.forEach(FilterButton::updateState);
                } else if (viewingDecorTab){
                    this.decorFilters.forEach(filters -> filters.setEnabled(true));
                    this.decorButtons.forEach(FilterButton::updateState);
                }
                Screen screen = Minecraft.getInstance().screen;
                if(screen instanceof CreativeModeInventoryScreen) {
                    if (viewingWoodTab)
                        this.updateWoodItems((CreativeModeInventoryScreen) screen);
                    else if (viewingDecorTab)
                        this.updateDecorItems((CreativeModeInventoryScreen) screen);
                }
            }, ICONS, 96, 0));

            event.addWidget(this.btnDisableAll = new IconButton(this.guiCenterX + 144, this.guiCenterY - 50, new TranslatableComponent("gui.nekoration.button.disable_all"), button -> {
                Screen screen = Minecraft.getInstance().screen;
                if (viewingWoodTab){
                    this.woodFilters.forEach(filters -> filters.setEnabled(false));
                    this.woodButtons.forEach(FilterButton::updateState);
                    if(screen instanceof CreativeModeInventoryScreen) {
                        this.updateWoodItems((CreativeModeInventoryScreen) screen);
                    }
                } else if (viewingDecorTab){
                    this.decorFilters.forEach(filters -> filters.setEnabled(false));
                    this.decorButtons.forEach(FilterButton::updateState);
                    if(screen instanceof CreativeModeInventoryScreen) {
                        this.updateDecorItems((CreativeModeInventoryScreen) screen);
                    }
                }
            }, ICONS, 112, 0));

            this.btnScrollUp.visible = false;
            this.btnScrollDown.visible = false;
            this.btnEnableAll.visible = false;
            this.btnDisableAll.visible = false;

            this.updateWoodTagButtons();
            this.updateDecorTagButtons();

            CreativeModeInventoryScreen screen = (CreativeModeInventoryScreen) event.getGui();
            if(screen.getSelectedTab() == ModItemTabs.WOODEN_GROUP.getId()){
                this.btnScrollUp.visible = this.btnScrollDown.visible = true;
                this.btnEnableAll.visible = this.btnDisableAll.visible = true;
                this.viewingWoodTab = true;
                this.viewingDecorTab = false;
                this.woodButtons.forEach(button -> button.visible = true);
                this.updateWoodItems(screen);
            } else if(screen.getSelectedTab() == ModItemTabs.DECOR_GROUP.getId()){
                this.btnScrollUp.visible = this.btnScrollDown.visible = true;
                this.btnEnableAll.visible = this.btnDisableAll.visible = true;
                this.viewingDecorTab = true;
                this.viewingWoodTab = false;
                this.decorButtons.forEach(button -> button.visible = true);
                this.updateDecorItems(screen);
            }
        }
    }

    @SubscribeEvent
    public void onScreenClick(GuiScreenEvent.MouseClickedEvent.Pre event){
        if(event.getButton() != GLFW.GLFW_MOUSE_BUTTON_LEFT)
            return;

        if(event.getGui() instanceof CreativeModeInventoryScreen){
            if (viewingWoodTab)
                for(Button button : this.woodButtons)
                    if(button.isMouseOver(event.getMouseX(), event.getMouseY()))
                        if(button.mouseClicked(event.getMouseX(), event.getMouseY(), event.getButton()))
                            return;
            
            if (viewingDecorTab)
                for(Button button : this.decorButtons)
                    if(button.isMouseOver(event.getMouseX(), event.getMouseY()))
                        if(button.mouseClicked(event.getMouseX(), event.getMouseY(), event.getButton()))
                            return;
        }
    }

    @SubscribeEvent
    public void onScreenDrawPre(GuiScreenEvent.DrawScreenEvent.Pre event){
        if(event.getGui() instanceof CreativeModeInventoryScreen){
            CreativeModeInventoryScreen screen = (CreativeModeInventoryScreen) event.getGui();
            if(screen.getSelectedTab() == ModItemTabs.WOODEN_GROUP.getId()){
                if(!this.viewingWoodTab){
                    this.updateWoodItems(screen);
                    this.viewingWoodTab = true;
                    this.viewingDecorTab = false;
                    // Update up/down buttons...
                    this.btnScrollUp.active = woodStartIndex > 0;
                    this.btnScrollDown.active = woodStartIndex <= this.woodFilters.size() - 4 - 1;
                }
            } else if(screen.getSelectedTab() == ModItemTabs.DECOR_GROUP.getId()){
                if(!this.viewingDecorTab){
                    this.updateDecorItems(screen);
                    this.viewingWoodTab = false;
                    this.viewingDecorTab = true;
                    // Update up/down buttons...
                    this.btnScrollUp.active = decorStartIndex > 0;
                    this.btnScrollDown.active = decorStartIndex <= this.decorFilters.size() - 4 - 1;
                }
            } else {
                this.viewingWoodTab = false;
                this.viewingDecorTab = false;
            }
        }
    }

    @SubscribeEvent
    public void onScreenDrawPost(GuiScreenEvent.DrawScreenEvent.Post event){
        if(event.getGui() instanceof CreativeModeInventoryScreen){
            CreativeModeInventoryScreen screen = (CreativeModeInventoryScreen) event.getGui();
            this.guiCenterX = screen.getGuiLeft();
            this.guiCenterY = screen.getGuiTop();

            if(screen.getSelectedTab() == ModItemTabs.WOODEN_GROUP.getId() || screen.getSelectedTab() == ModItemTabs.DECOR_GROUP.getId()){
                this.btnScrollUp.visible = true;
                this.btnScrollDown.visible = true;
                this.btnEnableAll.visible = true;
                this.btnDisableAll.visible = true;
                if (viewingWoodTab){
                    this.woodButtons.forEach(button -> button.visible = true);
                    /* Render buttons */
                    this.woodButtons.forEach(button -> {
                        button.render(event.getMatrixStack(), event.getMouseX(), event.getMouseY(), event.getRenderPartialTicks());
                    });
                    /* Render tooltips after so it renders above buttons */
                    this.woodButtons.forEach(button -> {
                        if(button.isMouseOver(event.getMouseX(), event.getMouseY())){
                            screen.renderTooltip(event.getMatrixStack(), button.getCategory().getName(), event.getMouseX(), event.getMouseY());
                        }
                    });
                } else if (viewingDecorTab){
                    this.decorButtons.forEach(button -> button.visible = true);
                    /* Render buttons */
                    this.decorButtons.forEach(button -> {
                        button.render(event.getMatrixStack(), event.getMouseX(), event.getMouseY(), event.getRenderPartialTicks());
                    });
                    /* Render tooltips after so it renders above buttons */
                    this.decorButtons.forEach(button -> {
                        if(button.isMouseOver(event.getMouseX(), event.getMouseY())){
                            screen.renderTooltip(event.getMatrixStack(), button.getCategory().getName(), event.getMouseX(), event.getMouseY());
                        }
                    });
                }
                if(this.btnEnableAll.isMouseOver(event.getMouseX(), event.getMouseY())){
                    screen.renderTooltip(event.getMatrixStack(), this.btnEnableAll.getMessage(), event.getMouseX(), event.getMouseY());
                }
                if(this.btnDisableAll.isMouseOver(event.getMouseX(), event.getMouseY())){
                    screen.renderTooltip(event.getMatrixStack(), this.btnDisableAll.getMessage(), event.getMouseX(), event.getMouseY());
                }
            } else {
                this.btnScrollUp.visible = false;
                this.btnScrollDown.visible = false;
                this.btnEnableAll.visible = false;
                this.btnDisableAll.visible = false;
                this.woodButtons.forEach(button -> button.visible = false);
                this.decorButtons.forEach(button -> button.visible = false);
            }
        }
    }

    @SuppressWarnings({"resource"})
    private void updateWoodTagButtons(){
        final Button.OnPress pressable = button -> {
            Screen screen = Minecraft.getInstance().screen;
            if(screen instanceof CreativeModeInventoryScreen){
                this.updateWoodItems((CreativeModeInventoryScreen) screen);
            }
        };
        this.woodButtons.clear();
        for(int i = woodStartIndex; i < woodStartIndex + 4 && i < this.woodFilters.size(); i++){
            FilterButton button = new FilterButton(this.guiCenterX - 28, this.guiCenterY + 29 * (i - woodStartIndex) + 10, this.woodFilters.get(i), pressable);
            this.woodButtons.add(button);
        }
        this.btnScrollUp.active = woodStartIndex > 0;
        this.btnScrollDown.active = woodStartIndex <= this.woodFilters.size() - 4 - 1;
    }

    @SuppressWarnings({"resource"})
    private void updateDecorTagButtons(){
        final Button.OnPress pressable = button -> {
            Screen screen = Minecraft.getInstance().screen;
            if(screen instanceof CreativeModeInventoryScreen){
                this.updateDecorItems((CreativeModeInventoryScreen) screen);
            }
        };
        this.decorButtons.clear();
        for(int i = decorStartIndex; i < decorStartIndex + 4 && i < this.decorFilters.size(); i++){
            FilterButton button = new FilterButton(this.guiCenterX - 28, this.guiCenterY + 29 * (i - decorStartIndex) + 10, this.decorFilters.get(i), pressable);
            this.decorButtons.add(button);
        }
        this.btnScrollUp.active = decorStartIndex > 0;
        this.btnScrollDown.active = decorStartIndex <= this.decorFilters.size() - 4 - 1;
    }

    private void updateWoodItems(CreativeModeInventoryScreen screen){
        CreativeModeInventoryScreen.ItemPickerMenu container = screen.getMenu();
        NonNullList<ItemStack> newItems = NonNullList.create();

        ForgeRegistries.ITEMS.getValues().stream()
        .filter(item -> item.getItemCategory() == ModItemTabs.WOODEN_GROUP)
        .filter(item -> item.getRegistryName().getNamespace().equals(Nekoration.MODID))
        .forEach(item -> {
            for(WoodFilter filter : woodFilters){
                if (filter.isEnabled() && item instanceof HalfTimberBlockItem){
                    ((HalfTimberBlockItem)item).fillItemCategoryWithWoodType(ModItemTabs.WOODEN_GROUP, filter.getWood(), newItems);
                }
            }
        });
        container.items.clear();
        container.items.addAll(newItems);
        container.items.sort(Comparator.comparingInt(o -> Item.getId(o.getItem())));
        container.scrollTo(0);
    }

    private void updateDecorItems(CreativeModeInventoryScreen screen){
        CreativeModeInventoryScreen.ItemPickerMenu container = screen.getMenu();
        NonNullList<ItemStack> newItems = NonNullList.create();

        ForgeRegistries.ITEMS.getValues().stream()
        .filter(item -> item.getItemCategory() == ModItemTabs.DECOR_GROUP)
        .filter(item -> item.getRegistryName().getNamespace().equals(Nekoration.MODID))
        .forEach(item -> {
            boolean t0, t1, t2, t3 = false;
            String itemId = ForgeRegistries.ITEMS.getKey(item).getPath();
            if ((t0 = item == ModBlocks.STONE_POT.get().asItem()) && decorFilters.get(0).enabled)
                item.fillItemCategory(ModItemTabs.DECOR_GROUP, newItems);
            else if ((t1 = item == ModBlocks.AWNING_PURE.get().asItem() ||
                    item == ModBlocks.AWNING_PURE_SHORT.get().asItem() ||
                    item == ModBlocks.AWNING_STRIPE.get().asItem() ||
                    item == ModBlocks.AWNING_STRIPE_SHORT.get().asItem()) && decorFilters.get(1).enabled)
                item.fillItemCategory(ModItemTabs.DECOR_GROUP, newItems);
            else if ((t2 = itemId.endsWith("table") || itemId.endsWith("chair")) && decorFilters.get(2).enabled)
                item.fillItemCategory(ModItemTabs.DECOR_GROUP, newItems);
            else if ((t3 = item == ModBlocks.EASEL_MENU.get().asItem() ||
                    item == ModBlocks.EASEL_MENU_WHITE.get().asItem() ||
                    item == ModBlocks.DRAWER.get().asItem() ||
                    item == ModBlocks.CABINET.get().asItem() ||
                    item == ModBlocks.DRAWER_CHEST.get().asItem() ||
                    item == ModBlocks.CUPBOARD.get().asItem() ||
                    item == ModBlocks.SHELF.get().asItem()) && decorFilters.get(3).enabled)
                item.fillItemCategory(ModItemTabs.DECOR_GROUP, newItems);
            else if (!(t0 || t1 || t2 || t3) && decorFilters.get(4).enabled) // Misc
                item.fillItemCategory(ModItemTabs.DECOR_GROUP, newItems);
        });
        container.items.clear();
        container.items.addAll(newItems);
        container.items.sort(Comparator.comparingInt(o -> Item.getId(o.getItem())));
        container.scrollTo(0);
    }

    private void compileWoodItems(){
        // Vanilla Wood Type
        final WoodFilter OAK = new WoodFilter(EnumWoodenColor.LIGHT_GRAY, new ItemStack(Blocks.OAK_PLANKS));
        final WoodFilter BIRCH = new WoodFilter(EnumWoodenColor.WHITE, new ItemStack(Blocks.BIRCH_PLANKS));
        final WoodFilter ACACIA = new WoodFilter(EnumWoodenColor.ORANGE, new ItemStack(Blocks.ACACIA_PLANKS));
        final WoodFilter JUNGLE = new WoodFilter(EnumWoodenColor.GRAY, new ItemStack(Blocks.JUNGLE_PLANKS));
        final WoodFilter SPRUCE = new WoodFilter(EnumWoodenColor.BROWN, new ItemStack(Blocks.SPRUCE_PLANKS));
        final WoodFilter WARPED = new WoodFilter(EnumWoodenColor.CYAN, new ItemStack(Blocks.WARPED_PLANKS));
        final WoodFilter CRIMSON = new WoodFilter(EnumWoodenColor.MAGENTA, new ItemStack(Blocks.CRIMSON_PLANKS));
        final WoodFilter DARK_OAK = new WoodFilter(EnumWoodenColor.BLACK, new ItemStack(Blocks.DARK_OAK_PLANKS));
        // BOP Wood Type
        final WoodFilter MAGIC = new WoodFilter(EnumWoodenColor.LIGHT_BLUE, ItemIconHelper.getCustomBlockItem(10001));
        final WoodFilter WILLOW = new WoodFilter(EnumWoodenColor.LIME, ItemIconHelper.getCustomBlockItem(10002));
        final WoodFilter MAHOGANY = new WoodFilter(EnumWoodenColor.PINK, ItemIconHelper.getCustomBlockItem(10003));
        final WoodFilter UMBRAN = new WoodFilter(EnumWoodenColor.PURPLE, ItemIconHelper.getCustomBlockItem(10004));
        final WoodFilter CHERRY = new WoodFilter(EnumWoodenColor.RED, ItemIconHelper.getCustomBlockItem(10005));
        final WoodFilter PALM = new WoodFilter(EnumWoodenColor.YELLOW, ItemIconHelper.getCustomBlockItem(10006));

        WoodFilter[] vanilla = new WoodFilter[] { OAK, BIRCH, ACACIA, JUNGLE, SPRUCE, WARPED, CRIMSON, DARK_OAK };
        WoodFilter[] extra   = new WoodFilter[] { MAGIC, WILLOW, MAHOGANY, UMBRAN, CHERRY, PALM };

        this.woodFilters = new ArrayList<>();
        this.woodFilters.addAll(Arrays.asList(vanilla));
        this.woodFilters.addAll(Arrays.asList(extra));
    }

    private void compileDecorItems(){
        // Prepare icon itemstacks...
        final ItemStack planterIcon = new ItemStack(ModBlocks.STONE_POT.get());
        DyeableBlockItem.setColor(planterIcon, EnumNekoColor.WHITE);
        final ItemStack awningIcon = new ItemStack(ModBlocks.AWNING_STRIPE_SHORT.get());
        DyeableBlockItem.setColor(awningIcon, EnumNekoColor.WHITE);
        final ItemStack containerIcon = new ItemStack(ModBlocks.DRAWER_CHEST.get());
        DyeableBlockItem.setColor(containerIcon, EnumNekoColor.ORANGE);
        final ItemStack miscIcon = new ItemStack(ModBlocks.CANDLE_HOLDER_GOLD.get());
        DyeableBlockItem.setColor(miscIcon, EnumNekoColor.WHITE);
        
        final DecorFilter PLANTER = new DecorFilter(EnumDecorType.PLANTER, planterIcon);
        final DecorFilter AWNING = new DecorFilter(EnumDecorType.AWNING, awningIcon);
        final DecorFilter FURNITURE = new DecorFilter(EnumDecorType.FURNITURE, new ItemStack(ModBlocks.SPRUCE_TABLE.get()));
        final DecorFilter CONTAINER = new DecorFilter(EnumDecorType.CONTAINER, containerIcon);
        final DecorFilter MISC = new DecorFilter(EnumDecorType.MISC, miscIcon);

        this.decorFilters = new ArrayList<>();
        this.decorFilters.addAll(Arrays.asList(PLANTER, AWNING, FURNITURE, CONTAINER, MISC));
    }

    public interface Filter {
        public void setEnabled(boolean enabled);
        public boolean isEnabled();
        public TranslatableComponent getName();
        public ItemStack getIcon();
    }

    public static class WoodFilter implements Filter {
        private final EnumWoodenColor wood;
        private final TranslatableComponent name;
        private final ItemStack icon;
        private boolean enabled = true;

        public WoodFilter(EnumWoodenColor type, ItemStack icon){
            this.wood = type;
            this.name = new TranslatableComponent(String.format("color.wooden.%s", type.getSerializedName().replace("/", ".")));
            this.icon = icon;
        }

        public EnumWoodenColor getWood(){
            return wood;
        }

        public ItemStack getIcon(){
            return this.icon;
        }

        public TranslatableComponent getName(){
            return this.name;
        }

        public void setEnabled(boolean enabled){
            this.enabled = enabled;
        }

        public boolean isEnabled(){
            return this.enabled;
        }
    }

    public static class DecorFilter implements Filter {
        private final EnumDecorType decor;
        private final TranslatableComponent name;
        private final ItemStack icon;
        private boolean enabled = true;

        public DecorFilter(EnumDecorType type, ItemStack icon){
            this.decor = type;
            this.name = new TranslatableComponent(String.format("decortype.%s", type.getSerializedName().replace("/", ".")));
            this.icon = icon;
        }

        public EnumDecorType getDecor(){
            return decor;
        }

        public ItemStack getIcon(){
            return this.icon;
        }

        public TranslatableComponent getName(){
            return this.name;
        }

        public void setEnabled(boolean enabled){
            this.enabled = enabled;
        }

        public boolean isEnabled(){
            return this.enabled;
        }
    }

    public enum EnumDecorType implements StringRepresentable {
        PLANTER("planter"),
        AWNING("awning"),
        FURNITURE("furniture"),
        CONTAINER("container"),
        MISC("misc");

        @Override
		public String toString() {
			return this.name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}

		private final String name;

        EnumDecorType(String i_name) {
			this.name = i_name;
		}
    }
}
