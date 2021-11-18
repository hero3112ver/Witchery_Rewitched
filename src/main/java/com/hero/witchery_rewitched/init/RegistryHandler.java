package com.hero.witchery_rewitched.init;

import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.util.rituals.AbstractRitual;
import com.hero.witchery_rewitched.crafting.recipe.ModRecipes;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.particles.ParticleType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

public class RegistryHandler {
    public static final DeferredRegister<Item> ITEMS = create(ForgeRegistries.ITEMS);
    public static final DeferredRegister<Block> BLOCKS = create(ForgeRegistries.BLOCKS);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = create(ForgeRegistries.CONTAINERS);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = create(ForgeRegistries.TILE_ENTITIES);
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = create(ForgeRegistries.RECIPE_SERIALIZERS);
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> LOOT_MODIFIER_SERIALIZERS = create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = create(ForgeRegistries.PARTICLE_TYPES);
    public static final DeferredRegister<EntityType<?>> ENTITIES = create(ForgeRegistries.ENTITIES);
    public static final DeferredRegister<AbstractRitual> RITUALS = DeferredRegister.create(AbstractRitual.class, WitcheryRewitched.MODID);


    public static final Lazy<IForgeRegistry<AbstractRitual>> RITUAL_REGISTRY = Lazy.of(RITUALS.makeRegistry("ritual_registry", RegistryBuilder::new));


    public static void init(){
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        CONTAINERS.register(modEventBus);
        TILE_ENTITIES.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
        PARTICLES.register(modEventBus);
        ENTITIES.register(modEventBus);
        RITUALS.register(modEventBus);

        ModItems.register();
        ModBlocks.register();
        ModContainers.register();
        ModRecipes.register();
        ModTileEntities.register();
        ModLootTableModifiers.register();
        ModEntities.register();
        Rituals.register();
    }
    private static <V extends IForgeRegistryEntry<V>> DeferredRegister<V> create(IForgeRegistry<V> Registry) {
        return DeferredRegister.create(Registry, WitcheryRewitched.MODID);
    }
}
