package com.kaleblangley.ring_of_the_hundred_curses.network;

import com.kaleblangley.ring_of_the_hundred_curses.advancement.CurseAdvancementManager;
import com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Requests a server-side advancement for a curse checked exclusively on the client. */
public final class CurseAdvancementPacket {
    private final String curseId;

    public CurseAdvancementPacket(String curseId) {
        this.curseId = curseId;
    }

    public static void encode(CurseAdvancementPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.curseId, 64);
    }

    public static CurseAdvancementPacket decode(FriendlyByteBuf buffer) {
        return new CurseAdvancementPacket(buffer.readUtf(64));
    }

    public static void handle(CurseAdvancementPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer player = context.getSender();
        if (player != null) {
            context.enqueueWork(() -> {
                if (CurseAdvancementManager.isEnabled(packet.curseId)
                        && RingUtil.isEquipRing(player)
                        && CurseAdvancementManager.isKnownCurse(packet.curseId)) {
                    CurseAdvancementManager.award(player, packet.curseId);
                }
            });
        }
        context.setPacketHandled(true);
    }
}
