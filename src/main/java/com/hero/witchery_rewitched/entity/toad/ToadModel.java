package com.hero.witchery_rewitched.entity.toad;// Made with Blockbench 3.9.2
// Exported for Minecraft version 1.15 - 1.16 with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.hero.witchery_rewitched.WitcheryRewitched;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ToadModel extends AnimatedGeoModel<ToadEntity> {

	@Override
	public ResourceLocation getModelLocation(ToadEntity object) {
		return object.getAge() < 0 ? new ResourceLocation(WitcheryRewitched.MODID, "geo/baby_toad.geo.json"): new ResourceLocation(WitcheryRewitched.MODID, "geo/toad.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(ToadEntity object) {
		return new ResourceLocation(WitcheryRewitched.MODID, "textures/entity/toad.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(ToadEntity animatable) {
		return new ResourceLocation(WitcheryRewitched.MODID, "animations/toad.animation.json");
	}
}