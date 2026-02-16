package com.kaleblangley.ring_of_the_hundred_curses.mixin.block;

import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RespawnAnchorBlock.class)
public class RespawnAnchorBlockMixin {

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    private void ring_of_the_hundred_curses$cafeteriaLady(ItemStack stack, int original,
            BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (RingUtil.configAndRing(pPlayer, ModConfigManager.getConfig().enableCafeteriaLady)) {
            int min = ModConfigManager.getConfig().cafeteriaLadyMinCost;
            int max = ModConfigManager.getConfig().cafeteriaLadyMaxCost;
            if (min > max) min = max;
            int cost = min + pLevel.random.nextInt(Math.max(1, max - min + 1));
            stack.shrink(cost);
        } else {
            stack.shrink(original);
        }
    }
}
