package com.hero.witchery_rewitched.entity.demon;

import com.hero.witchery_rewitched.WitcheryRewitched;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class DemonRenderer extends GeoEntityRenderer<DemonEntity> {
    public static final ResourceLocation DEMON_TEXTURE = new ResourceLocation(WitcheryRewitched.MODID,"textures/entity/demon.png");

    public DemonRenderer(EntityRendererManager rendererManager){
        super(rendererManager, new DemonModel());
        this.shadowRadius = 1f;
    }

    @Override
    public ResourceLocation getTextureLocation(DemonEntity instance) {
        return DEMON_TEXTURE;
    }
}
