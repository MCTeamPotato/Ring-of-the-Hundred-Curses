package com.kaleblangley.ring_of_the_hundred_curses.mixin.entity;

import com.kaleblangley.ring_of_the_hundred_curses.advancement.CurseAdvancementManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;
import static com.kaleblangley.ring_of_the_hundred_curses.init.ModPlayerEventKeys.SWIM_TIME_KEY;

@Mixin(Player.class)
public class PlayerMixin {

    @Inject(method = "setItemSlot", at = @At("HEAD"), cancellable = true)
    private void ring_of_the_hundred_curses$preventUntrimmedArmor(
            EquipmentSlot slot, ItemStack stack, CallbackInfo ci
    ) {
        if (slot.getType() != EquipmentSlot.Type.ARMOR || stack.isEmpty() || !(stack.getItem() instanceof ArmorItem)) {
            return;
        }
        Player player = (Player) (Object) this;
        if (RingUtil.configAndRing(player, getConfig().enableLavishTaste)
                && !ring_of_the_hundred_curses$hasTrim(stack)) {
            ci.cancel();
            CurseAdvancementManager.trigger(player, "lavish_taste");
        }
    }

    @ModifyVariable(method = "onEnchantmentPerformed", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int ring_of_the_hundred_curses$increaseGreedyTomeExperienceCost(int levels) {
        Player player = (Player) (Object) this;
        if (!(player.containerMenu instanceof EnchantmentMenu)
                || !RingUtil.configAndRing(player, getConfig().enableGreedyTome)) {
            return levels;
        }
        double multiplier = Math.max(0.0D, getConfig().greedyTomeCostMultiplier);
        int modifiedLevels = (int) Math.min(Integer.MAX_VALUE, Math.ceil(levels * multiplier));
        if (modifiedLevels != levels) {
            CurseAdvancementManager.trigger(player, "greedy_tome");
        }
        return modifiedLevels;
    }

    @Inject(method = "travel", at = @At("HEAD"))
    private void ring_of_the_hundred_curses$forceDeepSeaSinking(
            Vec3 travelVector, CallbackInfo ci
    ) {
        Player player = (Player) (Object) this;
        if (!ring_of_the_hundred_curses$isExhaustedInWater(player)) return;

        player.setJumping(false);
        player.setSwimming(false);
        Vec3 motion = player.getDeltaMovement();
        double sinkingSpeed = Math.max(0.01, getConfig().deepSeaEntanglementSinkingSpeed);
        if (motion.y > -sinkingSpeed) {
            player.setDeltaMovement(motion.x, -sinkingSpeed, motion.z);
            CurseAdvancementManager.trigger(player, "deep_sea_entanglement");
        }
    }

    @ModifyVariable(method = "travel", at = @At("HEAD"), argsOnly = true)
    private Vec3 ring_of_the_hundred_curses$removeUpwardWaterInput(Vec3 travelVector) {
        Player player = (Player) (Object) this;
        if (!ring_of_the_hundred_curses$isExhaustedInWater(player)) return travelVector;
        double sinkingSpeed = Math.max(0.01D, getConfig().deepSeaEntanglementSinkingSpeed);
        Vec3 modifiedVector = new Vec3(travelVector.x, Math.min(travelVector.y, -sinkingSpeed), travelVector.z);
        if (!modifiedVector.equals(travelVector)) {
            CurseAdvancementManager.trigger(player, "deep_sea_entanglement");
        }
        return modifiedVector;
    }

    @Inject(method = "travel", at = @At("RETURN"))
    private void ring_of_the_hundred_curses$keepDeepSeaSinking(
            Vec3 travelVector, CallbackInfo ci
    ) {
        Player player = (Player) (Object) this;
        if (!ring_of_the_hundred_curses$isExhaustedInWater(player)) return;

        Vec3 motion = player.getDeltaMovement();
        double sinkingSpeed = Math.max(0.01, getConfig().deepSeaEntanglementSinkingSpeed);
        if (motion.y > -sinkingSpeed) {
            player.setDeltaMovement(motion.x, -sinkingSpeed, motion.z);
            CurseAdvancementManager.trigger(player, "deep_sea_entanglement");
        }
    }

    @Unique
    private static boolean ring_of_the_hundred_curses$isExhaustedInWater(Player player) {
        if (!RingUtil.isInWaterOrAtSurface(player)) return false;
        if (!RingUtil.configAndRing(player, getConfig().enableDeepSeaEntanglement)) return false;
        int maxSwimTicks = Math.max(1, getConfig().deepSeaEntanglementSwimTime * 20);
        return player.getPersistentData().getInt(SWIM_TIME_KEY) >= maxSwimTicks;
    }

    @Unique
    private static boolean ring_of_the_hundred_curses$hasTrim(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains("Trim");
    }
}
