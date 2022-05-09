package com.devbobcorn.nekoration.recipes;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.blocks.ModBlocks;
import com.devbobcorn.nekoration.items.DyeableBlockItem;
import com.google.gson.JsonObject;

import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.StonecutterRecipe;

public class NekoStonecuttingRecipe extends StonecutterRecipe {
    private static final Ingredient NEKO_STONES = Ingredient.of(ModBlocks.STONE_LAYERED.get().asItem(),
            ModBlocks.STONE_BASE.get().asItem(), ModBlocks.STONE_BASE_BOTTOM.get().asItem(),
            ModBlocks.STONE_FRAME.get().asItem(), ModBlocks.STONE_FRAME_BOTTOM.get().asItem(),
            ModBlocks.STONE_PILLAR.get().asItem(), ModBlocks.STONE_PILLAR_BOTTOM.get().asItem(),
            ModBlocks.STONE_CORINTHIAN.get().asItem(), ModBlocks.STONE_DORIC.get().asItem(),
            ModBlocks.STONE_IONIC.get().asItem());

    private static final Ingredient NEKO_STONIES = Ingredient.of(ModBlocks.STONE_POT.get().asItem(),
            ModBlocks.STONE_PLANTER.get().asItem(),
            ModBlocks.WINDOW_TOP.get().asItem(), ModBlocks.WINDOW_SILL.get().asItem(),
            ModBlocks.WINDOW_FRAME.get().asItem());

    public NekoStonecuttingRecipe(ResourceLocation id, String group, Ingredient in, ItemStack out) {
        super(id, group, in, out);

        if (NEKO_STONES.test(out)){
            DyeableBlockItem.setColor(out, NekoColors.EnumNekoColor.LIGHT_GRAY);
        }
    }

    public ItemStack getResult(){
        ItemStack finalResult = this.result;
        if (NEKO_STONES.test(finalResult) || NEKO_STONIES.test(finalResult)){
            DyeableBlockItem.setColor(finalResult, NekoColors.EnumNekoColor.LIGHT_GRAY);
        }

        return finalResult;
    }

    public ItemStack assemble(Container inv){
        ItemStack finalResult = this.result;
        ItemStack in = inv.getItem(0);
        if (NEKO_STONES.test(finalResult) || NEKO_STONIES.test(finalResult)){
            if (NEKO_STONES.test(in)){
                DyeableBlockItem.setColor(finalResult, DyeableBlockItem.getColor(in));
            } else DyeableBlockItem.setColor(finalResult, NekoColors.EnumNekoColor.LIGHT_GRAY);
        }

        return finalResult;
    }

    public static class Serializer extends
            net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<NekoStonecuttingRecipe> {
        @SuppressWarnings("deprecation")
        public NekoStonecuttingRecipe fromJson(ResourceLocation id, JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            Ingredient in;
            if (GsonHelper.isArrayNode(json, "ingredient")) {
                in = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "ingredient"));
            } else {
                in = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
            }

            String s1 = GsonHelper.getAsString(json, "result");
            int i = GsonHelper.getAsInt(json, "count");
            ItemStack out = new ItemStack(Registry.ITEM.get(new ResourceLocation(s1)), i);
            return new NekoStonecuttingRecipe(id, group, in, out);
        }

        public NekoStonecuttingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf packet) {
            String group = packet.readUtf(32767);
            Ingredient in = Ingredient.fromNetwork(packet);
            ItemStack out = packet.readItem();
            return new NekoStonecuttingRecipe(id, group, in, out);
        }

        public void toNetwork(FriendlyByteBuf packet, NekoStonecuttingRecipe recipe) {
            packet.writeUtf(recipe.group);
            recipe.ingredient.toNetwork(packet);
            packet.writeItem(recipe.result);
        }
    }
}