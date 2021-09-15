package com.hero.witchery_rewitched.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hero.witchery_rewitched.api.rituals.AbstractRitual;
import com.hero.witchery_rewitched.crafting.recipe.ModRecipes;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import com.hero.witchery_rewitched.api.util.NameUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class RitualRecipeBuilder {
    private final IRecipeSerializer<?> serializer;
    private final List<Item> ingredients;
    private final String name;
    private final boolean stone;

    public RitualRecipeBuilder(IRecipeSerializer<?> serializer,List<Item> ingredients, String name, boolean stone){
        this.serializer = serializer;
        this.ingredients = ingredients;
        this.name = name;
        this.stone = stone;
    }

    public static RitualRecipeBuilder recipeBuilder(List<Item> ingredients, RegistryObject<AbstractRitual> name, boolean stone){
        return new RitualRecipeBuilder(ModRecipes.Serializers.RITUAL.get(), ingredients, name.get().getRegistryName().toString(), stone);
    }

    public void build(Consumer<IFinishedRecipe> consumer) {
        build(consumer, new ResourceLocation(serializer.getRegistryName() + "/" + name.substring(name.indexOf(":")+1) + (stone ? "_charged" : "")));
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation recipeId) {
        consumer.accept(new RitualRecipeBuilder.Result(recipeId));
    }

    public class Result implements IFinishedRecipe {
        private final ResourceLocation recipeId;

        public Result(ResourceLocation recipeId) {
            this.recipeId = recipeId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("items", serializeIngredients());
            json.addProperty("ritual", name);
        }

        private JsonArray serializeIngredients() {
            JsonArray ret = new JsonArray();
            for (Item item : ingredients) {
                ret.add(NameUtils.from(item).toString());
            }
            return ret;
        }

        @Override
        public ResourceLocation getId() {
            return recipeId;
        }

        @Override
        public IRecipeSerializer<?> getType() {
            return serializer;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
