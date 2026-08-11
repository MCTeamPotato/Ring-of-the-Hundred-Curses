package com.kaleblangley.ring_of_the_hundred_curses.mirage;

import java.util.List;

/** A compact, palette-based structure shell shared by the server and client. */
public final class MirageTemplateData {
    private final long hash;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;
    private final List<Integer> palette;
    private final List<Block> blocks;

    public MirageTemplateData(int sizeX, int sizeY, int sizeZ, List<Integer> palette, List<Block> blocks) {
        this.sizeX = Math.max(1, sizeX);
        this.sizeY = Math.max(1, sizeY);
        this.sizeZ = Math.max(1, sizeZ);
        this.palette = List.copyOf(palette);
        this.blocks = List.copyOf(blocks);
        this.hash = calculateHash();
    }

    public long hash() {
        return hash;
    }

    public int sizeX() {
        return sizeX;
    }

    public int sizeY() {
        return sizeY;
    }

    public int sizeZ() {
        return sizeZ;
    }

    public List<Integer> palette() {
        return palette;
    }

    public List<Block> blocks() {
        return blocks;
    }

    private long calculateHash() {
        long value = 0xcbf29ce484222325L;
        value = mix(value, sizeX);
        value = mix(value, sizeY);
        value = mix(value, sizeZ);
        for (int stateId : palette) {
            value = mix(value, stateId);
        }
        for (Block block : blocks) {
            value = mix(value, block.x());
            value = mix(value, block.y());
            value = mix(value, block.z());
            value = mix(value, block.paletteIndex());
        }
        return value;
    }

    private static long mix(long value, long input) {
        value ^= input;
        value *= 0x100000001b3L;
        return Long.rotateLeft(value, 7);
    }

    public record Block(int x, int y, int z, int paletteIndex) {
    }
}
