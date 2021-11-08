package com.hero.witchery_rewitched.util.listeners;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.data.altar.AltarDataProvider;
import net.minecraft.block.Block;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;


import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AltarPowererReloadListener implements IFutureReloadListener {
    public static volatile AltarPowererReloadListener INSTANCE;

    public Map<Block, Point> BLOCKS = new HashMap<>();
    public Map<ITag.INamedTag<Block>, Point> TAGS = new HashMap<>();

    @Override
    public CompletableFuture<Void> reload(IStage pStage, IResourceManager pResourceManager, IProfiler pPreparationsProfiler, IProfiler pReloadProfiler, Executor pBackgroundExecutor, Executor pGameExecutor) {
        CompletableFuture<Map<Block, Point>> blockFuture = getBlocks(pResourceManager, pBackgroundExecutor);
        CompletableFuture<Map<ITag.INamedTag<Block>, Point>> tagFuture = getTags(pResourceManager, pBackgroundExecutor);
        return CompletableFuture.allOf(blockFuture, tagFuture).thenCompose(pStage::wait).thenAcceptAsync((result) ->{
            BLOCKS = blockFuture.join();
            TAGS = tagFuture.join();
            INSTANCE = this;
        }, pGameExecutor);
    }

    public CompletableFuture<Map<Block, Point>> getBlocks(IResourceManager resourceManager, Executor executor){
        return CompletableFuture.supplyAsync(() -> {
            HashMap<Block, Point> map = new HashMap<>();
            try {
                InputStream blocksFile = resourceManager.getResource(new ResourceLocation(WitcheryRewitched.MODID, "altar/blocks.json")).getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(blocksFile, StandardCharsets.UTF_8));

                JsonObject blocksData = JSONUtils.fromJson(AltarDataProvider.GSON, reader, JsonObject.class);
                if(blocksData == null) throw new IOException();

                for(Map.Entry<String, JsonElement> bD : blocksData.entrySet()){
                    JsonObject blockData = bD.getValue().getAsJsonObject();
                    Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(bD.getKey()));
                    map.put(block, new Point(blockData.get("power").getAsInt(), blockData.get("max").getAsInt()));
                }

            } catch (IOException e) {
                WitcheryRewitched.LOGGER.error("Couldn't load power data from blocks, is it bugged?");
            }
            return map;
        });
    }
    public CompletableFuture<Map<ITag.INamedTag<Block>, Point>> getTags(IResourceManager resourceManager, Executor executor){
        return CompletableFuture.supplyAsync(() -> {
            HashMap<ITag.INamedTag<Block>, Point> map = new HashMap<>();
            try {
                InputStream blocksFile = resourceManager.getResource(new ResourceLocation(WitcheryRewitched.MODID, "altar/tags.json")).getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(blocksFile, StandardCharsets.UTF_8));

                JsonObject blocksData = JSONUtils.fromJson(AltarDataProvider.GSON, reader, JsonObject.class);
                if(blocksData == null) throw new IOException();

                for(Map.Entry<String, JsonElement> bD : blocksData.entrySet()){
                    JsonObject blockData = bD.getValue().getAsJsonObject();
                    ITag.INamedTag<Block> tag = BlockTags.createOptional(new ResourceLocation(bD.getKey()));
                    map.put(tag, new Point(blockData.get("power").getAsInt(), blockData.get("max").getAsInt()));
                }

            } catch (IOException e) {
                WitcheryRewitched.LOGGER.error("Couldn't load power data from blocks, is it bugged?");
            }
            return map;
        });
    }


}
