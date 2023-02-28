package com.hero.witchery_rewitched.init.datagen;

import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.init.WitcheryBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTags extends ItemTagsProvider {

    public static final TagKey<Item> ROWAN_LOG = mod("logs/rowan");
    public static final TagKey<Item> ALDER_LOG = mod("logs/alder");
    public static final TagKey<Item> HAWTHORN_LOG = mod("logs/hawthorn");
    public ModItemTags(PackOutput p_255871_, CompletableFuture<HolderLookup.Provider> p_256035_, TagsProvider<Block> p_256467_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_255871_, p_256035_, p_256467_, WitcheryRewitched.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ROWAN_LOG)
                .add(WitcheryBlocks.ROWAN_LOG.get().asItem())
                .add(WitcheryBlocks.STRIPPED_ROWAN_LOG.get().asItem());
        tag(ALDER_LOG)
                .add(WitcheryBlocks.ALDER_LOG.get().asItem())
                .add(WitcheryBlocks.STRIPPED_ALDER_LOG.get().asItem());
        tag(HAWTHORN_LOG)
                .add(WitcheryBlocks.HAWTHORN_LOG.get().asItem())
                .add(WitcheryBlocks.STRIPPED_HAWTHORN_LOG.get().asItem());

        tag(ItemTags.LOGS)
                .add(WitcheryBlocks.ROWAN_LOG.get().asItem())
                .add(WitcheryBlocks.ALDER_LOG.get().asItem())
                .add(WitcheryBlocks.HAWTHORN_LOG.get().asItem())
                .add(WitcheryBlocks.STRIPPED_ALDER_LOG.get().asItem())
                .add(WitcheryBlocks.STRIPPED_HAWTHORN_LOG.get().asItem())
                .add(WitcheryBlocks.STRIPPED_ROWAN_LOG.get().asItem());

        tag(ItemTags.LOGS_THAT_BURN)
                .add(WitcheryBlocks.ROWAN_LOG.get().asItem())
                .add(WitcheryBlocks.ALDER_LOG.get().asItem())
                .add(WitcheryBlocks.HAWTHORN_LOG.get().asItem())
                .add(WitcheryBlocks.STRIPPED_ALDER_LOG.get().asItem())
                .add(WitcheryBlocks.STRIPPED_HAWTHORN_LOG.get().asItem())
                .add(WitcheryBlocks.STRIPPED_ROWAN_LOG.get().asItem());

        tag(ItemTags.LEAVES)
                .add(WitcheryBlocks.ROWAN_LEAVES.get().asItem())
                .add(WitcheryBlocks.ALDER_LEAVES.get().asItem())
                .add(WitcheryBlocks.HAWTHORN_LEAVES.get().asItem());

        tag(ItemTags.PLANKS)
                .add(WitcheryBlocks.ROWAN_PLANKS.get().asItem())
                .add(WitcheryBlocks.ALDER_PLANKS.get().asItem())
                .add(WitcheryBlocks.HAWTHORN_PLANKS.get().asItem());
    }

    private static TagKey<Item> mod(String name){
        return ItemTags.create(new ResourceLocation(WitcheryRewitched.MODID, name));
    }
}
