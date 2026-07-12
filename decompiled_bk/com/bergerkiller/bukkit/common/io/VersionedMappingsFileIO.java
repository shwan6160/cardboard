/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class VersionedMappingsFileIO<T> {
    public final NavigableMap<String, MappedVersion<T>> byVersion;
    private final Function<MappedVersion<T>, T> mappingLoader;

    public VersionedMappingsFileIO(Comparator<String> versionComparator, Function<MappedVersion<T>, T> mappingLoader) {
        this.byVersion = new TreeMap<String, MappedVersion<T>>(versionComparator);
        this.mappingLoader = mappingLoader;
    }

    public Set<String> getVersions() {
        return this.byVersion.keySet();
    }

    public Optional<T> get(String version) {
        MappedVersion mapped = (MappedVersion)this.byVersion.get(version);
        return mapped == null ? Optional.empty() : Optional.of(mapped.data());
    }

    public List<T> getAll() {
        ArrayList result = new ArrayList(this.byVersion.size());
        for (MappedVersion mapped : this.byVersion.values()) {
            result.add(mapped.data());
        }
        return result;
    }

    public Optional<T> getOrOlder(String version) {
        return this.getOrOlderMappedVersion(version).map(MappedVersion::data);
    }

    private Optional<MappedVersion<T>> getOrOlderMappedVersion(String version) {
        Iterator iter = this.byVersion.headMap(version, true).descendingMap().values().iterator();
        return iter.hasNext() ? Optional.of((MappedVersion)iter.next()) : Optional.empty();
    }

    public Optional<T> getOrNewer(String version) {
        Iterator iter = this.byVersion.tailMap(version, true).values().iterator();
        return iter.hasNext() ? Optional.of(((MappedVersion)iter.next()).data()) : Optional.empty();
    }

    public void store(String version, Map<String, String> mappings) {
        this.byVersion.put(version, new MappedVersion(version, mappings, this.mappingLoader));
    }

    public void storeAppend(String version, Map<String, String> mappings) {
        MappedVersion v = (MappedVersion)this.byVersion.get(version);
        if (v == null) {
            throw new IllegalArgumentException("Version " + version + " does not exist yet, so it cannot be appended to");
        }
        v = v.copy();
        for (Map.Entry<String, String> e : mappings.entrySet()) {
            if (e.getValue() == null) {
                v.mappings.remove(e.getKey());
                continue;
            }
            v.mappings.put(e.getKey(), e.getValue());
        }
        this.byVersion.put(version, v);
    }

    public void storeAppendForVersionRange(String versionFrom, String versionTo, String key, String value) {
        this.storeAppendForVersionRange(versionFrom, versionTo, Collections.singletonMap(key, value));
    }

    public void storeAppendForVersionRange(String versionFrom, String versionTo, Map<String, String> mappings) {
        this.byVersion.computeIfAbsent(versionFrom, v -> this.getOrOlderMappedVersion((String)v).map(MappedVersion::copy).orElseThrow(() -> new IllegalStateException("From-Version " + v + " is too old to be used as a base for new mappings")));
        this.byVersion.computeIfAbsent(versionTo, v -> this.getOrOlderMappedVersion((String)v).map(MappedVersion::copy).orElseThrow(() -> new IllegalStateException("To-Version " + v + " is too old to be used as a base for new mappings")));
        for (String version : this.byVersion.subMap(versionFrom, true, versionTo, true).keySet()) {
            this.storeAppend(version, mappings);
        }
    }

    public void renameKey(String oldKey, String newKey) {
        this.modifyMappings(mapped -> {
            String value = mapped.mappings.remove(oldKey);
            if (value != null) {
                mapped.mappings.put(newKey, value);
            }
        });
    }

    public void invert() {
        this.modifyMappings(mapped -> {
            HashMap<String, String> inverted = new HashMap<String, String>();
            for (Map.Entry<String, String> e : mapped.mappings.entrySet()) {
                inverted.put(e.getValue(), e.getKey());
            }
            mapped.mappings.clear();
            mapped.mappings.putAll(inverted);
        });
    }

    private void modifyMappings(Consumer<MappedVersion<T>> modifier) {
        for (MappedVersion mapped : this.byVersion.values()) {
            modifier.accept(mapped);
            mapped.cachedValue = null;
        }
    }

    public void read(InputStream inputStream) throws IOException {
        this.byVersion.clear();
        HashMap<String, String> mappings = new HashMap<String, String>();
        try (DataInputStream stream = new DataInputStream(new InflaterInputStream(inputStream));){
            int numVersions = stream.readInt();
            if (numVersions == 0) {
                return;
            }
            String version = stream.readUTF();
            while (numVersions-- > 0) {
                String entry;
                String nextVersion = version;
                while (!(entry = stream.readUTF()).isEmpty()) {
                    int sep = entry.indexOf(61);
                    if (sep == -1) {
                        nextVersion = entry;
                        break;
                    }
                    if (sep == entry.length() - 1) {
                        mappings.remove(entry.substring(0, sep));
                        continue;
                    }
                    mappings.put(entry.substring(0, sep), entry.substring(sep + 1));
                }
                this.store(version, new HashMap<String, String>(mappings));
                version = nextVersion;
            }
        }
    }

    public void write(OutputStream outputStream) throws IOException {
        try (DataOutputStream stream = new DataOutputStream(new DeflaterOutputStream(outputStream));){
            stream.writeInt(this.byVersion.size());
            Map<Object, Object> previous = Collections.emptyMap();
            for (Map.Entry e : this.byVersion.entrySet()) {
                stream.writeUTF((String)e.getKey());
                Map<String, String> mappings = ((MappedVersion)e.getValue()).mappings;
                for (Map.Entry entry : previous.entrySet()) {
                    if (mappings.containsKey(entry.getKey())) continue;
                    stream.writeUTF((String)entry.getKey() + "=");
                }
                for (Map.Entry<Object, Object> entry : mappings.entrySet()) {
                    if (previous.getOrDefault(entry.getKey(), "").equals(entry.getValue())) continue;
                    stream.writeUTF((String)entry.getKey() + "=" + (String)entry.getValue());
                }
                previous = mappings;
            }
            stream.writeUTF("");
        }
    }

    public static class MappedVersion<T> {
        public final String version;
        public final Map<String, String> mappings;
        private final Function<MappedVersion<T>, T> loader;
        private T cachedValue;

        private MappedVersion(String version, Map<String, String> mappings, Function<MappedVersion<T>, T> loader) {
            this.version = version;
            this.mappings = mappings;
            this.loader = loader;
        }

        public MappedVersion<T> copy() {
            return new MappedVersion<T>(this.version, new HashMap<String, String>(this.mappings), this.loader);
        }

        public T data() {
            T value = this.cachedValue;
            if (value == null) {
                this.cachedValue = value = this.loader.apply(this);
            }
            return value;
        }
    }
}

