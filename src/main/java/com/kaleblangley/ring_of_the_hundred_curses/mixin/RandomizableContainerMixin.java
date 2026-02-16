package com.kaleblangley.ring_of_the_hundred_curses.mixin;

import com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(RandomizableContainerBlockEntity.class)
public class RandomizableContainerMixin {

    @Shadow
    @Nullable
    protected ResourceLocation lootTable;

    @Inject(method = "unpackLootTable", at = @At("HEAD"))
    private void ring_of_the_hundred_curses$incompetentThief(@Nullable Player pPlayer, CallbackInfo ci) {
        if (pPlayer == null) return;
        if (pPlayer.level().isClientSide) return;
        if (this.lootTable == null) return;
        if (!RingUtil.configAndRing(pPlayer, ModConfigManager.getConfig().enableIncompetentThief)) return;

        String lootTablePath = this.lootTable.getPath();
        Level level = pPlayer.level();
        RandomizableContainerBlockEntity self = (RandomizableContainerBlockEntity) (Object) this;
        BlockPos pos = self.getBlockPos();

        EntityType<? extends Mob> mobType = null;

        if (lootTablePath.contains("pillager_outpost")) {
            mobType = EntityType.PILLAGER;
        } else if (lootTablePath.contains("village")) {
            mobType = EntityType.VILLAGER;
        }

        if (mobType != null) {
            int min = ModConfigManager.getConfig().incompetentThiefMinSpawn;
            int max = ModConfigManager.getConfig().incompetentThiefMaxSpawn;
            if (min > max) min = max;
            int count = min + level.random.nextInt(Math.max(1, max - min + 1));
            ServerLevel serverLevel = (ServerLevel) level;
            DifficultyInstance difficulty = serverLevel.getCurrentDifficultyAt(pos);

            for (int i = 0; i < count; i++) {
                Mob mob = mobType.create(level);
                if (mob != null) {
                    double offsetX = (level.random.nextDouble() - 0.5) * 4.0;
                    double offsetZ = (level.random.nextDouble() - 0.5) * 4.0;
                    mob.setPos(pos.getX() + 0.5 + offsetX, pos.getY(), pos.getZ() + 0.5 + offsetZ);
                    ForgeEventFactory.onFinalizeSpawn(mob, serverLevel, difficulty, MobSpawnType.EVENT, null, null);
                    level.addFreshEntity(mob);
                }
            }
        }
    }
}
