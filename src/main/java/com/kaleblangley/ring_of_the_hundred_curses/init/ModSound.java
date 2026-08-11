package com.kaleblangley.ring_of_the_hundred_curses.init;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSound {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, RingOfTheHundredCurses.MODID);

    public static final RegistryObject<SoundEvent> BETTER_REMIX = SOUND_EVENTS.register(
            "betterremix",
            () -> SoundEvent.createVariableRangeEvent(
                    new ResourceLocation(RingOfTheHundredCurses.MODID, "betterremix")
            )
    );

    private ModSound() {
    }
}
