package com.hero.witchery_rewitched.init;

import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.block.ThreeStageCrop;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class WitcheryBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, WitcheryRewitched.MODID);

    /*
     *  TODO:: If I wasn't an idiot I would realize adding these to the proper tags would make it such that these items to the proper
     *   this wouldn't need to be added as flammable item
     */
    public static final RegistryObject<RotatedPillarBlock> ROWAN_LOG = registerFuel("rowan_log", WitcheryBlocks::log, 200);
    public static final RegistryObject<Block> ROWAN_PLANKS = registerFuel("rowan_planks", WitcheryBlocks::plank,200);
    public static final RegistryObject<RotatedPillarBlock> STRIPPED_ROWAN_LOG = registerFuel("stripped_rowan_log", WitcheryBlocks::log,200);

    public static final RegistryObject<RotatedPillarBlock> HAWTHORN_LOG = registerFuel("hawthorn_log", WitcheryBlocks::log,200);
    public static final RegistryObject<Block> HAWTHORN_PLANKS = registerFuel("hawthorn_planks", WitcheryBlocks::plank,200);
    public static final RegistryObject<RotatedPillarBlock> STRIPPED_HAWTHORN_LOG = registerFuel("stripped_hawthorn_log", WitcheryBlocks::log,200);

    public static final RegistryObject<RotatedPillarBlock> ALDER_LOG = registerFuel("alder_log", WitcheryBlocks::log,200);
    public static final RegistryObject<Block> ALDER_PLANKS = registerFuel("alder_planks", WitcheryBlocks::plank,200);
    public static final RegistryObject<RotatedPillarBlock> STRIPPED_ALDER_LOG = registerFuel("stripped_alder_log", WitcheryBlocks::log,200);

    public static final RegistryObject<LeavesBlock> ROWAN_LEAVES = registerFuel("rowan_leaves", WitcheryBlocks::leaves,10);
    public static final RegistryObject<LeavesBlock> HAWTHORN_LEAVES = registerFuel("hawthorn_leaves", WitcheryBlocks::leaves,10);
    public static final RegistryObject<LeavesBlock> ALDER_LEAVES = registerFuel("alder_leaves", WitcheryBlocks::leaves,10);

//    public static final RegistryObject<SaplingBlock> ROWAN_SAPLING = registerFuel("rowan_sapling", () ->
//            new SaplingBlock(new ModSaplings.RowanTree(), ModSaplings.getTreeProps()), 100);
//    public static final RegistryObject<SaplingBlock> HAWTHORN_SAPLING = registerFuel("hawthorn_sapling", () ->
//            new SaplingBlock(new ModSaplings.HawthornTree(), ModSaplings.getTreeProps()), 100);
//    public static final RegistryObject<SaplingBlock> ALDER_SAPLING = registerFuel("alder_sapling", () ->
//            new SaplingBlock(new ModSaplings.AlderTree(), ModSaplings.getTreeProps()), 100);
//
//    public static final RegistryObject<WitchOvenBlock> WITCH_OVEN = register("witch_oven", () ->
//            new WitchOvenBlock(BlockBehaviour.Properties.of(Material.METAL)
//                    .strength(4,20)
//                    .sound(SoundType.METAL)
//                    .noOcclusion()
//                    .requiresCorrectToolForDrops()
//            )
//    );
//
//
    public static RegistryObject<Block> BELLADONNA = registerBlock("belladonna_crop", () -> new ThreeStageCrop(WitcheryItems.BELLADONNA_SEEDS), false);
    public static RegistryObject<Block> GARLIC = registerBlock("garlic_crop", () -> new ThreeStageCrop(WitcheryItems.GARLIC), false);
    public static RegistryObject<Block> SNOWBELL = registerBlock("snowbell_crop", () -> new ThreeStageCrop(WitcheryItems.SNOWBELL_SEEDS), false);
    public static RegistryObject<Block> WATER_ARTICHOKE = registerBlock("water_artichoke_crop", () -> new ThreeStageCrop(WitcheryItems.WATER_ARTICHOKE_SEEDS, Blocks.WATER), false);
    public static RegistryObject<Block> WOLFSBANE = registerBlock("wolfsbane_crop", () -> new ThreeStageCrop(WitcheryItems.WOLFSBANE_SEEDS), false);
    //TODO: Mandrakes in general
    public static RegistryObject<Block> MANDRAKE = registerBlock("mandrake_crop", () -> new ThreeStageCrop(WitcheryItems.MANDRAKE_SEEDS), false);
//    public static RegistryObject<Block> MANDRAKE = registerBlock("mandrake_crop", MandrakeBlock::new);
//    public static RegistryObject<Block> SPANISH_MOSS = registerBlock("spanish_moss", () -> new VineBlock(BlockBehaviour.Properties.of(Material.REPLACEABLE_PLANT).noCollission().randomTicks().strength(0.2F).sound(SoundType.VINE)));
//    public static RegistryObject<Block> EMBER_MOSS = registerBlock("ember_moss", EmberMossBlock::new);
//    public static RegistryObject<Block> GLINTWEED = registerBlock("glintweed", GlintWeedBlock::new);
//
//    public static RegistryObject<Block> WITCH_CAULDRON = register("witch_cauldron", () ->
//            new WitchCauldronBlock(BlockBehaviour.Properties
//                    .of(Material.METAL)
//                    .strength(4, 20)
//                    .sound(SoundType.METAL)
//                    .requiresCorrectToolForDrops()
//                    .noOcclusion()
//            )
//    );
//
//    public static RegistryObject<Block> FUME_FUNNEL = register("fume_funnel", FumeFunnelBlock::new);
//    public static RegistryObject<Block> FILTERED_FUME_FUNNEL = register("filtered_fume_funnel", FumeFunnelBlock::new);
//
//    public static RegistryObject<Block> ALTAR = register("altar", AltarBlock::new);
//
//    public static RegistryObject<Block> GOLD_GLYPH = registerNoItem("gold_glyph", GoldGlyphBlock::new);
//    public static RegistryObject<Block> RITUAL_GLYPH = registerNoItem("ritual_glyph", GlyphBlock::new);
//    public static RegistryObject<Block> OTHERWHERE_GLYPH = registerNoItem("otherwhere_glyph", OtherewhereGlyphBlock::new);
//    public static RegistryObject<Block> INFERNAL_GLYPH = registerNoItem("infernal_glyph", InfernalGlyphBlock::new);
//
//    public static RegistryObject<Block> DISTILLERY = register("distillery", () ->
//            new DistilleryBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));
//
//    public static RegistryObject<Block> POPPET_SHELF = register("poppet_shelf", () ->
//            new PoppetShelfBlock(BlockBehaviour.Properties.copy(Blocks.COBBLESTONE)));
//
//    public static RegistryObject<Block> ARTHANA = registerNoItem("arthana", () -> new ArthanaBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_CARPET).noOcclusion()));
//
//    public static RegistryObject<Block> GRASSPER = register("grassper", () -> new GrassperBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT)));
//
//    public static RegistryObject<Block> CRITTER_SNARE = register("critter_snare", () -> new CritterSnareBlock(BlockBehaviour.Properties.copy(Blocks.VINE)));
//
//    public static RegistryObject<Block> BOUND_DEMON_HEART = register("bound_demon_heart", BoundDemonHeartBlock::new);


    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, boolean item){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        if(item)
            WitcheryItems.createItem(name, () -> new BlockItem(toReturn.get(), new Item.Properties()));
        return toReturn;
    }
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block){
        return registerBlock(name, block, true);
    }


    private static <T extends Block> RegistryObject<T> registerFuel(String name, Supplier<T> block, int fuelTicks){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        WitcheryItems.ITEMS.register(name, ()-> new BlockItem(toReturn.get(), new Item.Properties()){
            @Override
            public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
                return fuelTicks;
            }

        });
        return toReturn;
    }

    public static void register (IEventBus eventBus){
        BLOCKS.register(eventBus);
    }


    // HELPERS FROM HERE ON OUT


    private static Block plank() {
        return new Block(BlockBehaviour.Properties.of(Material.WOOD).strength(2.0F).sound(SoundType.WOOD)){
            @Override
            public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                return true;
            }

            @Override
            public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                return 5;
            }
        };
    }
    private static RotatedPillarBlock log() {
        return new RotatedPillarBlock(BlockBehaviour.Properties
                .of(Material.WOOD)
                .strength(2.0F)
                .sound(SoundType.WOOD)){
            @Override
            public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                return true;
            }

            @Override
            public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                return 5;
            }
        };
    }

    private static LeavesBlock leaves(){
        return new LeavesBlock(  BlockBehaviour
                .Properties
                .of(Material.LEAVES)
                .strength(0.2F)
                .randomTicks()
                .sound(SoundType.GRASS)
                .noOcclusion()
                .isValidSpawn((blockState, blockGetter, blockPos, entity)->(entity == EntityType.OCELOT || entity == EntityType.PARROT))
                .isSuffocating((blockState, blockGetter, blockPos)->false)
                .isViewBlocking((blockState, blockGetter, blockPos)->false)
        ){
            @Override
            public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                return true;
            }

            @Override
            public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                return 5;
            }
        };


    }
}
