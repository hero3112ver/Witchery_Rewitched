package com.hero.witchery_rewitched.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hero.witchery_rewitched.crafting.recipe.ModRecipes;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import com.hero.witchery_rewitched.util.util.NameUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WitchCauldronRecipeBuilder {
    private final IRecipeSerializer<?> serializer;
    private final List<Ingredient> ingredients = new ArrayList<>();
    private final Item result;
    private final int count;
    private final int power;

    public WitchCauldronRecipeBuilder(IRecipeSerializer<?> serializer, Item result, int count, int power){
        this.serializer = serializer;
        this.result = result;
        this.count = count;
        this.power = power;
    }

    public static WitchCauldronRecipeBuilder recipeBuilder(IItemProvider result, int count){
        return recipeBuilder(result, count, 0);
    }
    public static WitchCauldronRecipeBuilder recipeBuilder(IItemProvider result, int count, int power){
        return new WitchCauldronRecipeBuilder(ModRecipes.Serializers.WITCH_CAULDRON.get(), result.asItem(), count, power);
    }

    public WitchCauldronRecipeBuilder addIngredient(IItemProvider item) {
        return addIngredient(Ingredient.of(item));
    }

    public WitchCauldronRecipeBuilder addIngredient(ITag<Item> tag) {
        return addIngredient(Ingredient.of(tag));
    }

    public WitchCauldronRecipeBuilder addIngredient(Ingredient ingredient) {
        return addIngredient(ingredient, 1);
    }

    public WitchCauldronRecipeBuilder addIngredient(Ingredient ingredient, int count) {
        for (int i = 0; i < count; ++i) {
            this.ingredients.add(ingredient);
        }
        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumer) {
        String name = NameUtils.from(this.result).getPath();
        build(consumer, new ResourceLocation(serializer.getRegistryName() + "/" + name));
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation recipeId) {
        consumer.accept(new WitchCauldronRecipeBuilder.Result(recipeId));
    }

    public class Result implements IFinishedRecipe {
        private final ResourceLocation recipeId;

        public Result(ResourceLocation recipeId) {
            this.recipeId = recipeId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("ingredients", serializeIngredients());
            json.addProperty("power", power);
            json.add("result", serializeResult());
        }

        private JsonArray serializeIngredients() {
            JsonArray ret = new JsonArray();
            for (Ingredient ingredient : ingredients) {
                ret.add(ingredient.toJson());
            }
            return ret;
        }

        private JsonObject serializeResult() {
            JsonObject ret = new JsonObject();
            ret.addProperty("item", NameUtils.from(result).toString());
            ret.addProperty("count", count);
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
