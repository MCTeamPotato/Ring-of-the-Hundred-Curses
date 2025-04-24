package com.kaleblangley.ring_of_the_hundred_curses.util;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import net.minecraft.resources.ResourceLocation;

public class ResourceUtil {
    public static ResourceLocation ringResource(String path){
        return new ResourceLocation(RingOfTheHundredCurses.MODID, path);
    }
}
