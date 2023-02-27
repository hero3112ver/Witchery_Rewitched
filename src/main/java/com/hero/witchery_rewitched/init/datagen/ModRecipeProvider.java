package com.hero.witchery_rewitched.init.datagen;

import com.hero.witchery_rewitched.init.WitcheryItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        shapedRecipes(pWriter);
        shapelessRecipes(pWriter);
        cookingRecipes(pWriter);
    }

    private void shapedRecipes(Consumer<FinishedRecipe>  pWriter){
        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, WitcheryItems.UNCOOKED_CLAY_POT.get(), 4)
                .define('c', Items.CLAY_BALL)
                .pattern(" c ")
                .pattern("c c")
                .pattern("ccc")
                .unlockedBy("has_item", has(Items.CLAY_BALL))
                .save(pWriter);
    }

    private void shapelessRecipes(Consumer<FinishedRecipe> pWriter){

    }

    private void cookingRecipes(Consumer<FinishedRecipe> pWriter){
        SimpleCookingRecipeBuilder.generic(
                Ingredient.of(
                    WitcheryItems.UNCOOKED_CLAY_POT.get()),
                    RecipeCategory.BREWING,
                    WitcheryItems.COOKED_CLAY_POT.get(),
                    0,
                    200,
                    RecipeSerializer.SMELTING_RECIPE
                )
                .unlockedBy("has_item", has(WitcheryItems.UNCOOKED_CLAY_POT.get()))
                .save(pWriter);
    }

}
