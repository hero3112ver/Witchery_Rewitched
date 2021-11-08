package com.hero.witchery_rewitched.crafting.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hero.witchery_rewitched.util.rituals.AbstractRitual;
import com.hero.witchery_rewitched.init.RegistryHandler;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class RitualRecipe implements IRecipe<IInventory> {
    private ResourceLocation id;
    private final List<Item> ingredients = new ArrayList<>();
    private AbstractRitual ritual;

    public RitualRecipe(ResourceLocation id, AbstractRitual ritual) {
        this.id = id;
        this.ritual = ritual;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        for(Item item : ingredients){
            boolean flag = false;
            for(int i = 0; i < inv.getContainerSize(); i++) {
                if (item == inv.getItem(i).getItem()) {
                    flag = true;
                }
            }
            if(!flag)
                return flag;
        }
        return true;
    }

    public List<Item> getIngredientItems() {
        return ingredients;
    }

    @Override
    public ItemStack assemble(IInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.Serializers.RITUAL.get();
    }

    public AbstractRitual getRitual(){return ritual;}
    @Override
    public IRecipeType<?> getType() {
        return ModRecipes.Types.RITUAL;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<RitualRecipe>{
        @Override
        public RitualRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            JsonArray items = json.getAsJsonArray("items");
            ResourceLocation ritualID = new ResourceLocation(JSONUtils.getAsString(json, "ritual"));
            AbstractRitual ritual = RegistryHandler.RITUAL_REGISTRY.get().getValue(ritualID);
            RitualRecipe rec = new RitualRecipe(recipeId, ritual);
            for(JsonElement je : items){
                rec.ingredients.add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(je.getAsString())));
            }
            return rec;
        }

        @Nullable
        @Override
        public RitualRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
            List<Item> ingredients = new ArrayList<>();
            int count = buffer.readByte();
            for(int i = 0; i < count; i++){
                ingredients.add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(buffer.readUtf())));
            }
            RitualRecipe rec = new RitualRecipe(recipeId, RegistryHandler.RITUAL_REGISTRY.get().getValue(new ResourceLocation(buffer.readUtf())));
            rec.ingredients.addAll(ingredients);
            return rec;
        }

        @Override
        public void toNetwork(PacketBuffer buffer, RitualRecipe recipe) {
            buffer.writeByte(recipe.ingredients.size());
            recipe.ingredients.forEach(ingredient->buffer.writeUtf(ingredient.getRegistryName().toString()));
            buffer.writeUtf(recipe.ritual.getRegistryName().toString());

        }
    }
}
