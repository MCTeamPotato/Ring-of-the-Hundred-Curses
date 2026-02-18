package com.kaleblangley.ring_of_the_hundred_curses.mixin.items;

import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

@Mixin(BucketItem.class)
public class BucketItemMixin {

    @Inject(
            method = "emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void ring_of_the_hundred_curses$endWaterBan(@Nullable Player pPlayer, Level pLevel, BlockPos pPos, @Nullable BlockHitResult pResult, @Nullable ItemStack container, CallbackInfoReturnable<Boolean> cir) {
        if (pLevel.dimension() != Level.END) return;

        Fluid fluid = ((BucketItem) (Object) this).getFluid();
        if (!fluid.is(FluidTags.WATER)) return;

        boolean shouldEvaporate;
        if (pPlayer != null) {
            shouldEvaporate = RingUtil.configAndRing(pPlayer, getConfig().enableEndWaterBan);
        } else {
            shouldEvaporate = pLevel.players().stream()
                    .anyMatch(p -> RingUtil.configAndRing(p, getConfig().enableEndWaterBan));
        }

        if (shouldEvaporate) {
            int i = pPos.getX();
            int j = pPos.getY();
            int k = pPos.getZ();
            pLevel.playSound(pPlayer, pPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (pLevel.random.nextFloat() - pLevel.random.nextFloat()) * 0.8F);
            for (int l = 0; l < 8; ++l) {
                pLevel.addParticle(ParticleTypes.LARGE_SMOKE, (double) i + Math.random(), (double) j + Math.random(), (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
            }
            cir.setReturnValue(true);
        }
    }
}
