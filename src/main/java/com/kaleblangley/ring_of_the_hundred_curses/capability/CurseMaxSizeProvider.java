package com.kaleblangley.ring_of_the_hundred_curses.capability;

import com.kaleblangley.ring_of_the_hundred_curses.event.ModEvent;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public class CurseMaxSizeProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<ICurseMaxSize> CURSE_MAX_SIZE = CapabilityManager.get(new CapabilityToken<>() {});
    private final CurseMaxSizeImpl instance = new CurseMaxSizeImpl();
    private final LazyOptional<ICurseMaxSize> optional = LazyOptional.of(() -> instance);
    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        return cap == CURSE_MAX_SIZE ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return instance.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        instance.deserializeNBT(tag);
    }
}
