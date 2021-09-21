package com.hero.witchery_rewitched.data.client;

import com.hero.witchery_rewitched.WitcheryRewitched;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, WitcheryRewitched.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent("rowan_log", modLoc("block/rowan_log"));
        withExistingParent("rowan_planks", modLoc("block/rowan_planks"));
        withExistingParent("stripped_rowan_log", modLoc("block/stripped_rowan_log"));

        withExistingParent("alder_log", modLoc("block/alder_log"));
        withExistingParent("alder_planks", modLoc("block/alder_planks"));
        withExistingParent("stripped_alder_log", modLoc("block/stripped_alder_log"));

        withExistingParent("hawthorn_log", modLoc("block/hawthorn_log"));
        withExistingParent("hawthorn_planks", modLoc("block/hawthorn_planks"));
        withExistingParent("stripped_hawthorn_log", modLoc("block/stripped_hawthorn_log"));

        withExistingParent("rowan_leaves", modLoc("block/rowan_leaves"));
        withExistingParent("alder_leaves", modLoc("block/alder_leaves"));
        withExistingParent("hawthorn_leaves", modLoc("block/hawthorn_leaves"));
        withExistingParent("witch_oven", modLoc("block/witch_oven"));
        withExistingParent("witch_cauldron", modLoc("block/witch_cauldron"));
        withExistingParent("filtered_fume_funnel", modLoc("block/filtered_fume_funnel"));
        withExistingParent("fume_funnel", modLoc("block/fume_funnel"));
        withExistingParent("altar", modLoc("block/altar"));
        withExistingParent("distillery", modLoc("block/distillery"));
        withExistingParent("poppet_shelf", modLoc("block/poppet_shelf"));
        withExistingParent("grassper", modLoc("block/grassper"));
        withExistingParent("critter_snare", modLoc("block/critter_snare"));


        ModelFile itemGenerated = getExistingFile(mcLoc("item/generated"));

        builder(itemGenerated, "uncooked_clay_pot");
        builder(itemGenerated, "cooked_clay_pot");
        builder(itemGenerated, "rowan_berries");
        builderSap(itemGenerated,"rowan_sapling");
        builderSap(itemGenerated,"alder_sapling");
        builderSap(itemGenerated,"hawthorn_sapling");

        builder(itemGenerated, "breath_of_the_goddess");
        builder(itemGenerated, "exhale_of_the_horned_one");
        builder(itemGenerated, "foul_fume");
        builder(itemGenerated, "hint_of_rebirth");
        builder(itemGenerated, "odor_of_purity");
        builder(itemGenerated, "reek_of_misfortune");
        builder(itemGenerated, "whiff_of_magic");
        builder(itemGenerated, "wood_ash");

        builder(itemGenerated, "belladonna_seeds");
        builder(itemGenerated, "belladonna");
        builder(itemGenerated, "garlic");

        builder(itemGenerated, "snowbell_seeds");
        builder(itemGenerated, "icy_needle");
        builder(itemGenerated, "water_artichoke_bulb");
        builder(itemGenerated, "water_artichoke_seeds");
        builder(itemGenerated, "wolfsbane");
        builder(itemGenerated, "wolfsbane_seeds");
        builder(itemGenerated, "mandrake_root");
        builder(itemGenerated, "mandrake_seeds");
        builder(itemGenerated, "anointing_paste");
        builder(itemGenerated, "mutandis");
        builder(itemGenerated, "fume_filter");
        builder(itemGenerated, "attuned_stone");
        builder(itemGenerated, "charged_attuned_stone");
        builder(itemGenerated, "filled_taglock");
        builder(itemGenerated, "taglock_kit");
        builder(itemGenerated, "bone_needle");

        builder(itemGenerated, "gold_chalk");
        builder(itemGenerated, "ritual_chalk");
        builder(itemGenerated, "otherwhere_chalk");
        builder(itemGenerated, "infernal_chalk");
        builder(itemGenerated, "gypsum");
        builder(itemGenerated, "oil_of_vitriol");
        builder(itemGenerated, "quicklime");

        builder(itemGenerated, "poppet");
        builder(itemGenerated, "earth_protection_poppet");
        builder(itemGenerated, "water_protection_poppet");
        builder(itemGenerated, "fire_protection_poppet");
        builder(itemGenerated, "hunger_protection_poppet");
        builder(itemGenerated, "tool_protection_poppet");
        builder(itemGenerated, "armor_protection_poppet");
        builder(itemGenerated, "death_protection_poppet");

        builder(itemGenerated, "diamond_vapor");
        builder(itemGenerated, "refined_evil");
        builder(itemGenerated, "mutandis_extremis");
        builder(itemGenerated, "tear_of_the_goddess");
        builder(itemGenerated, "drop_of_luck");
        builder(itemGenerated, "wool_of_bat");

        builderSap(itemGenerated,"ember_moss");
        builderSap(itemGenerated,"spanish_moss");
        builderSap(itemGenerated,"glintweed");

        builder(itemGenerated, "arthana");
        builder(itemGenerated, "waystone");
        builder(itemGenerated, "bound_waystone");
        builder(itemGenerated, "ender_dew");
    }

    private ItemModelBuilder builder(ModelFile itemGenerated, String name) {
        return getBuilder(name).parent(itemGenerated).texture("layer0", "item/" + name);
    }
    private ItemModelBuilder builderSap(ModelFile itemGenerated, String name) {
        return getBuilder(name).parent(itemGenerated).texture("layer0", "block/" + name);
    }
}
