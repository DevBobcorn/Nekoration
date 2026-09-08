package io.devbobcorn.nekoration.datagen;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import io.devbobcorn.nekoration.NekoColors.EnumNekoColor;
import io.devbobcorn.nekoration.Nekoration;
import io.devbobcorn.nekoration.blocks.NekoStone;
import io.devbobcorn.nekoration.blocks.NekoWood;
import io.devbobcorn.nekoration.items.DyeableBlockItem;
import io.devbobcorn.nekoration.recipes.ColorInheritShapedRecipe;
import io.devbobcorn.nekoration.recipes.ColorInheritStonecuttingRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

/**
 * Recipe generator for wooden, cement and stone blocks, ported from
 * reference/nekoration-1.19.
 */
public final class NekoRecipeProvider extends RecipeProvider {
    private static final int SMELTING_TIME = 200;
    private static final float SMELTING_XP = 0.1F;

    private static final List<String> CEMENT_VARIANTS = List.of("trimmed_cement", "paneled_cement", "layered_cement",
            "cement_frame_head", "cement_frame_peak", "cement_frame_sill", "cement_frame_side", "cement_pot",
            "cement_planter");

    private static final List<String[]> HALF_TIMBER_VARIANT_PATTERNS = List.of(
            new String[] { "  1", " 0 ", "1  " },
            new String[] { "1  ", " 0 ", "  1" },
            new String[] { "  1", " 0 ", "1 1" },
            new String[] { "1  ", " 0 ", "1 1" },
            new String[] { " 1 ", "101" },
            new String[] { "1 1", " 0 ", "1 1" },
            new String[] { " 1 ", "101", " 1 " },
            new String[] { "111", "101", "111" },
            new String[] { " 1 ", " 0 ", " 1 " });

    public NekoRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output, HolderLookup.Provider registries) {
        woodenBlockRecipes(output);
        cementBlockRecipes(output);
        stoneBlockRecipes(output);
    }

    private void woodenBlockRecipes(RecipeOutput output) {
        for (NekoWood wood : NekoWood.values()) {
            if (wood.isBiomesOPlenty()) {
                continue;
            }
            String woodId = wood.id();
            ItemLike planks = wood.planks();
            String unlockPlanks = "has_" + woodId + "_planks";

            Item halfTimberBase = modItem(woodId + "_half_timber_p0");
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, dyed(halfTimberBase, EnumNekoColor.WHITE, 1))
                    .pattern("1P1").pattern("P0P").pattern("1P1")
                    .define('1', Items.STICK).define('P', Items.PAPER).define('0', planks)
                    .unlockedBy(unlockPlanks, has(planks))
                    .save(output, modLoc(woodId + "_half_timber_p0"));
            for (int p = 1; p <= 9; p++) {
                saveColorInheritShaped(output, modLoc(woodId + "_half_timber_p" + p),
                        RecipeCategory.BUILDING_BLOCKS,
                        dyed(modItem(woodId + "_half_timber_p" + p), EnumNekoColor.WHITE, 1),
                        HALF_TIMBER_VARIANT_PATTERNS.get(p - 1), halfTimberBase, planks);
            }

            Item windowBase = modItem(woodId + "_window_simple");
            String unlockWindow = "has_" + woodId + "_window_simple";
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, windowBase, 4)
                    .pattern("010").pattern("1#1").pattern("010")
                    .define('0', planks).define('1', Items.STICK).define('#', Items.GLASS)
                    .unlockedBy(unlockPlanks, has(planks))
                    .save(output, modLoc(woodId + "_window_simple"));
            saveWindowVariant(output, woodId, "arch", new String[] { "111", "101" }, windowBase, unlockWindow);
            saveWindowVariant(output, woodId, "cross", new String[] { " 1 ", "101", " 1 " }, windowBase, unlockWindow);
            saveWindowVariant(output, woodId, "lancet", new String[] { " 1 ", "101", "111" }, windowBase, unlockWindow);
            saveWindowVariant(output, woodId, "shade", new String[] { "111", "101", "111" }, windowBase, unlockWindow);

            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, modItem(woodId + "_chair"), 4)
                    .pattern("#  ").pattern("###").pattern("# #")
                    .define('#', planks)
                    .unlockedBy(unlockPlanks, has(planks))
                    .save(output, modLoc(woodId + "_chair"));
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, modItem(woodId + "_table"), 4)
                    .pattern("###").pattern("# #").pattern("# #")
                    .define('#', planks)
                    .unlockedBy(unlockPlanks, has(planks))
                    .save(output, modLoc(woodId + "_table"));
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, modItem(woodId + "_round_table"), 4)
                    .pattern("###").pattern(" # ").pattern(" # ")
                    .define('#', planks)
                    .unlockedBy(unlockPlanks, has(planks))
                    .save(output, modLoc(woodId + "_round_table"));
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, modItem(woodId + "_glass_table"), 4)
                    .pattern("#0#").pattern("# #").pattern("# #")
                    .define('#', planks).define('0', Items.GLASS)
                    .unlockedBy(unlockPlanks, has(planks))
                    .save(output, modLoc(woodId + "_glass_table"));
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, modItem(woodId + "_round_glass_table"), 4)
                    .pattern("000").pattern(" # ").pattern(" # ")
                    .define('0', Items.GLASS).define('#', planks)
                    .unlockedBy(unlockPlanks, has(planks))
                    .save(output, modLoc(woodId + "_round_glass_table"));
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, modItem(woodId + "_armchair"), 4)
                    .pattern("#  ").pattern("#11").pattern("###")
                    .define('1', Items.STICK).define('#', planks)
                    .unlockedBy(unlockPlanks, has(planks))
                    .save(output, modLoc(woodId + "_armchair"));
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, modItem(woodId + "_bench"), 4)
                    .pattern("T  ").pattern("###").pattern("# #")
                    .define('T', signOf(wood)).define('#', planks)
                    .unlockedBy(unlockPlanks, has(planks))
                    .save(output, modLoc(woodId + "_bench"));

            String unlockContainers = "has_" + woodId + "_cabinet";
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, modItem(woodId + "_drawer"))
                    .pattern("###").pattern("#0#").pattern("# #")
                    .define('#', planks).define('0', Items.CHEST)
                    .unlockedBy(unlockContainers, has(Items.CHEST))
                    .save(output, modLoc(woodId + "_drawer"));
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, modItem(woodId + "_cabinet"))
                    .pattern("###").pattern("0#0").pattern("###")
                    .define('#', planks).define('0', Items.CHEST)
                    .unlockedBy(unlockContainers, has(Items.CHEST))
                    .save(output, modLoc(woodId + "_cabinet"));
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, modItem(woodId + "_drawer_chest"))
                    .pattern("###").pattern("#0#").pattern("#0#")
                    .define('#', planks).define('0', Items.CHEST)
                    .unlockedBy(unlockContainers, has(Items.CHEST))
                    .save(output, modLoc(woodId + "_drawer_chest"));
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, modItem(woodId + "_cupboard"))
                    .pattern("###").pattern("#10").pattern("###")
                    .define('#', planks).define('1', pressurePlateOf(wood)).define('0', Items.GLASS_PANE)
                    .unlockedBy(unlockContainers, has(Items.CHEST))
                    .save(output, modLoc(woodId + "_cupboard"));
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, modItem(woodId + "_cupboard"))
                    .pattern("###").pattern("#1 ").pattern("###")
                    .define('#', planks).define('1', pressurePlateOf(wood))
                    .unlockedBy(unlockContainers, has(Items.CHEST))
                    .save(output, modLoc(woodId + "_shelf"));
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, modItem(woodId + "_wall_shelf"))
                    .pattern("##").pattern("1 ")
                    .define('1', Items.STICK).define('#', pressurePlateOf(wood))
                    .unlockedBy(unlockContainers, has(Items.CHEST))
                    .save(output, modLoc(woodId + "_wall_shelf"));
            Item easelMenu = modItem(woodId + "_easel_menu");
            for (EnumNekoColor color : EnumNekoColor.values()) {
                ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, dyed(easelMenu, color, 4))
                        .pattern("#0#").pattern("#0#").pattern("# #")
                        .define('#', planks).define('0', concreteOf(color))
                        .unlockedBy(unlockContainers, has(Items.CHEST))
                        .save(output, modLoc(woodId + "_easel_menu_" + color.getSerializedName()));
            }
        }
    }

    private void cementBlockRecipes(RecipeOutput output) {
        Item cement = modItem("cement");
        for (String variant : CEMENT_VARIANTS) {
            saveColorInheritStonecutting(output, modLoc(variant + "_from_cement_stonecutting"),
                    RecipeCategory.BUILDING_BLOCKS, cement, dyed(modItem(variant), EnumNekoColor.WHITE, 1));
        }
    }

    private void stoneBlockRecipes(RecipeOutput output) {
        for (NekoStone stone : NekoStone.values()) {
            String stoneId = stone.id();
            Item baseStone = stone.vanillaStoneBlock().asItem();
            Item smoothSource;
            if (stone.needsSmoothVariant()) {
                Item smooth = modItem("smooth_" + stoneId);
                smoothSource = smooth;
                SimpleCookingRecipeBuilder.smelting(Ingredient.of(baseStone), RecipeCategory.BUILDING_BLOCKS,
                        smooth, SMELTING_XP, SMELTING_TIME)
                        .unlockedBy(hasName(baseStone), has(baseStone))
                        .save(output, modLoc("smooth_" + stoneId + "_from_smelting"));
                stonecutting(output, smoothSource, "smooth_" + stoneId + "_stairs");
                stonecutting(output, smoothSource, "smooth_" + stoneId + "_slab");
            } else {
                smoothSource = switch (stone) {
                    case STONE -> Items.SMOOTH_STONE;
                    case SANDSTONE -> Items.SMOOTH_SANDSTONE;
                    case RED_SANDSTONE -> Items.SMOOTH_RED_SANDSTONE;
                    default -> throw new IllegalStateException("No vanilla smooth block for " + stone);
                };
                if (stone == NekoStone.STONE) {
                    stonecutting(output, smoothSource, "smooth_stone_stairs");
                }
            }
            stonecutting(output, smoothSource, "polished_smooth_" + stoneId);
            Item polishedSmooth = modItem("polished_smooth_" + stoneId);
            stonecutting(output, polishedSmooth, "polished_smooth_" + stoneId + "_stairs");
            stonecutting(output, polishedSmooth, "polished_smooth_" + stoneId + "_slab");
        }
    }

    private void stonecutting(RecipeOutput output, Item source, String resultId) {
        String sourceName = BuiltInRegistries.ITEM.getKey(source).getPath();
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(source), RecipeCategory.BUILDING_BLOCKS, modItem(resultId))
                .unlockedBy(hasName(source), has(source))
                .save(output, modLoc(resultId + "_from_" + sourceName + "_stonecutting"));
    }

    private void saveWindowVariant(RecipeOutput output, String woodId, String variant, String[] pattern,
            Item windowBase, String unlock) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS, modItem(woodId + "_window_" + variant))
                .define('0', windowBase).define('1', Items.STICK)
                .unlockedBy(unlock, has(windowBase));
        for (String row : pattern) {
            builder.pattern(row);
        }
        builder.save(output, modLoc(woodId + "_window_" + variant));
    }

    private void saveColorInheritShaped(RecipeOutput output, ResourceLocation id, RecipeCategory category,
            ItemStack result, String[] pattern, Item keyIngredient, ItemLike unlockItem) {
        Map<Character, Ingredient> key = new LinkedHashMap<>();
        key.put('1', Ingredient.of(Items.STICK));
        key.put('0', Ingredient.of(keyIngredient));
        ShapedRecipePattern shapedPattern = ShapedRecipePattern.of(key, Arrays.asList(pattern));
        AdvancementHolder advancement = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR)
                .addCriterion(hasName(unlockItem), has(unlockItem))
                .build(id.withPrefix("recipes/" + category.getFolderName() + "/"));
        ColorInheritShapedRecipe recipe = new ColorInheritShapedRecipe("",
                RecipeBuilder.determineBookCategory(category), shapedPattern, result, true);
        output.accept(id, recipe, advancement);
    }

    private void saveColorInheritStonecutting(RecipeOutput output, ResourceLocation id, RecipeCategory category,
            Item ingredient, ItemStack result) {
        AdvancementHolder advancement = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR)
                .addCriterion(hasName(ingredient), has(ingredient))
                .build(id.withPrefix("recipes/" + category.getFolderName() + "/"));
        ColorInheritStonecuttingRecipe recipe = new ColorInheritStonecuttingRecipe("",
                Ingredient.of(ingredient), result);
        output.accept(id, recipe, advancement);
    }

    private static ItemStack dyed(ItemLike item, EnumNekoColor color, int count) {
        ItemStack stack = new ItemStack(item, count);
        DyeableBlockItem.setColor(stack, color);
        return stack;
    }

    private static Item signOf(NekoWood wood) {
        return switch (wood) {
            case OAK -> Items.OAK_SIGN;
            case SPRUCE -> Items.SPRUCE_SIGN;
            case BIRCH -> Items.BIRCH_SIGN;
            case JUNGLE -> Items.JUNGLE_SIGN;
            case ACACIA -> Items.ACACIA_SIGN;
            case DARK_OAK -> Items.DARK_OAK_SIGN;
            case MANGROVE -> Items.MANGROVE_SIGN;
            case CHERRY -> Items.CHERRY_SIGN;
            case BAMBOO -> Items.BAMBOO_SIGN;
            case CRIMSON -> Items.CRIMSON_SIGN;
            case WARPED -> Items.WARPED_SIGN;
            default -> throw new IllegalArgumentException("No vanilla sign for " + wood);
        };
    }

    private static Item pressurePlateOf(NekoWood wood) {
        return switch (wood) {
            case OAK -> Items.OAK_PRESSURE_PLATE;
            case SPRUCE -> Items.SPRUCE_PRESSURE_PLATE;
            case BIRCH -> Items.BIRCH_PRESSURE_PLATE;
            case JUNGLE -> Items.JUNGLE_PRESSURE_PLATE;
            case ACACIA -> Items.ACACIA_PRESSURE_PLATE;
            case DARK_OAK -> Items.DARK_OAK_PRESSURE_PLATE;
            case MANGROVE -> Items.MANGROVE_PRESSURE_PLATE;
            case CHERRY -> Items.CHERRY_PRESSURE_PLATE;
            case BAMBOO -> Items.BAMBOO_PRESSURE_PLATE;
            case CRIMSON -> Items.CRIMSON_PRESSURE_PLATE;
            case WARPED -> Items.WARPED_PRESSURE_PLATE;
            default -> throw new IllegalArgumentException("No vanilla pressure plate for " + wood);
        };
    }

    private static Item concreteOf(EnumNekoColor color) {
        return vanillaItem(color.getSerializedName() + "_concrete");
    }

    private static Item vanillaItem(String id) {
        return BuiltInRegistries.ITEM.getOptional(ResourceLocation.withDefaultNamespace(id))
                .orElseThrow(() -> new IllegalStateException("Unknown vanilla item: " + id));
    }

    private static Item modItem(String id) {
        return BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, id))
                .orElseThrow(() -> new IllegalStateException("Unknown item: " + Nekoration.MODID + ":" + id));
    }

    private static String hasName(ItemLike item) {
        return "has_" + BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
    }

    private static ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(Nekoration.MODID, path);
    }
}
