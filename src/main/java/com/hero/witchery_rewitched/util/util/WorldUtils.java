package com.hero.witchery_rewitched.util.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

@SuppressWarnings("unused")
public final class WorldUtils {

    /**
     * Copied from SilentLib under MIT licensing all credit goes to SilentChaos512.
     * https://github.com/SilentChaos512/SilentLib
     */

    private WorldUtils() {throw new IllegalAccessError("Utility class");}

    /**
     * Gets blocks in a cube with sides of length {@code 2 * range + 1} and centered on pos.
     *
     * @param world  The world
     * @param pos    The center position
     * @param range  The "radius"
     * @param getter The method to get desired objects. Return empty optionals if the object at the
     *               given position is not wanted.
     * @param <T>    The type of object being collected
     * @return A map of BlockPos to T produced by getter
     */
    public static <T> Map<BlockPos, T> getBlocksInArea(World world, BlockPos pos, int range, BiFunction<World, BlockPos, Optional<T>> getter) {
        int xMin = pos.getX() - range;
        int xMax = pos.getX() + range;
        int yMin = pos.getY() - range;
        int yMax = pos.getY() + range;
        int zMin = pos.getZ() - range;
        int zMax = pos.getZ() + range;
        return getBlocks(world, xMin, yMin, zMin, xMax, yMax, zMax, getter);
    }

    /**
     * Gets blocks in a spherical area centered on pos and with the given radius.
     *
     * @param world  The world
     * @param pos    The center position
     * @param radius The radius of the sphere
     * @param getter The method to get desired objects. Return empty optionals if the object at the
     *               given position is not wanted.
     * @param <T>    The type of object being collected
     * @return A map of BlockPos to T produced by getter
     */
    public static <T> Map<BlockPos, T> getBlocksInSphere(World world, BlockPos pos, int radius, BiFunction<World, BlockPos, Optional<T>> getter) {
        final int radiusSq = radius * radius;
        int xMin = pos.getX() - radius;
        int xMax = pos.getX() + radius;
        int yMin = pos.getY() - radius;
        int yMax = pos.getY() + radius;
        int zMin = pos.getZ() - radius;
        int zMax = pos.getZ() + radius;
        return getBlocks(world, xMin, yMin, zMin, xMax, yMax, zMax, (world1, pos1) -> {
            if (distanceSq(pos, pos1) <= radiusSq) {
                return getter.apply(world1, pos1);
            }
            return Optional.empty();
        });
    }
    public static double distanceSq(Vector3i from, Vector3i to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        int dz = to.getZ() - from.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Gets objects from the world (usually blocks or tile entities) using the specified getter.
     *
     * @param world  The world
     * @param xMin   X position start
     * @param yMin   Y position start
     * @param zMin   Z position start
     * @param xMax   X position end
     * @param yMax   Y position end
     * @param zMax   Z position end
     * @param getter The method to get desired objects. Return empty optionals if the object at the
     *               given position is not wanted.
     * @param <T>    The type of object being collected
     * @return A map of BlockPos to T produced by getter
     */
    @SuppressWarnings("MethodWithTooManyParameters")
    public static <T> Map<BlockPos, T> getBlocks(World world, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax, BiFunction<World, BlockPos, Optional<T>> getter) {
        Map<BlockPos, T> map = new LinkedHashMap<>();
        //noinspection deprecation
        if (!world.hasChunksAt(xMin, yMin, zMin, xMax, yMax, zMax)) {
            return map;
        }

        BlockPos.Mutable blockPos = new BlockPos.Mutable();
        for (int x = xMin; x <= xMax; ++x) {
            for (int y = yMin; y <= yMax; ++y) {
                for (int z = zMin; z <= zMax; ++z) {
                    BlockPos pos = new BlockPos(x, y, z);
                    getter.apply(world, blockPos.set(x, y, z)).ifPresent(t -> map.put(pos, t));
                }
            }
        }

        return map;
    }

    public static <T extends TileEntity> Map<BlockPos, T> getTileEntitiesInArea(Class<? extends T> clazz, World world, BlockPos pos, int range) {
        int xMin = pos.getX() - range;
        int xMax = pos.getX() + range;
        int yMin = pos.getY() - range;
        int yMax = pos.getY() + range;
        int zMin = pos.getZ() - range;
        int zMax = pos.getZ() + range;
        return getTileEntities(clazz, world, xMin, yMin, zMin, xMax, yMax, zMax);
    }

    public static <T extends TileEntity> Map<BlockPos, T> getTileEntitiesInSphere(Class<? extends T> clazz, World world, BlockPos pos, int radius) {
        final int radiusSq = radius * radius;
        int xMin = pos.getX() - radius;
        int xMax = pos.getX() + radius;
        int yMin = pos.getY() - radius;
        int yMax = pos.getY() + radius;
        int zMin = pos.getZ() - radius;
        int zMax = pos.getZ() + radius;
        return getBlocks(world, xMin, yMin, zMin, xMax, yMax, zMax, (world1, pos1) -> {
            if (distanceSq(pos, pos1) <= radiusSq) {
                return getTileEntityOfType(clazz, pos, world1);
            }
            return Optional.empty();
        });
    }

    @SuppressWarnings("MethodWithTooManyParameters")
    public static <T extends TileEntity> Map<BlockPos, T> getTileEntities(Class<? extends T> clazz, World world, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
        return getBlocks(world, xMin, yMin, zMin, xMax, yMax, zMax, (world1, pos) ->
                getTileEntityOfType(clazz, pos, world1));
    }

    @SuppressWarnings("unchecked")
    private static <T extends TileEntity> Optional<T> getTileEntityOfType(Class<? extends T> clazz, BlockPos pos, IBlockReader world) {
        TileEntity te = world.getBlockEntity(pos);
        return clazz.isInstance(te) ? Optional.of((T) te) : Optional.empty();
    }
}