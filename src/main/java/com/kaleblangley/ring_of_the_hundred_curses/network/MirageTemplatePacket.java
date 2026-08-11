package com.kaleblangley.ring_of_the_hundred_curses.network;

import com.kaleblangley.ring_of_the_hundred_curses.mirage.MirageTemplateData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class MirageTemplatePacket {
    private static final int POSITION_BITS = 21;
    private static final long POSITION_MASK = (1L << POSITION_BITS) - 1L;

    private final MirageTemplateData template;

    public MirageTemplatePacket(MirageTemplateData template) {
        this.template = template;
    }

    public MirageTemplateData template() {
        return template;
    }

    public static void encode(MirageTemplatePacket packet, FriendlyByteBuf buffer) {
        buffer.writeLong(packet.template.hash());
        buffer.writeVarInt(packet.template.sizeX());
        buffer.writeVarInt(packet.template.sizeY());
        buffer.writeVarInt(packet.template.sizeZ());
        buffer.writeVarInt(packet.template.palette().size());
        for (int stateId : packet.template.palette()) {
            buffer.writeVarInt(stateId);
        }
        buffer.writeVarInt(packet.template.blocks().size());
        for (MirageTemplateData.Block block : packet.template.blocks()) {
            buffer.writeLong(packPosition(block.x(), block.y(), block.z()));
            buffer.writeVarInt(block.paletteIndex());
        }
    }

    public static MirageTemplatePacket decode(FriendlyByteBuf buffer) {
        long expectedHash = buffer.readLong();
        int sizeX = buffer.readVarInt();
        int sizeY = buffer.readVarInt();
        int sizeZ = buffer.readVarInt();

        int paletteSize = Math.min(buffer.readVarInt(), 16384);
        List<Integer> palette = new ArrayList<>(paletteSize);
        for (int index = 0; index < paletteSize; index++) {
            palette.add(buffer.readVarInt());
        }

        int blockCount = Math.min(buffer.readVarInt(), 65536);
        List<MirageTemplateData.Block> blocks = new ArrayList<>(blockCount);
        for (int index = 0; index < blockCount; index++) {
            long packedPosition = buffer.readLong();
            int paletteIndex = buffer.readVarInt();
            int x = unpackPosition(packedPosition, 0);
            int y = unpackPosition(packedPosition, 1);
            int z = unpackPosition(packedPosition, 2);
            if (paletteIndex >= 0 && paletteIndex < palette.size()) {
                blocks.add(new MirageTemplateData.Block(x, y, z, paletteIndex));
            }
        }

        MirageTemplateData template = new MirageTemplateData(sizeX, sizeY, sizeZ, palette, blocks);
        if (template.hash() != expectedHash) {
            return new MirageTemplatePacket(new MirageTemplateData(1, 1, 1, List.of(), List.of()));
        }
        return new MirageTemplatePacket(template);
    }

    public static void handle(MirageTemplatePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT,
                () -> () -> com.kaleblangley.ring_of_the_hundred_curses.client.mirage.MirageClientManager.receiveTemplate(packet.template)
        ));
        context.setPacketHandled(true);
    }

    private static long packPosition(int x, int y, int z) {
        return (x & POSITION_MASK)
                | ((long) (y & POSITION_MASK) << POSITION_BITS)
                | ((long) (z & POSITION_MASK) << (POSITION_BITS * 2));
    }

    private static int unpackPosition(long packed, int axis) {
        int value = (int) ((packed >> (POSITION_BITS * axis)) & POSITION_MASK);
        int signBit = 1 << (POSITION_BITS - 1);
        return (value & signBit) == 0 ? value : value | ~((int) POSITION_MASK);
    }
}
