package com.hero.witchery_rewitched.init.datagen;

import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.init.WitcheryBlocks;
import com.hero.witchery_rewitched.init.WitcheryItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;


public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, WitcheryRewitched.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(WitcheryItems.UNCOOKED_CLAY_POT);
        simpleItem(WitcheryItems.COOKED_CLAY_POT);
        simpleItem(WitcheryItems.ROWAN_BERRY);
        simpleItem(WitcheryItems.BREATH_OF_THE_GODDESS);
        simpleItem(WitcheryItems.EXHALE_OF_THE_HORNED_ONE);
        simpleItem(WitcheryItems.FOUL_FUME);
        simpleItem(WitcheryItems.HINT_OF_REBIRTH);
        simpleItem(WitcheryItems.ODOR_OF_PURITY);
        simpleItem(WitcheryItems.REEK_OF_MISFORTUNE);
        simpleItem(WitcheryItems.WHIFF_OF_MAGIC);
        simpleItem(WitcheryItems.WOOD_ASH);
        simpleItem(WitcheryItems.BELLADONNA);
        simpleItem(WitcheryItems.ICY_NEEDLE);
        simpleItem(WitcheryItems.WATER_ARTICHOKE_BULB);
        simpleItem(WitcheryItems.WOLFSBANE);
        simpleItem(WitcheryItems.ATTUNED_STONE);
        simpleItem(WitcheryItems.CHARGED_ATTUNED_STONE);
//        simpleItem(WitcheryItems.FUME_FILTER);
        simpleItem(WitcheryItems.BONE_NEEDLE);
        simpleItem(WitcheryItems.GYPSUM);
        simpleItem(WitcheryItems.OIL_OF_VITRIOL);
        simpleItem(WitcheryItems.QUICKLIME);
        simpleItem(WitcheryItems.POPPET);
        simpleItem(WitcheryItems.DROP_OF_LUCK);
        simpleItem(WitcheryItems.REFINED_EVIL);
        simpleItem(WitcheryItems.DIAMOND_VAPOR);
        simpleItem(WitcheryItems.TEAR_OF_THE_GODDESS);
        simpleItem(WitcheryItems.WOOL_OF_BAT);
        simpleItem(WitcheryItems.ENDER_DEW);
        simpleItem(WitcheryItems.ENT_TWIG);
        simpleItem(WitcheryItems.DEMON_HEART);
        simpleItem(WitcheryItems.DEMON_BLOOD);
        simpleItem(WitcheryItems.BELLADONNA_SEEDS);
        simpleItem(WitcheryItems.SNOWBELL_SEEDS);
        simpleItem(WitcheryItems.WOLFSBANE_SEEDS);
        simpleItem(WitcheryItems.GARLIC);
        simpleItem(WitcheryItems.MANDRAKE_ROOT);
        simpleItem(WitcheryItems.WATER_ARTICHOKE_SEEDS);
        simpleItem(WitcheryItems.MANDRAKE_SEEDS);

        createBlockItems();
    }

    private void simpleItem(RegistryObject<Item> item){
        withExistingParent(item.getId().getPath(), new ResourceLocation("item/generated"))
                .texture("layer0", new ResourceLocation(WitcheryRewitched.MODID, "item/" + item.getId().getPath()));
    }
    private ItemModelBuilder handheldItem(RegistryObject<Item> item){
        return withExistingParent(item.getId().getPath(), new ResourceLocation("item/handheld"))
                .texture("layer0", new ResourceLocation(WitcheryRewitched.MODID, "item/" + item.getId().getPath()));
    }

    private void simpleBlockItem(RegistryObject<Item> item){
        withExistingParent(item.getId().getPath(), new ResourceLocation("item/generated"))
                .texture("layer0", new ResourceLocation(WitcheryRewitched.MODID, "block/" + item.getId().getPath()));
    }

    private void createBlockItems(){
        simpleBlockItem(getItem(WitcheryBlocks.ROWAN_SAPLING));
        simpleBlockItem(getItem(WitcheryBlocks.ALDER_SAPLING));
        simpleBlockItem(getItem(WitcheryBlocks.HAWTHORN_SAPLING));
    }

    private RegistryObject<Item> getItem(RegistryObject< ? extends Block> block){
        return Objects.requireNonNull(WitcheryItems.ITEMS.getEntries().stream().filter((item) -> item.get() == block.get().asItem()).findFirst().orElse(null));
    }
}
