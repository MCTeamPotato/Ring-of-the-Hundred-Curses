package com.kaleblangley.ring_of_the_hundred_curses.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class MirageInstancePacket {
    private final long instanceId;
    private final long templateHash;
    private final BlockPos origin;
    private final int rotation;
    private final int duration;
    private final int retreatCount;
    private final boolean dissolving;
    private final boolean remove;

    public MirageInstancePacket(
            long instanceId,
            long templateHash,
            BlockPos origin,
            int rotation,
            int duration,
            int retreatCount,
            boolean dissolving,
            boolean remove
    ) {
        this.instanceId = instanceId;
        this.templateHash = templateHash;
        this.origin = origin;
        this.rotation = rotation;
        this.duration = duration;
        this.retreatCount = retreatCount;
        this.dissolving = dissolving;
        this.remove = remove;
    }

    public long instanceId() {
        return instanceId;
    }

    public long templateHash() {
        return templateHash;
    }

    public BlockPos origin() {
        return origin;
    }

    public int rotation() {
        return rotation;
    }

    public int duration() {
        return duration;
    }

    public int retreatCount() {
        return retreatCount;
    }

    public boolean dissolving() {
        return dissolving;
    }

    public boolean remove() {
        return remove;
    }

    public static void encode(MirageInstancePacket packet, FriendlyByteBuf buffer) {
        buffer.writeLong(packet.instanceId);
        buffer.writeLong(packet.templateHash);
        buffer.writeBlockPos(packet.origin);
        buffer.writeVarInt(packet.rotation);
        buffer.writeVarInt(packet.duration);
        buffer.writeVarInt(packet.retreatCount);
        buffer.writeBoolean(packet.dissolving);
        buffer.writeBoolean(packet.remove);
    }

    public static MirageInstancePacket decode(FriendlyByteBuf buffer) {
        return new MirageInstancePacket(
                buffer.readLong(),
                buffer.readLong(),
                buffer.readBlockPos(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readBoolean(),
                buffer.readBoolean()
        );
    }

    public static void handle(MirageInstancePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT,
                () -> () -> com.kaleblangley.ring_of_the_hundred_curses.client.mirage.MirageClientManager.receiveInstance(packet)
        ));
        context.setPacketHandled(true);
    }
}
