package com.kaleblangley.ring_of_the_hundred_curses.mixin.inventory;

import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

@Mixin(targets = "net.minecraft.world.inventory.InventoryMenu$1")
public abstract class InventoryArmorSlotMixin {

    @Redirect(
        method = "mayPlace",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;canEquip(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/Entity;)Z",
            remap = false
        )
    )
    private boolean ring_of_the_hundred_curses$preventUntrimmedArmor(
        ItemStack stack, EquipmentSlot equipmentSlot, Entity entity
    ) {
        if (entity instanceof Player player
                && stack.getItem() instanceof ArmorItem
                && RingUtil.configAndRing(player, getConfig().enableLavishTaste)
                && !ring_of_the_hundred_curses$hasTrim(stack)) {
            return false;
        }
        return stack.canEquip(equipmentSlot, entity);
    }

    @Unique
    private static boolean ring_of_the_hundred_curses$hasTrim(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains("Trim");
    }
}
