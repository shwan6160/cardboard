/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  com.bergerkiller.bukkit.common.utils.StreamUtil
 */
package com.bergerkiller.bukkit.tc.properties.standard.type;

import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public final class ChunkLoadOptions {
    public static final ChunkLoadOptions DEFAULT = new ChunkLoadOptions(Mode.DISABLED, 2);
    public static final ChunkLoadOptions LEGACY_FALSE = new ChunkLoadOptions(Mode.DISABLED, 2);
    public static final ChunkLoadOptions LEGACY_TRUE = new ChunkLoadOptions(Mode.FULL, 2);
    private final Mode mode;
    private final int radius;

    public static ChunkLoadOptions of(Mode mode, int radius) {
        return new ChunkLoadOptions(mode, Math.max(0, radius));
    }

    private ChunkLoadOptions(Mode mode, int radius) {
        this.mode = mode;
        this.radius = radius;
    }

    public Mode mode() {
        return this.mode;
    }

    public boolean keepLoaded() {
        return this.mode != Mode.DISABLED;
    }

    public int radius() {
        return this.radius;
    }

    public ChunkLoadOptions withMode(Mode newMode) {
        return ChunkLoadOptions.of(newMode, this.radius);
    }

    public ChunkLoadOptions withRadius(int newRadius) {
        return ChunkLoadOptions.of(this.mode, newRadius);
    }

    public int hashCode() {
        return this.radius * 4 + this.mode.ordinal();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ChunkLoadOptions) {
            ChunkLoadOptions other = (ChunkLoadOptions)o;
            return this.mode == other.mode && this.radius == other.radius;
        }
        return false;
    }

    public String toString() {
        return "ChunkLoadOptions{keepLoaded=" + this.keepLoaded() + ", mode=" + this.mode().name() + ", radius=" + this.radius() + "}";
    }

    public static enum Mode {
        DISABLED(Arrays.asList("disabled", "false"), 0),
        FULL(Arrays.asList("full", "true"), 2),
        REDSTONE(Collections.singletonList("redstone"), 1),
        MINIMAL(Collections.singletonList("minimal"), 0);

        private final List<String> names;
        private final int perChunkRadius;
        private static final List<String> allNames;
        private static final Map<String, Mode> byName;

        private Mode(List<String> names, int perChunkRadius) {
            this.names = names;
            this.perChunkRadius = perChunkRadius;
        }

        public int getPerChunkRadius() {
            return this.perChunkRadius;
        }

        public List<String> getNames() {
            return this.names;
        }

        public static List<String> getAllNames() {
            return allNames;
        }

        public static Optional<Mode> fromName(String name) {
            Mode mode = byName.get(name);
            if (mode != null) {
                return Optional.of(mode);
            }
            mode = byName.get(name.toUpperCase(Locale.ENGLISH));
            if (mode != null) {
                return Optional.of(mode);
            }
            if (ParseUtil.isBool((String)name)) {
                return Optional.of(ParseUtil.parseBool((String)name) ? FULL : DISABLED);
            }
            return Optional.empty();
        }

        static {
            allNames = (List)Stream.of(Mode.values()).flatMap(m -> m.getNames().stream()).collect(StreamUtil.toUnmodifiableList());
            byName = new HashMap<String, Mode>();
            for (Mode mode : Mode.values()) {
                for (String name : mode.getNames()) {
                    byName.put(name, mode);
                    byName.put(name.toUpperCase(Locale.ENGLISH), mode);
                }
            }
        }
    }
}

