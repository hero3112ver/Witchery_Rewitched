package com.hero.witchery_rewitched.entity.demon;

import com.hero.witchery_rewitched.WitcheryRewitched;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class DemonModel extends AnimatedGeoModel<DemonEntity> {

    @Override
    public ResourceLocation getModelLocation(DemonEntity object) {
        return new ResourceLocation(WitcheryRewitched.MODID, "geo/demon.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(DemonEntity object) {
        return DemonRenderer.DEMON_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(DemonEntity animatable) {
        return new ResourceLocation(WitcheryRewitched.MODID, "animations/demon.animation.json");
    }
}
