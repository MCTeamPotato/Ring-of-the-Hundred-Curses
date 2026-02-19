package com.kaleblangley.ring_of_the_hundred_curses.mixin.entity;

import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "getArmorValue", at = @At("RETURN"), cancellable = true)
    private void ring_of_the_hundred_curses$modifyArmorValueByDurability(CallbackInfoReturnable<Integer> cir) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (!(livingEntity instanceof Player player)) {
            return;
        }
        if (!RingUtil.configAndRing(player, getConfig().enableDilapidatedWarrior)) {
            return;
        }
        double originalArmorValue = cir.getReturnValue();
        double fullArmorValue = 0.0D;
        double effectiveArmorValue = 0.0D;
        for (ItemStack armorStack : player.getArmorSlots()) {
            if (!(armorStack.getItem() instanceof ArmorItem armorItem)) {
                continue;
            }
            double defense = armorItem.getDefense();
            fullArmorValue += defense;
            if (armorStack.isDamageableItem() && armorStack.getMaxDamage() > 0) {
                int remainingDurability = armorStack.getMaxDamage() - armorStack.getDamageValue();
                double durabilityRatio = Math.max(0.0D, Math.min(1.0D, (double) remainingDurability / armorStack.getMaxDamage()));
                effectiveArmorValue += defense * durabilityRatio;
            } else {
                effectiveArmorValue += defense;
            }
        }
        if (fullArmorValue <= 0.0D) {
            return;
        }
        double nonArmorBonusValue = Math.max(0.0D, originalArmorValue - fullArmorValue);
        double adjustedArmorValue = nonArmorBonusValue + effectiveArmorValue;
        if (adjustedArmorValue >= originalArmorValue) {
            return;
        }
        cir.setReturnValue((int) Math.max(0, Math.round(adjustedArmorValue)));
    }

    @Inject(method = "calculateFallDamage", at = @At("HEAD"), cancellable = true)
    private void ring_of_the_hundred_curses$fragileBodyCalculateFallDamage(float fallDistance, float damageMultiplier, CallbackInfoReturnable<Integer> cir) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (!(livingEntity instanceof Player player)) {
            return;
        }
        if (!RingUtil.configAndRing(player, getConfig().enableFragileBody)) {
            return;
        }
        if (livingEntity.getType().is(EntityTypeTags.FALL_DAMAGE_IMMUNE)) {
            cir.setReturnValue(0);
            return;
        }

        float effectiveMultiplier = Math.max(1.0F, damageMultiplier);
        cir.setReturnValue(Mth.ceil((fallDistance - 3.0F) * effectiveMultiplier));
    }

    @Inject(method = "getDamageAfterArmorAbsorb", at = @At("HEAD"), cancellable = true)
    private void ring_of_the_hundred_curses$fragileBodyBypassArmor(DamageSource damageSource, float damageAmount, CallbackInfoReturnable<Float> cir) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (ring_of_the_hundred_curses$isFragileBodyFall(livingEntity, damageSource)) {
            cir.setReturnValue(damageAmount);
        }
    }

    @Inject(method = "getDamageAfterMagicAbsorb", at = @At("HEAD"), cancellable = true)
    private void ring_of_the_hundred_curses$fragileBodyBypassMagic(DamageSource damageSource, float damageAmount, CallbackInfoReturnable<Float> cir) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (ring_of_the_hundred_curses$isFragileBodyFall(livingEntity, damageSource)) {
            cir.setReturnValue(damageAmount);
        }
    }

    @Redirect(
            method = "travel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFriction(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)F", remap = false)
    )
    private float ring_of_the_hundred_curses$modifyFriction(BlockState state, LevelReader levelReader, BlockPos pos, Entity entity) {
        float friction = state.getFriction(levelReader, pos, entity);
        if (!(entity instanceof Player player) || !(levelReader instanceof Level level)) {
            return friction;
        }

        if (RingUtil.configAndRing(player, getConfig().enableIceRink) && level.isRaining()) {
            friction = Math.max(friction, getConfig().iceRinkFriction);
        }

        if (RingUtil.configAndRing(player, getConfig().enableSlipperyAdventure)
                && level.getBiome(player.blockPosition()).value().coldEnoughToSnow(player.blockPosition())) {
            friction = Math.max(friction, getConfig().slipperyAdventureFriction);
        }

        return friction;
    }

    @Inject(method = "hasLineOfSight", at = @At("HEAD"), cancellable = true)
    private void ring_of_the_hundred_curses$noShelter(Entity target, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof Player player) {
            if (RingUtil.configAndRing(player, getConfig().enableNoShelter)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Unique
    private static boolean ring_of_the_hundred_curses$isFragileBodyFall(LivingEntity livingEntity, DamageSource damageSource) {
        if (!damageSource.is(DamageTypeTags.IS_FALL)) {
            return false;
        }
        if (!(livingEntity instanceof Player player)) {
            return false;
        }
        return RingUtil.configAndRing(player, getConfig().enableFragileBody);
    }
}
