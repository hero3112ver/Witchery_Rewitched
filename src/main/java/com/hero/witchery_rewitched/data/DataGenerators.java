package com.hero.witchery_rewitched.data;

import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.data.altar.AltarDataProvider;
import com.hero.witchery_rewitched.data.client.ModBlockStateProvider;
import com.hero.witchery_rewitched.data.client.ModItemModelProvider;
import com.hero.witchery_rewitched.data.loot.ModLootModifierProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = WitcheryRewitched.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {
    private DataGenerators(){}

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        System.out.println("Generating data");
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        gen.addProvider(new ModBlockStateProvider(gen, existingFileHelper));
        gen.addProvider(new ModItemModelProvider(gen, existingFileHelper));

        ModBlockTagsProvider blockTags = new ModBlockTagsProvider(gen, existingFileHelper);
        gen.addProvider(blockTags);
        gen.addProvider(new ModItemTagsProvider(gen, blockTags, existingFileHelper));

        gen.addProvider(new ModRecipesProvider(gen));
        gen.addProvider(new ModLootTableProvider(gen));
        gen.addProvider(new ModLootTableProvider(gen));
        gen.addProvider(new ModLootModifierProvider(gen));

        gen.addProvider(new AltarDataProvider(gen));
    }
}
