package com.kaleblangley.ring_of_the_hundred_curses.mixin;

import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Entity.class)
public class EntityMixin {

    @ModifyConstant(method = "getMaxAirSupply", constant = @Constant(intValue = 300))
    private int modifyMaxAirSupply(int originalValue) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof Player player) {
            if (RingUtil.configAndRing(player, ModConfigManager.getConfig().enablePulmonaryFibrosis)) {
                int reduction = ModConfigManager.getConfig().pulmonaryFibrosisAirReduction;
                return Math.max(20, originalValue - reduction);
            }
        }
        return originalValue;
    }
}
