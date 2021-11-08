package com.hero.witchery_rewitched.util.processors;

import com.hero.witchery_rewitched.crafting.recipe.WitchCauldronRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class CauldronRecipeProcessor implements IComponentProcessor {
    WitchCauldronRecipe recipe;
    String text;

    @Override
    public void setup(IVariableProvider variables) {
        String recipeId = variables.get("recipe").asString();
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        IRecipe<?> r = manager.byKey(new ResourceLocation(recipeId)).orElseThrow(IllegalArgumentException::new);
        if(!(r instanceof WitchCauldronRecipe))
            throw new IllegalStateException();

        recipe = (WitchCauldronRecipe) r;

        String text = variables.get("text").asString();
    }

    @Override
    public IVariable process(String key) {
        if(key.startsWith("item")){
            int index = Integer.parseInt(key.substring(4))-1;
            if( recipe.getIngredients().size() <= index  )
                return IVariable.from(ItemStack.EMPTY);
            Ingredient ingredient = recipe.getIngredients().get(index);
            ItemStack[] stacks = ingredient.getItems();
            ItemStack stack  = stacks.length == 0 ? ItemStack.EMPTY : stacks[0];
            return IVariable.from(stack);
        }
        else if(key.startsWith("result")){
            return IVariable.from(recipe.getResultItem());
        }
        else if(key.startsWith("power")){
            return IVariable.wrap("" + recipe.getPower());
        }
        return null;
    }
}
