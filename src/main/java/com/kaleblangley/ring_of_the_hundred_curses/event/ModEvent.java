package com.kaleblangley.ring_of_the_hundred_curses.event;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

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

            Double followRange = ModConfigManager.getConfig().entityFollowRange.get(ForgeRegistries.ENTITY_TYPES.getKey(entityType).toString());
            if (followRange != null) event.add(entityType, FOLLOW_RANGE, followRange);
        });



    }
}
