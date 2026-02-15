package com.kaleblangley.ring_of_the_hundred_curses.mixin.inventory;

import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(InventoryMenu.class)
public class InventoryMenuMixin {
    @Shadow @Final private Player owner;

    @ModifyVariable(method = "quickMoveStack", at = @At("STORE"), ordinal = 1)
    public ItemStack ring_of_the_hundred_curses$modifyItemSize(ItemStack itemStack){
        ItemStack newItemStack = itemStack.copy();
        RingUtil.backpackLimitSizeModify(this.owner, newItemStack);
        return newItemStack;
    }
}
