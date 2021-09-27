package com.hero.witchery_rewitched.data.client;

import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.init.ModItemModelProperties;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
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


        ModelFile itemGenerated = getExistingFile(mcLoc("item/generated"));

        builderBlock(itemGenerated, "critter_snare_none");
        builderBlock(itemGenerated, "critter_snare_bat");
        builderBlock(itemGenerated, "critter_snare_slime");
        builderBlock(itemGenerated, "critter_snare_silverfish");
        buildCritterSnare(itemGenerated);
        buildCircleTalisman(itemGenerated);

        builder(itemGenerated, "uncooked_clay_pot");
        builder(itemGenerated, "cooked_clay_pot");
        builder(itemGenerated, "rowan_berries");
        builderBlock(itemGenerated,"rowan_sapling");
        builderBlock(itemGenerated,"alder_sapling");
        builderBlock(itemGenerated,"hawthorn_sapling");

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

        builderBlock(itemGenerated,"ember_moss");
        builderBlock(itemGenerated,"spanish_moss");
        builderBlock(itemGenerated,"glintweed");

        builder(itemGenerated, "arthana");
        builder(itemGenerated, "waystone");
        builder(itemGenerated, "bound_waystone");
        builder(itemGenerated, "ender_dew");
        builder(itemGenerated, "ent_twig");
        builder(itemGenerated, "mutating_sprig");
    }

    private ItemModelBuilder builder(ModelFile itemGenerated, String name) {
        return getBuilder(name).parent(itemGenerated).texture("layer0", "item/" + name);
    }
    private ItemModelBuilder builderBlock(ModelFile itemGenerated, String name) {
        return getBuilder(name).parent(itemGenerated).texture("layer0", "block/" + name);
    }

    private ItemModelBuilder buildCritterSnare(ModelFile itemGenerated){
        ItemModelBuilder modelBuilder = getBuilder("critter_snare").parent(itemGenerated).texture("layer0", "block/critter_snare_none");
        return modelBuilder
                .override().predicate(new ResourceLocation(WitcheryRewitched.MODID, "critter"), 0).model(new ModelFile.ExistingModelFile(new ResourceLocation(WitcheryRewitched.MODID, "item/critter_snare_none"), existingFileHelper)).end()
                .override().predicate(new ResourceLocation(WitcheryRewitched.MODID, "critter"), 1).model(new ModelFile.ExistingModelFile(new ResourceLocation(WitcheryRewitched.MODID, "item/critter_snare_bat"), existingFileHelper)).end()
                .override().predicate(new ResourceLocation(WitcheryRewitched.MODID, "critter"), 2).model(new ModelFile.ExistingModelFile(new ResourceLocation(WitcheryRewitched.MODID, "item/critter_snare_silverfish"), existingFileHelper)).end()
                .override().predicate(new ResourceLocation(WitcheryRewitched.MODID, "critter"), 3).model(new ModelFile.ExistingModelFile(new ResourceLocation(WitcheryRewitched.MODID, "item/critter_snare_slime"), existingFileHelper)).end();
    }

    private ItemModelBuilder buildCircleTalisman(ModelFile itemGenerated){
        char[] circles = {'x','r','i','o'};
        for(int i = 0; i < 4; i++){
            for(int x = 0; x < 4; x++){
                for(int z = 0; z < 4; z++){
                    ItemModelBuilder builder = getBuilder("circle_talisman_" + circles[i] + circles[x] + circles[z]).parent(itemGenerated)
                            .texture("layer0", "item/circle_talisman/base");
                    int count  = 1;
                    if(i != 0){
                        builder.texture("layer" + count, "item/circle_talisman/" + circles[i] + 1);
                        count++;
                    }
                    if(x != 0){
                        builder.texture("layer" + count, "item/circle_talisman/" + circles[x] + 2);
                        count++;
                    }
                    if(z != 0){
                        builder.texture("layer" + count, "item/circle_talisman/" + circles[z] + 3);
                    }
                }
            }
        }


        ItemModelBuilder modelBuilder = getBuilder("circle_talisman").parent(itemGenerated).texture("layer0", "item/circle_talisman/base");
        // Sigdigit mappings, 0 = none, 1 = ritual chalk, 2 = infernal chalk, 3 = otherwhere chalk, 1s = 1st circle, 10s = 2nd  circle, 100s = 3rd circle
        for (int i = 0; i < 4; i++) {
            for (int x = 0; x < 4; x++) {
                for (int z = 0; z < 4; z++) {
                    modelBuilder.override().predicate(new ResourceLocation(WitcheryRewitched.MODID, "circle"), ModItemModelProperties.getTalismanPredicate(circles[z] + "" + circles[x] + circles[i]))
                            .model(new ModelFile.ExistingModelFile(new ResourceLocation(WitcheryRewitched.MODID, "item/circle_talisman_" + circles[z] + circles[x] + circles[i]), existingFileHelper)).end();
                }
            }
        }
        return modelBuilder;

    }
}
