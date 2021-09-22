package com.hero.witchery_rewitched.init;

import com.hero.witchery_rewitched.block.critter_snare.CritterEnum;
import com.hero.witchery_rewitched.init.ModBlocks;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class ModItemModelProperties extends ItemModelsProperties {
    static {
        register(ModBlocks.CRITTER_SNARE.get().asItem(), new ResourceLocation("critter"), (itemStack, clientWorld, entity) ->
                {
                    if(itemStack.hasTag() && itemStack.getTag().contains("BlockEntityTag")){
                        String id = ((CompoundNBT) itemStack.getTagElement("BlockEntityTag").get("entityData")).getString("id");
                        if(id.contains("bat"))
                            return 1;
                        else if(id.contains("silverfish"))
                            return 2;
                        else if(id.contains("slime"))
                            return 3;
                    }
                    return 0;
                }
        );
    }

    public static void register(){}
}
