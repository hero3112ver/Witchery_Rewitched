package com.hero.witchery_rewitched.data.client;

import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.block.critter_snare.CritterEnum;
import com.hero.witchery_rewitched.block.critter_snare.CritterSnareBlock;
import com.hero.witchery_rewitched.block.glyph.GlyphBlock;
import com.hero.witchery_rewitched.block.plants.EnumPlantAge;
import com.hero.witchery_rewitched.block.plants.ModCropBase;
import com.hero.witchery_rewitched.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.data.BlockStateVariantBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, WitcheryRewitched.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(ModBlocks.ROWAN_PLANKS.get());
        logBlock(ModBlocks.ROWAN_LOG.get());
        logBlock(ModBlocks.STRIPPED_ROWAN_LOG.get());
        simpleBlock(ModBlocks.ALDER_PLANKS.get());
        logBlock(ModBlocks.ALDER_LOG.get());
        logBlock(ModBlocks.STRIPPED_ALDER_LOG.get());
        simpleBlock(ModBlocks.HAWTHORN_PLANKS.get());
        logBlock(ModBlocks.HAWTHORN_LOG.get());
        logBlock(ModBlocks.STRIPPED_HAWTHORN_LOG.get());
        simpleBlock(ModBlocks.ROWAN_LEAVES.get());
        simpleBlock(ModBlocks.ALDER_LEAVES.get());
        simpleBlock(ModBlocks.HAWTHORN_LEAVES.get());
        crossBlock(ModBlocks.HAWTHORN_SAPLING.get());
        crossBlock(ModBlocks.ROWAN_SAPLING.get());
        crossBlock(ModBlocks.ALDER_SAPLING.get());
        createPlant(ModBlocks.BELLADONNA.get(), "belladonna", false);
        createPlant(ModBlocks.GARLIC.get(), "garlic", false);
        createPlant(ModBlocks.SNOWBELL.get(), "snowbell", true);
        createPlant(ModBlocks.WATER_ARTICHOKE.get(), "water_artichoke", false);
        createPlant(ModBlocks.WOLFSBANE.get(), "wolfsbane", true);
        createPlant(ModBlocks.MANDRAKE.get(), "mandrake", false);
        crossBlock(ModBlocks.EMBER_MOSS.get());
        crossBlock(ModBlocks.GLINTWEED.get());
        createGoldGlyph();
        chalkBlock(ModBlocks.RITUAL_GLYPH.get(), "ritual_glyph");
        chalkBlock(ModBlocks.INFERNAL_GLYPH.get(), "infernal_glyph");
        chalkBlock(ModBlocks.OTHERWHERE_GLYPH.get(), "otherwhere_glyph");
        createArthana();
        createSnare();
    }

    private void createPlant(Block plant, String name, boolean cross){
        VariantBlockStateBuilder builder = getVariantBuilder(plant);
        for(EnumPlantAge stage : EnumPlantAge.values()) {
            ModelFile model;
            if(cross) {
                model = models().cross("block/" + name + "/" + stage.getSerializedName(),
                        new ResourceLocation(WitcheryRewitched.MODID, "block/" + name + "/" + stage.getSerializedName()));
            }
            else {
                model = models().withExistingParent("block/" + name + "/" + stage.getSerializedName(),
                        new ResourceLocation("block/crop"))
                        .texture("crop", new ResourceLocation(WitcheryRewitched.MODID, "block/" + name + "/" + stage.getSerializedName()));
            }
            builder.partialState().with(ModCropBase.AGE, stage).setModels(new ConfiguredModel(model));
        }
    }

    private void createSnare(){
        VariantBlockStateBuilder builder = getVariantBuilder(ModBlocks.CRITTER_SNARE.get());
        ModelFile file = models().cross("block/critter_snare", new ResourceLocation(WitcheryRewitched.MODID, "block/critter_snare"));
        builder.partialState().with(CritterSnareBlock.HAS_ENTITY, CritterEnum.NONE).setModels(new ConfiguredModel(file));
        file = models().cross("block/critter_snare_bat", new ResourceLocation(WitcheryRewitched.MODID, "block/critter_snare_bat"));
        builder.partialState().with(CritterSnareBlock.HAS_ENTITY, CritterEnum.BAT).setModels(new ConfiguredModel(file));
        file = models().cross("block/critter_snare_silverfish", new ResourceLocation(WitcheryRewitched.MODID, "block/critter_snare_silverfish"));
        builder.partialState().with(CritterSnareBlock.HAS_ENTITY, CritterEnum.SILVERFISH).setModels(new ConfiguredModel(file));
        file = models().cross("block/critter_snare_slime", new ResourceLocation(WitcheryRewitched.MODID, "block/critter_snare_slime"));
        builder.partialState().with(CritterSnareBlock.HAS_ENTITY, CritterEnum.SLIME).setModels(new ConfiguredModel(file));
    }

    private void createArthana(){
        VariantBlockStateBuilder builder = getVariantBuilder(ModBlocks.ARTHANA.get());
        ModelFile file = models().carpet("/block/arthana", new ResourceLocation(WitcheryRewitched.MODID, "item/arthana"));
        builder.partialState().setModels(new ConfiguredModel(file));
    }

    private void chalkBlock(Block block, String name){
        Direction[] dirs = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        VariantBlockStateBuilder builder = getVariantBuilder(block);
        for(int i = 0; i < 9; i++){
            ModelFile model;
            model = models().carpet("block/"+ name + "/" + i , new ResourceLocation(WitcheryRewitched.MODID, "block/" + name + "/" + i ));
            for(int x = 0; x < 4; x++) {
                builder.partialState().with(GlyphBlock.VARIANT, i).with(GlyphBlock.DIRECTION, dirs[x]).setModels(new ConfiguredModel(model, 0,x * 90,  false));
            }
        }
    }

    private void createGoldGlyph(){
        VariantBlockStateBuilder builder = getVariantBuilder(ModBlocks.GOLD_GLYPH.get());
        Direction[] dirs = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        for(int i = 0; i < 4; i++) {
            builder.partialState().with(GlyphBlock.DIRECTION, dirs[i]).setModels(new ConfiguredModel(
                    models().carpet("block/gold_glyph", new ResourceLocation(WitcheryRewitched.MODID, "block/gold_glyph")),0, i * 90,  false
            ));
        }
    }

    private void crossBlock(Block block){
        ResourceLocation outputPath = blockTexture(block);
        BlockModelBuilder model = models().cross(outputPath.toString(), outputPath);
        simpleBlock(block, model);
        itemModels().withExistingParent(block.getRegistryName().toString(), mcLoc(ModelProvider.ITEM_FOLDER + "/generated")).texture("layer0", blockTexture(block));
    }

}
