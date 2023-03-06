package com.hero.witchery_rewitched.init.datagen.LootTables;

import com.hero.witchery_rewitched.block.ThreeStageCrop;
import com.hero.witchery_rewitched.init.WitcheryBlocks;
import com.hero.witchery_rewitched.init.WitcheryItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {


    public ModBlockLootTableProvider() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        dropSelf(WitcheryBlocks.ROWAN_LOG.get());
        dropSelf(WitcheryBlocks.ALDER_LOG.get());
        dropSelf(WitcheryBlocks.HAWTHORN_LOG.get());
        dropSelf(WitcheryBlocks.STRIPPED_ROWAN_LOG.get());
        dropSelf(WitcheryBlocks.STRIPPED_ALDER_LOG.get());
        dropSelf(WitcheryBlocks.STRIPPED_HAWTHORN_LOG.get());
        dropSelf(WitcheryBlocks.ROWAN_PLANKS.get());
        dropSelf(WitcheryBlocks.ALDER_PLANKS.get());
        dropSelf(WitcheryBlocks.HAWTHORN_PLANKS.get());
        add(WitcheryBlocks.SPANISH_MOSS.get(), createShearsOnlyDrop(WitcheryBlocks.SPANISH_MOSS.get()));
        add(WitcheryBlocks.GLINTWEED.get(), createShearsOnlyDrop(WitcheryBlocks.GLINTWEED.get()));
        add(WitcheryBlocks.EMBER_MOSS.get(), createShearsOnlyDrop(WitcheryBlocks.EMBER_MOSS.get()));

        add(WitcheryBlocks.ROWAN_LEAVES.get(), createLeavesDrops(WitcheryBlocks.ROWAN_LEAVES.get(), WitcheryBlocks.ROWAN_SAPLING.get(),
                BlockLootSubProvider.NORMAL_LEAVES_SAPLING_CHANCES).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(BlockLootSubProvider.HAS_SHEARS.or(BlockLootSubProvider.HAS_SILK_TOUCH).invert()).add(this.applyExplosionCondition(WitcheryBlocks.ROWAN_SAPLING.get(), LootItem.lootTableItem(WitcheryItems.ROWAN_BERRY.get())).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE,  .1F, .2F, .4F, .8F, 1.2F)))));
        add(WitcheryBlocks.ALDER_LEAVES.get(), createLeavesDrops(WitcheryBlocks.ALDER_LEAVES.get(), WitcheryBlocks.ALDER_SAPLING.get(),
                BlockLootSubProvider.NORMAL_LEAVES_SAPLING_CHANCES));
        add(WitcheryBlocks.HAWTHORN_LEAVES.get(), createLeavesDrops(WitcheryBlocks.HAWTHORN_LEAVES.get(), WitcheryBlocks.HAWTHORN_SAPLING.get(),
                BlockLootSubProvider.NORMAL_LEAVES_SAPLING_CHANCES));

        dropSelf(WitcheryBlocks.ROWAN_SAPLING.get());
        dropSelf(WitcheryBlocks.ALDER_SAPLING.get());
        dropSelf(WitcheryBlocks.HAWTHORN_SAPLING.get());

        createCrop(WitcheryBlocks.BELLADONNA.get(), WitcheryItems.BELLADONNA_SEEDS.get(), WitcheryItems.BELLADONNA.get());
        createCrop(WitcheryBlocks.WATER_ARTICHOKE.get(), WitcheryItems.WATER_ARTICHOKE_SEEDS.get(), WitcheryItems.WATER_ARTICHOKE_BULB.get());
        createCrop(WitcheryBlocks.WOLFSBANE.get(), WitcheryItems.WOLFSBANE_SEEDS.get(), WitcheryItems.WOLFSBANE.get());
        createCrop(WitcheryBlocks.MANDRAKE.get(), WitcheryItems.MANDRAKE_SEEDS.get(), WitcheryItems.MANDRAKE_ROOT.get());
        add(WitcheryBlocks.GARLIC.get(), applyExplosionDecay(WitcheryBlocks.GARLIC.get(), LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(WitcheryItems.GARLIC.get()))).withPool(LootPool.lootPool().when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(WitcheryBlocks.GARLIC.get()).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ThreeStageCrop.AGE, 2))).add(LootItem.lootTableItem(WitcheryItems.GARLIC.get())).apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3)))));
        add(WitcheryBlocks.SNOWBELL.get(), applyExplosionDecay(WitcheryBlocks.SNOWBELL.get(),
                LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .add(LootItem.lootTableItem(WitcheryItems.SNOWBELL_SEEDS.get())))
                .withPool(
                    LootPool.lootPool()
                        .when(
                            LootItemBlockStatePropertyCondition
                                .hasBlockStateProperties(WitcheryBlocks.SNOWBELL.get())
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ThreeStageCrop.AGE, 2)))
                            .add(LootItem.lootTableItem(WitcheryItems.ICY_NEEDLE.get())
                            )
                )
                .withPool(LootPool.lootPool()
                        .when(
                            LootItemBlockStatePropertyCondition
                                .hasBlockStateProperties(WitcheryBlocks.SNOWBELL.get())
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ThreeStageCrop.AGE, 2))
                        )
                        .setRolls(UniformGenerator.between(0,1))
                            .add(LootItem.lootTableItem(WitcheryItems.SNOWBELL_SEEDS.get()))
                )
                .withPool(LootPool.lootPool()
                        .when(
                                LootItemBlockStatePropertyCondition
                                        .hasBlockStateProperties(WitcheryBlocks.SNOWBELL.get())
                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ThreeStageCrop.AGE, 2)))
                        .add(LootItem.lootTableItem(Items.SNOWBALL))
                        .setRolls(UniformGenerator.between(0,2))
        )));
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return WitcheryBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }

    private void createCrop(Block crop, Item seed, Item drop){
        add(crop, block -> createCropDrops(
                crop,
                drop,
                seed,
                LootItemBlockStatePropertyCondition
                        .hasBlockStateProperties(crop)
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ThreeStageCrop.AGE, 2))
        ));
    }
}
