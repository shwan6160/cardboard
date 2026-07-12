/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 */
package com.bergerkiller.bukkit.tc.offline.train.format;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.offline.train.format.OfflineDataBlockSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class OfflineDataBlock {
    private static final byte[] NO_DATA = new byte[0];
    private final DataBlockBuilder dataBlockBuilder;
    public final String name;
    public final byte[] data;
    public final List<OfflineDataBlock> children;

    public static OfflineDataBlock read(DataInputStream stream) throws IOException {
        OfflineDataBlockSerializer serializer = new OfflineDataBlockSerializer();
        return serializer.readDataBlock(stream);
    }

    public static OfflineDataBlock create(String name) {
        return new OfflineDataBlock(new DataBlockBuilder(), name, NO_DATA);
    }

    public static OfflineDataBlock createWithData(String name, byte[] data) {
        return new OfflineDataBlock(new DataBlockBuilder(), name, data);
    }

    public static OfflineDataBlock createWithData(String name, DataWriter writer) throws IOException {
        return new DataBlockBuilder().create(name, writer);
    }

    OfflineDataBlock(DataBlockBuilder dataBlockBuilder, String name, byte[] data) {
        this.dataBlockBuilder = dataBlockBuilder;
        this.name = name;
        this.data = data;
        this.children = new ArrayList<OfflineDataBlock>();
    }

    public void writeTo(DataOutputStream stream) throws IOException {
        OfflineDataBlockSerializer serializer = new OfflineDataBlockSerializer();
        serializer.writeDataBlock(stream, this);
    }

    public DataInputStream readData() {
        return new DataInputStream(new ByteArrayInputStream(this.data));
    }

    public List<OfflineDataBlock> findChildren(String name) {
        return Util.filterList(Collections.unmodifiableList(this.children), c -> c.name.equals(name));
    }

    public OfflineDataBlock findChildOrThrow(String name) {
        return this.findChild(name).orElseThrow(() -> new RuntimeException("Data '" + name + "' is missing in '" + this.name + "' data"));
    }

    public Optional<OfflineDataBlock> findChild(String name) {
        for (OfflineDataBlock child : this.children) {
            if (!child.name.equals(name)) continue;
            return Optional.of(child);
        }
        return Optional.empty();
    }

    public boolean tryReadChild(String name, DataReader reader) throws IOException {
        OfflineDataBlock block = this.findChild(name).orElse(null);
        if (block == null) {
            return false;
        }
        try (DataInputStream stream = block.readData();){
            reader.read(stream);
        }
        return true;
    }

    public OfflineDataBlock addChild(String name) {
        return this.addChild(new OfflineDataBlock(this.dataBlockBuilder, name, NO_DATA));
    }

    public OfflineDataBlock addChild(String name, DataWriter writer) throws IOException {
        OfflineDataBlock child = this.addChildOrAbort(name, writer);
        if (child == null) {
            throw new IllegalStateException("AbortChildException thrown in addChild. Use addChildOrAbort instead!");
        }
        return child;
    }

    public OfflineDataBlock addChildOrAbort(String name, AbortableDataWriter writer) throws IOException {
        OfflineDataBlock child = this.dataBlockBuilder.create(name, writer);
        if (child == null) {
            return null;
        }
        return this.addChild(child);
    }

    public OfflineDataBlock addChild(String name, byte[] data) {
        return this.addChild(new OfflineDataBlock(this.dataBlockBuilder, name, data));
    }

    OfflineDataBlock addChild(OfflineDataBlock child) {
        this.children.add(child);
        return child;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        this.appendToString(str, 0);
        return str.toString();
    }

    private void appendToString(StringBuilder str, int indent) {
        for (int i = 0; i < indent; ++i) {
            str.append("  ");
        }
        str.append(this.name);
        if (this.data.length > 0) {
            str.append(" b[").append(this.data.length).append("]");
        }
        if (!this.children.isEmpty()) {
            str.append(':');
            for (OfflineDataBlock child : this.children) {
                str.append('\n');
                child.appendToString(str, indent + 1);
            }
        }
    }

    static final class DataBlockBuilder {
        private WeakReference<ByteArrayOutputStream> stream = LogicUtil.nullWeakReference();

        DataBlockBuilder() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public OfflineDataBlock create(String name, AbortableDataWriter writer) throws IOException {
            ByteArrayOutputStream tempByteArrayStream = (ByteArrayOutputStream)this.stream.get();
            if (tempByteArrayStream == null) {
                tempByteArrayStream = new ByteArrayOutputStream(64);
                this.stream = new WeakReference<ByteArrayOutputStream>(tempByteArrayStream);
            }
            try {
                try (Object stream = new DataOutputStream(tempByteArrayStream);){
                    writer.write((DataOutputStream)stream);
                }
                stream = new OfflineDataBlock(this, name, tempByteArrayStream.toByteArray());
                return stream;
            }
            catch (AbortChildException ex) {
                OfflineDataBlock offlineDataBlock = null;
                return offlineDataBlock;
            }
            finally {
                tempByteArrayStream.reset();
            }
        }
    }

    @FunctionalInterface
    public static interface AbortableDataWriter {
        public void write(DataOutputStream var1) throws IOException, AbortChildException;
    }

    @FunctionalInterface
    public static interface DataReader {
        public void read(DataInputStream var1) throws IOException;
    }

    public static final class AbortChildException
    extends Exception {
    }

    @FunctionalInterface
    public static interface DataWriter
    extends AbortableDataWriter {
        @Override
        public void write(DataOutputStream var1) throws IOException;
    }
}

