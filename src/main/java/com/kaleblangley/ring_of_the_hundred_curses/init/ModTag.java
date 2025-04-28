package com.kaleblangley.ring_of_the_hundred_curses.init;

import com.kaleblangley.ring_of_the_hundred_curses.util.ResourceUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class ModTag {
    public static final TagKey<Item> RAW_FOOD = createItem("raw_food");
    public static final TagKey<Block> ALWAYS_DIG = createBlock("always_dig");
    public static final TagKey<Block> AXE_DIG = createBlock("axe_dig");
    public static final TagKey<Block> HOE_DIG = createBlock("hoe_dig");
    public static final TagKey<Block> PICKAXE_DIG = createBlock("pickaxe_dig");
    public static final TagKey<Block> SHOVEL_DIG = createBlock("shovel_dig");


    private static TagKey<Item> createItem(String name) {
        return TagKey.create(Registries.ITEM, ResourceUtil.ringResource(name));
    }

    private static TagKey<Block> createBlock(String name) {
        return TagKey.create(Registries.BLOCK, ResourceUtil.ringResource(name));
    }
}
