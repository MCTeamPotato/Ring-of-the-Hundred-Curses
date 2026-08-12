package com.kaleblangley.ring_of_the_hundred_curses.config;

import me.shedaniel.autoconfig.AutoConfig;

public class ModConfigManager {
    private static final ModConfig CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

    public static ModConfig getConfig(){
        return CONFIG;
    }
}
