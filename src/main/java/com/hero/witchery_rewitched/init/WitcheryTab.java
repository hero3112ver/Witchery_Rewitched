package com.hero.witchery_rewitched.init;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import static com.hero.witchery_rewitched.WitcheryRewitched.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WitcheryTab {
    public static CreativeModeTab WITCHERY_TAB;

    @SubscribeEvent
    public static void registerCreativeModeTabs(CreativeModeTabEvent.Register event){
        WITCHERY_TAB = event.registerCreativeModeTab(new ResourceLocation(MODID, "witchery_tab"), (builder)->
                builder.title(Component.translatable("itemGroup.witchery_rewitched"))
                .icon(() -> WitcheryItems.UNCOOKED_CLAY_POT.get().getDefaultInstance())
        );
    }

    @SubscribeEvent
    public static void addCreative(CreativeModeTabEvent.BuildContents event)
    {
        if (event.getTab() == WITCHERY_TAB)
            event.acceptAll(    WitcheryItems.ITEMS
                    .getEntries()
                    .stream()
                    .map(RegistryObject::get)
                    .map(Item::getDefaultInstance)
                    .toList()
            );
    }
}
