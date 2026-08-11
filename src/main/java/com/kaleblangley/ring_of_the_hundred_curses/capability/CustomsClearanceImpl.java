package com.kaleblangley.ring_of_the_hundred_curses.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomsClearanceImpl implements ICustomsClearance, INBTSerializable<CompoundTag> {
    private static final String PENDING_ITEMS_TAG = "PendingItems";
    private static final String ITEM_TAG = "Item";
    private static final String DELIVERY_TIME_TAG = "DeliveryTime";

    private final List<PendingItem> pendingItems = new ArrayList<>();

    @Override
    public void addPendingItem(ItemStack item, long deliveryTime) {
        if (item.isEmpty()) return;
        pendingItems.add(new PendingItem(item.copy(), deliveryTime));
    }

    @Override
    public List<PendingItem> getPendingItems() {
        return pendingItems.stream()
                .map(item -> new PendingItem(item.item().copy(), item.deliveryTime()))
                .toList();
    }

    @Override
    public List<PendingItem> takeDueItems(long gameTime) {
        List<PendingItem> dueItems = new ArrayList<>();
        Iterator<PendingItem> iterator = pendingItems.iterator();
        while (iterator.hasNext()) {
            PendingItem pendingItem = iterator.next();
            if (gameTime >= pendingItem.deliveryTime()) {
                dueItems.add(new PendingItem(pendingItem.item().copy(), pendingItem.deliveryTime()));
                iterator.remove();
            }
        }
        return dueItems;
    }

    @Override
    public void copyFrom(ICustomsClearance source) {
        pendingItems.clear();
        for (PendingItem pendingItem : source.getPendingItems()) {
            addPendingItem(pendingItem.item(), pendingItem.deliveryTime());
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag pendingTag = new ListTag();
        for (PendingItem pendingItem : pendingItems) {
            CompoundTag entry = new CompoundTag();
            entry.put(ITEM_TAG, pendingItem.item().save(new CompoundTag()));
            entry.putLong(DELIVERY_TIME_TAG, pendingItem.deliveryTime());
            pendingTag.add(entry);
        }
        tag.put(PENDING_ITEMS_TAG, pendingTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        pendingItems.clear();
        if (!tag.contains(PENDING_ITEMS_TAG, Tag.TAG_LIST)) return;

        ListTag pendingTag = tag.getList(PENDING_ITEMS_TAG, Tag.TAG_COMPOUND);
        for (int i = 0; i < pendingTag.size(); i++) {
            CompoundTag entry = pendingTag.getCompound(i);
            ItemStack item = ItemStack.of(entry.getCompound(ITEM_TAG));
            if (!item.isEmpty()) {
                pendingItems.add(new PendingItem(item, entry.getLong(DELIVERY_TIME_TAG)));
            }
        }
    }
}
