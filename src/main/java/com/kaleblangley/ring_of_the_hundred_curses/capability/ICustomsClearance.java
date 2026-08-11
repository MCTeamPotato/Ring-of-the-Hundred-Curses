package com.kaleblangley.ring_of_the_hundred_curses.capability;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface ICustomsClearance {
    void addPendingItem(ItemStack item, long deliveryTime);

    List<PendingItem> getPendingItems();

    List<PendingItem> takeDueItems(long gameTime);

    void copyFrom(ICustomsClearance source);

    record PendingItem(ItemStack item, long deliveryTime) {
    }
}
