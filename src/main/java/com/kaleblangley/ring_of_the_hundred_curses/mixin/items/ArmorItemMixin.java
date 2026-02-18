package com.kaleblangley.ring_of_the_hundred_curses.mixin.items;

import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

@Mixin(ArmorItem.class)
public abstract class ArmorItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void ring_of_the_hundred_curses$lavishTasteUse(
            Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir
    ) {
        if (!RingUtil.configAndRing(player, getConfig().enableLavishTaste)) return;
        ItemStack stack = player.getItemInHand(hand);
        if (!ring_of_the_hundred_curses$hasTrim(stack)) {
            cir.setReturnValue(InteractionResultHolder.fail(stack));
        }
    }

    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        if (Mob.getEquipmentSlotForItem(stack) != armorType) return false;
        if (entity instanceof Player player && RingUtil.configAndRing(player, getConfig().enableLavishTaste)) {
            return ring_of_the_hundred_curses$hasTrim(stack);
        }
        return true;
    }

    @Unique
    private static boolean ring_of_the_hundred_curses$hasTrim(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains("Trim");
    }
}
