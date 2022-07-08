package com.devbobcorn.nekoration.recipes;

import com.devbobcorn.nekoration.NekoColors;
import com.devbobcorn.nekoration.items.DyeableBlockItem;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraftforge.registries.ForgeRegistries;

public class NekoStonecuttingRecipe extends StonecutterRecipe {
    public NekoStonecuttingRecipe(ResourceLocation id, String group, Ingredient in, ItemStack out) {
        super(id, group, in, out);

        if (!singleIngredientDyeable(in)){
            DyeableBlockItem.setColor(out, NekoColors.EnumNekoColor.LIGHT_GRAY);
        }
    }

    private static boolean singleIngredientDyeable(Ingredient in){
        // Ingredient should contain only one item in these recipes
        return in.getItems().length > 0 && in.getItems()[0].getItem() instanceof DyeableBlockItem;
    }

    public ItemStack getResult(){
        ItemStack finalResult = this.result.copy();

        return finalResult;
    }

    public ItemStack assemble(Container inv){
        ItemStack finalResult = this.result.copy();
        ItemStack in = inv.getItem(0);

        // Color inherit...
        if (in.getItem() instanceof DyeableBlockItem && finalResult.getItem() instanceof DyeableBlockItem){
            DyeableBlockItem.setColor(finalResult, DyeableBlockItem.getColor(in));
        }

        return finalResult;
    }

    public static class Serializer implements RecipeSerializer<NekoStonecuttingRecipe> {
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
            ItemStack out = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(s1)), i);

            if (!singleIngredientDyeable(in)){// Make from vanilla smooth stone, which is not dyeable
                // Set the result color to light gray
                DyeableBlockItem.setColor(out, NekoColors.EnumNekoColor.LIGHT_GRAY);
            }

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