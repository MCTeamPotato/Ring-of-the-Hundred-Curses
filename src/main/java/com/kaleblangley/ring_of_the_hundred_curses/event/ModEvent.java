package com.kaleblangley.ring_of_the_hundred_curses.event;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE;

@Mod.EventBusSubscriber(modid = RingOfTheHundredCurses.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvent {
    @SubscribeEvent
    public static void addAttribute(EntityAttributeModificationEvent event){
        event.getTypes().forEach(entityType -> {
            if (!event.has(entityType, ATTACK_DAMAGE)){
                event.add(entityType, ATTACK_DAMAGE);
            }
        });
    }
}
