package com.kaleblangley.ring_of_the_hundred_curses.init;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import com.kaleblangley.ring_of_the_hundred_curses.effect.BleedingEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEffect {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, RingOfTheHundredCurses.MODID);

    public static final RegistryObject<MobEffect> BLEEDING = MOB_EFFECTS.register(
            "bleeding", BleedingEffect::new
    );

    private ModEffect() {
    }
}
