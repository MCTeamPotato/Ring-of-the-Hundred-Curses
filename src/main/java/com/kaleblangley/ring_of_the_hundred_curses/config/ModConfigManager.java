package com.kaleblangley.ring_of_the_hundred_curses.config;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.ConfigScreenHandler;

public class ModConfigManager {
    private static final ModConfig CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    private static final ConfigScreenHandler.ConfigScreenFactory FACTORY = new ConfigScreenHandler.ConfigScreenFactory(ModConfigManager::getConfigScreen);
    public static Screen getConfigScreen(Screen parent){
        return AutoConfig.getConfigScreen(ModConfig.class, parent).get();
    }
    public static ModConfig getConfig(){
        return CONFIG;
    }
    public static ConfigScreenHandler.ConfigScreenFactory getConfigFactory(){
        return FACTORY;
    }
}