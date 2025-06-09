package com.kaleblangley.ring_of_the_hundred_curses.mixin;

import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    
    @Inject(method = "getMaxAirSupply", at = @At("RETURN"), cancellable = true)
    private void onGetMaxAirSupply(CallbackInfoReturnable<Integer> cir) {
        Entity entity = (Entity) (Object) this;
        
        if (entity instanceof Player player) {
            if (RingUtil.configAndRing(player, ModConfigManager.getConfig().enablePulmonaryFibrosis)) {
                int originalMaxAir = cir.getReturnValue();
                int reduction = ModConfigManager.getConfig().pulmonaryFibrosisAirReduction;
                int newMaxAir = Math.max(20, originalMaxAir - reduction);
                cir.setReturnValue(newMaxAir);
            }
        }
    }
}
