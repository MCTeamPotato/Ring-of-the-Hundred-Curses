package com.kaleblangley.ring_of_the_hundred_curses.event;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import com.kaleblangley.ring_of_the_hundred_curses.capability.CurseMaxSizeProvider;
import com.kaleblangley.ring_of_the_hundred_curses.capability.ICustomsClearance;
import com.kaleblangley.ring_of_the_hundred_curses.capability.ICurseMaxSize;
import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE;
import static net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE;

@Mod.EventBusSubscriber(modid = RingOfTheHundredCurses.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvent {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ICurseMaxSize.class);
        event.register(ICustomsClearance.class);
    }

    @SubscribeEvent
    public static void addAttribute(EntityAttributeModificationEvent event) {
        event.getTypes().forEach(entityType -> {
            if (!event.has(entityType, ATTACK_DAMAGE)) {
                event.add(entityType, ATTACK_DAMAGE, 1d);
            }

            ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
            if (entityId == null) return;

            Double followRange = findConfiguredFollowRange(entityId.toString());
            if (followRange != null) {
                event.add(entityType, FOLLOW_RANGE, followRange);
            }
        });
    }

    private static Double findConfiguredFollowRange(String entityId) {
        for (String entry : ModConfigManager.getConfig().entityFollowRange) {
            if (entry == null) continue;

            String[] parts = entry.split("=", 2);
            if (parts.length != 2 || !entityId.equals(parts[0].trim())) continue;

            try {
                double range = Double.parseDouble(parts[1].trim());
                if (Double.isFinite(range) && range >= 0.0d) {
                    return range;
                }
            } catch (NumberFormatException ignored) {
                // Ignore malformed entries so one bad value does not prevent the game from loading.
            }
        }
        return null;
    }

}
