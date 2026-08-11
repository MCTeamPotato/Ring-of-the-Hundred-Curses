package com.kaleblangley.ring_of_the_hundred_curses.init;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;

public final class ModDamageTypes {
    public static final ResourceKey<DamageType> BLEEDING = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            new ResourceLocation(RingOfTheHundredCurses.MODID, "bleeding")
    );

    private ModDamageTypes() {
    }

    public static DamageSource bleeding(LivingEntity entity) {
        return new DamageSource(
                entity.level().registryAccess()
                        .registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(BLEEDING)
        );
    }
}
