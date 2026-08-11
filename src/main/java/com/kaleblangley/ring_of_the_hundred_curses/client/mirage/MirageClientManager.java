package com.kaleblangley.ring_of_the_hundred_curses.client.mirage;

import com.mojang.blaze3d.vertex.PoseStack;
import com.kaleblangley.ring_of_the_hundred_curses.mirage.MirageTemplateData;
import com.kaleblangley.ring_of_the_hundred_curses.network.MirageInstancePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.RenderLevelStageEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

public final class MirageClientManager {
    private static final Map<Long, MirageTemplateData> TEMPLATES = new HashMap<>();
    private static final Map<Long, ClientInstance> INSTANCES = new HashMap<>();
    private static ClientLevel activeLevel;

    private MirageClientManager() {
    }

    public static void receiveTemplate(MirageTemplateData template) {
        if (template == null || template.palette().isEmpty() || template.blocks().isEmpty()) return;
        TEMPLATES.put(template.hash(), template);
    }

    public static void receiveInstance(MirageInstancePacket packet) {
        if (packet.remove()) {
            INSTANCES.remove(packet.instanceId());
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;
        long now = minecraft.level.getGameTime();
        INSTANCES.put(packet.instanceId(), new ClientInstance(
                packet.instanceId(),
                packet.templateHash(),
                packet.origin(),
                Math.floorMod(packet.rotation(), 4),
                packet.retreatCount(),
                packet.dissolving(),
                Math.max(1, packet.duration()),
                now + Math.max(1, packet.duration())
        ));
    }

    public static void tick() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            clear();
            return;
        }
        if (activeLevel != null && activeLevel != minecraft.level) {
            clear();
        }
        activeLevel = minecraft.level;

        long now = minecraft.level.getGameTime();
        Iterator<ClientInstance> iterator = INSTANCES.values().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().endTick <= now) iterator.remove();
        }
    }

    public static void clear() {
        INSTANCES.clear();
        TEMPLATES.clear();
        activeLevel = null;
    }

    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null || INSTANCES.isEmpty()) return;

        Camera camera = event.getCamera();
        double renderDistance = Math.max(32.0D, getConfig().mirageRenderDistance);
        double renderDistanceSquared = renderDistance * renderDistance;
        long now = minecraft.level.getGameTime();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();
        boolean rendered = false;

        for (ClientInstance instance : INSTANCES.values()) {
            MirageTemplateData template = TEMPLATES.get(instance.templateHash);
            if (template == null) continue;

            double centerX = instance.origin.getX() + rotatedWidth(template, instance.rotation) * 0.5D;
            double centerY = instance.origin.getY() + template.sizeY() * 0.5D;
            double centerZ = instance.origin.getZ() + rotatedDepth(template, instance.rotation) * 0.5D;
            double distanceSquared = minecraft.player.distanceToSqr(centerX, centerY, centerZ);
            if (distanceSquared > renderDistanceSquared) continue;

            double visibility = 1.0D;
            if (instance.dissolving) {
                visibility = Math.max(0.0D, Math.min(1.0D,
                        (double) (instance.endTick - now) / instance.phaseDuration));
            }

            poseStack.pushPose();
            var cameraPosition = camera.getPosition();
            poseStack.translate(
                    instance.origin.getX() - cameraPosition.x,
                    instance.origin.getY() - cameraPosition.y,
                    instance.origin.getZ() - cameraPosition.z
            );

            for (int index = 0; index < template.blocks().size(); index++) {
                MirageTemplateData.Block block = template.blocks().get(index);
                if (instance.dissolving && blockVisibility(instance.id, index) > visibility) continue;

                if (block.paletteIndex() < 0 || block.paletteIndex() >= template.palette().size()) continue;
                BlockState state = Block.stateById(template.palette().get(block.paletteIndex()));
                if (state.isAir()) continue;

                BlockPos rotated = rotate(block, template, instance.rotation);
                poseStack.pushPose();
                poseStack.translate(rotated.getX(), rotated.getY(), rotated.getZ());
                blockRenderer.renderSingleBlock(
                        state,
                        poseStack,
                        bufferSource,
                        LightTexture.FULL_BRIGHT,
                        OverlayTexture.NO_OVERLAY
                );
                poseStack.popPose();
                rendered = true;
            }
            poseStack.popPose();
        }

        if (rendered) bufferSource.endBatch();
    }

    private static BlockPos rotate(MirageTemplateData.Block block, MirageTemplateData template, int rotation) {
        return switch (rotation) {
            case 1 -> new BlockPos(template.sizeZ() - 1 - block.z(), block.y(), block.x());
            case 2 -> new BlockPos(template.sizeX() - 1 - block.x(), block.y(), template.sizeZ() - 1 - block.z());
            case 3 -> new BlockPos(block.z(), block.y(), template.sizeX() - 1 - block.x());
            default -> new BlockPos(block.x(), block.y(), block.z());
        };
    }

    private static int rotatedWidth(MirageTemplateData template, int rotation) {
        return rotation == 1 || rotation == 3 ? template.sizeZ() : template.sizeX();
    }

    private static int rotatedDepth(MirageTemplateData template, int rotation) {
        return rotation == 1 || rotation == 3 ? template.sizeX() : template.sizeZ();
    }

    private static double blockVisibility(long instanceId, int index) {
        long value = instanceId * 0x9E3779B97F4A7C15L + index * 0xBF58476D1CE4E5B9L;
        value ^= value >>> 30;
        value *= 0xBF58476D1CE4E5B9L;
        value ^= value >>> 27;
        return (value & Long.MAX_VALUE) / (double) Long.MAX_VALUE;
    }

    private static final class ClientInstance {
        private final long id;
        private final long templateHash;
        private final BlockPos origin;
        private final int rotation;
        private final int retreatCount;
        private final boolean dissolving;
        private final int phaseDuration;
        private final long endTick;

        private ClientInstance(
                long id,
                long templateHash,
                BlockPos origin,
                int rotation,
                int retreatCount,
                boolean dissolving,
                int phaseDuration,
                long endTick
        ) {
            this.id = id;
            this.templateHash = templateHash;
            this.origin = origin;
            this.rotation = rotation;
            this.retreatCount = retreatCount;
            this.dissolving = dissolving;
            this.phaseDuration = phaseDuration;
            this.endTick = endTick;
        }
    }
}
