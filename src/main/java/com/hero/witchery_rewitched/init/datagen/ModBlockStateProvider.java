package com.hero.witchery_rewitched.init.datagen;

import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.block.ThreeStageCrop;
import com.hero.witchery_rewitched.init.WitcheryBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, WitcheryRewitched.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        logWithItem(WitcheryBlocks.ROWAN_LOG);
        blockWithItem(WitcheryBlocks.ROWAN_PLANKS);
        logWithItem(WitcheryBlocks.STRIPPED_ROWAN_LOG);

        logWithItem(WitcheryBlocks.HAWTHORN_LOG);
        blockWithItem(WitcheryBlocks.HAWTHORN_PLANKS);
        logWithItem(WitcheryBlocks.STRIPPED_HAWTHORN_LOG);

        logWithItem(WitcheryBlocks.ALDER_LOG);
        blockWithItem(WitcheryBlocks.ALDER_PLANKS);
        logWithItem(WitcheryBlocks.STRIPPED_ALDER_LOG);

        blockWithItem(WitcheryBlocks.ROWAN_LEAVES);
        blockWithItem(WitcheryBlocks.HAWTHORN_LEAVES);
        blockWithItem(WitcheryBlocks.ALDER_LEAVES);

        createPlant(WitcheryBlocks.BELLADONNA, false);
        createPlant(WitcheryBlocks.GARLIC, false);
        createPlant(WitcheryBlocks.SNOWBELL, true);
        createPlant(WitcheryBlocks.WOLFSBANE, false);
        createPlant(WitcheryBlocks.WATER_ARTICHOKE, false);
        createPlant(WitcheryBlocks.MANDRAKE, false);
        createVine(WitcheryBlocks.SPANISH_MOSS);

        createCross(WitcheryBlocks.GLINTWEED);
        createCross(WitcheryBlocks.EMBER_MOSS);
    }

    private <T extends Block> void blockWithItem(RegistryObject<T> blockRegistryObject){
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }

    private void logWithItem(RegistryObject<RotatedPillarBlock> blockRegistryObject){
        logBlock(blockRegistryObject.get());

        // To generate an item during this phase of generation call item models and create from a parent of a block
        itemModels().getBuilder(blockRegistryObject.getId().getPath()).parent(models().getBuilder(blockRegistryObject.getId().getPath()));
    }

    private  void  createCross(RegistryObject<Block> block){
        String name = block.getKey().location().getPath();
        getVariantBuilder(block.get()).partialState().setModels(
                new ConfiguredModel(models()
                        .cross("block/"  + name, new ResourceLocation(WitcheryRewitched.MODID, "block/" + name))
                        .renderType("cutout")
                        .texture("cross", new ResourceLocation(WitcheryRewitched.MODID, "block/" + name) ))
        );
        itemModels().withExistingParent(name, new ResourceLocation("item/generated"))
                .texture("layer0", new ResourceLocation(WitcheryRewitched.MODID, "block/" + name));
    }

    private void createPlant(RegistryObject<Block> plant, boolean cross){
        VariantBlockStateBuilder builder = getVariantBuilder(plant.get());
        String name = plant.getKey().location().getPath();
        for(int i = 0; i < 3; i++) {
            ModelFile model;
            if(cross) {
                model = models().cross("block/" + name + "/age" + i,
                        new ResourceLocation(WitcheryRewitched.MODID, "block/" + name + "/age" + i)).renderType("cutout");
            }
            else {
                model = models().withExistingParent("block/" + name + "/age" + i,
                                new ResourceLocation("block/crop"))
                        .texture("crop", new ResourceLocation(WitcheryRewitched.MODID, "block/" + name + "/age" + i)).renderType("cutout");
            }
            builder.partialState().with(ThreeStageCrop.AGE, i).setModels(new ConfiguredModel(model));
        }
    }

    private void createVine(RegistryObject<Block> vine){
        // Created based around minecraft's vine block
        MultiPartBlockStateBuilder builder = getMultipartBuilder(vine.get());
        String name = vine.getKey().location().getPath();
        ModelFile model = models()
            .getBuilder("block/"  + name)
            .texture("vine", "block/" + name)
            .texture("particle", "block/" + name)
            .renderType("cutout_mipped")
            .element()
                .from(0,0,0.8f)
                .to(16,16,0.8f)
                .shade(false)
                .face(Direction.NORTH)
                    .texture("#vine")
                    .uvs(16,0,0,16)
                    .tintindex(0)
                    .end()
                .face(Direction.SOUTH)
                    .texture("#vine")
                    .uvs(0,0,16,16)
                    .tintindex(0)
                    .end()
                .end();

        builder.part()
                .modelFile(model)
                .addModel()
                .condition(PipeBlock.NORTH,  true)
                .end()
            .part()
                .modelFile(model)
                .rotationY(180)
                .addModel()
                .condition(PipeBlock.SOUTH,  true)
                .end()
            .part()
                .modelFile(model)
                .rotationY(90)
                .addModel()
                .condition(PipeBlock.EAST,  true)
                .end()
            .part()
                .modelFile(model)
                .rotationY(270)
                .addModel()
                .condition(PipeBlock.WEST,  true)
                .end()
            .part()
                .modelFile(model)
                .rotationX(270)
                .addModel()
                .condition(PipeBlock.UP,  true)
                .end()
            .part()
                .modelFile(model)
                .addModel()
                .condition(PipeBlock.EAST, false)
                .condition(PipeBlock.WEST, false)
                .condition(PipeBlock.SOUTH,  false)
                .condition(PipeBlock.NORTH, false)
                .condition(PipeBlock.UP, false)
            .end();
        itemModels().withExistingParent(vine.getId().getPath(), new ResourceLocation("item/generated"))
                .texture("layer0", new ResourceLocation(WitcheryRewitched.MODID, "block/" + vine.getId().getPath()));
    }
}
