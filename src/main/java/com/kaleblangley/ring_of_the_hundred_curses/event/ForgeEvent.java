package com.kaleblangley.ring_of_the_hundred_curses.event;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import com.kaleblangley.ring_of_the_hundred_curses.init.ModItem;
import com.kaleblangley.ring_of_the_hundred_curses.item.CursedRing;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = RingOfTheHundredCurses.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvent {
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void ringTooltip(ItemTooltipEvent event){
        List<Component> toolTips = event.getToolTip();
        MutableComponent slot = Component.translatable("curios.tooltip.slot").withStyle(ChatFormatting.GOLD);
        MutableComponent identifier = Component.translatable("curios.identifier.ring").withStyle(ChatFormatting.YELLOW);

        if (event.getItemStack().getItem() instanceof CursedRing){
            toolTips.add(Component.translatable("message.ring_of_the_hundred_curses.description_1"));
            toolTips.add(slot.append(" ").append(identifier));
        }
    }
}
