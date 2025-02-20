package com.kaleblangley.ring_of_the_hundred_curses;

import com.kaleblangley.ring_of_the_hundred_curses.init.ModItem;
import com.kaleblangley.ring_of_the_hundred_curses.init.ModTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(RingOfTheHundredCurses.MODID)
public class RingOfTheHundredCurses {

    public static final String MODID = "ring_of_the_hundred_curses";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public RingOfTheHundredCurses(){
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItem.ITEMS.register(modEventBus);
        ModTab.TAB.register(modEventBus);
    }
}
