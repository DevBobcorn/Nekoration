package com.devbobcorn.nekoration.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

// This type of shaped crafting recipe is used in Nekoration to get result items with NBT,
// which is not supported in vanilla recipes.
public class NekoShapedRecipe extends CustomRecipe implements net.minecraftforge.common.crafting.IShapedRecipe<CraftingContainer> {
    static int MAX_WIDTH = 3;
    static int MAX_HEIGHT = 3;

    /**
     * Expand the max width and height allowed in the deserializer. This should be
     * called by modders who add custom crafting tables that are larger than the
     * vanilla 3x3.
     * 
     * @param width
     *            your max recipe width
     * @param height
     *            your max recipe height
     */
    public static void setCraftingSize(int width, int height) {
        if (MAX_WIDTH < width)
            MAX_WIDTH = width;
        if (MAX_HEIGHT < height)
            MAX_HEIGHT = height;
    }

    private final int width;
    private final int height;
    private final NonNullList<Ingredient> recipeItems;
    private final ItemStack result;
    private final String group;

    public NekoShapedRecipe(ResourceLocation id, String group, int w, int h, NonNullList<Ingredient> in, ItemStack out) {
        super(id, CraftingBookCategory.MISC);
        this.group = group;
        this.width = w;
        this.height = h;
        this.recipeItems = in;
        this.result = out;
    }

    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.NEKO_SHAPED.get();
    }

    public String getGroup() {
        return this.group;
    }

    public ItemStack getResultItem() {
        return this.result;
    }

    public NonNullList<Ingredient> getIngredients() {
        return this.recipeItems;
    }

    public boolean canCraftInDimensions(int w, int h) {
        return w >= this.width && h >= this.height;
    }

    public boolean matches(CraftingContainer inv, Level world) {
        for (int i = 0; i <= inv.getWidth() - this.width; ++i) {
            for (int j = 0; j <= inv.getHeight() - this.height; ++j) {
                if (this.matches(inv, i, j, true)) {
                    return true;
                }
                if (this.matches(inv, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matches(CraftingContainer inv, int a, int b, boolean mirrored) {
        for (int i = 0; i < inv.getWidth(); ++i) {
            for (int j = 0; j < inv.getHeight(); ++j) {
                int k = i - a;
                int l = j - b;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < this.width && l < this.height) {
                    if (mirrored) {
                        ingredient = this.recipeItems.get(this.width - k - 1 + l * this.width);
                    } else {
                        ingredient = this.recipeItems.get(k + l * this.width);
                    }
                }

                if (!ingredient.test(inv.getItem(i + j * inv.getWidth()))) {
                    return false;
                }
            }
        }

        return true;
    }

    public ItemStack assemble(CraftingContainer inv) {
        return this.result.copy();
    }

    public int getWidth() {
        return this.width;
    }

    @Override
    public int getRecipeWidth() {
        return getWidth();
    }

    public int getHeight() {
        return this.height;
    }

    @Override
    public int getRecipeHeight() {
        return getHeight();
    }

    private static NonNullList<Ingredient> dissolvePattern(String[] pattern, Map<String, Ingredient> ingredients, int width, int height) {
        NonNullList<Ingredient> nonnulllist = NonNullList.withSize(width * height, Ingredient.EMPTY);
        Set<String> set = Sets.newHashSet(ingredients.keySet());
        set.remove(" ");

        for (int i = 0; i < pattern.length; ++i) {
            for (int j = 0; j < pattern[i].length(); ++j) {
                String s = pattern[i].substring(j, j + 1);
                Ingredient ingredient = ingredients.get(s);
                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
                }
                set.remove(s);
                nonnulllist.set(j + width * i, ingredient);
            }
        }

        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        } else {
            return nonnulllist;
        }
    }

    @VisibleForTesting
    static String[] shrink(String... recipe) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for (int i1 = 0; i1 < recipe.length; ++i1) {
            String s = recipe[i1];
            i = Math.min(i, firstNonSpace(s));
            int j1 = lastNonSpace(s);
            j = Math.max(j, j1);
            if (j1 < 0) {
                if (k == i1) {
                    ++k;
                }
                ++l;
            } else {
                l = 0;
            }
        }
        if (recipe.length == l) {
            return new String[0];
        } else {
            String[] astring = new String[recipe.length - l - k];

            for (int k1 = 0; k1 < astring.length; ++k1) {
                astring[k1] = recipe[k1 + k].substring(i, j + 1);
            }
            return astring;
        }
    }

    private static int firstNonSpace(String line) {
        int i;
        for (i = 0; i < line.length() && line.charAt(i) == ' '; ++i) {
        }
        return i;
    }

    private static int lastNonSpace(String line) {
        int i;
        for (i = line.length() - 1; i >= 0 && line.charAt(i) == ' '; --i) {
        }
        return i;
    }

    private static String[] patternFromJson(JsonArray arr) {
        String[] astring = new String[arr.size()];
        if (astring.length > MAX_HEIGHT) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum");
        } else if (astring.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for (int i = 0; i < astring.length; ++i) {
                String s = GsonHelper.convertToString(arr.get(i), "pattern[" + i + "]");
                if (s.length() > MAX_WIDTH) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, " + MAX_WIDTH + " is maximum");
                }
                if (i > 0 && astring[0].length() != s.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }
                astring[i] = s;
            }
            return astring;
        }
    }

    private static Map<String, Ingredient> keyFromJson(JsonObject obj) {
        Map<String, Ingredient> map = Maps.newHashMap();

        for (Entry<String, JsonElement> entry : obj.entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + (String) entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }
            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }
            map.put(entry.getKey(), Ingredient.fromJson(entry.getValue()));
        }
        map.put(" ", Ingredient.EMPTY);
        return map;
    }

    public static ItemStack itemFromJson(JsonObject obj) {
        if (obj.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        } else {
            return net.minecraftforge.common.crafting.CraftingHelper.getItemStack(obj, true);
        }
    }

    public static class Serializer implements RecipeSerializer<NekoShapedRecipe> {
        public NekoShapedRecipe fromJson(ResourceLocation id, JsonObject json) {
            String s = GsonHelper.getAsString(json, "group", "");
            Map<String, Ingredient> ingredientKeys = NekoShapedRecipe
                    .keyFromJson(GsonHelper.getAsJsonObject(json, "key"));
            String[] astring = NekoShapedRecipe
                    .shrink(NekoShapedRecipe.patternFromJson(GsonHelper.getAsJsonArray(json, "pattern")));
            int i = astring[0].length();
            int j = astring.length;
            NonNullList<Ingredient> nonnulllist = NekoShapedRecipe.dissolvePattern(astring, ingredientKeys, i, j);
            ItemStack itemstack = NekoShapedRecipe.itemFromJson(GsonHelper.getAsJsonObject(json, "result"));
            return new NekoShapedRecipe(id, s, i, j, nonnulllist, itemstack);
        }

        public NekoShapedRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf packet) {
            int i = packet.readVarInt();
            int j = packet.readVarInt();
            String s = packet.readUtf(32767);
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);

            for (int k = 0; k < nonnulllist.size(); ++k) {
                nonnulllist.set(k, Ingredient.fromNetwork(packet));
            }
            ItemStack itemstack = packet.readItem();
            return new NekoShapedRecipe(id, s, i, j, nonnulllist, itemstack);
        }

        public void toNetwork(FriendlyByteBuf packet, NekoShapedRecipe recipe) {
            packet.writeVarInt(recipe.width);
            packet.writeVarInt(recipe.height);
            packet.writeUtf(recipe.group);

            for (Ingredient ingredient : recipe.recipeItems) {
                ingredient.toNetwork(packet);
            }
            packet.writeItem(recipe.result);
        }
    }

    // JEI Support
    public List<List<ItemStack>> getInputs(){
        List<List<ItemStack>> slots = new ArrayList<>();
        for (Ingredient ingredient : this.recipeItems){ // For each slot...
            List<ItemStack> variants = new ArrayList<>();
            for (ItemStack ingrediantStack : ingredient.getItems()){ // For each possible item in this slot...
                variants.add(ingrediantStack.copy());
            }
            slots.add(variants);
        }
        return slots;
    }

    public List<ItemStack> getOutputs(){
        List<ItemStack> variants = new ArrayList<>();
        variants.add(this.result.copy());
        return variants;
    }
}
