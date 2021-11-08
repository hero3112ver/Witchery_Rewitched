package com.hero.witchery_rewitched.util.processors;

import com.hero.witchery_rewitched.crafting.recipe.DistilleryRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class DistilleryRecipeProcessor implements IComponentProcessor {
    DistilleryRecipe recipe1;
    DistilleryRecipe recipe2;
    String text = "";

    @Override
    public void setup(IVariableProvider variables) {
        String recipeId = variables.get("recipe").asString();
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        IRecipe<?> r = manager.byKey(new ResourceLocation(recipeId)).orElseThrow(IllegalArgumentException::new);
        if(!(r instanceof DistilleryRecipe))
            throw new IllegalStateException();
        recipe1 = (DistilleryRecipe) r;

        if(variables.has("recipe2")) {
            String recipeId2 = variables.get("recipe2").asString();
            IRecipe<?> r2 = manager.byKey(new ResourceLocation(recipeId2)).orElseThrow(IllegalArgumentException::new);
            if (!(r2 instanceof DistilleryRecipe))
                throw new IllegalStateException();
            recipe2 = (DistilleryRecipe) r2;
        }

        if(variables.has("text"))
            text = variables.get("text").asString();


    }

    @Override
    public IVariable process(String key) {
        if(key.startsWith("input")){
            int num = Integer.parseInt(key.substring(5));
            if(num/10 == 1){
                if(num % 10 == 1)
                    return IVariable.from(recipe1.getIngredients().get(0).getItems()[0]);

                if(recipe1.getIngredients2().size() == 0)
                    return IVariable.from(ItemStack.EMPTY);
                return IVariable.from(recipe1.getIngredients2().get(0).getItems()[0]);
            }
            else if(recipe2 == null){
                return IVariable.from(ItemStack.EMPTY);
            }
            else {
                if(num % 10 == 1)
                    return IVariable.from(recipe2.getIngredients().get(0).getItems()[0]);

                if(recipe2.getIngredients2().size() == 0)
                    return IVariable.from(ItemStack.EMPTY);
                return IVariable.from(recipe2.getIngredients2().get(0).getItems()[0]);
            }
        }
        else if(key.startsWith("result")){
         int num = Integer.parseInt(key.substring(6));
         if(num / 10 == 1){
             if(num % 10 > recipe1.getResults().size())
                 return IVariable.from(ItemStack.EMPTY);
             return IVariable.from(recipe1.getResults().get(num % 10 - 1));
         }
         else if(recipe2 == null){
             return IVariable.from(ItemStack.EMPTY);
         }
         else {
             if(num % 10 > recipe2.getResults().size())
                 return IVariable.from(ItemStack.EMPTY);
             return IVariable.from(recipe2.getResults().get(num % 10 - 1));
         }
        }
        else if(key.startsWith("image")){
            if(recipe2 != null)
                return IVariable.wrap("witchery_rewitched:textures/book/distillery.png");
            else
                return IVariable.wrap("witchery_rewitched:textures/book/blank.png");
        }
        else if(key.equals("rName")){
            if(recipe2 == null)
                return IVariable.wrap("");
            return IVariable.wrap(recipe2.getResults().get(0).getDisplayName().getString().replace('[', ' ').replace(']', ' ').trim());
        }
        else if(key.equals("text")){
            return IVariable.wrap(text);
        }
        return null;
    }
}
