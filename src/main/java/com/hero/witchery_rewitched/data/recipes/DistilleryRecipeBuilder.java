package com.hero.witchery_rewitched.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hero.witchery_rewitched.crafting.recipe.ModRecipes;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import com.hero.witchery_rewitched.api.util.NameUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DistilleryRecipeBuilder {
    private final IRecipeSerializer<?> serializer;
    private final List<Ingredient> ingredients = new ArrayList<>();
    private final List<Ingredient> ingredients2 = new ArrayList<>();
    private final List<ItemStack> results;
    private final int fumeAmount;

    public DistilleryRecipeBuilder(IRecipeSerializer<?> serializer, List<ItemStack> result, int fumeAmount){

        this.serializer = serializer;
        this.results = result;
        this.fumeAmount = fumeAmount;
    }

    public static DistilleryRecipeBuilder recipeBuilder(List<ItemStack> list, int fumeAmount){
        return new DistilleryRecipeBuilder(ModRecipes.Serializers.DISTILLERY.get(), list,fumeAmount);
    }

    public DistilleryRecipeBuilder addIngredient(IItemProvider item) {
        return addIngredient(Ingredient.of(item));
    }

    public DistilleryRecipeBuilder addIngredient(IItemProvider item, int count) {
        return addIngredient(Ingredient.of(item), count);
    }

    public DistilleryRecipeBuilder addIngredient(ITag<Item> tag) {
        return addIngredient(Ingredient.of(tag));
    }

    public DistilleryRecipeBuilder addIngredient(ITag<Item> tag, int count) {
        return addIngredient(Ingredient.of(tag), count);
    }

    public DistilleryRecipeBuilder addIngredient(Ingredient ingredient) {
        return addIngredient(ingredient, 1);
    }

    public DistilleryRecipeBuilder addIngredient(Ingredient ingredient, int count) {
        this.ingredients.add(ingredient);
        return this;
    }

    public DistilleryRecipeBuilder addIngredient2(IItemProvider item) {
        return addIngredient2(Ingredient.of(item));
    }

    public DistilleryRecipeBuilder addIngredient2(IItemProvider item, int count) {
        return addIngredient2(Ingredient.of(item), count);
    }

    public DistilleryRecipeBuilder addIngredient2(ITag<Item> tag) {
        return addIngredient2(Ingredient.of(tag));
    }

    public DistilleryRecipeBuilder addIngredient2(ITag<Item> tag, int count) {
        return addIngredient2(Ingredient.of(tag), count);
    }

    public DistilleryRecipeBuilder addIngredient2(Ingredient ingredient) {
        return addIngredient2(ingredient, 1);
    }

    public DistilleryRecipeBuilder addIngredient2(Ingredient ingredient, int count) {
        this.ingredients2.add(ingredient);
        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumer) {
        String name = NameUtils.from(this.results.get(0).getItem()).getPath();
        build(consumer, new ResourceLocation(serializer.getRegistryName() + "/" + name));
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation recipeId) {
        consumer.accept(new DistilleryRecipeBuilder.Result(recipeId));
    }

    public class Result implements IFinishedRecipe {
        private final ResourceLocation recipeId;

        public Result(ResourceLocation recipeId) {
            this.recipeId = recipeId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("ingredients", serializeIngredients());
            json.add("ingredients2", serializeIngredients2());
            json.add("result", serializeResults());
            json.addProperty("fumeAmount", fumeAmount);
        }

        private JsonArray serializeIngredients() {
            JsonArray ret = new JsonArray();
            for (Ingredient ingredient : ingredients) {
                ret.add(ingredient.toJson());
            }
            return ret;
        }
        private JsonArray serializeIngredients2() {
            JsonArray ret = new JsonArray();
            for (Ingredient ingredient : ingredients2) {
                ret.add(ingredient.toJson());
            }
            return ret;
        }

        private JsonArray serializeResults() {
            JsonArray ret = new JsonArray();
            for(ItemStack item : results){
                JsonObject js = new JsonObject();
                js.addProperty("item", NameUtils.from(item.getItem()).toString());
                js.addProperty("count", item.getCount());
                ret.add(js);
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
