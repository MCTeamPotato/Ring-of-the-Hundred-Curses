package com.kaleblangley.ring_of_the_hundred_curses.client.advancement;

import com.kaleblangley.ring_of_the_hundred_curses.network.CurseAdvancementPacket;
import com.kaleblangley.ring_of_the_hundred_curses.network.ModNetwork;

import java.util.HashSet;
import java.util.Set;

/** Sends client-only curse checks to the server once per curse and connection. */
public final class CurseAdvancementClient {
    private static final Set<String> REQUESTED = new HashSet<>();

    private CurseAdvancementClient() {
    }

    public static void request(String curseId) {
        if (REQUESTED.add(curseId)) {
            ModNetwork.CHANNEL.sendToServer(new CurseAdvancementPacket(curseId));
        }
    }

    public static void reset() {
        REQUESTED.clear();
    }
}
