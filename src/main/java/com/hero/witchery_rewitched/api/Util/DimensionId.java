package com.hero.witchery_rewitched.api.Util;

import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Objects;

/**
 * Copied from SilentLib under MIT licensing all credit goes to SilentChaos512.
 * https://github.com/SilentChaos512/SilentLib
 */

/**
 * Copied from McJtyLib on 2020-11-06. It was too good to pass up.
 * <p>
 * https://github.com/McJtyMods/McJtyLib/blob/1.16/src/main/java/mcjty/lib/varia/DimensionId.java
 */
public class DimensionId {
    private final RegistryKey<World> id;

    private final static Lazy<DimensionId> OVERWORLD = Lazy.of(() -> new DimensionId(World.OVERWORLD));

    private DimensionId(RegistryKey<World> id) {
        this.id = id;
    }

    public static DimensionId overworld() {
        return OVERWORLD.get();
    }

    public static DimensionId fromId(RegistryKey<World> id) {
        return new DimensionId(id);
    }

    public static DimensionId fromPacket(PacketBuffer buf) {
        RegistryKey<World> key = RegistryKey.create(Registry.DIMENSION_REGISTRY, buf.readResourceLocation());
        return new DimensionId(key);
    }

    public static DimensionId fromWorld(World world) {
        return new DimensionId(world.dimension());
    }

    public static DimensionId fromResourceLocation(ResourceLocation location) {
        RegistryKey<World> key = RegistryKey.create(Registry.DIMENSION_REGISTRY, location);
        return new DimensionId(key);
    }

    public RegistryKey<World> getId() {
        return id;
    }

    public ResourceLocation getRegistryName() {
        return id.location();
    }

    // Is this a good way to get the dimension name?
    public String getName() { return id.location().getPath(); }

    public boolean isOverworld() {
        return id.equals(World.OVERWORLD);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeResourceLocation(id.location());
    }

    public ServerWorld loadWorld() {
        // Worlds in 1.16 are always loaded
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server.getLevel(id);
    }

    // Do not load the world if it is not there
    public ServerWorld getWorld() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server.getLevel(id);
    }

    public ServerWorld loadWorld(World otherWorld) {
        // Worlds in 1.16 are always loaded
        return otherWorld.getServer().getLevel(id);
    }

    public static boolean sameDimension(World world1, World world2) {
        return world1.dimension().equals(world2.dimension());
    }

    public boolean sameDimension(World world) {
        return id.equals(world.dimension());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DimensionId that = (DimensionId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}