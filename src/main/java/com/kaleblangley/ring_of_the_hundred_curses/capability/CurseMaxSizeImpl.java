package com.kaleblangley.ring_of_the_hundred_curses.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;


public class CurseMaxSizeImpl implements ICurseMaxSize, INBTSerializable<CompoundTag> {
    private int curseMaxSize = 0;
    @Override
    public int getCurseMaxSize() {
        return this.curseMaxSize;
    }

    @Override
    public void setCurseMaxSize(int curseMaxSize) {
        this.curseMaxSize = curseMaxSize;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag sizeTag = new CompoundTag();
        sizeTag.putInt("ring_of_the_hundred_curses:maxSize", this.curseMaxSize);
        return sizeTag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.curseMaxSize = nbt.getInt("ring_of_the_hundred_curses:maxSize");
    }
}
