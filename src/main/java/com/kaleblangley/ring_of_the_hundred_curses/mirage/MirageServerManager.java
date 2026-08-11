package com.kaleblangley.ring_of_the_hundred_curses.mirage;

import com.kaleblangley.ring_of_the_hundred_curses.network.MirageInstancePacket;
import com.kaleblangley.ring_of_the_hundred_curses.network.MirageTemplatePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;
import static com.kaleblangley.ring_of_the_hundred_curses.util.RingUtil.configAndRing;

public final class MirageServerManager {
    private static final Map<UUID, PlayerState> PLAYERS = new HashMap<>();
    private static long nextInstanceId = 1L;

    private MirageServerManager() {
    }

    public static void tick(ServerPlayer player) {
        if (player.isSpectator() || !configAndRing(player, getConfig().enableMirage)) {
            clear(player);
            return;
        }

        PlayerState state = PLAYERS.computeIfAbsent(player.getUUID(), ignored -> new PlayerState());
        Iterator<MirageInstance> iterator = state.instances.values().iterator();
        while (iterator.hasNext()) {
            MirageInstance instance = iterator.next();
            instance.remainingLifetime--;
            if (instance.dissolving) {
                instance.dissolveRemaining--;
                if (instance.dissolveRemaining <= 0) {
                    if (!repositionOrRemove(player, state, instance)) {
                        sendRemove(player, instance);
                        iterator.remove();
                    }
                }
                continue;
            }

            if (instance.remainingLifetime <= 0 || isPlayerApproaching(player, instance)) {
                beginDissolve(player, instance);
            }
        }

        int maxActiveInstances = Math.max(0, getConfig().mirageMaxActiveInstances);
        if (maxActiveInstances > 0 && state.instances.size() < maxActiveInstances) {
            int refreshInterval = state.instances.isEmpty()
                    ? Math.max(1, getConfig().mirageCheckInterval)
                    : Math.max(1, getConfig().mirageRefreshInterval);
            if (player.tickCount % refreshInterval == 0) {
                double chance = Math.max(0.0D, Math.min(1.0D, getConfig().mirageSpawnChance));
                if (player.getRandom().nextDouble() < chance) {
                    spawnMirage(player, state);
                }
            }
        }
    }

    public static void clear(ServerPlayer player) {
        PlayerState state = PLAYERS.remove(player.getUUID());
        if (state == null) return;
        for (MirageInstance instance : state.instances.values()) {
            sendRemove(player, instance);
        }
    }

    public static void forget(ServerPlayer player) {
        PLAYERS.remove(player.getUUID());
    }

    private static void spawnMirage(ServerPlayer player, PlayerState state) {
        Set<String> rejectedSources = new HashSet<>();
        for (int attempt = 0; attempt < 8; attempt++) {
            MirageSource source = findSource(player, state, rejectedSources);
            if (source == null && state.instances.isEmpty() && !state.usedSources.isEmpty()) {
                state.usedSources.clear();
                source = findSource(player, state, rejectedSources);
            }
            if (source == null) return;

            Vec2 direction = createWrongDirection(player, source.center, player.getRandom());
            double realDistance = Math.sqrt(source.center.distSqr(player.blockPosition()));
            double minDistance = Math.max(8.0D, getConfig().miragePlacementMinDistance);
            double maxDistance = Math.max(minDistance, getConfig().miragePlacementMaxDistance);
            double distance = Math.max(minDistance, Math.min(maxDistance, Math.max(realDistance, minDistance)));
            Rotation rotation = Rotation.getRandom(player.getRandom());
            BlockPos origin = calculateOrigin(player, source.center.getY(), source.template, rotation, direction, distance);

            if (tooCloseToExisting(state, origin, source.template, rotation)) {
                rejectedSources.add(source.sourceKey);
                continue;
            }

            MirageInstance instance = new MirageInstance(
                    nextInstanceId++,
                    source.template,
                    source.sourceKey,
                    source.center,
                    direction,
                    distance,
                    origin,
                    rotation,
                    Math.max(1, getConfig().mirageMaxDuration),
                    0
            );
            state.instances.put(instance.id, instance);
            state.usedSources.add(source.sourceKey);
            sendTemplateIfNeeded(player, state, source.template);
            sendInstance(player, instance);
            return;
        }
    }

    private static MirageSource findSource(ServerPlayer player, PlayerState state) {
        return findSource(player, state, Set.of());
    }

    private static MirageSource findSource(
            ServerPlayer player, PlayerState state, Set<String> rejectedSources
    ) {
        ServerLevel level = player.serverLevel();
        int radius = Math.max(16, (int) Math.ceil(getConfig().mirageSearchRadius));
        int chunkRadius = (radius + 15) >> 4;
        ChunkPos playerChunk = new ChunkPos(player.blockPosition());
        Set<String> seen = new HashSet<>();
        List<MirageSourceCandidate> candidates = new ArrayList<>();
        var structureRegistry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);

        for (int chunkX = playerChunk.x - chunkRadius; chunkX <= playerChunk.x + chunkRadius; chunkX++) {
            for (int chunkZ = playerChunk.z - chunkRadius; chunkZ <= playerChunk.z + chunkRadius; chunkZ++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
                if (chunk == null) continue;
                for (Map.Entry<Structure, StructureStart> entry : chunk.getAllStarts().entrySet()) {
                    StructureStart start = entry.getValue();
                    if (start == null || !start.isValid()) continue;
                    Structure structure = entry.getKey();
                    ResourceLocation structureId = structureRegistry.getKey(structure);
                    if (structureId == null || MirageTemplateCatalog.groupsFor(level, structureId).isEmpty()) continue;

                    String sourceKey = level.dimension().location() + "|" + structureId + "|" + start.getChunkPos().toLong();
                    if (!seen.add(sourceKey)
                            || state.usedSources.contains(sourceKey)
                            || rejectedSources.contains(sourceKey)) continue;
                    BlockPos center = start.getBoundingBox().getCenter();
                    double distanceSquared = center.distSqr(player.blockPosition());
                    if (distanceSquared > (double) radius * radius) continue;
                    candidates.add(new MirageSourceCandidate(sourceKey, structureId, center, distanceSquared));
                }
            }
        }

        if (candidates.isEmpty()) return null;
        candidates.sort((left, right) -> Double.compare(left.distanceSquared, right.distanceSquared));
        int randomOffset = player.getRandom().nextInt(candidates.size());
        for (int index = 0; index < candidates.size(); index++) {
            MirageSourceCandidate candidate = candidates.get((randomOffset + index) % candidates.size());
            var template = MirageTemplateExtractor.extractForStructure(
                    level, candidate.structureId, player.getRandom()
            );
            if (template.isPresent()) {
                return new MirageSource(
                        candidate.sourceKey,
                        candidate.center,
                        template.get()
                );
            }
        }
        return null;
    }

    private static boolean tooCloseToExisting(
            PlayerState state, BlockPos origin, MirageTemplateData template, Rotation rotation
    ) {
        double minimumSeparation = Math.max(0.0D, getConfig().mirageMinimumSeparation);
        double centerX = origin.getX() + rotatedWidth(template, rotation) * 0.5D;
        double centerZ = origin.getZ() + rotatedDepth(template, rotation) * 0.5D;
        double halfSize = Math.max(rotatedWidth(template, rotation), rotatedDepth(template, rotation)) * 0.5D;

        for (MirageInstance existing : state.instances.values()) {
            double existingCenterX = existing.origin.getX() + rotatedWidth(existing.template, existing.rotation) * 0.5D;
            double existingCenterZ = existing.origin.getZ() + rotatedDepth(existing.template, existing.rotation) * 0.5D;
            double existingHalfSize = Math.max(
                    rotatedWidth(existing.template, existing.rotation),
                    rotatedDepth(existing.template, existing.rotation)
            ) * 0.5D;
            double allowedDistance = minimumSeparation + halfSize + existingHalfSize;
            double deltaX = centerX - existingCenterX;
            double deltaZ = centerZ - existingCenterZ;
            if (deltaX * deltaX + deltaZ * deltaZ < allowedDistance * allowedDistance) {
                return true;
            }
        }
        return false;
    }

    private static void beginDissolve(ServerPlayer player, MirageInstance instance) {
        if (instance.dissolving) return;
        instance.dissolving = true;
        instance.dissolveRemaining = Math.max(
                1,
                Math.min(Math.max(1, getConfig().mirageDissolveDuration), Math.max(1, instance.remainingLifetime))
        );
        sendInstance(player, instance);
    }

    private static boolean repositionOrRemove(ServerPlayer player, PlayerState state, MirageInstance instance) {
        int maxRetreats = Math.max(0, getConfig().mirageMaxRetreats);
        if (instance.retreatCount >= maxRetreats || instance.remainingLifetime <= 0) return false;

        instance.retreatCount++;
        instance.distance += Math.max(8.0D, getConfig().mirageRetreatDistance);
        instance.origin = calculateOrigin(
                player,
                instance.realCenter.getY(),
                instance.template,
                instance.rotation,
                instance.wrongDirection,
                instance.distance
        );
        instance.dissolving = false;
        sendTemplateIfNeeded(player, state, instance.template);
        sendInstance(player, instance);
        return true;
    }

    private static boolean isPlayerApproaching(ServerPlayer player, MirageInstance instance) {
        int width = rotatedWidth(instance.template, instance.rotation);
        int depth = rotatedDepth(instance.template, instance.rotation);
        double radius = Math.max(1.0D, getConfig().mirageApproachDistance)
                + Math.max(width, depth) * 0.5D;
        return player.distanceToSqr(
                instance.origin.getX() + width * 0.5D,
                instance.origin.getY() + instance.template.sizeY() * 0.5D,
                instance.origin.getZ() + depth * 0.5D
        ) <= radius * radius;
    }

    private static BlockPos calculateOrigin(
            ServerPlayer player,
            int y,
            MirageTemplateData template,
            Rotation rotation,
            Vec2 direction,
            double distance
    ) {
        int centerX = (int) Math.floor(player.getX() + direction.x * distance);
        int centerZ = (int) Math.floor(player.getZ() + direction.z * distance);
        int width = rotatedWidth(template, rotation);
        int depth = rotatedDepth(template, rotation);
        return new BlockPos(centerX - width / 2, y, centerZ - depth / 2);
    }

    private static int rotatedWidth(MirageTemplateData template, Rotation rotation) {
        return rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90
                ? template.sizeZ() : template.sizeX();
    }

    private static int rotatedDepth(MirageTemplateData template, Rotation rotation) {
        return rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90
                ? template.sizeX() : template.sizeZ();
    }

    private static Vec2 createWrongDirection(ServerPlayer player, BlockPos center, RandomSource random) {
        double x = center.getX() + 0.5D - player.getX();
        double z = center.getZ() + 0.5D - player.getZ();
        double length = Math.sqrt(x * x + z * z);
        if (length < 0.001D) {
            x = player.getLookAngle().x;
            z = player.getLookAngle().z;
            length = Math.sqrt(x * x + z * z);
        }
        x /= length;
        z /= length;

        double minAngle = Math.max(0.0D, Math.min(89.0D, getConfig().mirageAngleMinDegrees));
        double maxAngle = Math.max(minAngle, Math.min(89.0D, getConfig().mirageAngleMaxDegrees));
        double angle = Math.toRadians(minAngle + random.nextDouble() * (maxAngle - minAngle));
        if (random.nextBoolean()) angle = -angle;
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vec2(x * cos - z * sin, x * sin + z * cos);
    }

    private static void sendTemplateIfNeeded(ServerPlayer player, PlayerState state, MirageTemplateData template) {
        if (state.sentTemplates.add(template.hash())) {
            com.kaleblangley.ring_of_the_hundred_curses.network.ModNetwork.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player), new MirageTemplatePacket(template)
            );
        }
    }

    private static void sendInstance(ServerPlayer player, MirageInstance instance) {
        com.kaleblangley.ring_of_the_hundred_curses.network.ModNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new MirageInstancePacket(
                        instance.id,
                        instance.template.hash(),
                        instance.origin,
                        instance.rotation.ordinal(),
                        Math.max(1, instance.dissolving ? instance.dissolveRemaining : instance.remainingLifetime),
                        instance.retreatCount,
                        instance.dissolving,
                        false
                )
        );
    }

    private static void sendRemove(ServerPlayer player, MirageInstance instance) {
        com.kaleblangley.ring_of_the_hundred_curses.network.ModNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new MirageInstancePacket(
                        instance.id,
                        instance.template.hash(),
                        instance.origin,
                        instance.rotation.ordinal(),
                        0,
                        instance.retreatCount,
                        false,
                        true
                )
        );
    }

    private static final class PlayerState {
        private final Map<Long, MirageInstance> instances = new HashMap<>();
        private final Set<String> usedSources = new HashSet<>();
        private final Set<Long> sentTemplates = new HashSet<>();
    }

    private static final class MirageInstance {
        private final long id;
        private final MirageTemplateData template;
        private final String sourceKey;
        private final BlockPos realCenter;
        private final Vec2 wrongDirection;
        private double distance;
        private BlockPos origin;
        private Rotation rotation;
        private int remainingLifetime;
        private int dissolveRemaining;
        private int retreatCount;
        private boolean dissolving;

        private MirageInstance(
                long id,
                MirageTemplateData template,
                String sourceKey,
                BlockPos realCenter,
                Vec2 wrongDirection,
                double distance,
                BlockPos origin,
                Rotation rotation,
                int remainingLifetime,
                int retreatCount
        ) {
            this.id = id;
            this.template = template;
            this.sourceKey = sourceKey;
            this.realCenter = realCenter;
            this.wrongDirection = wrongDirection;
            this.distance = distance;
            this.origin = origin;
            this.rotation = rotation;
            this.remainingLifetime = remainingLifetime;
            this.retreatCount = retreatCount;
        }
    }

    private record MirageSource(String sourceKey, BlockPos center, MirageTemplateData template) {
    }

    private record MirageSourceCandidate(
            String sourceKey, ResourceLocation structureId, BlockPos center, double distanceSquared
    ) {
    }

    private record Vec2(double x, double z) {
    }
}
