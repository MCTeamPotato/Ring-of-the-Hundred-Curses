package com.kaleblangley.ring_of_the_hundred_curses.mixin.entity;

import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.storage.loot.LootParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin {

    @Shadow
    @Nullable
    public abstract Player getPlayerOwner();

    @Redirect(
            method = "retrieve",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootParams$Builder;withLuck(F)Lnet/minecraft/world/level/storage/loot/LootParams$Builder;")
    )
    private LootParams.Builder ring_of_the_hundred_curses$abandonedByPoseidon(LootParams.Builder builder, float originalLuck) {
        Player player = this.getPlayerOwner();
        if (player != null && RingUtil.configAndRing(player, ModConfigManager.getConfig().enableAbandonedByPoseidon)) {
            float penalty = ModConfigManager.getConfig().abandonedByPoseidonLuckPenalty;
            return builder.withLuck(originalLuck - penalty);
        }
        return builder.withLuck(originalLuck);
    }
}
