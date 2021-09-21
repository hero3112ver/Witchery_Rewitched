package com.hero.witchery_rewitched.init;

import com.hero.witchery_rewitched.block.altar.AltarTileEntity;
import com.hero.witchery_rewitched.block.critter_snare.CritterSnareTileEntity;
import com.hero.witchery_rewitched.block.distillery.DistilleryTileEntity;
import com.hero.witchery_rewitched.block.glyph.GoldGlyphTileEntity;
import com.hero.witchery_rewitched.block.plants.grassper.GrassperRenderer;
import com.hero.witchery_rewitched.block.plants.grassper.GrassperTileEntity;
import com.hero.witchery_rewitched.block.poppet_shelf.PoppetShelfTileEntity;
import com.hero.witchery_rewitched.block.poppet_shelf.PoppetShelfTileEntityRenderer;
import com.hero.witchery_rewitched.block.witch_cauldron.WitchCauldronTileEntity;
import com.hero.witchery_rewitched.block.witch_oven.WitchOvenTileEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;

public class ModTileEntities {
    public static RegistryObject<TileEntityType<WitchOvenTileEntity>> WITCH_OVEN = register(
            "witch_oven",
            WitchOvenTileEntity::new,
            ModBlocks.WITCH_OVEN
    );

    public static RegistryObject<TileEntityType<WitchCauldronTileEntity>> WITCH_CAULDRON = register(
            "witch_cauldron",
            WitchCauldronTileEntity::new,
            ModBlocks.WITCH_CAULDRON
    );

    public static RegistryObject<TileEntityType<AltarTileEntity>> ALTAR = register(
            "altar",
            () ->new AltarTileEntity(0),
            ModBlocks.ALTAR
    );

    public static RegistryObject<TileEntityType<DistilleryTileEntity>> DISTILLERY = register(
            "distillery",
            DistilleryTileEntity::new,
            ModBlocks.DISTILLERY
    );

    public static  RegistryObject<TileEntityType<PoppetShelfTileEntity>> POPPET_SHELF = register(
            "poppet_shelf",
            PoppetShelfTileEntity::new,
            ModBlocks.POPPET_SHELF
    );

    public static RegistryObject<TileEntityType<GoldGlyphTileEntity>> GOLD_GLYPH = register(
            "gold_glyph",
            GoldGlyphTileEntity::new,
            ModBlocks.GOLD_GLYPH
    );

    public static RegistryObject<TileEntityType<GrassperTileEntity>> GRASSPER = register(
            "grassper",
            GrassperTileEntity::new,
            ModBlocks.GRASSPER
    );

    public static RegistryObject<TileEntityType<CritterSnareTileEntity>> CRITTER_SNARE = register(
            "critter_snare",
            CritterSnareTileEntity::new,
            ModBlocks.CRITTER_SNARE
    );

    static void register(){}

    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String name, Supplier<T> factory, RegistryObject<? extends Block> block){
        return RegistryHandler.TILE_ENTITIES.register(name, () -> {
            //noinspection ConstantConditions
            return TileEntityType.Builder.of(factory, block.get()).build(null);
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(FMLClientSetupEvent event){
        ClientRegistry.bindTileEntityRenderer(POPPET_SHELF.get(), PoppetShelfTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(GRASSPER.get(), GrassperRenderer::new);
    }

}
