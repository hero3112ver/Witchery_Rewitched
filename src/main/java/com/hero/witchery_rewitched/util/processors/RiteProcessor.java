package com.hero.witchery_rewitched.util.processors;

import com.hero.witchery_rewitched.block.glyph.GlyphBlock;
import com.hero.witchery_rewitched.crafting.recipe.RitualRecipe;
import com.hero.witchery_rewitched.init.ModBlocks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.List;

public class RiteProcessor implements IComponentProcessor {
    private RitualRecipe recipe;

    @Override
    public void setup(IVariableProvider variables) {
        String recipeId = variables.get("recipe").asString();
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        IRecipe<?> r = manager.byKey(new ResourceLocation(recipeId)).orElseThrow(IllegalArgumentException::new);
        if(!(r instanceof RitualRecipe))
            throw new IllegalStateException();

        recipe = (RitualRecipe) r;
    }

    @Override
    public IVariable process(String key) {
        if(key.startsWith("item")){
            int index = Integer.parseInt(key.substring(4))-1;
            if( recipe.getIngredientItems().size() <= index  )
                return IVariable.from(ItemStack.EMPTY);
            Ingredient ingredient = Ingredient.of(recipe.getIngredientItems().get(index));
            ItemStack[] stacks = ingredient.getItems();
            ItemStack stack  = stacks.length == 0 ? ItemStack.EMPTY : stacks[0];
            return IVariable.from(stack);
        } else if (key.equals("name")){
            return IVariable.wrap(recipe.getRitual().getName());
        } else if (key.startsWith("circle")){
            List<Pair<Integer, GlyphBlock>> lst = recipe.getRitual().getCircles();
            StringBuilder text = new StringBuilder();
            for(int i = 0; i < lst.size(); i++){
                int size = 0;
                if(lst.get(i).getFirst() == 1) size = 7;
                else if(lst.get(i).getFirst()== 2)size = 11;
                else if(lst.get(i).getFirst()== 3)size = 15;

                if (lst.get(i).getSecond() == ModBlocks.OTHERWHERE_GLYPH.get())
                    text.append("$(li)").append("$(5)").append(size).append("x").append(size).append(" Otherwhere Circle");
                else if (lst.get(i).getSecond() == ModBlocks.INFERNAL_GLYPH.get())
                    text.append("$(li)").append("$(4)").append(size).append("x").append(size).append(" Infernal Circle");
                else if (lst.get(i).getSecond() == ModBlocks.RITUAL_GLYPH.get())
                    text.append("$(li)").append("$(7)").append(size).append("x").append(size).append(" Ritual Circle");

            }
            return IVariable.wrap(String.valueOf(text));
        }
        else if(key.startsWith("shape"))
        {
            int spot = Integer.parseInt(key.charAt(key.length()-1) +"");
            Pair<Integer, GlyphBlock> pair = null;
            for(Pair<Integer, GlyphBlock> p : recipe.getRitual().getCircles()) {
                if(p.getFirst() == spot)
                    pair = p;
            }
            String c = "";
            if(pair == null)
                return IVariable.wrap("witchery_rewitched:textures/book/blank.png");

            if(pair.getSecond() == ModBlocks.RITUAL_GLYPH.get())
                c = "r";
            if(pair.getSecond() == ModBlocks.INFERNAL_GLYPH.get())
                c = "i";
            if(pair.getSecond() == ModBlocks.OTHERWHERE_GLYPH.get())
               c = "o";
            String returnString = "witchery_rewitched:textures/book/" + c + spot +".png";
            return IVariable.wrap(returnString);
        }
        else if(key.equals("requirements"))
        {
            return IVariable.wrap(recipe.getRitual().getRequirements());
        }
        else if(key.equals("description"))
        {
            return IVariable.wrap(recipe.getRitual().getDescription());
        }
        else if(key.equals("power")){
            int power = recipe.getRitual().getStartPower();
            if(recipe.getId().toString().contains("_charged"))
                power = 0;
            return IVariable.wrap("Altar Power: "+ power + (recipe.getRitual().getPowerperSecond() > 0 ? ", " + recipe.getRitual().getPowerperSecond() + "per second" : "" ) );
        }

        return null;
    }
}
