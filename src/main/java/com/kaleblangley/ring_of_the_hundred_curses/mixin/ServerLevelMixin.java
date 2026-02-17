package com.kaleblangley.ring_of_the_hundred_curses.mixin;

import com.kaleblangley.ring_of_the_hundred_curses.api.event.ChunkThunderEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    @Inject(method = "tickChunk", at = @At("TAIL"))
    private void ring_of_the_hundred_curses$fireChunkThunderEvent(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        ServerLevel level = (ServerLevel) (Object) this;
        if (!level.isThundering()) return;
        ChunkPos cp = chunk.getPos();
        for (ServerPlayer player : level.players()) {
            if (player.chunkPosition().x == cp.x && player.chunkPosition().z == cp.z
                    && level.canSeeSky(player.blockPosition())) {
                MinecraftForge.EVENT_BUS.post(new ChunkThunderEvent(level, chunk, player));
            }
        }
    }
}
