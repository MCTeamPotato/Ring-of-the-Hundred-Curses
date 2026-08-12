package com.kaleblangley.ring_of_the_hundred_curses.mixin.inventory;

import com.kaleblangley.ring_of_the_hundred_curses.advancement.CurseAdvancementManager;
import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public class CreativeModeInventoryScreenMixin {
    @Unique
    private Player ringOfTheHundredCurses$player;
    @Inject(method = "<init>", at = @At(value = "TAIL"))
    public void ring_of_the_hundred_curses$savePlayer(Player pPlayer, FeatureFlagSet pEnabledFeatures, boolean pDisplayOperatorCreativeTab, CallbackInfo ci){
        this.ringOfTheHundredCurses$player = pPlayer;
    }
    @Redirect(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getMaxStackSize()I", ordinal = 2))
    public int ring_of_the_hundred_curses$modifyCreateMax(ItemStack instance){
        if (RingUtil.configAndRing(this.ringOfTheHundredCurses$player, ModConfigManager.getConfig().enableBackpackLimit)){
            int modified = Math.min(ModConfigManager.getConfig().maxStackSize, instance.getMaxStackSize());
            if (modified != instance.getMaxStackSize()) {
                CurseAdvancementManager.trigger(this.ringOfTheHundredCurses$player, "backpack_limit");
            }
            return modified;
        }
        return instance.getMaxStackSize();
    }
}
