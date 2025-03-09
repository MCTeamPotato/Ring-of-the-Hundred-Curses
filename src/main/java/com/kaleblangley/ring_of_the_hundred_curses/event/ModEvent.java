package com.kaleblangley.ring_of_the_hundred_curses.event;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.Objects;

import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE;
import static net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE;

@Mod.EventBusSubscriber(modid = RingOfTheHundredCurses.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvent {
    @SubscribeEvent
    public static void addAttribute(EntityAttributeModificationEvent event) {
        event.getTypes().forEach(entityType -> {
            if (!event.has(entityType, ATTACK_DAMAGE)) {
                event.add(entityType, ATTACK_DAMAGE, 1d);
            }

            String entityKey = ForgeRegistries.ENTITY_TYPES.getKey(entityType).toString();
            Arrays.stream(ModConfigManager.getConfig().entityFollowRange)
                    .filter(pair -> entityKey.equals(pair.getFirst()))
                    .findFirst()
                    .ifPresent(pair -> event.add(entityType, FOLLOW_RANGE, pair.getSecond()));
        });

    }
}
