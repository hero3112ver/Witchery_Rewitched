package com.hero.witchery_rewitched.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
public class WitcheryRewitchedConfig {

    public static final class Server {
        public static final ForgeConfigSpec spec;

        public static final ForgeConfigSpec.BooleanValue debug;
        public static final ForgeConfigSpec.IntValue altarRadius;

        static {
           ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
           {
               builder.comment("Witchery: Rewitched's server-side configuration options").push("General Settings");

               debug = builder
                       .comment("Turn on debug mode, which increases the speed of machines, the altar recharge rate, and more to solve problems quickly.")
                       .translation("config.wrw.server.debug")
                       .define("debug", false);

               altarRadius = builder
                       .comment("Range of the altar, defualt: 16")
                       .translation("config.wrw.server.altarRadius")
                       .defineInRange("altarRadius", 16, 0, 100);

               builder.pop();
           }
           spec = builder.build();
        }

    }

    public static void initialize(){
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Server.spec);
    };
}
