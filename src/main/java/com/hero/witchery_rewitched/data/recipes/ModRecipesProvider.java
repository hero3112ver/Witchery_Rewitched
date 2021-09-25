package com.hero.witchery_rewitched.data.recipes;

import com.hero.witchery_rewitched.init.ModBlocks;
import com.hero.witchery_rewitched.init.ModItems;
import com.hero.witchery_rewitched.init.ModTags;
import com.hero.witchery_rewitched.init.Rituals;
import net.minecraft.block.Blocks;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potions;
import net.minecraft.tags.ItemTags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ModRecipesProvider extends RecipeProviderHelper {

    Item[] rawFood = new Item[]{Items.BEEF, Items.CHICKEN, Items.PORKCHOP, Items.MUTTON, Items.POTATO, Items.COD, Items.RABBIT, Items.SALMON};
    Item[] cookedFood = new Item[]{Items.COOKED_BEEF, Items.COOKED_CHICKEN,Items.COOKED_PORKCHOP, Items.COOKED_MUTTON, Items.BAKED_POTATO, Items.COOKED_COD, Items.COOKED_RABBIT, Items.COOKED_SALMON};
    Item[] sapling = new Item[]{Items.OAK_SAPLING, Items.BIRCH_SAPLING, Items.SPRUCE_SAPLING, ModBlocks.ROWAN_SAPLING.get().asItem(), ModBlocks.HAWTHORN_SAPLING.get().asItem(), ModBlocks.ALDER_SAPLING.get().asItem(), Items.JUNGLE_SAPLING};
    Item[] fumes = new Item[]{ModItems.EXHALE_OF_THE_HORNED_ONE.get(), ModItems.BREATH_OF_THE_GODDESS.get(), ModItems.HINT_OF_REBIRTH.get(), ModItems.WHIFF_OF_MAGIC.get(), ModItems.ODOR_OF_PURITY.get(), ModItems.REEK_OF_MISFORTUNE.get(), ModItems.FOUL_FUME.get()};


    public ModRecipesProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }


    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        // Crafting Recipes
        ShapelessRecipeBuilder.shapeless(ModBlocks.ROWAN_PLANKS.get(), 4)
                .requires(ModTags.Items.ROWAN_LOG)
                .unlockedBy("has_item", has(ModBlocks.ROWAN_LOG.get()))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(ModBlocks.HAWTHORN_PLANKS.get(), 4)
                .requires(ModTags.Items.HAWTHORN_LOG)
                .unlockedBy("has_item", has(ModBlocks.HAWTHORN_LOG.get()))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(ModBlocks.ALDER_PLANKS.get(), 4)
                .requires(ModTags.Items.ALDER_LOG)
                .unlockedBy("has_item", has(ModBlocks.ALDER_LOG.get()))
                .save(consumer);
        ShapedRecipeBuilder.shaped(ModItems.ATTUNED_STONE.get())
                .define('d', Items.DIAMOND).define('w', ModItems.WHIFF_OF_MAGIC.get()).define('l', Items.LAVA_BUCKET)
                .pattern(" w ")
                .pattern(" d ")
                .pattern(" l ")
                .unlockedBy("has_item", has(Items.DIAMOND))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(ModItems.BONE_NEEDLE.get(), 8)
                .requires(Items.BONE)
                .requires(Items.FLINT)
                .unlockedBy("has_item", has(Items.BONE))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(ModItems.TAGLOCK_KIT.get())
                .requires(Items.GLASS_BOTTLE)
                .requires(ModItems.BONE_NEEDLE.get())
                .unlockedBy("has_item", has(ModItems.BONE_NEEDLE.get()))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(ModItems.ANOINTING_PASTE.get())
                .requires(ModItems.MANDRAKE_SEEDS.get())
                .requires(ModItems.SNOWBELL_SEEDS.get())
                .requires(ModItems.WATER_ARTICHOKE_SEEDS.get())
                .requires(ModItems.BELLADONNA_SEEDS.get())
                .unlockedBy("has_item", has(ModItems.BELLADONNA_SEEDS.get()))
                .save(consumer);
        ShapedWithPotionBuilder.shapedRecipe(ModBlocks.ALTAR.get(), 3)
                .key('s', ItemTags.STONE_BRICKS)
                .key('r', ModBlocks.ROWAN_LOG.get())
                .key('w', Potions.WATER)
                .key('b', ModItems.BREATH_OF_THE_GODDESS.get())
                .key('e', ModItems.EXHALE_OF_THE_HORNED_ONE.get())
                .patternLine("bwe")
                .patternLine("srs")
                .patternLine("srs")
                .addCriterion("has_item", has(ModItems.BREATH_OF_THE_GODDESS.get()))
                .build(consumer);
        ShapedRecipeBuilder.shaped(ModBlocks.POPPET_SHELF.get())
                .define('a', ModItems.ATTUNED_STONE.get())
                .define('n', Blocks.NETHER_BRICKS)
                .define('w', Blocks.GREEN_WOOL)
                .pattern("ana")
                .pattern("nwn")
                .pattern("ana")
                .unlockedBy("has_item", has(ModItems.ATTUNED_STONE.get()))
                .save(consumer);
        ShapedRecipeBuilder.shaped(ModItems.ARTHANA.get())
                .define('g', Items.GOLD_INGOT)
                .define('e', Items.EMERALD)
                .define('n', Items.GOLD_NUGGET)
                .define('s', Items.STICK)
                .pattern(" g ")
                .pattern("nen")
                .pattern(" s ")
                .unlockedBy("has_item", has(Items.EMERALD))
                .save(consumer);
        ShapedRecipeBuilder.shaped(ModItems.RITUAL_CHALK.get())
                .define('w', ModItems.WOOD_ASH.get())
                .define('g', ModItems.GYPSUM.get())
                .define('t', ModItems.TEAR_OF_THE_GODDESS.get())
                .pattern("wtw")
                .pattern("wgw")
                .pattern("wgw")
                .unlockedBy("has_item", has(ModItems.GYPSUM.get()))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(ModItems.QUICKLIME.get())
                .requires(ModItems.WOOD_ASH.get())
                .unlockedBy("has_item", has(ModItems.GYPSUM.get()))
                .save(consumer);
        ShapedRecipeBuilder.shaped(ModBlocks.DISTILLERY.get())
                .define('c', ModItems.COOKED_CLAY_POT.get())
                .define('i', Items.IRON_INGOT)
                .define('g', Items.GOLD_INGOT)
                .define('a', ModItems.ATTUNED_STONE.get())
                .pattern("cic")
                .pattern("iii")
                .pattern("gag")
                .unlockedBy("has_item", has(ModItems.ATTUNED_STONE.get()))
                .save(consumer);


        ShapedRecipeBuilder.shaped(ModItems.POPPET.get(), 1)
                .define('m', ModBlocks.SPANISH_MOSS.get())
                .define('w', Items.WHITE_WOOL)
                .define('s', Items.STRING)
                .define('b', ModItems.BONE_NEEDLE.get())
                .pattern("wmw")
                .pattern("bms")
                .pattern("w w")
                .unlockedBy("has_item", has(ModBlocks.SPANISH_MOSS.get()))
                .save(consumer);
        ShapedRecipeBuilder.shaped(ModItems.HUNGER_PROTECTION_POPPET.get(), 1)
                .define('p', ModItems.POPPET.get())
                .define('g', Items.GLISTERING_MELON_SLICE)
                .define('z', Items.ROTTEN_FLESH)
                .pattern(" z ")
                .pattern("gpg")
                .pattern(" z ")
                .unlockedBy("has_item", has(ModItems.POPPET.get()))
                .save(consumer);
        ShapedRecipeBuilder.shaped(ModItems.TOOL_PROTECTION_POPPET.get(), 1)
                .define('p', ModItems.POPPET.get())
                .define('d', ModItems.REEK_OF_MISFORTUNE.get())
                .define('r', ModItems.DROP_OF_LUCK.get())
                .pattern(" r ")
                .pattern("dpd")
                .pattern(" r ")
                .unlockedBy("has_item", has(ModItems.POPPET.get()))
                .save(consumer);
        ShapedRecipeBuilder.shaped(ModItems.ARMOR_PROTECTION_POPPET.get(), 1)
                .define('p', ModItems.POPPET.get())
                .define('r', ModItems.REEK_OF_MISFORTUNE.get())
                .define('d', ModItems.DROP_OF_LUCK.get())
                .define('v', ModItems.DIAMOND_VAPOR.get())
                .pattern(" r ")
                .pattern("dpd")
                .pattern(" v ")
                .unlockedBy("has_item", has(ModItems.POPPET.get()))
                .save(consumer);
        ShapedRecipeBuilder.shaped(ModItems.DEATH_PROTECTION_POPPET.get(), 1)
                .define('p', ModItems.POPPET.get())
                .define('g', Items.GOLD_NUGGET)
                .define('d', ModItems.DROP_OF_LUCK.get())
                .define('v', ModItems.DIAMOND_VAPOR.get())
                .pattern("dgv")
                .pattern("gpg")
                .pattern(" g ")
                .unlockedBy("has_item", has(ModItems.POPPET.get()))
                .save(consumer);
        ShapedRecipeBuilder.shaped(ModItems.FIRE_PROTECTION_POPPET.get(), 1)
                .define('p', ModItems.POPPET.get())
                .define('e', ModBlocks.EMBER_MOSS.get())
                .define('w', ModItems.WOOL_OF_BAT.get())
                .pattern(" e ")
                .pattern("wpw")
                .pattern(" e ")
                .unlockedBy("has_item", has(ModItems.POPPET.get()))
                .save(consumer);
        ShapedRecipeBuilder.shaped(ModItems.EARTH_PROTECTION_POPPET.get(), 1)
                .define('p', ModItems.POPPET.get())
                .define('c', Items.CLAY_BALL)
                .define('f', Items.FEATHER)
                .define('w', Items.DIRT)
                .pattern(" c ")
                .pattern("fpf")
                .pattern(" w ")
                .unlockedBy("has_item", has(ModItems.POPPET.get()))
                .save(consumer);
        ShapedRecipeBuilder.shaped(ModItems.WATER_PROTECTION_POPPET.get(), 1)
                .define('p', ModItems.POPPET.get())
                .define('w', ModItems.WATER_ARTICHOKE_BULB.get())
                .define('i', Items.INK_SAC)
                .pattern(" w ")
                .pattern("ipi")
                .pattern(" w ")
                .unlockedBy("has_item", has(ModItems.POPPET.get()))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(ModItems.WAYSTONE.get(), 1)
                .requires(Items.FLINT)
                .requires(ModItems.BONE_NEEDLE.get())
                .unlockedBy("has_item", has(ModItems.POPPET.get()))
                .save(consumer);

        // Clay Pot Recipes
        ShapedRecipeBuilder.shaped(ModItems.UNCOOKED_CLAY_POT.get(), 8)
                .define('#', Items.CLAY_BALL)
                .pattern(" # ")
                .pattern("# #")
                .pattern("###")
                .unlockedBy("has_item", has(Items.CLAY_BALL))
                .save(consumer);
        CookingRecipeBuilder.smelting(Ingredient.of(ModItems.UNCOOKED_CLAY_POT.get()), ModItems.COOKED_CLAY_POT.get(), 0f, 200)
                .unlockedBy("has_item", has(ModItems.UNCOOKED_CLAY_POT.get()))
                .save(consumer);

        // Witch Oven Recipes
        ShapedRecipeBuilder.shaped(ModBlocks.WITCH_OVEN.get(), 1)
                .define('#', Items.IRON_BARS)
                .define('-', Items.IRON_INGOT)
                .pattern(" # ")
                .pattern("---")
                .pattern("-#-")
                .unlockedBy("has_item", has(Items.IRON_INGOT))
                .save(consumer);
        ShapedRecipeBuilder.shaped(ModBlocks.FUME_FUNNEL.get())
                .define('b', Items.BUCKET)
                .define('l', Items.LAVA_BUCKET)
                .define('g', Items.GLOWSTONE)
                .define('i', Items.IRON_BARS)
                .define('I', Items.IRON_BLOCK)
                .pattern("blb")
                .pattern("bgb")
                .pattern("IiI")
                .unlockedBy("has_item", has(ModBlocks.WITCH_OVEN.get()))
                .save(consumer);
        ShapedRecipeBuilder.shaped(ModItems.FUME_FILTER.get())
                .define('g', Items.GLASS).define('i', Items.IRON_INGOT).define('c', ModItems.CHARGED_ATTUNED_STONE.get())
                .pattern("ggg")
                .pattern("ici")
                .pattern("ggg")
                .unlockedBy("has_item", has(ModBlocks.WITCH_OVEN.get()))
                .save(consumer);
        ShapelessRecipeBuilder.shapeless(ModBlocks.FILTERED_FUME_FUNNEL.get())
                .requires(ModBlocks.FUME_FUNNEL.get())
                .requires(ModItems.FUME_FILTER.get())
                .unlockedBy("has_item", has(ModBlocks.FUME_FUNNEL.get()))
                .save(consumer);

        for(int i = 0; i < rawFood.length; i++) {
            WitchOvenRecipeBuilder.recipeBuilder(cookedFood[i], ModItems.FOUL_FUME.get())
                    .addIngredient(rawFood[i])
                    .build(consumer);
        }
        for(int i = 0; i < fumes.length; i++){
            WitchOvenRecipeBuilder.recipeBuilder(ModItems.WOOD_ASH.get(), fumes[i])
                    .addIngredient(sapling[i])
                    .build(consumer);
        }
        WitchOvenRecipeBuilder.recipeBuilder(Items.CHARCOAL, ModItems.FOUL_FUME.get())
                .addIngredient(ItemTags.LOGS)
                .build(consumer);

        // Witch Cauldron Recipes
        WitchCauldronRecipeBuilder.recipeBuilder(ModItems.MUTANDIS.get(), 4)
                .addIngredient(ModItems.MANDRAKE_ROOT.get())
                .addIngredient(ModItems.EXHALE_OF_THE_HORNED_ONE.get())
                .addIngredient(Items.EGG)
                .build(consumer);
        WitchCauldronRecipeBuilder.recipeBuilder(ModItems.MUTANDIS_EXTREMIS.get(), 1, 100)
                .addIngredient(ModItems.MUTANDIS.get())
                .addIngredient(Items.NETHER_WART)
                .build(consumer);
        WitchCauldronRecipeBuilder.recipeBuilder(ModItems.DROP_OF_LUCK.get(), 1, 100)
                .addIngredient(ModItems.MANDRAKE_ROOT.get())
                .addIngredient(Items.NETHER_WART)
                .addIngredient(ModItems.TEAR_OF_THE_GODDESS.get())
                .addIngredient(ModItems.REFINED_EVIL.get())
                .addIngredient(ModItems.MUTANDIS_EXTREMIS.get())
                .build(consumer);
        WitchCauldronRecipeBuilder.recipeBuilder(ModItems.OTHERWHERE_CHALK.get(), 1, 2000)
                .addIngredient(Items.NETHER_WART)
                .addIngredient(ModItems.TEAR_OF_THE_GODDESS.get())
                .addIngredient(Items.ENDER_PEARL)
                .addIngredient(ModItems.RITUAL_CHALK.get())
                .build(consumer);
        WitchCauldronRecipeBuilder.recipeBuilder(ModItems.INFERNAL_CHALK.get(), 1, 2000)
                .addIngredient(Items.NETHER_WART)
                .addIngredient(Items.BLAZE_POWDER)
                .addIngredient(ModItems.RITUAL_CHALK.get())
                .build(consumer);
        WitchCauldronRecipeBuilder.recipeBuilder(ModItems.GOLD_CHALK.get(), 1, 2000)
                .addIngredient(ModItems.MANDRAKE_ROOT.get())
                .addIngredient(Items.GOLD_NUGGET)
                .addIngredient(ModItems.RITUAL_CHALK.get())
                .build(consumer);

        // Distillery Recipes
        DistilleryRecipeBuilder.recipeBuilder(Arrays.asList(new ItemStack(ModItems.GYPSUM.get()), new ItemStack(ModItems.OIL_OF_VITRIOL.get()), new ItemStack(Items.SLIME_BALL)), 1)
                .addIngredient(ModItems.FOUL_FUME.get())
                .addIngredient2(ModItems.QUICKLIME.get())
                .build(consumer);
        DistilleryRecipeBuilder.recipeBuilder(Arrays.asList(new ItemStack(ModItems.DIAMOND_VAPOR.get(),2), new ItemStack(ModItems.ODOR_OF_PURITY.get())), 2)
                .addIngredient(Items.DIAMOND)
                .addIngredient2(ModItems.OIL_OF_VITRIOL.get())
                .build(consumer);
        DistilleryRecipeBuilder.recipeBuilder(Arrays.asList(new ItemStack(ModItems.TEAR_OF_THE_GODDESS.get()), new ItemStack(ModItems.WHIFF_OF_MAGIC.get()), new ItemStack(Items.SLIME_BALL), new ItemStack(ModItems.FOUL_FUME.get())), 3)
                .addIngredient(ModItems.BREATH_OF_THE_GODDESS.get())
                .addIngredient2(Items.LAPIS_LAZULI)
                .build(consumer);
        DistilleryRecipeBuilder.recipeBuilder(Arrays.asList(new ItemStack(ModItems.ENDER_DEW.get(),5), new ItemStack(ModItems.WHIFF_OF_MAGIC.get())), 4)
                .addIngredient(Items.ENDER_PEARL)
                .build(consumer);
        DistilleryRecipeBuilder.recipeBuilder(Arrays.asList(new ItemStack(ModItems.REFINED_EVIL.get()), new ItemStack(ModItems.ODOR_OF_PURITY.get()), new ItemStack(ModItems.REEK_OF_MISFORTUNE.get()), new ItemStack(ModItems.FOUL_FUME.get())),3)
                .addIngredient(ModItems.DIAMOND_VAPOR.get())
                .addIngredient2(Items.GHAST_TEAR)
                .build(consumer);


        RitualRecipeBuilder.recipeBuilder(Arrays.asList(Items.STONE_AXE, ModItems.QUICKLIME.get()), Rituals.RITE_OF_TOTAL_ECLIPSE, false).build(consumer);
        RitualRecipeBuilder.recipeBuilder(Arrays.asList(Items.IRON_AXE, ModItems.QUICKLIME.get(), ModItems.CHARGED_ATTUNED_STONE.get()), Rituals.RITE_OF_TOTAL_ECLIPSE, true).build(consumer);
        RitualRecipeBuilder.recipeBuilder(Arrays.asList(Items.GLOWSTONE_DUST, Items.REDSTONE, ModItems.WOOD_ASH.get(), ModItems.QUICKLIME.get(), ModItems.ATTUNED_STONE.get()), Rituals.RITE_OF_CHARGING,  false).build(consumer);
        RitualRecipeBuilder.recipeBuilder(Collections.singletonList(ModItems.BOUND_WAYSTONE.get()), Rituals.RITE_OF_TRANSPORTATION_PLAYER, false).build(consumer);
        RitualRecipeBuilder.recipeBuilder(Arrays.asList(ModItems.WOOD_ASH.get(), Items.BLAZE_ROD, Items.COAL), Rituals.RITE_OF_BROILING, false).build(consumer);
        RitualRecipeBuilder.recipeBuilder(Arrays.asList(ModItems.WOOD_ASH.get(), Items.BLAZE_ROD, Items.BLAZE_POWDER, ModItems.CHARGED_ATTUNED_STONE.get()), Rituals.RITE_OF_BROILING, true).build(consumer);
        RitualRecipeBuilder.recipeBuilder(Arrays.asList(ModItems.WAYSTONE.get(), ModItems.ENDER_DEW.get(), Items.GLOWSTONE_DUST), Rituals.RITE_OF_BINDING_WAYSTONE, false).build(consumer);
        RitualRecipeBuilder.recipeBuilder(Arrays.asList(ModItems.WAYSTONE.get(), ModItems.ENDER_DEW.get(), ModItems.WOOD_ASH.get(), ModItems.ATTUNED_STONE.get()), Rituals.RITE_OF_BINDING_WAYSTONE, true).build(consumer);
        RitualRecipeBuilder.recipeBuilder(Arrays.asList(ModItems.CIRCLE_TALISMAN.get(), ModItems.CHARGED_ATTUNED_STONE.get(), Items.GLOWSTONE_DUST), Rituals.RITE_OF_BINDING_CIRCLE_TALISMAN, true).build(consumer);
    }
}
