package com.kaleblangley.ring_of_the_hundred_curses.mixin;

import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ForgeGui.class, remap = false)
public class ForgeGuiMixin {
    
    @Redirect(method = "renderHealth", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"))
    public float maxHealth(float a, float b){
        return a;
    }
    
    @ModifyConstant(method = "renderFood", constant = @Constant(intValue = 10))
    public int modifyFoodBarCount(int original) {
        Player player = Minecraft.getInstance().player;
        if (player != null && RingUtil.configAndRing(player, ModConfigManager.getConfig().enableHollowStomach)) {
            int maxHunger = ModConfigManager.getConfig().hollowStomachMaxHunger;
            return Math.max(1, (maxHunger + 1) / 2);
        }
        return original;
    }
    
}
