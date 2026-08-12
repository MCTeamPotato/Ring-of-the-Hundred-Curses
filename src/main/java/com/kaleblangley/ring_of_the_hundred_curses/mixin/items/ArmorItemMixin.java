package com.kaleblangley.ring_of_the_hundred_curses.mixin.items;

import com.kaleblangley.ring_of_the_hundred_curses.advancement.CurseAdvancementManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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
            CurseAdvancementManager.trigger(player, "lavish_taste");
        }
    }

    @Unique
    private static boolean ring_of_the_hundred_curses$hasTrim(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains("Trim");
    }
}
