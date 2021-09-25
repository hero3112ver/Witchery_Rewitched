package com.hero.witchery_rewitched.init;

import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.block.critter_snare.CritterEnum;
import com.hero.witchery_rewitched.init.ModBlocks;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class ModItemModelProperties extends ItemModelsProperties {
    static {
        register(ModBlocks.CRITTER_SNARE.get().asItem(), new ResourceLocation(WitcheryRewitched.MODID, "critter"), (itemStack, clientWorld, entity) -> {
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

        register(ModItems.CIRCLE_TALISMAN.get(), new ResourceLocation(WitcheryRewitched.MODID, "circle"), (itemStack, clientWorld, entity) -> {

                if(itemStack.hasTag() && itemStack.getTag().contains("circles")){
                    String tag = itemStack.getTag().getString("circles");
                    return getTalismanPredicate(tag);
                }
              return 0;
            }
        );


    }

    public static float getTalismanPredicate(String str) {
        double predicate = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            int num = 0;
            if (c == 'r')
                num = 1;
            else if (c == 'i')
                num = 2;
            else if (c == 'o')
                num = 3;
            predicate += Math.pow(10, i) * num;
        }
        return (float) predicate;
    }

    public static void register(){}
}
