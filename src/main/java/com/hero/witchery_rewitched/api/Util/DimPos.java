package com.hero.witchery_rewitched.api.Util;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

/**
 * Copied from SilentLib under MIT licensing all credit goes to SilentChaos512.
 * https://github.com/SilentChaos512/SilentLib
 */

/**
 * Basically a BlockPos with a dimension coordinate.
 * <p>
 * I tried to extend BlockPos here, but that caused some strange issues with Silent's Gems'
 * teleporters. They would apparently fail to link, but would work correctly after reloading the
 * world. No idea why this happens, but extending BlockPos seems to be the cause.
 */
public final class DimPos {
    /**
     * Origin (0, 0, 0) in dimension 0
     */
    public static final DimPos ZERO = new DimPos(0, 0, 0, World.OVERWORLD);

    private final int posX;
    private final int posY;
    private final int posZ;
    private final DimensionId dimension;

    //region Static factory methods

    public static DimPos of(BlockPos pos, RegistryKey<World> dimension) {
        return new DimPos(pos, dimension);
    }

    public static DimPos of(int x, int y, int z, RegistryKey<World> dimension) {
        return new DimPos(x, y, z, dimension);
    }

    public static DimPos of(Entity entity) {
        return new DimPos(entity.blockPosition(), entity.level.dimension());
    }

    //endregion

    private DimPos(BlockPos pos, RegistryKey<World> dimension) {
        this(pos.getX(), pos.getY(), pos.getZ(), dimension);
    }

    private DimPos(int x, int y, int z, RegistryKey<World> dimension) {
        this(x, y, z, DimensionId.fromId(dimension));
    }

    private DimPos(int x, int y, int z, DimensionId dimension) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.dimension = dimension;
    }

    public int getX() {
        return this.posX;
    }

    public int getY() {
        return this.posY;
    }

    public int getZ() {
        return this.posZ;
    }

    public DimensionId getDimensionId() {
        return dimension;
    }

    public RegistryKey<World> getDimension() {
        return this.dimension.getId();
    }

    public static DimPos read(CompoundNBT tags) {
        return DimPos.of(
                tags.getInt("posX"),
                tags.getInt("posY"),
                tags.getInt("posZ"),
                RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(tags.getString("dim"))));
    }

    public void write(CompoundNBT tags) {
        tags.putInt("posX", this.posX);
        tags.putInt("posY", this.posY);
        tags.putInt("posZ", this.posZ);
        tags.putString("dim", dimension.getRegistryName().toString());
    }

    /**
     * Converts to a BlockPos
     *
     * @return A BlockPos with the same coordinates
     */
    public BlockPos getPos() {
        return new BlockPos(posX, posY, posZ);
    }

    public Vector3d getPosCentered(double yOffset) {
        return new Vector3d(posX + 0.5, posY + yOffset, posZ + 0.5);
    }

    /**
     * Offset the DimPos in the given direction by the given distance.
     *
     * @param facing The direction to offset
     * @param n      The distance
     * @return A new DimPos with offset coordinates.
     * @since 4.0.10
     */
    public DimPos offset(Direction facing, int n) {
        if (n == 0) {
            return this;
        }
        return new DimPos(
                this.posX + facing.getStepX() * n,
                this.posY + facing.getStepY() * n,
                this.posZ + facing.getStepZ() * n,
                this.dimension);
    }

    @Override
    public String toString() {
        return String.format("(%d, %d, %s) in %s", this.posX, this.posY, this.posZ, dimension.getRegistryName());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DimPos)) {
            return false;
        }
        DimPos pos = (DimPos) other;
        return pos.dimension == dimension && pos.posX == posX && pos.posY == posY && pos.posZ == posZ;
    }

    @Override
    public int hashCode() {
        return 31 * (31 * (31 * posX + posY) + posZ) + dimension.getRegistryName().hashCode();
    }
}