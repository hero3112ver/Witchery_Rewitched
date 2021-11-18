package com.hero.witchery_rewitched.data.altar;

import com.google.gson.*;
import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.util.util.NameUtils;
import com.hero.witchery_rewitched.init.ModBlocks;
import com.hero.witchery_rewitched.init.ModTags;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class AltarDataProvider implements IDataProvider {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator gen;
    private final Map<Block, Point> toSerializeBlocks = new HashMap<>();
    private final Map<ITag<Block>, Point> toSerializeTags = new HashMap<>();

    public AltarDataProvider(DataGenerator gen){
        this.gen = gen;
    }

    @Override
    public void run(DirectoryCache pCache) throws IOException {
        start();

        JsonObject json = new JsonObject();
        toSerializeBlocks.forEach(LamdbaExceptionUtils.rethrowBiConsumer((block, point) -> {
            JsonObject elem = new JsonObject();
            elem.addProperty("power", point.getX());
            elem.addProperty("max", point.getY());
            json.add(NameUtils.from(block).toString(), elem);
        }));
        IDataProvider.save(GSON, pCache, json, gen.getOutputFolder().resolve("data/" + WitcheryRewitched.MODID + "/altar/blocks.json"));

        JsonObject json2 = new JsonObject();
        toSerializeTags.forEach(LamdbaExceptionUtils.rethrowBiConsumer((tag, point) -> {
            JsonObject elem = new JsonObject();
            elem.addProperty("power", point.getX());
            elem.addProperty("max", point.getY());
            json2.add(tag.toString().substring(tag.toString().indexOf('[')+1, tag.toString().indexOf(']')), elem);
        }));
        IDataProvider.save(GSON, pCache, json2, gen.getOutputFolder().resolve("data/" + WitcheryRewitched.MODID + "/altar/tags.json"));
    }

    private void add(Block block, int power, int max){
        toSerializeBlocks.put(block, new Point(power, max));
    }

    private void add(ITag<Block> tags, int power, int max){
        toSerializeTags.put(tags, new Point(power, max));
    }
    @Override
    public String getName() {
        return "Altar Data : " + WitcheryRewitched.MODID;
    }

    private void start(){
        add(Blocks.DRAGON_EGG,250, 1);
        add(ModBlocks.BOUND_DEMON_HEART.get(), 40, 2);
        add(ModBlocks.SPANISH_MOSS.get(), 3, 20);
        add(BlockTags.FLOWERS ,4, 30);
        add(Blocks.WHEAT,4, 20);
        add(Blocks.PUMPKIN,4, 20);
        add(Blocks.SEA_PICKLE, 4,20);
        add(Blocks.MELON,4, 20);
        add(Blocks.CARROTS, 4, 20);
        add(Blocks.POTATOES, 4, 20);
        add(ModBlocks.BELLADONNA.get(), 4, 20);
        add(ModBlocks.MANDRAKE.get(), 4, 20);
        add(ModBlocks.WATER_ARTICHOKE.get(), 4, 20);
        add(ModBlocks.SNOWBELL.get(), 4, 20);
        add(ModBlocks.EMBER_MOSS.get(), 4, 20);
        add(ModTags.Blocks.WITCH_LEAVES ,4, 50);
        add(ModTags.Blocks.WITCH_LOG, 3, 100);
        add(BlockTags.SAPLINGS, 4, 20);
        add(BlockTags.LOGS,4, 20);
        add(BlockTags.LEAVES,3, 100);
        add(Blocks.TALL_GRASS,3, 50);
        add(Blocks.RED_MUSHROOM, 3, 20);
        add(Blocks.BROWN_MUSHROOM, 3, 20);
        add(Blocks.CRIMSON_FUNGUS, 3,20);
        add(Blocks.WARPED_FUNGUS, 3,20);
        add(Blocks.CACTUS, 3, 50);
        add(Blocks.BAMBOO, 3, 50);
        add(BlockTags.CORAL_BLOCKS, 3, 20);
        add(BlockTags.CORALS, 3, 20);
        add(Blocks.SUGAR_CANE,3, 50);
        add(Blocks.PUMPKIN_STEM, 3, 20);
        add(Blocks.RED_MUSHROOM_BLOCK, 3, 20);
        add(Blocks.BROWN_MUSHROOM_BLOCK, 3, 20);
        add(Blocks.MELON_STEM, 3, 20);
        add(Blocks.COCOA, 3, 20);
        //add(ModBLocks.WISPY_COTTON.get(),  3, 20);
        add(BlockTags.LOGS, 2, 50);
        add(Blocks.GRASS_BLOCK, 2, 80);
        add(Blocks.SEAGRASS, 2, 80);
        add(Blocks.CRIMSON_FUNGUS,2, 80);
        add(Blocks.WARPED_FUNGUS,2, 80);
        add(Blocks.VINE, 2, 50);
        add(Blocks.WEEPING_VINES, 2, 50);
        add(Blocks.TWISTING_VINES, 2, 50);
        add(ModBlocks.GLINTWEED.get(), 2, 20);
        //add(ModBlocks.CRITTER_SNARE.get(), 2, 10);
        //add(ModBlocks.GRASSPER.get(), 2, 10);
        //add(ModBlocks.BLOOD_POPPY.get(), 2,10);
        add(Blocks.DIRT, 1, 80);
        add(Blocks.FARMLAND, 1, 100);
        add(Blocks.WATER, 1, 50);
        add(Blocks.MYCELIUM, 1, 80);
    }

}
