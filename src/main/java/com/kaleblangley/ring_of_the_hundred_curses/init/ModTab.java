package com.kaleblangley.ring_of_the_hundred_curses.init;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTab {
    public static final DeferredRegister<CreativeModeTab> TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RingOfTheHundredCurses.MODID);

    public static final RegistryObject<CreativeModeTab> CURSED_RING_TAB = TAB.register("cursed_ring_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .title(ModItem.RING.get().getDescription())
            .icon(() -> ModItem.RING.get().getDefaultInstance())
            .displayItems((parameters, output) -> output.accept(ModItem.RING.get()))
            .build());
}
