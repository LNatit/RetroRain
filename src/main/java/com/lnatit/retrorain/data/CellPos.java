package com.lnatit.retrorain.data;

import net.minecraft.core.BlockPos;

// TODO override hashCode() & equals()
public class CellPos
{
    private int x;
    private int z;

    public CellPos(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public CellPos(BlockPos pos) {
        this.x = pos.getX() >> 2;
        this.z = pos.getZ() >> 2;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getChunkX() {
        return x >> 2;
    }

    public int getChunkZ() {
        return z >> 2;
    }

    public int getLocalX() {
        return x & 3;
    }

    public int getLocalZ() {
        return z & 3;
    }
}
