package com.kaleblangley.ring_of_the_hundred_curses.mixin.block;

import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import com.kaleblangley.ring_of_the_hundred_curses.init.ModTag;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public class BlockMixin {
    
    private static final float HURT_SPEED_THRESHOLD = 0.003F;
    
    @Inject(method = "entityInside", at = @At("TAIL"))
    private void ring_of_the_hundred_curses$onEntityInside(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (entity instanceof Player player &&
            RingUtil.configAndRing(player, ModConfigManager.getConfig().enableHostileFlora) &&
            state.is(ModTag.HOSTILE_PLANTS) &&
            !level.isClientSide && 
            (entity.xOld != entity.getX() || entity.zOld != entity.getZ())) {
            double d0 = Math.abs(entity.getX() - entity.xOld);
            double d1 = Math.abs(entity.getZ() - entity.zOld);
            if (d0 >= HURT_SPEED_THRESHOLD || d1 >= HURT_SPEED_THRESHOLD) {
                float damage = ModConfigManager.getConfig().hostileFloraDamage;
                boolean wasHurt = entity.hurt(level.damageSources().sweetBerryBush(), damage);
                if (wasHurt) {
                    level.playSound(null, pos, SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH,
                        SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
                }
            }
        }
    }
} 