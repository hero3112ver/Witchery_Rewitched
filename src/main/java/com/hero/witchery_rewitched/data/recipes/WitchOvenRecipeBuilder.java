package com.hero.witchery_rewitched.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hero.witchery_rewitched.api.util.NameUtils;
import com.hero.witchery_rewitched.crafting.recipe.ModRecipes;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WitchOvenRecipeBuilder {
    private final IRecipeSerializer<?> serializer;
    private final List<Ingredient> ingredients = new ArrayList<>();
    private final Item result;
    private final Item possibleResult;
    private final int resultCount;
    private final int possibleResultCount;

    public WitchOvenRecipeBuilder(IRecipeSerializer<?> serializer, Item result, Item possibleResult, int resultCount, int possibleResultCount){

        this.serializer = serializer;
        this.result = result;
        this.possibleResult = possibleResult;
        this.resultCount = resultCount;
        this.possibleResultCount = possibleResultCount;
    }

    public static WitchOvenRecipeBuilder recipeBuilder(IItemProvider result, IItemProvider possibleResult){
        return new WitchOvenRecipeBuilder(ModRecipes.Serializers.WITCH_OVEN.get(), result.asItem(), possibleResult.asItem(), 1, 1);
    }

    public WitchOvenRecipeBuilder addIngredient(IItemProvider item) {
        return addIngredient(Ingredient.of(item));
    }

    public WitchOvenRecipeBuilder addIngredient(IItemProvider item, int count) {
        return addIngredient(Ingredient.of(item), count);
    }

    public WitchOvenRecipeBuilder addIngredient(ITag<Item> tag) {
        return addIngredient(Ingredient.of(tag));
    }

    public WitchOvenRecipeBuilder addIngredient(ITag<Item> tag, int count) {
        return addIngredient(Ingredient.of(tag), count);
    }

    public WitchOvenRecipeBuilder addIngredient(Ingredient ingredient) {
        return addIngredient(ingredient, 1);
    }

    public WitchOvenRecipeBuilder addIngredient(Ingredient ingredient, int count) {
        for (int i = 0; i < count; ++i) {
            this.ingredients.add(ingredient);
        }
        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumer) {
        String name = NameUtils.from(this.result).getPath() + "_" + NameUtils.from(this.possibleResult).getPath();
        build(consumer, new ResourceLocation(serializer.getRegistryName() + "/" + name));
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation recipeId) {
        consumer.accept(new Result(recipeId));
    }

    public class Result implements IFinishedRecipe {
        private final ResourceLocation recipeId;

        public Result(ResourceLocation recipeId) {
            this.recipeId = recipeId;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("ingredients", serializeIngredients());
            json.add("result", serializeResult());
            json.add("possibleResult", serializePossibleResult());
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
            if (resultCount > 1) {
                ret.addProperty("count", resultCount);
            }
            return ret;
        }

        private JsonObject serializePossibleResult() {
            JsonObject ret = new JsonObject();
            ret.addProperty("item", NameUtils.from(possibleResult).toString());
            if (possibleResultCount > 1) {
                ret.addProperty("count", possibleResultCount);
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
