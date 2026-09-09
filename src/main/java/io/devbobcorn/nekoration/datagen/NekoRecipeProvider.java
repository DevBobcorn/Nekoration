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
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;

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
        for (EnumNekoColor color : EnumNekoColor.values()) {
            Item dye = vanillaItem(color.getSerializedName() + "_dye");
            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, dyed(cement, color, 2))
                    .requires(Items.DRIPSTONE_BLOCK)
                    .requires(Items.CLAY)
                    .requires(dye)
                    .unlockedBy(hasName(dye), has(dye))
                    .save(output, modLoc("cement_" + color.getSerializedName()));
        }
    }

    private void stoneBlockRecipes(RecipeOutput output) {
        for (NekoStone stone : NekoStone.values()) {
            String stoneId = stone.id();
            Item baseStone = stone.vanillaStoneBlock().asItem();
            if (stone.vanillaWallBlock() == null) {
                String wallId = stoneId + "_wall";
                wallRecipe(output, baseStone, wallId);
                stonecutting(output, baseStone, wallId);
            }
            Item smoothSource;
            Item smoothSlab;
            if (stone.needsSmoothVariant()) {
                Item smooth = modItem("smooth_" + stoneId);
                smoothSource = smooth;
                smoothSlab = modItem("smooth_" + stoneId + "_slab");
                SimpleCookingRecipeBuilder.smelting(Ingredient.of(baseStone), RecipeCategory.BUILDING_BLOCKS,
                        smooth, SMELTING_XP, SMELTING_TIME)
                        .unlockedBy(hasName(baseStone), has(baseStone))
                        .save(output, modLoc("smooth_" + stoneId + "_from_smelting"));
                stonecutting(output, smoothSource, "smooth_" + stoneId + "_stairs");
                stonecutting(output, smoothSource, "smooth_" + stoneId + "_slab");
                stairsRecipe(output, smoothSource, "smooth_" + stoneId + "_stairs");
                slabRecipe(output, smoothSource, "smooth_" + stoneId + "_slab");
            } else {
                smoothSource = switch (stone) {
                    case STONE -> Items.SMOOTH_STONE;
                    case SANDSTONE -> Items.SMOOTH_SANDSTONE;
                    case RED_SANDSTONE -> Items.SMOOTH_RED_SANDSTONE;
                    default -> throw new IllegalStateException("No vanilla smooth block for " + stone);
                };
                smoothSlab = stone.vanillaSmoothSlabBlock().asItem();
                if (stone == NekoStone.STONE) {
                    stonecutting(output, smoothSource, "smooth_stone_stairs");
                    stairsRecipe(output, smoothSource, "smooth_stone_stairs");
                }
            }
            String polishedSmoothId = "polished_smooth_" + stoneId;
            Item polishedSmooth = modItem(polishedSmoothId);
            stonecutting(output, smoothSource, polishedSmoothId);
            stonecutting(output, polishedSmooth, polishedSmoothId + "_stairs");
            stonecutting(output, polishedSmooth, polishedSmoothId + "_slab");
            twoByTwoRecipe(output, smoothSource, polishedSmoothId);
            stairsRecipe(output, polishedSmooth, polishedSmoothId + "_stairs");
            slabRecipe(output, polishedSmooth, polishedSmoothId + "_slab");

            Item polishedSource;
            if (stone.needsPolishedVariant()) {
                Item polished = modItem("polished_" + stoneId);
                polishedSource = polished;
                twoByTwoRecipe(output, baseStone, "polished_" + stoneId);
                stonecutting(output, baseStone, "polished_" + stoneId);
                stairsRecipe(output, polished, "polished_" + stoneId + "_stairs");
                slabRecipe(output, polished, "polished_" + stoneId + "_slab");
                stonecutting(output, baseStone, "polished_" + stoneId + "_stairs");
                stonecutting(output, polished, "polished_" + stoneId + "_stairs");
                stonecutting(output, baseStone, "polished_" + stoneId + "_slab");
                stonecutting(output, polished, "polished_" + stoneId + "_slab");
            } else {
                polishedSource = stone.vanillaPolishedStoneBlock().asItem();
            }
            if (stone.vanillaPolishedWallBlock() == null) {
                String polishedWallId = "polished_" + stoneId + "_wall";
                wallRecipe(output, polishedSource, polishedWallId);
                stonecutting(output, baseStone, polishedWallId);
                stonecutting(output, polishedSource, polishedWallId);
            }

            Item bricksSource;
            if (stone.needsBricksVariant()) {
                Item bricks = modItem(stoneId + "_bricks");
                bricksSource = bricks;
                twoByTwoRecipe(output, polishedSource, stoneId + "_bricks");
                stonecutting(output, baseStone, stoneId + "_bricks");
                stonecutting(output, polishedSource, stoneId + "_bricks");
                stairsRecipe(output, bricks, stoneId + "_brick_stairs");
                slabRecipe(output, bricks, stoneId + "_brick_slab");
                stonecutting(output, baseStone, stoneId + "_brick_stairs");
                stonecutting(output, bricks, stoneId + "_brick_stairs");
                stonecutting(output, baseStone, stoneId + "_brick_slab");
                stonecutting(output, bricks, stoneId + "_brick_slab");
            } else {
                bricksSource = stone.vanillaBricksStoneBlock().asItem();
            }
            if (stone.vanillaBrickWallBlock() == null) {
                String brickWallId = stoneId + "_brick_wall";
                wallRecipe(output, bricksSource, brickWallId);
                stonecutting(output, baseStone, brickWallId);
                stonecutting(output, bricksSource, brickWallId);
            }

            Item tiles = modItem(stoneId + "_tiles");
            twoByTwoRecipe(output, bricksSource, stoneId + "_tiles");
            stonecutting(output, baseStone, stoneId + "_tiles");
            stonecutting(output, bricksSource, stoneId + "_tiles");
            stairsRecipe(output, tiles, stoneId + "_tile_stairs");
            slabRecipe(output, tiles, stoneId + "_tile_slab");
            stonecutting(output, baseStone, stoneId + "_tile_stairs");
            stonecutting(output, tiles, stoneId + "_tile_stairs");
            stonecutting(output, baseStone, stoneId + "_tile_slab");
            stonecutting(output, tiles, stoneId + "_tile_slab");

            String tileWallId = stoneId + "_tile_wall";
            wallRecipe(output, tiles, tileWallId);
            stonecutting(output, baseStone, tileWallId);
            stonecutting(output, tiles, tileWallId);

            if (stone.needsChiseledVariant()) {
                Item polishedSlab = stone.needsPolishedVariant()
                        ? modItem("polished_" + stoneId + "_slab")
                        : stone.vanillaPolishedSlabBlock().asItem();
                chiseledRecipe(output, polishedSlab, "chiseled_" + stoneId);
                stonecutting(output, baseStone, "chiseled_" + stoneId);
                stonecutting(output, polishedSource, "chiseled_" + stoneId);
            }
            if (stone.needsChiseledBricksVariant()) {
                chiseledRecipe(output, modItem(stoneId + "_brick_slab"), "chiseled_" + stoneId + "_bricks");
                stonecutting(output, baseStone, "chiseled_" + stoneId + "_bricks");
                stonecutting(output, bricksSource, "chiseled_" + stoneId + "_bricks");
            }
            chiseledRecipe(output, smoothSlab, "chiseled_smooth_" + stoneId);
            stonecutting(output, smoothSource, "chiseled_smooth_" + stoneId);
        }
    }

    private void stonecutting(RecipeOutput output, Item source, String resultId) {
        String sourceName = BuiltInRegistries.ITEM.getKey(source).getPath();
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(source), RecipeCategory.BUILDING_BLOCKS, modItem(resultId))
                .unlockedBy(hasName(source), has(source))
                .save(output, modLoc(resultId + "_from_" + sourceName + "_stonecutting"));
    }

    /** {@code SS}/{@code SS} -> 4 results (vanilla polished/bricks/tiles pattern). */
    private void twoByTwoRecipe(RecipeOutput output, ItemLike source, String resultId) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, modItem(resultId), 4)
                .pattern("SS").pattern("SS")
                .define('S', source)
                .unlockedBy(hasName(source), has(source))
                .save(output, modLoc(resultId));
    }

    /** Vanilla stairs pattern -> 4 stairs. */
    private void stairsRecipe(RecipeOutput output, ItemLike source, String resultId) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, modItem(resultId), 4)
                .pattern("S  ").pattern("SS ").pattern("SSS")
                .define('S', source)
                .unlockedBy(hasName(source), has(source))
                .save(output, modLoc(resultId));
    }

    /** Vanilla slab pattern -> 6 slabs. */
    private void slabRecipe(RecipeOutput output, ItemLike source, String resultId) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, modItem(resultId), 6)
                .pattern("SSS")
                .define('S', source)
                .unlockedBy(hasName(source), has(source))
                .save(output, modLoc(resultId));
    }

    /** Vanilla wall pattern: two rows of 3 -> 6 walls. */
    private void wallRecipe(RecipeOutput output, ItemLike source, String resultId) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, modItem(resultId), 6)
                .pattern("SSS").pattern("SSS")
                .define('S', source)
                .unlockedBy(hasName(source), has(source))
                .save(output, modLoc(resultId));
    }

    /** Vanilla chiseled pattern: 2 slabs in a column -> 1 result. */
    private void chiseledRecipe(RecipeOutput output, ItemLike source, String resultId) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, modItem(resultId))
                .pattern("S").pattern("S")
                .define('S', source)
                .unlockedBy(hasName(source), has(source))
                .save(output, modLoc(resultId));
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
