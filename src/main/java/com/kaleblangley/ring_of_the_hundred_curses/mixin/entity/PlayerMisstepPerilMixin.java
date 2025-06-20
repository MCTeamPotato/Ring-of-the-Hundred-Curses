package com.kaleblangley.ring_of_the_hundred_curses.mixin.entity;

import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

@Mixin(Player.class)
public class PlayerMisstepPerilMixin {
    @Inject(method = "maybeBackOffFromEdge", at = @At("HEAD"), cancellable = true)
    private void onMaybeBackOffFromEdge(Vec3 vec, MoverType mover, CallbackInfoReturnable<Vec3> cir) {
        Player player = (Player) (Object) this;
        if (RingUtil.configAndRing(player, getConfig().enableMisstepPeril)) {
            cir.setReturnValue(vec);
        }
    }
} 