package com.kaleblangley.ring_of_the_hundred_curses.api.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.eventbus.api.Event;

/**
 * 当雷雨天区块 tick 时，且该区块内有露天玩家时触发。
 * 由 ServerLevelMixin 从 tickChunk 中发射。
 */
public class ChunkThunderEvent extends Event {
    private final ServerLevel level;
    private final LevelChunk chunk;
    private final Player player;

    public ChunkThunderEvent(ServerLevel level, LevelChunk chunk, Player player) {
        this.level = level;
        this.chunk = chunk;
        this.player = player;
    }

    public ServerLevel getLevel() {
        return level;
    }

    public LevelChunk getChunk() {
        return chunk;
    }

    public Player getPlayer() {
        return player;
    }
}
