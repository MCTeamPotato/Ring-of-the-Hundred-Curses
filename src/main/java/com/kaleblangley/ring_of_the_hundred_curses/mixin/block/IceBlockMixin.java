package com.kaleblangley.ring_of_the_hundred_curses.mixin.block;

import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

@Mixin(IceBlock.class)
public class IceBlockMixin {

    @Redirect(
            method = "playerDestroy",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z")
    )
    private boolean ring_of_the_hundred_curses$endNoWaterOnBreak(Level instance, BlockPos pos, BlockState state) {
        if (instance.dimension() == Level.END) {
            boolean shouldEvaporate = instance.players().stream()
                    .anyMatch(p -> RingUtil.configAndRing(p, getConfig().enableEndWaterBan));
            if (shouldEvaporate) {
                return instance.removeBlock(pos, false);
            }
        }
        return instance.setBlockAndUpdate(pos, state);
    }

    @Redirect(
            method = "melt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z")
    )
    private boolean ring_of_the_hundred_curses$endNoWaterOnMelt(Level instance, BlockPos pos, BlockState state) {
        if (instance.dimension() == Level.END) {
            boolean shouldEvaporate = instance.players().stream()
                    .anyMatch(p -> RingUtil.configAndRing(p, getConfig().enableEndWaterBan));
            if (shouldEvaporate) {
                return instance.removeBlock(pos, false);
            }
        }
        return instance.setBlockAndUpdate(pos, state);
    }
}
