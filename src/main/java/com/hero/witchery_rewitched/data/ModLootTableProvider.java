package com.hero.witchery_rewitched.data;

import com.google.common.collect.ImmutableList;
import com.hero.witchery_rewitched.block.plants.ModCropBase;
import com.hero.witchery_rewitched.init.ModBlocks;
import com.hero.witchery_rewitched.init.ModEntities;
import com.hero.witchery_rewitched.init.ModItems;
import com.hero.witchery_rewitched.init.RegistryHandler;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.data.loot.EntityLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.*;
import net.minecraft.loot.functions.ApplyBonus;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ModLootTableProvider extends LootTableProvider {
    private static final float[] LEAVES_SAPLING_CHANCES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};

    public ModLootTableProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        ArrayList<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> ls = new ArrayList<>();
        ls.add(Pair.of(ModBlockLootTables::new, LootParameterSets.BLOCK));
        ls.add(Pair.of(ModMobsLootTables::new, LootParameterSets.ENTITY));
        return ls;
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
        map.forEach((p_218436_2_, p_218436_3_) -> LootTableManager.validate(validationtracker, p_218436_2_, p_218436_3_));
    }

    public static class ModBlockLootTables extends BlockLootTables {
        @Override
        protected Iterable<Block> getKnownBlocks() {
            return RegistryHandler.BLOCKS.getEntries()
                    .stream()
                    .map(RegistryObject::get)
                    .collect(Collectors.toList());
        }

        @Override
        protected void addTables() {
            dropSelf(ModBlocks.ROWAN_LOG.get());
            dropSelf(ModBlocks.ROWAN_PLANKS.get());
            dropSelf(ModBlocks.STRIPPED_ROWAN_LOG.get());
            dropSelf(ModBlocks.ALDER_LOG.get());
            dropSelf(ModBlocks.ALDER_PLANKS.get());
            dropSelf(ModBlocks.STRIPPED_ALDER_LOG.get());
            dropSelf(ModBlocks.HAWTHORN_LOG.get());
            dropSelf(ModBlocks.HAWTHORN_PLANKS.get());
            dropSelf(ModBlocks.STRIPPED_HAWTHORN_LOG.get());
            add(ModBlocks.ROWAN_LEAVES.get(), (leaves) ->
                    createLeavesDrops(leaves, ModBlocks.ROWAN_SAPLING.get(), LEAVES_SAPLING_CHANCES).withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS)).or(MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))))).invert()).add(applyExplosionCondition(ModBlocks.ROWAN_LEAVES.get(), ItemLootEntry.lootTableItem(ModItems.ROWAN_BERRY.get())).when(TableBonus.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.05F, 0.12F, 0.15F, 0.175F, 0.50F)))));
            add(ModBlocks.ALDER_LEAVES.get(), (leaves) ->
                    createLeavesDrops(leaves, ModBlocks.ALDER_SAPLING.get(), LEAVES_SAPLING_CHANCES));
            add(ModBlocks.HAWTHORN_LEAVES.get(), (leaves) ->
                    createLeavesDrops(leaves, ModBlocks.HAWTHORN_SAPLING.get(), LEAVES_SAPLING_CHANCES));
            dropSelf(ModBlocks.ROWAN_SAPLING.get());
            dropSelf(ModBlocks.ALDER_SAPLING.get());
            dropSelf(ModBlocks.HAWTHORN_SAPLING.get());
            dropSelf(ModBlocks.WITCH_OVEN.get());
            dropSelf(ModBlocks.WITCH_CAULDRON.get());
            registerPlants();
            dropSelf(ModBlocks.FILTERED_FUME_FUNNEL.get());
            dropSelf(ModBlocks.FUME_FUNNEL.get());
            dropSelf(ModBlocks.ALTAR.get());
            add(ModBlocks.GLINTWEED.get(), createShearsOnlyDrop(ModBlocks.GLINTWEED.get()));
            add(ModBlocks.EMBER_MOSS.get(), createShearsOnlyDrop(ModBlocks.EMBER_MOSS.get()));
            add(ModBlocks.SPANISH_MOSS.get(), createShearsOnlyDrop(ModBlocks.SPANISH_MOSS.get()));
            add(ModBlocks.GRASSPER.get(), createShearsOnlyDrop(ModBlocks.GRASSPER.get()));
            dropSelf(ModBlocks.DISTILLERY.get());
            dropSelf(ModBlocks.POPPET_SHELF.get());
            dropOther(ModBlocks.ARTHANA.get(), ModItems.ARTHANA.get());
            // TODO: this needs to be fixed
            add(ModBlocks.CRITTER_SNARE.get(), noDrop());
        }


        private void registerPlants(){
            // FIXME: Get more effective drop rates
            registerPlant(ModItems.BELLADONNA.get(), ModItems.BELLADONNA_SEEDS.get(), ModBlocks.BELLADONNA.get(), null);
            registerPlant(ModItems.GARLIC.get(), ModItems.GARLIC.get(), ModBlocks.GARLIC.get(), null);
            registerPlant(Items.SNOWBALL, ModItems.SNOWBELL_SEEDS.get(), ModBlocks.SNOWBELL.get(), ModItems.ICY_NEEDLE.get());
            registerPlant(ModItems.WATER_ARTICHOKE_BULB.get(), ModItems.WATER_ARTICHOKE_SEEDS.get(),ModBlocks.WATER_ARTICHOKE.get(), null);
            registerPlant(ModItems.WOLFSBANE.get(), ModItems.WOLFSBANE_SEEDS.get(), ModBlocks.WOLFSBANE.get(), null);
            registerPlant(ModItems.MANDRAKE_ROOT.get(), ModItems.MANDRAKE_SEEDS.get(), ModBlocks.MANDRAKE.get(), null);
        }
        private void registerPlant(Item itemDrop, Item seed, Block block, Item optional){
            LootTable.Builder builder = buildBaseTable(seed, 1);
            builder.withPool(
                    addBinomialDistChance(
                            itemDrop,
                            ModCropBase.getMaxAge().ordinal()/8f,
                            block,
                            1,
                            StatePropertiesPredicate.Builder.properties().hasProperty(ModCropBase.AGE, ModCropBase.getMaxAge())
                    )
            );
            builder.withPool(
                    addBinomialDistChance(
                            seed,
                            ModCropBase.getMaxAge().ordinal()/8f,
                            block,
                            1,
                            StatePropertiesPredicate.Builder.properties().hasProperty(ModCropBase.AGE, ModCropBase.getMaxAge())
                    )
            );

            if(optional != null)
                builder.withPool(
                        addBinomialDistChance(
                                optional,
                                ModCropBase.getMaxAge().ordinal()/16f,
                                block,
                                0,
                                StatePropertiesPredicate.Builder.properties().hasProperty(ModCropBase.AGE, ModCropBase.getMaxAge())
                        )
                );
            add(block, builder);
        }

        private LootTable.Builder buildBaseTable(Item item,int rolls){
            return LootTable.lootTable().withPool(
                    LootPool.lootPool()
                            .when(SurvivesExplosion.survivesExplosion())
                            .setRolls(ConstantRange.exactly(rolls))
                            .add(ItemLootEntry.lootTableItem(item))
            );
        }

        private LootPool.Builder addBinomialDistChance(Item itemDrop, float chance, Block block, int extra, StatePropertiesPredicate.Builder builder){
            return LootPool.lootPool().when(SurvivesExplosion.survivesExplosion())
                    .add(ItemLootEntry.lootTableItem(itemDrop))
                    .apply(ApplyBonus.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, chance, extra))
                    .when(BlockStateProperty.hasBlockStateProperties(block).setProperties(builder));
        }

    }

    public static class ModMobsLootTables extends EntityLootTables{
        @Override
        protected Iterable<EntityType<?>> getKnownEntities() {
            return RegistryHandler.ENTITIES.getEntries()
                    .stream()
                    .map(RegistryObject::get)
                    .collect(Collectors.toList());
        }

        @Override
        protected void addTables() {
            add(ModEntities.MANDRAKE.get(), LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(3)).add(ItemLootEntry.lootTableItem(ModItems.MANDRAKE_ROOT.get())).add(ItemLootEntry.lootTableItem(ModItems.MANDRAKE_SEEDS.get()))));
            add(ModEntities.ENT.get(), LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(3)).add(ItemLootEntry.lootTableItem(ModItems.ENT_TWIG.get()))));
            add(ModEntities.TOAD.get(), LootTable.lootTable());
            add(ModEntities.DEMON.get(), LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(3)).add(ItemLootEntry.lootTableItem(ModItems.DEMON_HEART.get())).add(ItemLootEntry.lootTableItem(Items.GUNPOWDER)).add(ItemLootEntry.lootTableItem(Items.MAGMA_CREAM))));
        }
    }
}
