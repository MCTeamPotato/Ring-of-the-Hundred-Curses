package com.kaleblangley.ring_of_the_hundred_curses.mirage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.kaleblangley.ring_of_the_hundred_curses.config.ModConfigManager.getConfig;

public final class MirageTemplateExtractor {
    private MirageTemplateExtractor() {
    }

    public static Optional<MirageTemplateData> extractForStructure(
            ServerLevel level, ResourceLocation structureId, RandomSource random
    ) {
        List<MirageTemplateCatalog.TemplateGroup> groups = MirageTemplateCatalog.groupsFor(level, structureId);
        if (groups.isEmpty()) return Optional.empty();

        MirageTemplateCatalog.TemplateGroup group = groups.get(random.nextInt(groups.size()));
        List<ResourceLocation> templateIds = new ArrayList<>(group.templates());
        List<MirageTemplateData> pieces = new ArrayList<>();

        if (group.pieceCount() > 1 && !templateIds.isEmpty()) {
            ResourceLocation anchor = templateIds.remove(0);
            Optional<MirageTemplateData> anchorTemplate = extract(level, anchor, random);
            anchorTemplate.ifPresent(pieces::add);
            if (anchorTemplate.isEmpty()) templateIds.add(anchor);
        }

        shuffle(templateIds, random);
        for (ResourceLocation templateId : templateIds) {
            if (pieces.size() >= group.pieceCount()) break;
            Optional<MirageTemplateData> piece = extract(level, templateId, random);
            piece.ifPresent(pieces::add);
        }

        if (pieces.isEmpty()) return Optional.empty();
        if (pieces.size() == 1) return Optional.of(pieces.get(0));
        return combine(pieces, group.layout());
    }

    public static Optional<MirageTemplateData> extract(
            ServerLevel level, ResourceLocation templateId, RandomSource random
    ) {
        Optional<StructureTemplate> templateOptional = level.getStructureManager().get(templateId);
        if (templateOptional.isEmpty()) return Optional.empty();

        CompoundTag saved = templateOptional.get().save(new CompoundTag());
        ListTag palette = choosePalette(saved, random);
        if (palette.isEmpty()) return Optional.empty();

        int[] stateIds = new int[palette.size()];
        BlockState[] states = new BlockState[palette.size()];
        Map<Integer, Integer> localPalette = new HashMap<>();
        List<RawBlock> rawBlocks = new ArrayList<>();
        Set<BlockPos> occupied = new HashSet<>();

        for (int index = 0; index < palette.size(); index++) {
            CompoundTag stateTag = palette.getCompound(index);
            BlockState state = NbtUtils.readBlockState(
                    level.registryAccess().lookupOrThrow(Registries.BLOCK), stateTag
            );
            states[index] = state;
            stateIds[index] = net.minecraft.world.level.block.Block.getId(state);
        }

        if (!saved.contains(StructureTemplate.BLOCKS_TAG, Tag.TAG_LIST)) return Optional.empty();
        ListTag blocks = saved.getList(StructureTemplate.BLOCKS_TAG, Tag.TAG_COMPOUND);
        for (int index = 0; index < blocks.size(); index++) {
            CompoundTag blockTag = blocks.getCompound(index);
            int stateIndex = blockTag.getInt(StructureTemplate.BLOCK_TAG_STATE);
            if (stateIndex < 0 || stateIndex >= states.length) continue;

            ListTag positionTag = blockTag.getList(StructureTemplate.BLOCK_TAG_POS, Tag.TAG_INT);
            if (positionTag.size() < 3) continue;
            BlockPos position = new BlockPos(
                    positionTag.getInt(0), positionTag.getInt(1), positionTag.getInt(2)
            );
            BlockState state = states[stateIndex];
            if (state.isAir() || state.is(Blocks.STRUCTURE_VOID) || state.getBlock() instanceof EntityBlock) {
                continue;
            }

            int localIndex = localPalette.computeIfAbsent(
                    stateIds[stateIndex], ignored -> localPalette.size()
            );
            rawBlocks.add(new RawBlock(position, localIndex));
            occupied.add(position);
        }

        if (rawBlocks.isEmpty()) return Optional.empty();

        List<MirageTemplateData.Block> shell = new ArrayList<>();
        for (RawBlock raw : rawBlocks) {
            boolean exterior = false;
            for (Direction direction : Direction.values()) {
                if (!occupied.contains(raw.position.relative(direction))) {
                    exterior = true;
                    break;
                }
            }
            if (exterior) {
                shell.add(new MirageTemplateData.Block(
                        raw.position.getX(), raw.position.getY(), raw.position.getZ(), raw.paletteIndex
                ));
            }
        }
        if (shell.isEmpty()) {
            for (RawBlock raw : rawBlocks) {
                shell.add(new MirageTemplateData.Block(
                        raw.position.getX(), raw.position.getY(), raw.position.getZ(), raw.paletteIndex
                ));
            }
        }

        int maxBlocks = Math.max(1, getConfig().mirageMaxRenderBlocks);
        if (shell.size() > maxBlocks) {
            shell.sort(Comparator.comparingLong(block -> stableBlockOrder(block, saved.hashCode())));
            shell = new ArrayList<>(shell.subList(0, maxBlocks));
        }

        List<Integer> compactPalette = new ArrayList<>(localPalette.size());
        compactPalette.addAll(Collections.nCopies(localPalette.size(), 0));
        for (Map.Entry<Integer, Integer> entry : localPalette.entrySet()) {
            compactPalette.set(entry.getValue(), entry.getKey());
        }

        ListTag sizeTag = saved.getList(StructureTemplate.SIZE_TAG, Tag.TAG_INT);
        int sizeX = sizeTag.size() > 0 ? sizeTag.getInt(0) : 1;
        int sizeY = sizeTag.size() > 1 ? sizeTag.getInt(1) : 1;
        int sizeZ = sizeTag.size() > 2 ? sizeTag.getInt(2) : 1;
        return Optional.of(new MirageTemplateData(sizeX, sizeY, sizeZ, compactPalette, shell));
    }

    private static Optional<MirageTemplateData> combine(
            List<MirageTemplateData> pieces,
            MirageTemplateCatalog.TemplateGroup.Layout layout
    ) {
        int maxWidth = pieces.stream().mapToInt(MirageTemplateData::sizeX).max().orElse(1);
        int maxDepth = pieces.stream().mapToInt(MirageTemplateData::sizeZ).max().orElse(1);
        int spacingX = Math.max(12, maxWidth + 8);
        int spacingZ = Math.max(12, maxDepth + 8);
        int[][] slots = {
                {0, 0}, {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-1, -1}, {1, -1}, {-1, 1}, {1, 1}
        };

        List<PlacedPiece> placedPieces = new ArrayList<>();
        if (layout == MirageTemplateCatalog.TemplateGroup.Layout.VERTICAL) {
            int y = 0;
            for (MirageTemplateData piece : pieces) {
                placedPieces.add(new PlacedPiece(piece, 0, y, 0));
                y += piece.sizeY();
            }
        } else {
            for (int index = 0; index < pieces.size(); index++) {
                int[] slot = index < slots.length
                        ? slots[index]
                        : new int[]{(index % 3) - 1, (index / 3) - 1};
                placedPieces.add(new PlacedPiece(
                        pieces.get(index), slot[0] * spacingX, 0, slot[1] * spacingZ
                ));
            }
        }

        List<PlacedBlock> blocks = new ArrayList<>();
        for (PlacedPiece placed : placedPieces) {
            MirageTemplateData piece = placed.template;
            for (MirageTemplateData.Block block : piece.blocks()) {
                if (block.paletteIndex() < 0 || block.paletteIndex() >= piece.palette().size()) continue;
                blocks.add(new PlacedBlock(
                        placed.x + block.x(),
                        placed.y + block.y(),
                        placed.z + block.z(),
                        piece.palette().get(block.paletteIndex())
                ));
            }
        }
        if (blocks.isEmpty()) return Optional.empty();

        int maxBlocks = Math.max(1, getConfig().mirageMaxRenderBlocks);
        if (blocks.size() > maxBlocks) {
            blocks.sort(Comparator.comparingLong(MirageTemplateExtractor::stableBlockOrder));
            blocks = new ArrayList<>(blocks.subList(0, maxBlocks));
        }

        int minX = blocks.stream().mapToInt(PlacedBlock::x).min().orElse(0);
        int minY = blocks.stream().mapToInt(PlacedBlock::y).min().orElse(0);
        int minZ = blocks.stream().mapToInt(PlacedBlock::z).min().orElse(0);
        int maxX = blocks.stream().mapToInt(PlacedBlock::x).max().orElse(0);
        int maxY = blocks.stream().mapToInt(PlacedBlock::y).max().orElse(0);
        int maxZ = blocks.stream().mapToInt(PlacedBlock::z).max().orElse(0);

        Map<Integer, Integer> localPalette = new HashMap<>();
        List<Integer> palette = new ArrayList<>();
        List<MirageTemplateData.Block> output = new ArrayList<>(blocks.size());
        for (PlacedBlock block : blocks) {
            int paletteIndex = localPalette.computeIfAbsent(block.stateId, stateId -> {
                palette.add(stateId);
                return palette.size() - 1;
            });
            output.add(new MirageTemplateData.Block(
                    block.x - minX,
                    block.y - minY,
                    block.z - minZ,
                    paletteIndex
            ));
        }

        return Optional.of(new MirageTemplateData(
                maxX - minX + 1,
                maxY - minY + 1,
                maxZ - minZ + 1,
                palette,
                output
        ));
    }

    private static void shuffle(List<ResourceLocation> values, RandomSource random) {
        for (int index = values.size() - 1; index > 0; index--) {
            Collections.swap(values, index, random.nextInt(index + 1));
        }
    }

    private static ListTag choosePalette(CompoundTag saved, RandomSource random) {
        if (saved.contains(StructureTemplate.PALETTE_LIST_TAG, Tag.TAG_LIST)) {
            ListTag palettes = saved.getList(StructureTemplate.PALETTE_LIST_TAG, Tag.TAG_LIST);
            if (!palettes.isEmpty()) {
                return palettes.getList(random.nextInt(palettes.size()));
            }
        }
        if (saved.contains(StructureTemplate.PALETTE_TAG, Tag.TAG_LIST)) {
            return saved.getList(StructureTemplate.PALETTE_TAG, Tag.TAG_COMPOUND);
        }
        return new ListTag();
    }

    private static long stableBlockOrder(MirageTemplateData.Block block, int salt) {
        long value = salt;
        value = value * 31L + block.x();
        value = value * 31L + block.y();
        value = value * 31L + block.z();
        return value * 31L + block.paletteIndex();
    }

    private static long stableBlockOrder(PlacedBlock block) {
        long value = 0x9E3779B97F4A7C15L;
        value = value * 31L + block.x;
        value = value * 31L + block.y;
        value = value * 31L + block.z;
        return value * 31L + block.stateId;
    }

    private record RawBlock(BlockPos position, int paletteIndex) {
    }

    private record PlacedPiece(MirageTemplateData template, int x, int y, int z) {
    }

    private record PlacedBlock(int x, int y, int z, int stateId) {
    }
}
