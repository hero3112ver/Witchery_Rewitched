package com.hero.witchery_rewitched.init;

import com.hero.witchery_rewitched.WitcheryRewitched;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.MegaJungleFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

import java.util.OptionalInt;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?,?>> ROWAN_KEY = registerKey("rowan");
    public static final ResourceKey<ConfiguredFeature<?,?>> ALDER_KEY = registerKey("alder");
    public static final ResourceKey<ConfiguredFeature<?,?>> HAWTHORN_KEY = registerKey("hawthorn");
    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context){
        register(context, ROWAN_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(WitcheryBlocks.ROWAN_LOG.get()),
                new StraightTrunkPlacer(5, 0, 0),
                BlockStateProvider.simple(WitcheryBlocks.ROWAN_LEAVES.get().defaultBlockState()),
                new BlobFoliagePlacer(ConstantInt.of(3), ConstantInt.of(1), 4),
                new TwoLayersFeatureSize(2, 0, 2)).ignoreVines().build());

        register(context, ALDER_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(WitcheryBlocks.ALDER_LOG.get().defaultBlockState()),
                new FancyTrunkPlacer(8, 2, 0),
                BlockStateProvider.simple(WitcheryBlocks.ALDER_LEAVES.get().defaultBlockState()),
                new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(2), 4),
                new TwoLayersFeatureSize(1, 0, 2, OptionalInt.of(4))).ignoreVines().build());

        register(context, HAWTHORN_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(WitcheryBlocks.HAWTHORN_LOG.get().defaultBlockState()),
                new ForkingTrunkPlacer(5, 2, 0),
                BlockStateProvider.simple(WitcheryBlocks.HAWTHORN_LEAVES.get().defaultBlockState()),
                new MegaJungleFoliagePlacer(UniformInt.of(0, 2), UniformInt.of(0, 1), 3),
                new TwoLayersFeatureSize(1, 0, 2)).ignoreVines().build());
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name){
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(WitcheryRewitched.MODID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration){
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
