package com.hero.witchery_rewitched.init.datagen;

import com.hero.witchery_rewitched.WitcheryRewitched;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = WitcheryRewitched.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new ModRecipeProvider(packOutput));
        // generator.addProvider(true, ModLootTableProvider.create(packOutput));
        generator.addProvider(true, new ModItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(true, new ModBlockStateProvider(packOutput, existingFileHelper));
        BlockTagsProvider btp = new ModBlockTags(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(true, btp);
        generator.addProvider(true, new ModItemTags(packOutput, lookupProvider, btp,existingFileHelper));
    }
}
