package com.kaleblangley.ring_of_the_hundred_curses.init;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import com.kaleblangley.ring_of_the_hundred_curses.item.CursedRing;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItem {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RingOfTheHundredCurses.MODID);
    public static final RegistryObject<Item> RING = ITEMS.register("ring_of_the_hundred_curses", CursedRing::new);
    public static final RegistryObject<Item> EXTINGUISHED_TORCH = ITEMS.register("extinguished_torch",
            () -> new StandingAndWallBlockItem(ModBlock.EXTINGUISHED_TORCH.get(), ModBlock.EXTINGUISHED_WALL_TORCH.get(), new Item.Properties(), Direction.DOWN));
}
