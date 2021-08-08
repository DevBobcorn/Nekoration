package com.devbobcorn.nekoration.client.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.devbobcorn.nekoration.NekoColors.EnumWoodenColor;
import com.devbobcorn.nekoration.Nekoration;
import com.devbobcorn.nekoration.client.gui.widget.IconButton;
import com.devbobcorn.nekoration.client.gui.widget.WoodTypeButton;
import com.devbobcorn.nekoration.items.HalfTimberBlockItem;
import com.devbobcorn.nekoration.items.ModItemTabs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

// Creative Screen Things, adapted from MrCrayfish's Furniture Mod...
@SuppressWarnings("unused")
public class CreativeInventoryEvents
{
    private static final ResourceLocation ICONS = new ResourceLocation(Nekoration.MODID, "textures/gui/icons.png");
    private static int startIndex;

    private List<WoodFilter> filters;
    private List<WoodTypeButton> buttons;
    private Button btnScrollUp;
    private Button btnScrollDown;
    private Button btnEnableAll;
    private Button btnDisableAll;
    private boolean viewingFurnitureTab;
    private int guiCenterX = 0;
    private int guiCenterY = 0;

    private static final Logger LOGGER = LogManager.getLogger("Creative Tab");

    @SubscribeEvent
    public void onPlayerLogout(ClientPlayerNetworkEvent.LoggedOutEvent event)
    {
        this.filters = null;
    }

    @SubscribeEvent
    @SuppressWarnings({"resource"})
    public void onScreenInit(GuiScreenEvent.InitGuiEvent.Post event)
    {
        if(event.getGui() instanceof CreativeScreen)
        {
            //LOGGER.info("Creative Screen Inited");
            if(this.filters == null)
            {
                this.compileItems();
            }

            this.viewingFurnitureTab = false;
            this.guiCenterX = ((CreativeScreen) event.getGui()).getGuiLeft();
            this.guiCenterY = ((CreativeScreen) event.getGui()).getGuiTop();
            this.buttons = new ArrayList<>();

            event.addWidget(this.btnScrollUp = new IconButton(this.guiCenterX - 22, this.guiCenterY - 12, new TranslationTextComponent("gui.nekoration.button.scroll_up"), button -> {
                if(startIndex > 0) startIndex--;
                this.updateTagButtons();
            }, ICONS, 64, 0));

            event.addWidget(this.btnScrollDown = new IconButton(this.guiCenterX - 22, this.guiCenterY + 127, new TranslationTextComponent("gui.nekoration.button.scroll_down"), button -> {
                if(startIndex <= filters.size() - 4 - 1) startIndex++;
                this.updateTagButtons();
            }, ICONS, 80, 0));

            event.addWidget(this.btnEnableAll = new IconButton(this.guiCenterX + 32, this.guiCenterY - 50, new TranslationTextComponent("gui.nekoration.button.enable_all"), button -> {
                this.filters.forEach(filters -> filters.setEnabled(true));
                this.buttons.forEach(WoodTypeButton::updateState);
                Screen screen = Minecraft.getInstance().screen;
                if(screen instanceof CreativeScreen) {
                    this.updateItems((CreativeScreen) screen);
                }
            }, ICONS, 96, 0));

            event.addWidget(this.btnDisableAll = new IconButton(this.guiCenterX + 144, this.guiCenterY - 50, new TranslationTextComponent("gui.nekoration.button.disable_all"), button -> {
                this.filters.forEach(filters -> filters.setEnabled(false));
                this.buttons.forEach(WoodTypeButton::updateState);
                Screen screen = Minecraft.getInstance().screen;
                if(screen instanceof CreativeScreen) {
                    this.updateItems((CreativeScreen) screen);
                }
            }, ICONS, 112, 0));

            this.btnScrollUp.visible = false;
            this.btnScrollDown.visible = false;
            this.btnEnableAll.visible = false;
            this.btnDisableAll.visible = false;

            this.updateTagButtons();

            CreativeScreen screen = (CreativeScreen) event.getGui();
            if(screen.getSelectedTab() == ModItemTabs.WOODEN_GROUP.getId())
            {
                this.btnScrollUp.visible = true;
                this.btnScrollDown.visible = true;
                this.btnEnableAll.visible = true;
                this.btnDisableAll.visible = true;
                this.viewingFurnitureTab = true;
                this.buttons.forEach(button -> button.visible = true);
                this.updateItems(screen);
            }
        }
    }

    @SubscribeEvent
    public void onScreenClick(GuiScreenEvent.MouseClickedEvent.Pre event)
    {
        if(event.getButton() != GLFW.GLFW_MOUSE_BUTTON_LEFT)
            return;

        if(event.getGui() instanceof CreativeScreen)
        {
            for(Button button : this.buttons)
            {
                if(button.isMouseOver(event.getMouseX(), event.getMouseY()))
                {
                    if(button.mouseClicked(event.getMouseX(), event.getMouseY(), event.getButton()))
                    {
                        return;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onScreenDrawPre(GuiScreenEvent.DrawScreenEvent.Pre event)
    {
        if(event.getGui() instanceof CreativeScreen)
        {
            CreativeScreen screen = (CreativeScreen) event.getGui();
            if(screen.getSelectedTab() == ModItemTabs.WOODEN_GROUP.getId())
            {
                if(!this.viewingFurnitureTab)
                {
                    this.updateItems(screen);
                    this.viewingFurnitureTab = true;
                }
            }
            else
            {
                this.viewingFurnitureTab = false;
            }
        }
    }

    @SubscribeEvent
    public void onScreenDrawPost(GuiScreenEvent.DrawScreenEvent.Post event)
    {
        if(event.getGui() instanceof CreativeScreen)
        {
            CreativeScreen screen = (CreativeScreen) event.getGui();
            this.guiCenterX = screen.getGuiLeft();
            this.guiCenterY = screen.getGuiTop();

            if(screen.getSelectedTab() == ModItemTabs.WOODEN_GROUP.getId())
            {
                this.btnScrollUp.visible = true;
                this.btnScrollDown.visible = true;
                this.btnEnableAll.visible = true;
                this.btnDisableAll.visible = true;
                this.buttons.forEach(button -> button.visible = true);

                /* Render buttons */
                this.buttons.forEach(button ->
                {
                    button.render(event.getMatrixStack(), event.getMouseX(), event.getMouseY(), event.getRenderPartialTicks());
                });

                /* Render tooltips after so it renders above buttons */
                this.buttons.forEach(button ->
                {
                    if(button.isMouseOver(event.getMouseX(), event.getMouseY()))
                    {
                        screen.renderTooltip(event.getMatrixStack(), button.getCategory().getName(), event.getMouseX(), event.getMouseY());
                    }
                });

                if(this.btnEnableAll.isMouseOver(event.getMouseX(), event.getMouseY()))
                {
                    screen.renderTooltip(event.getMatrixStack(), this.btnEnableAll.getMessage(), event.getMouseX(), event.getMouseY());
                }

                if(this.btnDisableAll.isMouseOver(event.getMouseX(), event.getMouseY()))
                {
                    screen.renderTooltip(event.getMatrixStack(), this.btnDisableAll.getMessage(), event.getMouseX(), event.getMouseY());
                }
            }
            else
            {
                this.btnScrollUp.visible = false;
                this.btnScrollDown.visible = false;
                this.btnEnableAll.visible = false;
                this.btnDisableAll.visible = false;
                this.buttons.forEach(button -> button.visible = false);
            }
        }
    }

    @SuppressWarnings({"resource"})
    private void updateTagButtons()
    {
        final Button.IPressable pressable = button ->
        {
            Screen screen = Minecraft.getInstance().screen;
            if(screen instanceof CreativeScreen)
            {
                this.updateItems((CreativeScreen) screen);
            }
        };
        this.buttons.clear();
        for(int i = startIndex; i < startIndex + 4 && i < this.filters.size(); i++)
        {
            WoodTypeButton button = new WoodTypeButton(this.guiCenterX - 28, this.guiCenterY + 29 * (i - startIndex) + 10, this.filters.get(i), pressable);
            this.buttons.add(button);
        }
        this.btnScrollUp.active = startIndex > 0;
        this.btnScrollDown.active = startIndex <= this.filters.size() - 4 - 1;
    }

    private void updateItems(CreativeScreen screen)
    {
        CreativeScreen.CreativeContainer container = screen.getMenu();

        NonNullList<ItemStack> newItems = NonNullList.create();

        ForgeRegistries.ITEMS.getValues().stream()
        .filter(item -> item.getItemCategory() == ModItemTabs.WOODEN_GROUP)
        .filter(item -> item.getRegistryName().getNamespace().equals(Nekoration.MODID))
        .forEach(item ->
        {
            //LOGGER.info(item.toString());
            for(WoodFilter filter : filters)
            {
                if (filter.isEnabled() && item instanceof HalfTimberBlockItem){
                    ((HalfTimberBlockItem)item).fillItemCategoryWithWoodType(ModItemTabs.WOODEN_GROUP, filter.getWood(), newItems);
                }
            }
        });
        // item.fillItemCategory(ModItemTabs.WOODEN_GROUP, newItems);

        container.items.clear();
        container.items.addAll(newItems);
        container.items.sort(Comparator.comparingInt(o -> Item.getId(o.getItem())));
        container.scrollTo(0);
    }

    private void compileItems()
    {
        final WoodFilter OAK = new WoodFilter(EnumWoodenColor.LIGHT_GRAY, new ItemStack(Blocks.OAK_PLANKS));
        final WoodFilter BIRCH = new WoodFilter(EnumWoodenColor.WHITE, new ItemStack(Blocks.BIRCH_PLANKS));
        final WoodFilter ACACIA = new WoodFilter(EnumWoodenColor.ORANGE, new ItemStack(Blocks.ACACIA_PLANKS));
        final WoodFilter JUNGLE = new WoodFilter(EnumWoodenColor.GRAY, new ItemStack(Blocks.JUNGLE_PLANKS));
        final WoodFilter SPRUCE = new WoodFilter(EnumWoodenColor.BROWN, new ItemStack(Blocks.SPRUCE_PLANKS));
        final WoodFilter WARPED = new WoodFilter(EnumWoodenColor.CYAN, new ItemStack(Blocks.WARPED_PLANKS));
        final WoodFilter CRIMSON = new WoodFilter(EnumWoodenColor.MAGENTA, new ItemStack(Blocks.CRIMSON_PLANKS));
        final WoodFilter DARK_OAK = new WoodFilter(EnumWoodenColor.BLACK, new ItemStack(Blocks.DARK_OAK_PLANKS));
        WoodFilter[] filters = new WoodFilter[] { OAK, BIRCH, ACACIA, JUNGLE, SPRUCE, WARPED, CRIMSON, DARK_OAK };

        this.filters = new ArrayList<>();
        this.filters.addAll(Arrays.asList(filters));
    }

    public static class WoodFilter
    {
        private final EnumWoodenColor wood;
        private final TranslationTextComponent name;
        private final ItemStack icon;
        private boolean enabled = true;

        public WoodFilter(EnumWoodenColor type, ItemStack icon)
        {
            this.wood = type;
            this.name = new TranslationTextComponent(String.format("color.wooden.%s", type.getSerializedName().replace("/", ".")));
            this.icon = icon;
        }

        public EnumWoodenColor getWood()
        {
            return wood;
        }

        public ItemStack getIcon()
        {
            return this.icon;
        }

        public TranslationTextComponent getName()
        {
            return this.name;
        }

        public void setEnabled(boolean enabled)
        {
            this.enabled = enabled;
        }

        public boolean isEnabled()
        {
            return this.enabled;
        }
    }
}
