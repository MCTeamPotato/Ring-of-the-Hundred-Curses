package com.kaleblangley.ring_of_the_hundred_curses.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * 当实体踩在方块上时触发
 * Fired when an entity steps on a block (from Block.stepOn)
 */
@Cancelable
public class StepOnBlockEvent extends Event {
    private final Level level;
    private final BlockPos pos;
    private final BlockState state;
    private final Entity entity;

    public StepOnBlockEvent(Level level, BlockPos pos, BlockState state, Entity entity) {
        this.level = level;
        this.pos = pos;
        this.state = state;
        this.entity = entity;
    }

    public Level getLevel() {
        return level;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockState getState() {
        return state;
    }

    public Entity getEntity() {
        return entity;
    }
}
