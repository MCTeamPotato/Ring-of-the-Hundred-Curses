package com.kaleblangley.ring_of_the_hundred_curses.mirage;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/** Discovers structure templates from the active server registries and resource packs. */
public final class MirageTemplateCatalog {
    private static final Map<ServerLevel, Map<ResourceLocation, List<TemplateGroup>>> CATALOGS = new WeakHashMap<>();

    private MirageTemplateCatalog() {
    }

    public static List<TemplateGroup> groupsFor(ServerLevel level, ResourceLocation structureId) {
        Map<ResourceLocation, List<TemplateGroup>> catalog = CATALOGS.computeIfAbsent(
                level,
                MirageTemplateCatalog::scan
        );
        return catalog.getOrDefault(structureId, List.of());
    }

    public static List<ResourceLocation> templatesFor(ServerLevel level, ResourceLocation structureId) {
        List<ResourceLocation> templates = new ArrayList<>();
        for (TemplateGroup group : groupsFor(level, structureId)) {
            templates.addAll(group.templates());
        }
        return List.copyOf(templates);
    }

    private static Map<ResourceLocation, List<TemplateGroup>> scan(ServerLevel level) {
        List<ResourceLocation> templates = scanTemplateResources(level);
        Map<ResourceLocation, List<TemplateGroup>> catalog = new HashMap<>();
        Registry<Structure> structureRegistry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);

        for (ResourceLocation structureId : structureRegistry.keySet()) {
            List<ScoredTemplate> candidates = new ArrayList<>();
            for (ResourceLocation templateId : templates) {
                int score = matchScore(structureId, templateId);
                if (score > 0) {
                    candidates.add(new ScoredTemplate(templateId, score));
                }
            }
            if (candidates.isEmpty()) continue;

            candidates.sort(Comparator
                    .comparingInt(ScoredTemplate::score)
                    .reversed()
                    .thenComparing(candidate -> candidate.templateId().toString()));
            List<ResourceLocation> selected = selectTemplates(candidates);
            if (!selected.isEmpty()) {
                catalog.put(structureId, List.of(createGroup(selected)));
            }
        }
        return catalog;
    }

    private static List<ResourceLocation> scanTemplateResources(ServerLevel level) {
        Map<ResourceLocation, ?> resources = level.getServer().getResourceManager().listResources(
                "structures",
                resourceId -> resourceId.getPath().endsWith(".nbt")
        );
        List<ResourceLocation> templates = new ArrayList<>();
        for (ResourceLocation resourceId : resources.keySet()) {
            String path = resourceId.getPath();
            if (path.startsWith("structures/")) {
                path = path.substring("structures/".length());
            }
            if (!path.endsWith(".nbt")) continue;
            path = path.substring(0, path.length() - ".nbt".length());
            ResourceLocation templateId = ResourceLocation.tryParse(resourceId.getNamespace() + ":" + path);
            if (templateId != null && !isAuxiliary(templateId)) {
                templates.add(templateId);
            }
        }
        templates.sort(Comparator.comparing(ResourceLocation::toString));
        return templates;
    }

    private static boolean isAuxiliary(ResourceLocation templateId) {
        String path = templateId.getPath();
        return path.contains("/zombie/")
                || path.contains("/mobs/")
                || path.contains("/animals/")
                || path.contains("/villagers/")
                || path.contains("/blocks/");
    }

    private static int matchScore(ResourceLocation structureId, ResourceLocation templateId) {
        if (!structureId.getNamespace().equals(templateId.getNamespace())) return -1;

        List<String> structureTokens = tokens(structureId.getPath());
        List<String> templateTokens = tokens(templateId.getPath());
        int matched = 0;
        for (String structureToken : structureTokens) {
            if (templateTokens.stream().anyMatch(templateToken -> tokenMatches(structureToken, templateToken))) {
                matched++;
            }
        }
        if (matched == 0) return -1;

        int score = matched * 100;
        if (!structureTokens.isEmpty()
                && !templateTokens.isEmpty()
                && tokenMatches(structureTokens.get(0), templateTokens.get(0))) {
            score += 50;
        }
        return score;
    }

    private static List<ResourceLocation> selectTemplates(List<ScoredTemplate> candidates) {
        List<ResourceLocation> complete = candidates.stream()
                .map(ScoredTemplate::templateId)
                .filter(MirageTemplateCatalog::looksComplete)
                .toList();
        if (!complete.isEmpty()) return complete;

        List<ResourceLocation> vertical = candidates.stream()
                .map(ScoredTemplate::templateId)
                .filter(template -> hasFileName(template, "bottom")
                        || hasFileName(template, "middle")
                        || hasFileName(template, "top"))
                .toList();
        if (vertical.size() >= 3
                && vertical.stream().anyMatch(template -> hasFileName(template, "bottom"))
                && vertical.stream().anyMatch(template -> hasFileName(template, "middle"))
                && vertical.stream().anyMatch(template -> hasFileName(template, "top"))) {
            return orderVertical(vertical);
        }

        return candidates.stream()
                .map(ScoredTemplate::templateId)
                .limit(64)
                .toList();
    }

    private static TemplateGroup createGroup(List<ResourceLocation> templates) {
        boolean vertical = templates.size() >= 3
                && hasFileName(templates.get(0), "bottom")
                && hasFileName(templates.get(1), "middle")
                && hasFileName(templates.get(2), "top");
        int pieceCount = templates.size() == 1
                ? 1
                : Math.min(6, templates.size());
        return new TemplateGroup(
                templates,
                pieceCount,
                vertical ? TemplateGroup.Layout.VERTICAL : TemplateGroup.Layout.CLUSTER
        );
    }

    private static List<ResourceLocation> orderVertical(List<ResourceLocation> templates) {
        return templates.stream()
                .sorted(Comparator.comparingInt(template -> {
                    if (hasFileName(template, "bottom")) return 0;
                    if (hasFileName(template, "middle")) return 1;
                    if (hasFileName(template, "top")) return 2;
                    return 3;
                }))
                .toList();
    }

    private static boolean looksComplete(ResourceLocation templateId) {
        String fileName = fileName(templateId);
        return fileName.contains("full")
                || fileName.contains("with_mast")
                || fileName.matches("portal_[0-9]+")
                || fileName.matches("fossil_[0-9]+");
    }

    private static boolean hasFileName(ResourceLocation templateId, String expected) {
        return fileName(templateId).equals(expected);
    }

    private static String fileName(ResourceLocation templateId) {
        String path = templateId.getPath();
        int slash = path.lastIndexOf('/');
        return slash >= 0 ? path.substring(slash + 1) : path;
    }

    private static List<String> tokens(String path) {
        return Arrays.stream(path.toLowerCase().split("[_/.-]+"))
                .filter(token -> !token.isBlank())
                .toList();
    }

    private static boolean tokenMatches(String left, String right) {
        return left.equals(right) || left.startsWith(right) || right.startsWith(left);
    }

    private record ScoredTemplate(ResourceLocation templateId, int score) {
    }

    public record TemplateGroup(List<ResourceLocation> templates, int pieceCount, Layout layout) {
        public TemplateGroup {
            templates = List.copyOf(templates);
            pieceCount = Math.max(1, pieceCount);
        }

        public enum Layout {
            CLUSTER,
            VERTICAL
        }
    }
}
