package com.hero.witchery_rewitched.init;

import com.hero.witchery_rewitched.WitcheryRewitched;
import com.hero.witchery_rewitched.entity.demon.DemonEntity;
import com.hero.witchery_rewitched.entity.demon.DemonRenderer;
import com.hero.witchery_rewitched.entity.ent.EntEntity;
import com.hero.witchery_rewitched.entity.ent.EntRenderer;
import com.hero.witchery_rewitched.entity.mandrake.MandrakeEntity;
import com.hero.witchery_rewitched.entity.mandrake.MandrakeRenderer;
import com.hero.witchery_rewitched.entity.toad.ToadEntity;
import com.hero.witchery_rewitched.entity.toad.ToadRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.network.FMLPlayMessages;

import java.util.function.BiFunction;

@Mod.EventBusSubscriber(modid = WitcheryRewitched.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {
    public static RegistryObject<EntityType<MandrakeEntity>> MANDRAKE = register("mandrake",
            MandrakeEntity::new,
            EntityClassification.CREATURE,
            MandrakeEntity::new,
            .5f, .5f);

    public static RegistryObject<EntityType<EntEntity>> ENT = register("ent",
            EntEntity::new,
            EntityClassification.MONSTER,
            EntEntity::new,
            1.5f, 3f);

    public static RegistryObject<EntityType<ToadEntity>> TOAD = register("toad",
            ToadEntity::new,
            EntityClassification.CREATURE,
            ToadEntity::new,
            .5f, .5f);

    public static RegistryObject<EntityType<DemonEntity>> DEMON = register("demon",
            DemonEntity::new,
            EntityClassification.MONSTER,
            DemonEntity::new,
            1.5f, 3);

    static void register() {}

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(MANDRAKE.get(), MandrakeEntity.registerAttributes().build());
        event.put(ENT.get(), EntEntity.registerAttributes().build());
        event.put(TOAD.get(), ToadEntity.registerAttributes().build());
        event.put(DEMON.get(), DemonEntity.registerAttributes().build());
    }

    @OnlyIn (Dist.CLIENT)
    public static void registerRenderers(FMLClientSetupEvent event){
        RenderingRegistry.registerEntityRenderingHandler(MANDRAKE.get(), MandrakeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ENT.get(), EntRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(TOAD.get(), ToadRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(DEMON.get(), DemonRenderer::new);
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, EntityType.IFactory<T> factory, EntityClassification type, BiFunction<FMLPlayMessages.SpawnEntity, World, T> customClientFactory, float width, float height){
        return RegistryHandler.ENTITIES.register(name, () -> EntityType.Builder.of(factory, type)
                .setCustomClientFactory(customClientFactory)
                .sized(width, height)
                .build(WitcheryRewitched.MODID + ":" + name));

    }
}
