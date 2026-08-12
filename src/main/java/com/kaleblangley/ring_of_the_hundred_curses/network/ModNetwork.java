package com.kaleblangley.ring_of_the_hundred_curses.network;

import com.kaleblangley.ring_of_the_hundred_curses.RingOfTheHundredCurses;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public final class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(RingOfTheHundredCurses.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int nextMessageId;

    private ModNetwork() {
    }

    public static void register() {
        CHANNEL.registerMessage(
                nextMessageId++,
                MirageTemplatePacket.class,
                MirageTemplatePacket::encode,
                MirageTemplatePacket::decode,
                MirageTemplatePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
        CHANNEL.registerMessage(
                nextMessageId++,
                MirageInstancePacket.class,
                MirageInstancePacket::encode,
                MirageInstancePacket::decode,
                MirageInstancePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
        CHANNEL.registerMessage(
                nextMessageId++,
                CurseAdvancementPacket.class,
                CurseAdvancementPacket::encode,
                CurseAdvancementPacket::decode,
                CurseAdvancementPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
    }
}
