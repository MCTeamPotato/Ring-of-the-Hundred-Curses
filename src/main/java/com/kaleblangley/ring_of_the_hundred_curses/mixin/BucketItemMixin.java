package com.kaleblangley.ring_of_the_hundred_curses.mixin;

import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

@Mixin(BucketItem.class)
public class BucketItemMixin {

    @ModifyExpressionValue(method = "emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;ultraWarm()Z"))
    private boolean makeEndBehaveLikeNether(boolean original, Player player, Level level) {
        if (player != null && RingUtil.configAndRing(player, getConfig().enableEndWaterBan) && level.dimension() == Level.END) {
            return ((BucketItem) (Object) this).getFluid().is(FluidTags.WATER);
        }
        return false;
    }


} 