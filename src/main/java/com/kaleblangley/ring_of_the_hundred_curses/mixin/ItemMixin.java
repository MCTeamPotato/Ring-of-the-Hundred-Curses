package com.kaleblangley.ring_of_the_hundred_curses.mixin;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Item.class)
public class ItemMixin {
    @Shadow @Final private int maxStackSize;
}
