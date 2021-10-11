package com.hero.witchery_rewitched.entity.toad;

import com.hero.witchery_rewitched.WitcheryRewitched;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ToadRenderer extends GeoEntityRenderer<ToadEntity> {
    private static final ResourceLocation TOAD_TEXTURE = new ResourceLocation(WitcheryRewitched.MODID,"textures/entity/toad.png");

    public ToadRenderer(EntityRendererManager entityRendererManagerIn){
        super(entityRendererManagerIn, new ToadModel());
        this.shadowRadius = .3f;
    }

    @Override
    public ResourceLocation getTextureLocation(ToadEntity pEntity) {
        return TOAD_TEXTURE;
    }


}
