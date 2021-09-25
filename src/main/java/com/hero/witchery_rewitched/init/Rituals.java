package com.hero.witchery_rewitched.init;

import com.hero.witchery_rewitched.api.rituals.*;
import net.minecraftforge.fml.RegistryObject;
import java.util.function.Supplier;

public class Rituals {

    public static RegistryObject<AbstractRitual> RITE_OF_TOTAL_ECLIPSE = register("rite_of_total_eclipse", RiteOfTotalEclipse::new);
    public static RegistryObject<AbstractRitual> RITE_OF_CHARGING = register("rite_of_charging", RiteOfCharging::new);
    public static RegistryObject<AbstractRitual> RITE_OF_TRANSPORTATION_PLAYER = register("rite_of_transportation_player", RiteOfTransposition::new);
    public static RegistryObject<AbstractRitual> RITE_OF_BROILING = register("rite_of_broiling", RiteOfBroiling::new);
    public static RegistryObject<AbstractRitual> RITE_OF_BINDING_WAYSTONE = register("rite_of_binding_waystone", RiteOfBindingWaystone::new);
    public static RegistryObject<AbstractRitual> RITE_OF_BINDING_CIRCLE_TALISMAN = register("rite_of_binding_talisman", RiteOfBindingCircleTalisman::new);

    private static <T extends AbstractRitual> RegistryObject<T> register(String name, Supplier<T> ritual) {
        return RegistryHandler.RITUALS.register(name, ritual);
    }

    public static void register(){};
}
