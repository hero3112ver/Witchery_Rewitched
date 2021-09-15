package com.hero.witchery_rewitched.data.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.init.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
public class GrassDropModifier {
        public static class SeedDropModifier extends LootModifier{

        @SubscribeEvent
        public static void registerModifiers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> ev)
        {
            ev.getRegistry().register(
                    new Serializer().setRegistryName(WitcheryRewitched.MODID, "grass")
            );
        }

        public SeedDropModifier(ILootCondition[] conditionsIn){
            super(conditionsIn);
        }

        @Nonnull
        @Override
        protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {



            generatedLoot = doGrassDrops(context.getParamOrNull(LootParameters.BLOCK_STATE), generatedLoot);
            generatedLoot = doArthanaDrops(context.getParamOrNull(LootParameters.KILLER_ENTITY), context.getParamOrNull(LootParameters.THIS_ENTITY), generatedLoot);

            return generatedLoot;
        }

        private List<ItemStack> doGrassDrops(BlockState blockState, List<ItemStack> generatedLoot){
            Random rand = new Random();
            Item[] seeds = {ModItems.BELLADONNA_SEEDS.get(), ModItems.GARLIC.get(), ModItems.SNOWBELL_SEEDS.get(), ModItems.WATER_ARTICHOKE_SEEDS.get(), ModItems.WOLFSBANE_SEEDS.get(), ModItems.MANDRAKE_SEEDS.get()};
            if(blockState == null) return generatedLoot;
            if(blockState.getBlock() != Blocks.GRASS && blockState.getBlock() != Blocks.TALL_GRASS)
                return generatedLoot;
            if(generatedLoot.size() < 1 && rand.nextInt(10) < 1)
                generatedLoot.add(seeds[rand.nextInt(seeds.length)].getDefaultInstance());
            return generatedLoot;
        }
        private List<ItemStack> doArthanaDrops(Entity killer, Entity entity, List<ItemStack> generatedLoot){
            if(killer instanceof PlayerEntity) {
                ItemStack stack = ((PlayerEntity) killer).getItemInHand(((PlayerEntity)killer).getUsedItemHand());

                int chance = stack.getItem() == ModItems.ARTHANA.get() ? 35 : 5;
                chance = chance * (EnchantmentHelper.getEnchantments(stack).get(Enchantments.BLOCK_FORTUNE) != null ? EnchantmentHelper.getEnchantments(stack).get(Enchantments.BLOCK_FORTUNE) * 2 : 1);
                int drops = chance / 100;
                chance = chance % 100;
                drops += (new Random().nextInt(100) < chance) ? 1 : 0;
                if(drops > 0) {
                    if ((entity instanceof BatEntity)) generatedLoot.add(new ItemStack(ModItems.WOOL_OF_BAT.get(), drops));
                }

            }


            //TODO: Unfinished
            return generatedLoot;
        }

    }

    public static class Serializer extends GlobalLootModifierSerializer<SeedDropModifier>{

        @Override
        public SeedDropModifier read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition) {

            return new SeedDropModifier(ailootcondition);
        }

        @Override
        public JsonObject write(SeedDropModifier instance) {
            return makeConditions(new ILootCondition[]{});
        }
    }

}