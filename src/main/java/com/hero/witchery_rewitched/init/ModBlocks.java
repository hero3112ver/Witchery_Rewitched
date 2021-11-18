package com.hero.witchery_rewitched.init;

import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.block.*;
import com.hero.witchery_rewitched.block.altar.AltarBlock;
import com.hero.witchery_rewitched.block.critter_snare.CritterSnareBlock;
import com.hero.witchery_rewitched.block.distillery.DistilleryBlock;
import com.hero.witchery_rewitched.block.distillery.DistilleryTileEntity;
import com.hero.witchery_rewitched.block.glyph.*;
import com.hero.witchery_rewitched.block.plants.*;
import com.hero.witchery_rewitched.block.plants.grassper.GrassperBlock;
import com.hero.witchery_rewitched.block.plants.WaterArtichokeBlock;
import com.hero.witchery_rewitched.block.poppet_shelf.PoppetShelfBlock;
import com.hero.witchery_rewitched.block.poppet_shelf.PoppetShelfTileEntity;
import com.hero.witchery_rewitched.block.witch_cauldron.WitchCauldronBlock;
import com.hero.witchery_rewitched.block.witch_cauldron.WitchCauldronTileEntity;
import com.hero.witchery_rewitched.block.witch_oven.WitchOvenBlock;
import com.hero.witchery_rewitched.block.witch_oven.WitchOvenTileEntity;
import com.hero.witchery_rewitched.item.ModFuelBlock;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.function.Supplier;

public class ModBlocks {
    public static final RegistryObject<RotatedPillarBlock> ROWAN_LOG = registerFuel("rowan_log", ModFlammableBlock.ModLog::new, 200);

    public static final RegistryObject<Block> ROWAN_PLANKS = registerFuel("rowan_planks", () ->
            new ModFlammableBlock.PlankBlock(5),200);
    public static final RegistryObject<RotatedPillarBlock> STRIPPED_ROWAN_LOG = registerFuel("stripped_rowan_log", ModFlammableBlock.ModLog::new,200);

    public static final RegistryObject<RotatedPillarBlock> HAWTHORN_LOG = registerFuel("hawthorn_log", ModFlammableBlock.ModLog::new,200);
    public static final RegistryObject<Block> HAWTHORN_PLANKS = registerFuel("hawthorn_planks", () ->
            new ModFlammableBlock.PlankBlock(5),200);
    public static final RegistryObject<RotatedPillarBlock> STRIPPED_HAWTHORN_LOG = registerFuel("stripped_hawthorn_log", ModFlammableBlock.ModLog::new,200);

    public static final RegistryObject<RotatedPillarBlock> ALDER_LOG = registerFuel("alder_log", ModFlammableBlock.ModLog::new,200);
    public static final RegistryObject<Block> ALDER_PLANKS = registerFuel("alder_planks", () ->
            new ModFlammableBlock.PlankBlock(5),200);
    public static final RegistryObject<RotatedPillarBlock> STRIPPED_ALDER_LOG = registerFuel("stripped_alder_log", ModFlammableBlock.ModLog::new,200);

    public static final RegistryObject<LeavesBlock> ROWAN_LEAVES = registerFuel("rowan_leaves", () ->
            new ModFlammableBlock.ModLeaves(30),200);
    public static final RegistryObject<LeavesBlock> HAWTHORN_LEAVES = registerFuel("hawthorn_leaves", () ->
            new ModFlammableBlock.ModLeaves(30),200);
    public static final RegistryObject<LeavesBlock> ALDER_LEAVES = registerFuel("alder_leaves", () ->
            new ModFlammableBlock.ModLeaves(30),200);

    public static final RegistryObject<SaplingBlock> ROWAN_SAPLING = registerFuel("rowan_sapling", () ->
            new SaplingBlock(new ModSaplings.RowanTree(), ModSaplings.getTreeProps()), 100);
    public static final RegistryObject<SaplingBlock> HAWTHORN_SAPLING = registerFuel("hawthorn_sapling", () ->
            new SaplingBlock(new ModSaplings.HawthornTree(), ModSaplings.getTreeProps()), 100);
    public static final RegistryObject<SaplingBlock> ALDER_SAPLING = registerFuel("alder_sapling", () ->
            new SaplingBlock(new ModSaplings.AlderTree(), ModSaplings.getTreeProps()), 100);

    public static final RegistryObject<WitchOvenBlock> WITCH_OVEN = register("witch_oven", () ->
            new WitchOvenBlock((state, world) -> new WitchOvenTileEntity(),AbstractBlock.Properties.of(Material.METAL)
                    .strength(4,20)
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()
            )
    );


    public static RegistryObject<Block> BELLADONNA = register("belladonna_crop", () -> new ModCropBase(ModItems.BELLADONNA_SEEDS,Blocks.FARMLAND));
    public static RegistryObject<Block> GARLIC = register("garlic_crop", () -> new ModCropBase(ModItems.GARLIC, Blocks.FARMLAND));
    public static RegistryObject<Block> SNOWBELL = register("snowbell_crop", () -> new ModCropBase(ModItems.SNOWBELL_SEEDS, Blocks.FARMLAND));
    public static RegistryObject<Block> WATER_ARTICHOKE = register("water_artichoke_crop", () -> new WaterArtichokeBlock(ModItems.WATER_ARTICHOKE_SEEDS, Blocks.WATER));
    public static RegistryObject<Block> WOLFSBANE = register("wolfsbane_crop", () -> new ModCropBase(ModItems.WOLFSBANE_SEEDS, Blocks.FARMLAND));
    public static RegistryObject<Block> MANDRAKE = register("mandrake_crop", MandrakeBlock::new);
    public static RegistryObject<Block> SPANISH_MOSS = register("spanish_moss", () -> new VineBlock(AbstractBlock.Properties.of(Material.REPLACEABLE_PLANT).noCollission().randomTicks().strength(0.2F).sound(SoundType.VINE)));
    public static RegistryObject<Block> EMBER_MOSS = register("ember_moss", EmberMossBlock::new);
    public static RegistryObject<Block> GLINTWEED = register("glintweed", GlintWeedBlock::new);

    public static RegistryObject<Block> WITCH_CAULDRON = register("witch_cauldron", () ->
            new WitchCauldronBlock( (state, world) -> new WitchCauldronTileEntity(), AbstractBlock.Properties
                    .of(Material.METAL)
                    .strength(4, 20)
                    .sound(SoundType.METAL)
                    .harvestTool(ToolType.PICKAXE)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
        )
    );

    public static RegistryObject<Block> FUME_FUNNEL = register("fume_funnel", FumeFunnelBlock::new);
    public static RegistryObject<Block> FILTERED_FUME_FUNNEL = register("filtered_fume_funnel", FumeFunnelBlock::new);

    public static RegistryObject<Block> ALTAR = register("altar", AltarBlock::new);

    public static RegistryObject<Block> GOLD_GLYPH = registerNoItem("gold_glyph", GoldGlyphBlock::new);
    public static RegistryObject<Block> RITUAL_GLYPH = registerNoItem("ritual_glyph", GlyphBlock::new);
    public static RegistryObject<Block> OTHERWHERE_GLYPH = registerNoItem("otherwhere_glyph", OtherewhereGlyphBlock::new);
    public static RegistryObject<Block> INFERNAL_GLYPH = registerNoItem("infernal_glyph", InfernalGlyphBlock::new);

    public static RegistryObject<Block> DISTILLERY = register("distillery", () ->
            new DistilleryBlock((state, world) -> new DistilleryTileEntity(), AbstractBlock.Properties.copy(Blocks.IRON_BLOCK)));

    public static RegistryObject<Block> POPPET_SHELF = register("poppet_shelf", () ->
            new PoppetShelfBlock((state, world) -> new PoppetShelfTileEntity(), AbstractBlock.Properties.copy(Blocks.COBBLESTONE)));

    public static RegistryObject<Block> ARTHANA = registerNoItem("arthana", () -> new ArthanaBlock(AbstractBlock.Properties.copy(Blocks.BLACK_CARPET).noOcclusion()));

    public static RegistryObject<Block> GRASSPER = register("grassper", () -> new GrassperBlock(AbstractBlock.Properties.copy(Blocks.WHEAT)));

    public static RegistryObject<Block> CRITTER_SNARE = register("critter_snare", () -> new CritterSnareBlock(AbstractBlock.Properties.copy(Blocks.VINE)));

    public static RegistryObject<Block> BOUND_DEMON_HEART = register("bound_demon_heart", BoundDemonHeartBlock::new);

    public static void register(){}

    private static <T extends Block>RegistryObject<T> registerFuel(String name, Supplier<T> block, int burnTime)
    {
        RegistryObject<T> ret = registerNoItem(name, block);
        RegistryHandler.ITEMS.register(name, () -> new ModFuelBlock(ret.get(), new Item.Properties().tab(WitcheryRewitched.WITCHERY_GROUP), burnTime));
        return ret;
    }

    private static <T extends Block>RegistryObject<T> registerNoItem(String name, Supplier<T> block){
        return RegistryHandler.BLOCKS.register(name, block);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        RegistryObject<T> ret = registerNoItem(name, block);
        RegistryHandler.ITEMS.register(name, () -> new BlockItem(ret.get(), new Item.Properties().tab(WitcheryRewitched.WITCHERY_GROUP)));
        return ret;
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderTypes(FMLClientSetupEvent event) {
        final RenderType cutoutMipped = RenderType.cutoutMipped();
        final RenderType cutout = RenderType.cutout();
        setRenderLayer(ModBlocks.ROWAN_LEAVES.get(), cutoutMipped);
        setRenderLayer(ModBlocks.ALDER_LEAVES.get(), cutoutMipped);
        setRenderLayer(ModBlocks.HAWTHORN_LEAVES.get(), cutoutMipped);
        setRenderLayer(ModBlocks.ROWAN_SAPLING.get(), cutout);
        setRenderLayer(ModBlocks.ALDER_SAPLING.get(), cutout);
        setRenderLayer(ModBlocks.HAWTHORN_SAPLING.get(), cutout);
        setRenderLayer(ModBlocks.WITCH_OVEN.get(), cutout);
        setRenderLayer(ModBlocks.BELLADONNA.get(), cutout);
        setRenderLayer(ModBlocks.GARLIC.get(), cutout);
        setRenderLayer(ModBlocks.SNOWBELL.get(), cutout);
        setRenderLayer(ModBlocks.WATER_ARTICHOKE.get(), cutout);
        setRenderLayer(ModBlocks.WOLFSBANE.get(), cutout);
        setRenderLayer(ModBlocks.MANDRAKE.get(), cutout);
        setRenderLayer(ModBlocks.WITCH_CAULDRON.get(), cutoutMipped);
        setRenderLayer(ModBlocks.GLINTWEED.get(), cutout);
        setRenderLayer(ModBlocks.EMBER_MOSS.get(), cutout);
        setRenderLayer(ModBlocks.SPANISH_MOSS.get(), cutout);
        setRenderLayer(ModBlocks.GOLD_GLYPH.get(), cutout);
        setRenderLayer(ModBlocks.OTHERWHERE_GLYPH.get(), cutout);
        setRenderLayer(ModBlocks.INFERNAL_GLYPH.get(), cutout);
        setRenderLayer(ModBlocks.RITUAL_GLYPH.get(), cutout);
        setRenderLayer(ModBlocks.DISTILLERY.get(), cutoutMipped);
        setRenderLayer(ModBlocks.ARTHANA.get(), cutout);
        setRenderLayer(ModBlocks.GRASSPER.get(), cutout);
        setRenderLayer(ModBlocks.CRITTER_SNARE.get(), cutout);
        setRenderLayer(ModBlocks.BOUND_DEMON_HEART.get(), cutoutMipped);
    }

    public static void registerBlockColors(ColorHandlerEvent.Block event){
        BlockColors colors = event.getBlockColors();
        colors.register((state, reader, pos, color) -> {
            if(state.getValue(WitchCauldronBlock.HAS_POTION_RECIPE))
                return PotionUtils.getColor(Potions.POISON);
            else if(state.getValue(WitchCauldronBlock.HAS_CAULDRON_RECIPE))
                return PotionUtils.getColor(Potions.REGENERATION);
            return reader != null && pos != null ? BiomeColors.getAverageWaterColor(reader, pos) : -1;
        }, ModBlocks.WITCH_CAULDRON.get());
    }

    public static void setRenderLayer(Block block, RenderType renderType)
    {
        RenderTypeLookup.setRenderLayer(block, renderType);
    }
}
