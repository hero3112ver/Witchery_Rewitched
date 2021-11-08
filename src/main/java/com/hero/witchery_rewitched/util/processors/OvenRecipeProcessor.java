package com.hero.witchery_rewitched.util.processors;

import com.hero.witchery_rewitched.crafting.recipe.OvenCookingRecipe;
import com.hero.witchery_rewitched.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class OvenRecipeProcessor implements IComponentProcessor {
    OvenCookingRecipe recipe1;
    OvenCookingRecipe recipe2;
    String text;

    @Override
    public void setup(IVariableProvider variables) {
        String recipeId = variables.get("recipe").asString();
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        IRecipe<?> r = manager.byKey(new ResourceLocation(recipeId)).orElseThrow(IllegalArgumentException::new);
        if(!(r instanceof OvenCookingRecipe))
            throw new IllegalStateException();
        recipe1 = (OvenCookingRecipe) r;

        if(variables.has("recipe2")) {
            String recipeId2 = variables.get("recipe2").asString();
            IRecipe<?> r2 = manager.byKey(new ResourceLocation(recipeId2)).orElseThrow(IllegalArgumentException::new);
            if (!(r2 instanceof OvenCookingRecipe))
                throw new IllegalStateException();
            recipe2 = (OvenCookingRecipe) r2;
        }

        if(variables.has("text")){
            text = variables.get("text").asString();
        }


    }

    @Override
    public IVariable process(String key) {
        if(key.startsWith("item")) {
            int index = Integer.parseInt(key.substring(4));
            if (index % 2 == 1) {
                if (index == 1) {
                    Ingredient ingredient = recipe1.getIngredients().get(0);
                    ItemStack[] stacks = ingredient.getItems();
                    ItemStack stack = stacks.length == 0 ? ItemStack.EMPTY : stacks[0];
                    return IVariable.from(stack);
                } else if (recipe2 == null) {
                    return IVariable.from(ItemStack.EMPTY);
                } else {
                    Ingredient ingredient = recipe2.getIngredients().get(0);
                    ItemStack[] stacks = ingredient.getItems();
                    ItemStack stack = stacks.length == 0 ? ItemStack.EMPTY : stacks[0];
                    return IVariable.from(stack);
                }
            }
            else{
                if(index == 2) return IVariable.from(new ItemStack(ModItems.COOKED_CLAY_POT.get()));
                else if(recipe2 == null) return IVariable.from(ItemStack.EMPTY);
                else return IVariable.from(new ItemStack(ModItems.COOKED_CLAY_POT.get()));
            }
        }
        else if(key.startsWith("result")){
            int index = Integer.parseInt(key.substring(6));
            if (index % 2 == 1) {
                if (index == 1) {
                    return IVariable.from(recipe1.getResultItem());
                } else if (recipe2 == null) {
                    return IVariable.from(ItemStack.EMPTY);
                } else {
                    return IVariable.from(recipe2.getResultItem());
                }
            }
            else{
                if (index == 2) {
                    return IVariable.from(recipe1.getPossibleResult());
                } else if (recipe2 == null) {
                    return IVariable.from(ItemStack.EMPTY);
                } else {
                    return IVariable.from(recipe2.getPossibleResult());
                }
            }
        }
        else if(key.startsWith("image")){
            if(recipe2 != null)
                return IVariable.wrap("witchery_rewitched:textures/book/witch_oven.png");
            else
                return IVariable.wrap("witchery_rewitched:textures/book/blank.png");
        }
        else if(key.equals("rName")){
            if(recipe2 == null)
                return IVariable.wrap("");
            return IVariable.wrap(recipe2.getPossibleResult().getDisplayName().getString().replace('[', ' ').replace(']', ' ').trim());
        }
        else if(key.equals("text")){
            return IVariable.wrap(text);
        }
        return null;
    }
}
