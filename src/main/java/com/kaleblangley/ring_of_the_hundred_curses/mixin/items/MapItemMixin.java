package com.kaleblangley.ring_of_the_hundred_curses.mixin.items;

import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapItem.class)
public class MapItemMixin {

    @Inject(method = "inventoryTick", at = @At("HEAD"), cancellable = true)
    private void ring_of_the_hundred_curses$disableMapUpdate(ItemStack pStack, Level pLevel, Entity pEntity, int pItemSlot, boolean pIsSelected, CallbackInfo ci) {
        if (pEntity instanceof Player player && RingUtil.configAndRing(player, ModConfigManager.getConfig().enableLostDirection)) {
            ci.cancel();
        }
    }
}
