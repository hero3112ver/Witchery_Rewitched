package com.hero.witchery_rewitched.init.datagen;

import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.init.WitcheryBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTags extends BlockTagsProvider {

    public static final TagKey<Block> MUTANDIS_EXTREMIS_PLANTS = mod("mutandis_extremis_plants");
    public static final TagKey<Block> MUTANDIS_PLANTS = mod("mutandis_plants");
    public static final TagKey<Block> WITCH_LOG = mod("witch_log");
    public static final TagKey<Block> WITCH_LEAVES = mod("witch_leaves");
    public ModBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, WitcheryRewitched.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        addAxeTags(pProvider);

        tag(BlockTags.LOGS)
                .add(WitcheryBlocks.ROWAN_LOG.get())
                .add(WitcheryBlocks.ALDER_LOG.get())
                .add(WitcheryBlocks.HAWTHORN_LOG.get())
                .add(WitcheryBlocks.STRIPPED_ALDER_LOG.get())
                .add(WitcheryBlocks.STRIPPED_ROWAN_LOG.get())
                .add(WitcheryBlocks.STRIPPED_HAWTHORN_LOG.get());
        tag(BlockTags.LOGS_THAT_BURN)
                .add(WitcheryBlocks.ROWAN_LOG.get())
                .add(WitcheryBlocks.ALDER_LOG.get())
                .add(WitcheryBlocks.HAWTHORN_LOG.get())
                .add(WitcheryBlocks.STRIPPED_ALDER_LOG.get())
                .add(WitcheryBlocks.STRIPPED_ROWAN_LOG.get())
                .add(WitcheryBlocks.STRIPPED_HAWTHORN_LOG.get());
        tag(BlockTags.PLANKS)
                .add(WitcheryBlocks.ROWAN_PLANKS.get())
                .add(WitcheryBlocks.ALDER_LOG.get())
                .add(WitcheryBlocks.HAWTHORN_LOG.get());

        tag((BlockTags.LEAVES))
                .add(WitcheryBlocks.ROWAN_LEAVES.get())
                .add(WitcheryBlocks.ALDER_LEAVES.get())
                .add(WitcheryBlocks.HAWTHORN_LEAVES.get());

        /*tag(BlockTags.SAPLINGS)
                .add(WitcheryBlocks.ROWAN_SAPLING.get())
                .add(WitcheryBlocks.ALDER_SAPLING.get())
                .add(WitcheryBlocks.HAWTHORN_SAPLING.get());*/

        tag(ModBlockTags.MUTANDIS_PLANTS)
                .addTag(BlockTags.SMALL_FLOWERS)
                .addTag(BlockTags.SAPLINGS)
                .add(Blocks.GRASS)
                //.add(WitcheryBlocks.ROWAN_SAPLING.get())
                .add(Blocks.TALL_GRASS)
                //.add(WitcheryBlocks.ALDER_SAPLING.get())
                //.add(WitcheryBlocks.HAWTHORN_SAPLING.get())
                .add(WitcheryBlocks.SPANISH_MOSS.get())
                //.add(WitcheryBlocks.EMBER_MOSS.get())
                //.add(WitcheryBlocks.GLINTWEED.get())
                ;

        tag(ModBlockTags.WITCH_LEAVES)
                .add(WitcheryBlocks.ROWAN_LEAVES.get())
                .add(WitcheryBlocks.ALDER_LEAVES.get())
                .add(WitcheryBlocks.HAWTHORN_LEAVES.get());

        tag(ModBlockTags.WITCH_LOG)
                .add(WitcheryBlocks.ROWAN_LOG.get())
                .add(WitcheryBlocks.ALDER_LOG.get())
                .add(WitcheryBlocks.HAWTHORN_LOG.get());

        tag(BlockTags.CROPS)
                .add(WitcheryBlocks.BELLADONNA.get())
                .add(WitcheryBlocks.GARLIC.get())
                .add(WitcheryBlocks.SNOWBELL.get())
                .add(WitcheryBlocks.WOLFSBANE.get())
                .add(WitcheryBlocks.WATER_ARTICHOKE.get());

        tag(ModBlockTags.MUTANDIS_EXTREMIS_PLANTS)
                .addTag(BlockTags.CROPS)
                .add(Blocks.SUGAR_CANE)
                .add(Blocks.CACTUS);
    }

    private void addAxeTags(HolderLookup.Provider pProvider) {
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(WitcheryBlocks.ROWAN_LOG.get())
                .add(WitcheryBlocks.STRIPPED_ROWAN_LOG.get())
                .add(WitcheryBlocks.ROWAN_PLANKS.get())
                .add(WitcheryBlocks.ALDER_LOG.get())
                .add(WitcheryBlocks.STRIPPED_ALDER_LOG.get())
                .add(WitcheryBlocks.ALDER_PLANKS.get())
                .add(WitcheryBlocks.HAWTHORN_LOG.get())
                .add(WitcheryBlocks.STRIPPED_HAWTHORN_LOG.get())
                .add(WitcheryBlocks.HAWTHORN_PLANKS.get());
    }


    private static TagKey<Block> mod(String name){
        return BlockTags.create(new ResourceLocation(WitcheryRewitched.MODID, name));
    }

}
