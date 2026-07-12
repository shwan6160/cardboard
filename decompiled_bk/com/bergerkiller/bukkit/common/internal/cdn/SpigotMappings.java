/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.cdn;

import com.bergerkiller.bukkit.common.internal.cdn.MojangIO;
import com.bergerkiller.bukkit.common.internal.cdn.MojangMappings;
import com.bergerkiller.bukkit.common.io.VersionedMappingsFileIO;
import com.bergerkiller.bukkit.common.server.CraftBukkitServer;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.mountiplex.logic.TextValueSequence;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SpigotMappings
extends VersionedMappingsFileIO<ClassMappings> {
    public SpigotMappings() {
        super(TextValueSequence.STRING_COMPARATOR, x$0 -> new ClassMappings((VersionedMappingsFileIO.MappedVersion)x$0));
    }

    public boolean assertMappings(String ... versions) throws IOException {
        boolean changed = false;
        for (String version : versions) {
            if (this.byVersion.containsKey(version)) continue;
            this.downloadMappings(null, version);
            changed = true;
        }
        return changed;
    }

    public void downloadMappings(MojangMappings mojangMappings, String version) throws IOException {
        if (mojangMappings == null) {
            mojangMappings = MojangMappings.fromCacheOrDownload(version);
        }
        SpigotVersionMeta versionMeta = MojangIO.downloadJson(SpigotVersionMeta.class, "https://hub.spigotmc.org/versions/" + version + ".json");
        if (versionMeta.refs == null || versionMeta.refs.BuildData == null) {
            throw new IOException("No BuildData ref in response to /versions");
        }
        File mappingsFile = File.createTempFile("bukkit-" + version + "-cl", ".csrg");
        MojangIO.VersionManifest.VersionAssets.Download download = new MojangIO.VersionManifest.VersionAssets.Download();
        download.sha1 = null;
        download.size = 0L;
        download.url = "https://hub.spigotmc.org/stash/projects/SPIGOT/repos/builddata/raw/mappings/bukkit-" + version + "-cl.csrg?at=" + versionMeta.refs.BuildData;
        MojangIO.downloadFile("Minecraft - Bukkit " + version + " class mappings", download, mappingsFile);
        Pattern classNamePattern = Pattern.compile("([\\w\\._\\-/$]+)\\s+([\\w\\._\\-/$]+)");
        HashMap<String, String> obfuscatedToBukkit = new HashMap<String, String>();
        try (BufferedReader br = new BufferedReader(new FileReader(mappingsFile));){
            String line;
            while ((line = br.readLine()) != null) {
                Matcher m = classNamePattern.matcher(line);
                if (!m.matches()) continue;
                String obfuscatedName = m.group(1).replace('/', '.');
                String bukkitFullName = m.group(2).replace('/', '.');
                obfuscatedToBukkit.put(obfuscatedName, bukkitFullName);
            }
        }
        HashMap<String, String> newMappings = new HashMap<String, String>();
        for (MojangMappings.ClassMappings cl : mojangMappings.classes()) {
            String bukkitFullName = (String)obfuscatedToBukkit.get(cl.name_obfuscated);
            if (bukkitFullName == null) {
                int subClassIndex = cl.name_obfuscated.length();
                while ((subClassIndex = cl.name_obfuscated.lastIndexOf(36, subClassIndex - 1)) != -1) {
                    bukkitFullName = (String)obfuscatedToBukkit.get(cl.name_obfuscated.substring(0, subClassIndex));
                    if (bukkitFullName == null) continue;
                    bukkitFullName = bukkitFullName + cl.name_obfuscated.substring(subClassIndex);
                    break;
                }
            }
            if (bukkitFullName == null) continue;
            newMappings.put(cl.name, bukkitFullName);
        }
        this.store(version, newMappings);
        mappingsFile.delete();
    }

    public static ClassMappings forVersion(String minecraftVersion) {
        SpigotMappings spigotMappings = new SpigotMappings();
        String classMappingsFile = "/com/bergerkiller/bukkit/common/internal/resources/class_mappings.dat";
        try (InputStream in = CraftBukkitServer.class.getResourceAsStream(classMappingsFile);){
            spigotMappings.read(in);
        }
        catch (IOException ex) {
            throw new IllegalStateException("Failed to read class mappings (corrupted BKCommonLib jar?)");
        }
        return (ClassMappings)spigotMappings.getOrOlder(minecraftVersion).orElseThrow(() -> new IllegalStateException("Version " + minecraftVersion + " has no mappings"));
    }

    public void visualizeMapping(String searchQuery) {
        Set mojangMatches = this.getAll().stream().flatMap(m -> m.getMojangToSpigot().keySet().stream()).filter(e -> e.contains(searchQuery)).collect(Collectors.toSet());
        Set spigotMatches = this.getAll().stream().flatMap(m -> m.getSpigotToMojang().keySet().stream()).filter(e -> e.contains(searchQuery)).collect(Collectors.toSet());
        System.out.println("All mapping details for search query: " + searchQuery);
        for (String mojangClassName : mojangMatches) {
            this.visualizeMappingsForMojangClass(mojangClassName);
        }
        for (String spigotClassName : spigotMatches) {
            this.visualizeMappingsForSpigotClass(spigotClassName);
        }
    }

    public void visualizeMappingsForMojangClass(String mojangClassName) {
        boolean first = true;
        String prev = null;
        String startVersion = null;
        String endVersion = null;
        for (String version : this.getVersions()) {
            String spigotName = ((ClassMappings)this.get(version).orElseThrow(() -> new UnsupportedOperationException("Version bug: " + version))).getMojangToSpigot().get(mojangClassName);
            if (startVersion == null) {
                startVersion = version;
                endVersion = version;
            }
            if (!first && !LogicUtil.bothNullOrEqual(prev, spigotName)) {
                System.out.println("[" + startVersion + " - " + endVersion + "] " + mojangClassName + " -> " + prev);
                startVersion = version;
                endVersion = version;
            }
            endVersion = version;
            prev = spigotName;
            first = false;
        }
        if (startVersion != null) {
            System.out.println("[" + startVersion + " - " + endVersion + "] " + mojangClassName + " -> " + prev);
        }
    }

    public void visualizeMappingsForSpigotClass(String spigotClassName) {
        boolean first = true;
        Set<String> prev = null;
        String startVersion = null;
        String endVersion = null;
        for (String version : this.getVersions()) {
            Set<String> mojangNames = ((ClassMappings)this.get(version).orElseThrow(() -> new UnsupportedOperationException("Version bug: " + version))).getSpigotToMojang().get(spigotClassName);
            if (startVersion == null) {
                startVersion = version;
                endVersion = version;
            }
            if (!first && !LogicUtil.bothNullOrEqual(prev, mojangNames)) {
                if (prev != null) {
                    System.out.println("[" + startVersion + " - " + endVersion + "]:");
                    for (String mojangName : prev) {
                        System.out.println("  - " + mojangName + " -> " + spigotClassName);
                    }
                } else {
                    System.out.println("[" + startVersion + " - " + endVersion + "]: None");
                }
                startVersion = version;
                endVersion = version;
            }
            endVersion = version;
            prev = mojangNames;
            first = false;
        }
        if (startVersion != null) {
            if (prev != null) {
                System.out.println("[" + startVersion + " - " + endVersion + "]:");
                for (String mojangName : prev) {
                    System.out.println("  - " + mojangName + " -> " + spigotClassName);
                }
            } else {
                System.out.println("[" + startVersion + " - " + endVersion + "]: None");
            }
        }
    }

    private static class SpigotVersionMeta {
        public Refs refs;

        private SpigotVersionMeta() {
        }

        private static class Refs {
            public String BuildData;

            private Refs() {
            }
        }
    }

    public static class ClassMappings {
        private final Map<String, String> mojangToSpigot;
        private Map<String, Set<String>> cachedSpigotToMojang;

        private ClassMappings(VersionedMappingsFileIO.MappedVersion<ClassMappings> mappedVersion) {
            this(mappedVersion.mappings);
        }

        public ClassMappings(Map<String, String> mojangToSpigot) {
            this.mojangToSpigot = mojangToSpigot;
            this.cachedSpigotToMojang = null;
        }

        public Map<String, String> getMojangToSpigot() {
            return this.mojangToSpigot;
        }

        public Map<String, Set<String>> getSpigotToMojang() {
            if (this.cachedSpigotToMojang == null) {
                this.cachedSpigotToMojang = new HashMap<String, Set<String>>(this.mojangToSpigot.size());
                for (Map.Entry<String, String> entry : this.mojangToSpigot.entrySet()) {
                    this.cachedSpigotToMojang.compute(entry.getValue(), (k, oldValues) -> {
                        if (oldValues == null) {
                            return Collections.singleton((String)entry.getKey());
                        }
                        Set newValues = oldValues.size() == 1 ? new HashSet(oldValues) : oldValues;
                        newValues.add((String)entry.getKey());
                        return newValues;
                    });
                }
            }
            return this.cachedSpigotToMojang;
        }

        public void put(String mojangClassName, String spigotClassName) {
            this.mojangToSpigot.put(mojangClassName, spigotClassName);
            this.cachedSpigotToMojang = null;
        }

        public void remapMojangSubClass(String mojangClassName, String subClassName) {
            this.put(mojangClassName + "$" + subClassName, this.toSpigot(mojangClassName) + "$" + subClassName);
        }

        public String toSpigot(String mojangClassName) {
            return this.mojangToSpigot.getOrDefault(mojangClassName, mojangClassName);
        }

        public Set<String> toMojang(String spigotClassName) {
            return this.getSpigotToMojang().getOrDefault(spigotClassName, Collections.singleton(spigotClassName));
        }
    }
}

