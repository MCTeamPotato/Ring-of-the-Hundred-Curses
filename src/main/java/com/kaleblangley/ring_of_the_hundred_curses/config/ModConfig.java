package com.kaleblangley.ring_of_the_hundred_curses.config;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = RingOfTheHundredCurses.MODID)
public class ModConfig implements ConfigData {
    /*
     * 命薄如纸：减少玩家的部分固定生命值
     * Fragile Life: Reduces a portion of the player's base health.
     */
    public int reducesHealth = 10;
}
