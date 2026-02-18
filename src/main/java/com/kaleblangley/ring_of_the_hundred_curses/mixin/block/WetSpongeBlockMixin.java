package com.kaleblangley.ring_of_the_hundred_curses.mixin.block;

import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WetSpongeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

@Mixin(WetSpongeBlock.class)
public class WetSpongeBlockMixin {

    @Inject(method = "onPlace", at = @At("HEAD"), cancellable = true)
    private void ring_of_the_hundred_curses$endWetSpongeDry(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving, CallbackInfo ci) {
        if (pLevel.dimension() == Level.END) {
            boolean shouldDry = pLevel.players().stream()
                    .anyMatch(p -> RingUtil.configAndRing(p, getConfig().enableEndWaterBan));
            if (shouldDry) {
                pLevel.setBlock(pPos, Blocks.SPONGE.defaultBlockState(), 3);
                pLevel.levelEvent(2009, pPos, 0);
                pLevel.playSound(null, pPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, (1.0F + pLevel.getRandom().nextFloat() * 0.2F) * 0.7F);
                ci.cancel();
            }
        }
    }
}
