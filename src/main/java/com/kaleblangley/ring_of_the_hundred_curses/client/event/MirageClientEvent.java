package com.kaleblangley.ring_of_the_hundred_curses.client.event;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import com.kaleblangley.ring_of_the_hundred_curses.client.mirage.MirageClientManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = RingOfTheHundredCurses.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public final class MirageClientEvent {
    private MirageClientEvent() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) MirageClientManager.tick();
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        MirageClientManager.render(event);
    }
}
