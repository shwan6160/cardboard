/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.logic.ItemStackDeserializerUtils;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class ItemStackDeserializerMigrator
extends ItemStackDeserializerUtils {
    private final int curr_version;
    private int max_version = 0;
    private final List<Entry> entries = new ArrayList<Entry>();

    public ItemStackDeserializerMigrator() {
        this.curr_version = CraftMagicNumbersHandle.getDataVersion();
    }

    public int getCurrentDataVersion() {
        return this.curr_version;
    }

    public int getMaximumDataVersion() {
        return this.max_version;
    }

    public void setMaximumDataVersion(int max) {
        this.max_version = Math.max(this.max_version, max);
    }

    public void register(int data_version, ConverterFunction converter) {
        if (data_version <= this.curr_version && !this.entries.isEmpty()) {
            this.entries.remove(0);
        }
        this.entries.add(0, new Entry(data_version, converter));
        this.max_version = Math.max(this.max_version, data_version);
    }

    protected abstract void baseMigrate(Map<String, Object> var1);

    public void migrate(Map<String, Object> args, String dataVersionKey) {
        int version;
        this.baseMigrate(args);
        Object version_raw = args.get(dataVersionKey);
        if (version_raw instanceof Number && (version = ((Number)version_raw).intValue()) > this.curr_version && version <= this.max_version) {
            for (Entry entry : this.entries) {
                if (version <= entry.output_version) continue;
                if (!entry.converter.convert(args)) break;
                version = entry.output_version;
            }
            if (version == 0) {
                args.remove(dataVersionKey);
            } else {
                args.put(dataVersionKey, version);
            }
        }
    }

    protected static void logFailDeserialize(Map<String, Object> args) {
        Logging.LOGGER_CONFIG.warning("Failed to deserialize ItemStack: " + ItemStackDeserializerMigrator.stringifyPrintDocument(args));
    }

    private static String stringifyPrintDocument(Object value) {
        StringBuilder str = new StringBuilder();
        ItemStackDeserializerMigrator.stringifyPrintDocument(str, 0, value);
        return str.toString();
    }

    private static void stringifyPrintDocument(StringBuilder str, int indent, Object value) {
        if (value instanceof Map) {
            Map map = (Map)value;
            if (map.isEmpty()) {
                str.append("{}");
                return;
            }
            str.append('\n');
            for (Map.Entry e : map.entrySet()) {
                ItemStackDeserializerMigrator.stringifyIndent(str, indent + 1);
                str.append(e.getKey()).append(": ");
                ItemStackDeserializerMigrator.stringifyPrintDocument(str, indent + 1, e.getValue());
                str.append('\n');
            }
        } else if (value instanceof Collection) {
            Collection list = (Collection)value;
            if (list.isEmpty()) {
                str.append("[]");
                return;
            }
            str.append('\n');
            for (Object listItem : list) {
                ItemStackDeserializerMigrator.stringifyIndent(str, indent);
                str.append("- ");
                ItemStackDeserializerMigrator.stringifyPrintDocument(str, indent + 1, listItem);
            }
        } else {
            str.append(value);
        }
    }

    private static void stringifyIndent(StringBuilder str, int indent) {
        for (int i = 0; i < indent; ++i) {
            str.append("  ");
        }
    }

    private static final class Entry {
        public final int output_version;
        public final ConverterFunction converter;

        public Entry(int output_version, ConverterFunction converter) {
            if (converter == null) {
                throw new IllegalArgumentException("Converter can not be null");
            }
            this.output_version = output_version;
            this.converter = converter;
        }
    }

    @FunctionalInterface
    public static interface ConverterFunction {
        public static final ConverterFunction NO_CONVERSION = map -> true;

        public boolean convert(Map<String, Object> var1);
    }
}

