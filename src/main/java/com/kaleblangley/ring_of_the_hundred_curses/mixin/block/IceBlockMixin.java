package com.kaleblangley.ring_of_the_hundred_curses.mixin.block;

import com.kaleblangley.ring_of_the_hundred_curses.advancement.CurseAdvancementManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
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
            Player player = instance.players().stream()
                    .filter(p -> RingUtil.configAndRing(p, getConfig().enableEndWaterBan))
                    .findFirst().orElse(null);
            if (player != null) {
                boolean removed = instance.removeBlock(pos, false);
                if (removed) {
                    CurseAdvancementManager.trigger(player, "end_water_ban");
                }
                return removed;
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
            Player player = instance.players().stream()
                    .filter(p -> RingUtil.configAndRing(p, getConfig().enableEndWaterBan))
                    .findFirst().orElse(null);
            if (player != null) {
                boolean removed = instance.removeBlock(pos, false);
                if (removed) {
                    CurseAdvancementManager.trigger(player, "end_water_ban");
                }
                return removed;
            }
        }
        return instance.setBlockAndUpdate(pos, state);
    }
}
