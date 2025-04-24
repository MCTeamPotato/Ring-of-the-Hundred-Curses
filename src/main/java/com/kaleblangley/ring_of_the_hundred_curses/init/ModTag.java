package com.kaleblangley.ring_of_the_hundred_curses.init;

import com.kaleblangley.ring_of_the_hundred_curses.util.ResourceUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTag {
    public static final TagKey<Item> RAW_FOOD = bind("raw_food");

    private static TagKey<Item> bind(String name) {
        return TagKey.create(Registries.ITEM, ResourceUtil.ringResource(name));
    }
}
