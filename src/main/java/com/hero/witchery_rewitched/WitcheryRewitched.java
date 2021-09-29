package com.hero.witchery_rewitched;

import com.hero.witchery_rewitched.config.WitcheryRewitchedConfig;
import com.hero.witchery_rewitched.init.ModItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.CallbackI;
import software.bernie.geckolib3.GeckoLib;

@Mod(WitcheryRewitched.MODID)
public class WitcheryRewitched
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "witchery_rewitched";
    public static IProxy PROXY;
    public static final ItemGroup WITCHERY_GROUP  = new ItemGroup(MODID) {
        @Override
        public ItemStack makeIcon() {
            return ModItems.UNCOOKED_CLAY_POT.get().getDefaultInstance();
        }
    };

    public WitcheryRewitched() {
        MinecraftForge.EVENT_BUS.register(this);
        GeckoLib.initialize();
        PROXY = DistExecutor.safeRunForDist(() -> SideProxy.Client::new, () -> SideProxy.Server::new);
    }

}

